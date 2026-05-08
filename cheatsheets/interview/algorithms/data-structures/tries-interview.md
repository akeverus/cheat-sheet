---
title: "Вопросы на собеседовании: Trie и специальные структуры"
description: "Trie (префиксное дерево), Compressed trie (Radix tree), Suffix tree/array, Union-Find (DSU), применение в автодополнении, IP-routing, поиске подстрок"
tags:
  - interview
  - algorithms
  - tries-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Trie и специальные структуры"
  - "Trie interview"
  - "Radix tree interview"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Trie и специальные структуры`

Trie (префиксное дерево) — основа автодополнения, spell-checkers, IP-routing. Вместе с Trie часто спрашивают **Union-Find** (DSU) — структуру для непересекающихся множеств, применяемую в Kruskal MST, обнаружении циклов и задачах вроде Number of Islands.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Trie Data Structure in Java — Baeldung](https://www.baeldung.com/trie-java)
- [Disjoint Set Union (Union-Find) — Baeldung](https://www.baeldung.com/cs/disjoint-set-union)
- [Suffix Tree — Baeldung](https://www.baeldung.com/cs/suffix-tree)
- [Aho-Corasick Algorithm — Baeldung](https://www.baeldung.com/cs/aho-corasick-algorithm)
- [Trie — GeeksforGeeks](https://www.geeksforgeeks.org/trie-insert-and-search/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Trie**
- [Q1. (!) Что такое Trie?](#q1--что-такое-trie)
- [Q2. (!) Реализация Trie на HashMap?](#q2--реализация-trie-на-hashmap)
- [Q3. Реализация Trie на массиве (для ASCII)?](#q3-реализация-trie-на-массиве-для-ascii)
- [Q4. (!) Сложности операций Trie?](#q4--сложности-операций-trie)
- [Q5. (!) Зачем Trie вместо HashMap?](#q5--зачем-trie-вместо-hashmap)

**Расширения Trie**
- [Q6. (!) Compressed trie (Radix tree)?](#q6--compressed-trie-radix-tree)
- [Q7. (!) Что такое Suffix tree и зачем?](#q7--что-такое-suffix-tree-и-зачем)
- [Q8. Suffix array — альтернатива suffix tree?](#q8-suffix-array--альтернатива-suffix-tree)
- [Q9. Aho-Corasick — поиск множества образцов?](#q9-aho-corasick--поиск-множества-образцов)

**Классические задачи**
- [Q10. (!) Implement autocomplete?](#q10--implement-autocomplete)
- [Q11. Word Search II — поиск множества слов в grid?](#q11-word-search-ii--поиск-множества-слов-в-grid)
- [Q12. (!) Longest Common Prefix?](#q12--longest-common-prefix)
- [Q13. Replace Words — замена корнями?](#q13-replace-words--замена-корнями)
- [Q14. Maximum XOR of Two Numbers?](#q14-maximum-xor-of-two-numbers)

**Union-Find (DSU)**
- [Q15. (!) Что такое Union-Find?](#q15--что-такое-union-find)
- [Q16. (!) Реализация Union-Find?](#q16--реализация-union-find)
- [Q17. (!) Что такое path compression?](#q17--что-такое-path-compression)
- [Q18. (!) Что такое union by rank/size?](#q18--что-такое-union-by-ranksize)
- [Q19. Какова амортизированная сложность с обеими оптимизациями?](#q19-какова-амортизированная-сложность-с-обеими-оптимизациями)

**Применения Union-Find**
- [Q20. (!) Number of Islands через Union-Find?](#q20--number-of-islands-через-union-find)
- [Q21. (!) Detect cycle in undirected graph через Union-Find?](#q21--detect-cycle-in-undirected-graph-через-union-find)
- [Q22. (!) Connected Components в графе?](#q22--connected-components-в-графе)
- [Q23. Accounts Merge — объединение аккаунтов?](#q23-accounts-merge--объединение-аккаунтов)
- [Q24. Redundant Connection?](#q24-redundant-connection)

**Real-world применения**
- [Q25. (!) Где Trie используется в production?](#q25--где-trie-используется-в-production)
- [Q26. Где Union-Find используется в production?](#q26-где-union-find-используется-в-production)
- [Q27. Что такое Patricia Trie?](#q27-что-такое-patricia-trie)
- [Q28. (!) Что такое Bloom filter и связь с Trie?](#q28--что-такое-bloom-filter-и-связь-с-trie)

## Q1. (!) Что такое Trie?

**Trie (prefix tree, префиксное дерево)** — дерево, где каждый путь от корня до узла представляет строку (или её префикс). Каждый узел хранит:
- Ссылки на детей (по одной на возможный символ)
- Флаг `isEndOfWord`

```mermaid
graph TD
    Root([root]) --> C1([c])
    Root --> A1([a])
    C1 --> A2([a])
    A2 --> T1([t ✓])
    A2 --> R1([r ✓])
    A1 --> N1([n])
    N1 --> D1([d ✓])

    style T1 fill:#4a9eff,color:white
    style R1 fill:#4a9eff,color:white
    style D1 fill:#4a9eff,color:white
```

Дерево для слов: `cat`, `car`, `and`. Узлы с ✓ — конец слова.

**Применения:**
- Autocomplete (поисковая строка)
- Spell-checker
- IP-routing tables (longest prefix match)
- Поиск слов с общим префиксом
- Dictionary для Word Search


> [!mcq]
> - [ ] Хеш-таблица строк с O(1) поиском | ❌ ПОСЛЕДСТВИЕ: startsWith("ca") требует O(N·L) перебора всех N ключей — autocomplete деградирует до full scan
> - [x] Дерево, где путь от корня до узла = строка/префикс; каждый узел хранит children-ссылки и флаг isEndOfWord | ✓ ПРИМЕНЯТЬ: autocomplete, IP routing, spell-check 📋 ПРАВИЛО: Trie = prefix-indexed tree, O(L) на insert/search/startsWith 🔗 См. Q4
> - [ ] Сбалансированное BST строк с бинарным поиском | ❌ ПОСЛЕДСТВИЕ: insert/search O(L·log N) из-за посимвольных сравнений — Trie даёт O(L) без сравнений между словами
> - [ ] Граф с направленными рёбрами без ограничения ацикличности | ❌ ПОСЛЕДСТВИЕ: циклы делают рекурсивный insert/search бесконечным; Trie строго ациклично

## Q2. (!) Реализация Trie на HashMap?

```java
class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord = false;
}

class Trie {
    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            curr.children.putIfAbsent(c, new TrieNode());
            curr = curr.children.get(c);
        }
        curr.isEndOfWord = true;
    }

    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    private TrieNode findNode(String s) {
        TrieNode curr = root;
        for (char c : s.toCharArray()) {
            if (!curr.children.containsKey(c)) return null;
            curr = curr.children.get(c);
        }
        return curr;
    }
}
```

**Плюсы HashMap:** работает с любым unicode символом, занимает место только на реальные символы.
**Минусы:** overhead на HashMap (boxing, hashing).


> [!mcq]
> - [ ] Хранить все слова целиком как ключи одной Map<String, Boolean> | ❌ ПОСЛЕДСТВИЕ: startsWith(prefix) требует O(N·L) перебора всех ключей — нет prefix structure
> - [ ] TrieNode[] children = new TrieNode[26] для поддержки unicode | ❌ ПОСЛЕДСТВИЕ: ArrayIndexOutOfBoundsException для символов > 'z'; HashMap нужен для произвольного алфавита
> - [x] Map<Character, TrieNode> children + boolean isEndOfWord; insert/search итерируют по символам, putIfAbsent для новых узлов | ✓ ПРИМЕНЯТЬ: любой алфавит, unicode 📋 ПРАВИЛО: HashMap-Trie = гибкий алфавит, O(L) операции 🔗 См. Q3
> - [ ] Рекурсивно передавать String как ключ без ссылок на узлы | ❌ ПОСЛЕДСТВИЕ: это hash lookup, не Trie — теряется структура prefix sharing; не работает startsWith

## Q3. Реализация Trie на массиве (для ASCII)?

```java
class TrieNode {
    TrieNode[] children = new TrieNode[26]; // только 'a'-'z'
    boolean isEndOfWord = false;
}

class Trie {
    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (curr.children[idx] == null) curr.children[idx] = new TrieNode();
            curr = curr.children[idx];
        }
        curr.isEndOfWord = true;
    }

    public boolean search(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (curr.children[idx] == null) return false;
            curr = curr.children[idx];
        }
        return curr.isEndOfWord;
    }
}
```

**Плюсы массива:** быстрее (нет hashing), кэш-friendly.
**Минусы:** фиксированный алфавит, память на пустые слоты. Для unicode — нерационально.


> [!mcq]
> - [ ] Map<Character, TrieNode> children для поддержки только ASCII-символов | ❌ ПОСЛЕДСТВИЕ: HashMap добавляет boxing/hashing overhead; для ASCII массив cache-friendly и быстрее
> - [ ] Хранить символы как String вместо индекса c - 'a' | ❌ ПОСЛЕДСТВИЕ: нарушает O(1) доступ к дочернему узлу — нужна хеш-операция вместо прямой индексации
> - [ ] TrieNode[128] для полного ASCII с проверкой всех символов | ❌ ПОСЛЕДСТВИЕ: пустые слоты [0..64] и [91..96] тратят O(128·N) памяти вместо O(26·N)
> - [x] TrieNode[] children = new TrieNode[26]; idx = c - 'a'; быстрее HashMap, нет boxing, cache-friendly, но только 'a'-'z' | ✓ ПРИМЕНЯТЬ: когда алфавит фиксирован (a-z) 📋 ПРАВИЛО: array-Trie = скорость + фиксированный алфавит 🔗 См. Q2

## Q4. (!) Сложности операций Trie?

| Операция | Сложность | Память |
|----------|-----------|--------|
| `insert(word)` | `O(L)`, L = длина | `O(L · ALPHABET)` |
| `search(word)` | `O(L)` | — |
| `startsWith(prefix)` | `O(L)` | — |
| `delete(word)` | `O(L)` | — |
| `autocomplete(prefix)` | `O(L + K)`, K = ответов | — |

**Память:** `O(N · L · ALPHABET)` в худшем случае (где N — число слов). Compressed trie уменьшает это.


> [!mcq]
> - [ ] insert = O(L²) потому что строки immutable и нужны substring на каждом шаге | ❌ ПОСЛЕДСТВИЕ: Trie итерирует по символам, не создаёт подстрок; insert = O(L)
> - [x] insert/search/startsWith/delete = O(L), autocomplete = O(L + K) где K = число результатов | ✓ ПРИМЕНЯТЬ: анализ эффективности Trie vs HashMap 📋 ПРАВИЛО: все операции Trie = O(L), L = длина слова 🔗 См. Q5
> - [ ] search = O(log N) как бинарный поиск в отсортированном массиве | ❌ ПОСЛЕДСТВИЕ: Trie не сравнивает слова между собой; каждый символ = один шаг O(1)
> - [ ] autocomplete = O(N·L) потому что нужно проверить все N слов | ❌ ПОСЛЕДСТВИЕ: Trie идёт к узлу prefix за O(L), затем DFS по поддереву — не N слов целиком

## Q5. (!) Зачем Trie вместо HashMap?

Если нужно искать слово целиком — HashMap проще и быстрее (`O(L)` vs `O(L)`, но с меньшими константами).

**Trie выигрывает когда:**
1. **Префиксный поиск** — `startsWith`, autocomplete: `O(L + K)` vs HashMap `O(N · L)` (нужно перебрать все)
2. **Меньше памяти при общих префиксах** — `cat, car, card` — общий путь `ca` хранится один раз
3. **Lexicographic ordering** — естественный порядок обхода
4. **Prefix-based deletion** — удалить все слова с префиксом
5. **Spell-check / fuzzy search** — поиск с расстоянием Левенштейна


> [!mcq]
> - [x] При prefix search (startsWith, autocomplete) Trie O(L+K) против HashMap O(N·L) — нужно перебрать все ключи | ✓ ПРИМЕНЯТЬ: autocomplete, spell-check, IP routing 📋 ПРАВИЛО: Trie побеждает HashMap только при prefix queries 🔗 См. Q4
> - [ ] Trie быстрее HashMap при поиске полного слова (search) | ❌ ПОСЛЕДСТВИЕ: HashMap O(L) (хэш+сравнение) = Trie O(L); HashMap имеет меньший константный фактор при full-match
> - [ ] Trie всегда потребляет меньше памяти чем HashMap | ❌ ПОСЛЕДСТВИЕ: только при большом числе общих префиксов; для уникальных слов Trie хуже из-за пустых узлов
> - [ ] Trie поддерживает O(1) insert в отличие от HashMap O(L) | ❌ ПОСЛЕДСТВИЕ: Trie insert = O(L) посимвольно; HashMap O(L) для хэша; обе O(L)

## Q6. (!) Compressed trie (Radix tree)?

**Compressed trie (Patricia trie, Radix tree)** — Trie с объединёнными цепочками: если узел имеет одного ребёнка и не является концом слова, его сжимают.

```
Обычный Trie:           Compressed:
  c                       c
  └ a                     └ a
    └ r ✓                   └ r ✓
    └ t ✓                   └ t ✓
```

**Применения:**
- IP routing tables (Patricia trie)
- File system paths
- Database indexes (Btrfs, Ethereum tries)

**Плюсы:** компактнее, быстрее на больших словарях.
**Минусы:** сложнее реализовать, особенно delete с разделением узлов.


> [!mcq]
> - [ ] Хранит только конечные узлы слов, промежуточные отбрасывает | ❌ ПОСЛЕДСТВИЕ: теряется prefix structure; невозможен startsWith и autocomplete
> - [ ] Сжимает дерево через хеш-коллизии для экономии памяти | ❌ ПОСЛЕДСТВИЕ: это hash map collision bucketing, не Radix tree; Patricia сжимает цепочки узлов с одним потомком
> - [x] Объединяет цепочки узлов с одним потомком в одну метку; Patricia/Radix; меньше узлов при длинных уникальных суффиксах | ✓ ПРИМЕНЯТЬ: IP routing tables, file system paths, Ethereum state 📋 ПРАВИЛО: Radix = Trie без single-child chains 🔗 См. Q27
> - [ ] Использует B-Tree вместо дерева для cache-эффективности | ❌ ПОСЛЕДСТВИЕ: B-Tree оптимизирован для дискового хранения, не для prefix search в памяти

## Q7. (!) Что такое Suffix tree и зачем?

**Suffix tree** — Trie всех суффиксов строки. Позволяет за `O(m)` найти любую подстроку длины `m`.

Для строки `"banana$"` (символ `$` — терминатор):
```
Суффиксы: banana$, anana$, nana$, ana$, na$, a$, $
```

**Применения:**
- Биоинформатика (ДНК-последовательности)
- Plagiarism detection
- Поиск повторов в строке
- Longest common substring двух строк
- String compression

**Алгоритм Ukkonen** строит за `O(n)` время.

**Память:** `O(n²)` наивно, `O(n)` с оптимизациями (compressed).


> [!mcq]
> - [ ] Дерево, хранящее суффиксы в бинарном отсортированном виде | ❌ ПОСЛЕДСТВИЕ: это suffix array + LCP, не suffix tree; разная структура данных и API
> - [x] Trie всех суффиксов строки; поиск любой подстроки за O(m); строится алгоритмом Ukkonen за O(n) | ✓ ПРИМЕНЯТЬ: биоинформатика (ДНК), longest common substring, plagiarism detection 📋 ПРАВИЛО: suffix tree = O(m) substring search via all-suffixes trie 🔗 См. Q8
> - [ ] Trie только первых N суффиксов без терминатора "$" | ❌ ПОСЛЕДСТВИЕ: без терминатора суффиксы могут совпадать и алгоритм Ukkonen некорректно строит дерево
> - [ ] Инвертированный индекс строки для быстрого поиска слов | ❌ ПОСЛЕДСТВИЕ: инвертированный индекс = mapping слово→позиции; suffix tree = все суффиксы в одном дереве

## Q8. Suffix array — альтернатива suffix tree?

**Suffix array** — отсортированный массив всех суффиксов (точнее, их индексов).

Для `"banana"`:
```
Индекс  Суффикс
5       a
3       ana
1       anana
0       banana
4       na
2       nana
```

Suffix array: `[5, 3, 1, 0, 4, 2]`.

**Плюсы перед suffix tree:**
- Меньше памяти (4-8 байт на суффикс)
- Проще реализация
- Лучше cache locality

**Сложность построения:** `O(n log² n)` или `O(n)` с DC3/SA-IS.

Поиск подстроки — `O(m log n)` через бинарный поиск + `O(m)` сравнение.


> [!mcq]
> - [ ] Массив длин суффиксов для поиска подстроки | ❌ ПОСЛЕДСТВИЕ: это LCP array (longest common prefix array), не suffix array; SA хранит индексы начала суффиксов
> - [ ] Хеш-таблица всех подстрок для O(1) поиска | ❌ ПОСЛЕДСТВИЕ: хеш всех подстрок занимает O(n²) памяти; suffix array = O(n) памяти
> - [ ] Сжатый Trie суффиксов в виде DAG | ❌ ПОСЛЕДСТВИЕ: DAG суффиксов = suffix automaton; SA = отсортированный массив индексов, не граф
> - [x] Отсортированный массив индексов всех суффиксов; меньше памяти чем suffix tree; поиск за O(m log n) через binary search | ✓ ПРИМЕНЯТЬ: когда памяти мало, строится за O(n log²n) 📋 ПРАВИЛО: suffix array = sorted suffix indices, O(m log n) search 🔗 См. Q7

## Q9. Aho-Corasick — поиск множества образцов?

**Aho-Corasick** — алгоритм для одновременного поиска **множества образцов** в тексте за `O(n + m + z)`, где `z` — число найденных вхождений.

Использует Trie + **failure links** (как KMP, но для дерева).

```java
// Псевдокод
class AhoCorasick {
    TrieNode root;

    void buildFailureLinks() {
        // BFS, для каждого узла находим suffix link на самый длинный суффикс,
        // который тоже является префиксом какого-то слова
    }

    List<int[]> search(String text) {
        // Проходим по тексту, при каждой неуспешной попытке —
        // переходим по failure link
    }
}
```

**Применения:**
- Антивирусы (поиск сигнатур)
- Spam filters (multiple keyword matching)
- DNA sequence analysis
- Snort IDS (intrusion detection)

В Java есть библиотека **ahocorasick** (`org.ahocorasick:ahocorasick`).


> [!mcq]
> - [ ] DFS по Trie для каждого образца отдельно | ❌ ПОСЛЕДСТВИЕ: O(n·K·m) против O(n+m+z); без failure links повторно сканируется текст для каждого паттерна
> - [ ] KMP для каждого образца параллельно | ❌ ПОСЛЕДСТВИЕ: K·(n+m) вместо O(n+m+z); Aho-Corasick объединяет все паттерны в одном Trie
> - [x] Trie + failure links (как KMP для дерева); поиск всех K образцов за O(n + m + z), z = число найденных вхождений | ✓ ПРИМЕНЯТЬ: антивирусы, spam filters, DNA analysis 📋 ПРАВИЛО: Aho-Corasick = multi-pattern search O(n+m+z) 🔗 См. Q9
> - [ ] Regex matching с backtracking по Trie | ❌ ПОСЛЕДСТВИЕ: catastrophic backtracking O(2^n) в худшем случае; Aho-Corasick всегда O(n+m+z)

## Q10. (!) Implement autocomplete?

```java
class Trie {
    TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode curr = root;
        for (char c : word.toCharArray()) {
            curr.children.putIfAbsent(c, new TrieNode());
            curr = curr.children.get(c);
        }
        curr.isEndOfWord = true;
    }

    public List<String> autocomplete(String prefix) {
        List<String> result = new ArrayList<>();
        TrieNode curr = root;
        for (char c : prefix.toCharArray()) {
            if (!curr.children.containsKey(c)) return result;
            curr = curr.children.get(c);
        }
        dfs(curr, new StringBuilder(prefix), result);
        return result;
    }

    private void dfs(TrieNode node, StringBuilder sb, List<String> result) {
        if (node.isEndOfWord) result.add(sb.toString());
        for (Map.Entry<Character, TrieNode> e : node.children.entrySet()) {
            sb.append(e.getKey());
            dfs(e.getValue(), sb, result);
            sb.deleteCharAt(sb.length() - 1);
        }
    }
}
```

`O(L + K · M)`, где `L` — префикс, `K` — число ответов, `M` — длина каждого. Для **top-K** автодополнений можно хранить частоту в каждом узле и применять heap.


> [!mcq]
> - [ ] Отсортированный список слов + binary search на prefix | ❌ ПОСЛЕДСТВИЕ: нет быстрого DFS по совпавшим словам; нельзя вернуть все слова с prefix без O(K) сканирования
> - [x] Вставляем слова в Trie; для prefix находим узел за O(L); затем DFS по поддереву собирает все слова за O(K·M) | ✓ ПРИМЕНЯТЬ: autocomplete, top-K с частотой в узлах 📋 ПРАВИЛО: autocomplete = Trie lookup O(L) + DFS subtree O(K·M) 🔗 См. Q11
> - [ ] HashMap<String, List<String>> prefix→words хранит все prefixes | ❌ ПОСЛЕДСТВИЕ: O(L²) памяти на слово (все prefixes явно); Trie хранит только O(L) узлов
> - [ ] Хранить частоту слов только в корне Trie для top-K | ❌ ПОСЛЕДСТВИЕ: корень не знает о распределении по поддереву; частота нужна в каждом узле

## Q11. Word Search II — поиск множества слов в grid?

Дан 2D grid и список слов. Найти все слова, существующие в grid (по соседним клеткам).

```java
List<String> findWords(char[][] board, String[] words) {
    Trie trie = new Trie();
    for (String w : words) trie.insert(w);

    Set<String> result = new HashSet<>();
    int rows = board.length, cols = board[0].length;
    for (int r = 0; r < rows; r++)
        for (int c = 0; c < cols; c++)
            dfs(board, r, c, trie.root, new StringBuilder(), result);
    return new ArrayList<>(result);
}

void dfs(char[][] board, int r, int c, TrieNode node,
         StringBuilder path, Set<String> result) {
    if (r < 0 || r >= board.length || c < 0 || c >= board[0].length
        || board[r][c] == '#') return;

    char ch = board[r][c];
    TrieNode next = node.children.get(ch);
    if (next == null) return;

    path.append(ch);
    if (next.isEndOfWord) result.add(path.toString());

    board[r][c] = '#'; // mark visited
    dfs(board, r + 1, c, next, path, result);
    dfs(board, r - 1, c, next, path, result);
    dfs(board, r, c + 1, next, path, result);
    dfs(board, r, c - 1, next, path, result);
    board[r][c] = ch; // restore

    path.deleteCharAt(path.length() - 1);
}
```

Без Trie — `O(W · R · C · 4^L)` (для каждого слова DFS). С Trie — все слова обрабатываются параллельно, `O(R · C · 4^L)`.


> [!mcq]
> - [x] Строим Trie из слов + DFS по grid с текущим TrieNode; прекращаем если нет дочернего узла для символа | ✓ ПРИМЕНЯТЬ: поиск множества слов в grid, Word Search II 📋 ПРАВИЛО: Trie+DFS = параллельный поиск всех слов O(R·C·4^L) 🔗 См. Q10
> - [ ] HashSet слов + DFS от каждой клетки для каждого слова отдельно | ❌ ПОСЛЕДСТВИЕ: O(W·R·C·4^L) — в W раз медленнее; Trie устраняет повторный DFS для каждого слова
> - [ ] BFS по grid и сравнение каждого пути со словами через сортировку | ❌ ПОСЛЕДСТВИЕ: BFS не отслеживает путь через несмежные клетки; DFS+Trie прекращает ветку при несовпадении
> - [ ] TreeMap слов + поиск по лексикографическому порядку клеток | ❌ ПОСЛЕДСТВИЕ: TreeMap не помогает при обходе 2D grid; нет связи между позицией символа и узлом дерева

## Q12. (!) Longest Common Prefix?

Самый длинный общий префикс массива строк.

**Подход 1 — Trie:**

```java
String longestCommonPrefix(String[] strs) {
    if (strs.length == 0) return "";
    Trie trie = new Trie();
    for (String s : strs) trie.insert(s);

    StringBuilder sb = new StringBuilder();
    TrieNode curr = trie.root;
    while (curr.children.size() == 1 && !curr.isEndOfWord) {
        char c = curr.children.keySet().iterator().next();
        sb.append(c);
        curr = curr.children.get(c);
    }
    return sb.toString();
}
```

**Подход 2 — vertical scan (без Trie, проще):**

```java
String longestCommonPrefix(String[] strs) {
    if (strs.length == 0) return "";
    for (int i = 0; i < strs[0].length(); i++) {
        char c = strs[0].charAt(i);
        for (int j = 1; j < strs.length; j++) {
            if (i >= strs[j].length() || strs[j].charAt(i) != c)
                return strs[0].substring(0, i);
        }
    }
    return strs[0];
}
```

`O(S)`, где `S` — общая длина строк.


> [!mcq]
> - [ ] Sorted array + сравнить первую и последнюю строки | ❌ ПОСЛЕДСТВИЕ: O(N log N) сортировка + O(L) сравнение; vertical scan O(S) без сортировки делает то же
> - [x] Вставить строки в Trie; идти пока children.size()==1 && !isEndOfWord; или vertical scan O(S) | ✓ ПРИМЕНЯТЬ: LCP нескольких строк 📋 ПРАВИЛО: LCP через Trie = двигаться пока один потомок + не конец слова 🔗 См. Q13
> - [ ] Binary search на длину prefix + проверка каждой строки | ❌ ПОСЛЕДСТВИЕ: O(N·mid) на каждую проверку; vertical scan O(S) — один проход
> - [ ] Reduce через zip всех пар строк | ❌ ПОСЛЕДСТВИЕ: zip создаёт промежуточные объекты; тот же O(S) но с лишним overhead

## Q13. Replace Words — замена корнями?

Дан словарь корней и предложение. Заменить каждое слово на корень из словаря, если он является префиксом.

```java
String replaceWords(List<String> dict, String sentence) {
    Trie trie = new Trie();
    for (String root : dict) trie.insert(root);

    StringBuilder sb = new StringBuilder();
    String[] words = sentence.split(" ");
    for (int i = 0; i < words.length; i++) {
        if (i > 0) sb.append(" ");
        sb.append(findRoot(trie, words[i]));
    }
    return sb.toString();
}

String findRoot(Trie trie, String word) {
    TrieNode curr = trie.root;
    StringBuilder root = new StringBuilder();
    for (char c : word.toCharArray()) {
        if (!curr.children.containsKey(c) || curr.isEndOfWord) break;
        root.append(c);
        curr = curr.children.get(c);
    }
    return curr.isEndOfWord ? root.toString() : word;
}
```

`O(N + M)`, где N — общая длина словаря, M — длина предложения.


> [!mcq]
> - [x] Вставить корни в Trie; для каждого слова идти до isEndOfWord — это корень; O(N+M), N=длина словаря, M=длина предложения | ✓ ПРИМЕНЯТЬ: замена слов кратчайшим префиксом из словаря 📋 ПРАВИЛО: Replace Words = Trie stopProp at isEndOfWord 🔗 См. Q12
> - [ ] Сортировать корни по длине + startsWith() для каждого слова | ❌ ПОСЛЕДСТВИЕ: O(R·W·L) для R корней, W слов; Trie O(N+M)
> - [ ] Regex с alternation (root1|root2|...) всех корней | ❌ ПОСЛЕДСТВИЕ: catastrophic backtracking O(2^R) при совпадающих префиксах; Trie гарантирует O(L)
> - [ ] HashMap корней + проверка всех L prefixes каждого слова | ❌ ПОСЛЕДСТВИЕ: O(L²) на слово — L prefixes × L хеш; Trie проходит L символов однократно

## Q14. Maximum XOR of Two Numbers?

Найти пару чисел в массиве с максимальным XOR.

**Trick — Trie на битах:** для каждого числа храним его биты от старшего к младшему.

```java
class BitTrie {
    BitTrie[] children = new BitTrie[2];
}

int findMaximumXOR(int[] nums) {
    BitTrie root = new BitTrie();
    for (int num : nums) {
        BitTrie curr = root;
        for (int i = 31; i >= 0; i--) {
            int bit = (num >> i) & 1;
            if (curr.children[bit] == null) curr.children[bit] = new BitTrie();
            curr = curr.children[bit];
        }
    }

    int max = 0;
    for (int num : nums) {
        BitTrie curr = root;
        int xor = 0;
        for (int i = 31; i >= 0; i--) {
            int bit = (num >> i) & 1;
            int wanted = 1 - bit; // ищем противоположный для max XOR
            if (curr.children[wanted] != null) {
                xor |= (1 << i);
                curr = curr.children[wanted];
            } else {
                curr = curr.children[bit];
            }
        }
        max = Math.max(max, xor);
    }
    return max;
}
```

`O(N · 32) = O(N)`. Без Trie — `O(N²)` через все пары.


> [!mcq]
> - [ ] Сортировка + два указателя для максимального XOR | ❌ ПОСЛЕДСТВИЕ: XOR не монотонен по отсортированным значениям — нельзя два-указателя; нужен bit-wise подход
> - [ ] Nested loop O(N²) через все пары | ❌ ПОСЛЕДСТВИЕ: при N=10^5 это 10^10 операций; Bit Trie O(N·32)=O(N)
> - [ ] Greedy побитово без Trie, sorting по каждому биту | ❌ ПОСЛЕДСТВИЕ: нельзя greedy без структуры — для каждого числа нужен быстрый поиск "противоположного бита"
> - [x] Bit Trie на 32 битах числа; для каждого числа ищем в Trie противоположный бит (1→0, 0→1); O(N·32) | ✓ ПРИМЕНЯТЬ: Maximum XOR в массиве 📋 ПРАВИЛО: Bit Trie = хранить биты от MSB, искать complement 🔗 См. Q14

## Q15. (!) Что такое Union-Find?

**Union-Find (Disjoint Set Union, DSU)** — структура для отслеживания разбиения элементов на **непересекающиеся группы**. Поддерживает:
- `find(x)` — найти представителя (корень) группы элемента `x`
- `union(x, y)` — объединить группы элементов `x` и `y`

С оптимизациями **path compression** + **union by rank** обе операции работают за **амортизированный `O(α(n)) ≈ O(1)`** (где `α` — обратная функция Аккермана).


> [!mcq]
> - [ ] BFS/DFS для поиска связных компонент | ❌ ПОСЛЕДСТВИЕ: BFS/DFS = O(V+E) за запрос; UF с оптимизациями O(α(n)) для online-запросов connectivity
> - [ ] HashMap<Integer, Set<Integer>> для хранения групп | ❌ ПОСЛЕДСТВИЕ: union требует merge двух Set = O(min(s1,s2)); UF с path compression = O(α(n))
> - [x] Структура для непересекающихся множеств; find(x) = корень группы; union(x,y) = объединить группы; O(α(n)) с path compression + union by rank | ✓ ПРИМЕНЯТЬ: dynamic connectivity, cycle detection, Kruskal MST 📋 ПРАВИЛО: UF = disjoint sets, find+union O(α(n)) 🔗 См. Q16
> - [ ] Сбалансированное BST компонент для log-time lookup | ❌ ПОСЛЕДСТВИЕ: BST для ordered sets; UF оптимизирован для connectivity, не для ordering

## Q16. (!) Реализация Union-Find?

```java
class UnionFind {
    private final int[] parent;
    private final int[] rank;
    private int components;

    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i; // каждый сам себе корень
        components = n;
    }

    public int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]); // path compression
        return parent[x];
    }

    public boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false; // уже в одной группе

        // union by rank: меньший подвешиваем под больший
        if (rank[px] < rank[py]) { int tmp = px; px = py; py = tmp; }
        parent[py] = px;
        if (rank[px] == rank[py]) rank[px]++;
        components--;
        return true;
    }

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    public int getComponents() { return components; }
}
```


> [!mcq]
> - [ ] Хранить group_id в HashMap; при union перезаписывать все элементы меньшей группы | ❌ ПОСЛЕДСТВИЕ: union = O(min(s1,s2)) перезаписей; UF union = O(α(n)) с lazy parent update
> - [x] int[] parent (каждый сам себе); find с path compression; union by rank; O(α(n)) амортизированно | ✓ ПРИМЕНЯТЬ: Kruskal MST, cycle detection, Number of Islands 📋 ПРАВИЛО: UF = parent array + path compression + rank 🔗 См. Q17
> - [ ] LinkedList цепочек, parent[x] = следующий в цепи | ❌ ПОСЛЕДСТВИЕ: find = O(n) обход цепочки; без path compression дерево вырастает в линию
> - [ ] parent[] хранит значение корня, обновляется при каждом union | ❌ ПОСЛЕДСТВИЕ: без lazy path compression find остаётся O(h), h растёт до O(n)

## Q17. (!) Что такое path compression?

**Path compression** — при `find(x)` все узлы на пути сразу указывают на корень.

```java
int find(int x) {
    if (parent[x] != x) parent[x] = find(parent[x]); // recursive compression
    return parent[x];
}
```

**До:** `0 → 1 → 2 → 3 → root`
**После:** `0, 1, 2, 3 → root`

Без path compression find — `O(h)`. С — амортизированный почти `O(1)`.

**Альтернатива (итеративная):**

```java
int find(int x) {
    int root = x;
    while (parent[root] != root) root = parent[root];
    // Идём ещё раз, перенаправляя
    while (parent[x] != root) {
        int next = parent[x];
        parent[x] = root;
        x = next;
    }
    return root;
}
```


> [!mcq]
> - [ ] При union переименовывать все элементы меньшей группы в ID большей | ❌ ПОСЛЕДСТВИЕ: O(min(s1,s2)) переименований при каждом union; lazy pointer update = O(1)
> - [x] При find(x) все узлы на пути к корню получают parent = root напрямую; амортизированный O(α(n)) вместо O(h) | ✓ ПРИМЕНЯТЬ: всегда использовать с union by rank 📋 ПРАВИЛО: path compression = flat tree after find 🔗 См. Q18
> - [ ] Хранить глубину и прыгать через depth/2 шагов (path halving) | ❌ ПОСЛЕДСТВИЕ: path halving — частичная оптимизация; full compression лучше амортизирует
> - [ ] После find перестраивать всё дерево сбалансированным образом | ❌ ПОСЛЕДСТВИЕ: full rebalancing = O(n); path compression плющит только текущий путь

## Q18. (!) Что такое union by rank/size?

**Union by rank** — при объединении подвешиваем дерево с меньшим **rank** под дерево с большим. Если ranks равны — выбираем любое и инкрементируем rank.

```java
boolean union(int x, int y) {
    int px = find(x), py = find(y);
    if (px == py) return false;
    if (rank[px] < rank[py]) parent[px] = py;
    else if (rank[px] > rank[py]) parent[py] = px;
    else { parent[py] = px; rank[px]++; }
    return true;
}
```

**Union by size** — аналогично, но используем размер дерева вместо rank.

Без union by rank — деревья могут вырастать в линию (`O(n)` высота). С — высота `O(log n)`.


> [!mcq]
> - [ ] Всегда подвешивать новое дерево под старое (по порядку union) | ❌ ПОСЛЕДСТВИЕ: порядок union не коррелирует с размером; вырождение в связный список O(n) высоты
> - [x] Подвешивать дерево с меньшим rank под большее; если rank равен — любое и rank++; высота ≤ O(log n) | ✓ ПРИМЕНЯТЬ: в паре с path compression для O(α(n)) 📋 ПРАВИЛО: union by rank = малое под большое = log-height bound 🔗 См. Q17
> - [ ] Случайный выбор направления подвески | ❌ ПОСЛЕДСТВИЕ: без гарантии баланса высота растёт до O(n) при неудачных union
> - [ ] Union by alphabetical order элементов | ❌ ПОСЛЕДСТВИЕ: UF работает с числовыми ID; алфавитный порядок не снижает высоту дерева

## Q19. Какова амортизированная сложность с обеими оптимизациями?

С **path compression + union by rank** последовательность из `m` операций над `n` элементами выполняется за **`O(m · α(n))`**, где `α(n)` — обратная функция Аккермана.

`α(n) < 5` для всех практических `n` (до 2^65536). Поэтому каждая операция считается **константной** на практике.

Без оптимизаций — `O(m · n)` в худшем случае.


> [!mcq]
> - [ ] O(m·log n) — с union by rank без path compression | ❌ ПОСЛЕДСТВИЕ: с обеими оптимизациями O(m·α(n)), лучше чем O(m·log n)
> - [ ] O(m·log log n) — с path compression без union by rank | ❌ ПОСЛЕДСТВИЕ: это сложность только с PC; нужны обе оптимизации для O(m·α(n))
> - [x] O(m·α(n)) для m операций над n элементами; α(n) < 5 для всех практических n; считается O(1) на практике | ✓ ПРИМЕНЯТЬ: когда нужна nearly-constant connectivity 📋 ПРАВИЛО: PC + union by rank = O(α(n)) ≈ O(1) 🔗 См. Q17, Q18
> - [ ] O(m·n) — без оптимизаций | ❌ ПОСЛЕДСТВИЕ: O(m·n) только без обеих оптимизаций; вопрос про ОБЕИМИ оптимизациями

## Q20. (!) Number of Islands через Union-Find?

Подсчёт связных регионов из `1` в матрице `0/1`.

```java
int numIslands(char[][] grid) {
    int rows = grid.length, cols = grid[0].length;
    UnionFind uf = new UnionFind(rows * cols);
    int waterCount = 0;

    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (grid[r][c] == '0') { waterCount++; continue; }
            if (r + 1 < rows && grid[r+1][c] == '1')
                uf.union(r * cols + c, (r+1) * cols + c);
            if (c + 1 < cols && grid[r][c+1] == '1')
                uf.union(r * cols + c, r * cols + (c+1));
        }
    }
    return uf.getComponents() - waterCount;
}
```

Альтернативное решение — DFS/BFS — обычно проще для статической задачи. UF выигрывает при **динамическом** добавлении земли (LeetCode 305 «Number of Islands II»).


> [!mcq]
> - [ ] Union-Find всегда быстрее DFS для Number of Islands — выбирать только его | ❌ ПОСЛЕДСТВИЕ: DFS/BFS проще реализовать для статической задачи; UF нужен только при динамическом добавлении земли
> - [ ] return uf.getComponents() без вычитания waterCount | ❌ ПОСЛЕДСТВИЕ: waterCells образуют отдельные «компоненты» в UF(rows*cols); без вычитания waterCount результат завышен на число water-ячеек
> - [ ] Union-Find нельзя применить к матрицам — только к спискам рёбер | ❌ ПОСЛЕДСТВИЕ: матрица маппируется в индексы r*cols+c; любая grid-задача совместима с UF через этот маппинг
> - [x] UF лучше DFS при динамических updates (Number of Islands II); для статической задачи DFS/BFS проще | ✓ ПРИМЕНЯТЬ: dynamic land-add → UF; static grid → DFS 📋 ПРАВИЛО: UF = dynamic connectivity; DFS = static traversal 🔗 См. Q15

## Q21. (!) Detect cycle in undirected graph через Union-Find?

Идём по рёбрам. Если оба конца уже в одной группе — цикл.

```java
boolean hasCycle(int n, int[][] edges) {
    UnionFind uf = new UnionFind(n);
    for (int[] e : edges) {
        if (!uf.union(e[0], e[1])) return true; // уже соединены — цикл
    }
    return false;
}
```

`O(E · α(V))`. Альтернатива — DFS с проверкой parent.


> [!mcq]
> - [ ] Перебрать все пары вершин и проверить find() равенство | ❌ ПОСЛЕДСТВИЕ: O(V²·α(V)); достаточно итерировать только по рёбрам O(E·α(V))
> - [ ] После union всех рёбер — цикл есть если число компонент < n | ❌ ПОСЛЕДСТВИЕ: это Connected Components, не cycle detection; цикл есть если union вернул false при обработке ребра
> - [x] Итерировать рёбра: если union(u,v) вернул false (find(u)==find(v)) — цикл найден | ✓ ПРИМЕНЯТЬ: undirected graph cycle detection O(E·α(V)) 📋 ПРАВИЛО: union вернул false = оба в одном компоненте = цикл 🔗 См. Q20
> - [ ] DFS с visited[] — Union-Find не применим к cycle detection | ❌ ПОСЛЕДСТВИЕ: UF отлично детектирует циклы: union(u,v)=false означает u и v уже связаны

## Q22. (!) Connected Components в графе?

```java
int countComponents(int n, int[][] edges) {
    UnionFind uf = new UnionFind(n);
    for (int[] e : edges) uf.union(e[0], e[1]);
    return uf.getComponents();
}
```

`O((V + E) · α(V))`. Простота реализации делает Union-Find популярным выбором.


> [!mcq]
> - [ ] BFS от каждой вершины — Union-Find здесь не подходит | ❌ ПОСЛЕДСТВИЕ: UF отлично подходит; O((V+E)·α(V)) vs O(V+E) BFS — одинаковая асимптотика, но UF concise
> - [x] Union по всем рёбрам, getComponents() возвращает число компонент; O((V+E)·α(V)) | ✓ ПРИМЕНЯТЬ: статический граф, подсчёт connected components 📋 ПРАВИЛО: UF components = N - число успешных union 🔗 См. Q20
> - [ ] Union-Find возвращает список компонент, а не их количество | ❌ ПОСЛЕДСТВИЕ: стандартный UF хранит только count; для списка нужен дополнительный Map<root, List<node>>
> - [ ] Union-Find работает только для connected графов | ❌ ПОСЛЕДСТВИЕ: UF специально предназначен для disconnected компонент; 0 рёбер = n компонент = n isolated vertices

## Q23. Accounts Merge — объединение аккаунтов?

Каждый аккаунт — список email'ов. Если два аккаунта имеют общий email — это один человек, объединить.

```java
List<List<String>> accountsMerge(List<List<String>> accounts) {
    UnionFind uf = new UnionFind(accounts.size());
    Map<String, Integer> emailToIdx = new HashMap<>();

    for (int i = 0; i < accounts.size(); i++) {
        for (int j = 1; j < accounts.get(i).size(); j++) {
            String email = accounts.get(i).get(j);
            if (emailToIdx.containsKey(email)) {
                uf.union(i, emailToIdx.get(email));
            } else {
                emailToIdx.put(email, i);
            }
        }
    }

    Map<Integer, TreeSet<String>> groups = new HashMap<>();
    for (var e : emailToIdx.entrySet()) {
        int root = uf.find(e.getValue());
        groups.computeIfAbsent(root, k -> new TreeSet<>()).add(e.getKey());
    }

    List<List<String>> result = new ArrayList<>();
    for (var e : groups.entrySet()) {
        List<String> merged = new ArrayList<>();
        merged.add(accounts.get(e.getKey()).get(0));
        merged.addAll(e.getValue());
        result.add(merged);
    }
    return result;
}
```


> [!mcq]
> - [ ] Маппить emailToIdx с root = первый аккаунт с этим email (не find) | ❌ ПОСЛЕДСТВИЕ: без UF.find() не отследить транзитивные объединения; аккаунты A→B и B→C не объединятся в A→C
> - [x] Map email→arbitraryIndex; union(email1Idx, email2Idx) внутри аккаунта; groupBy find(root) в конце | ✓ ПРИМЕНЯТЬ: email deduplication с транзитивными связями 📋 ПРАВИЛО: UF + TreeSet emails + groupBy root = Accounts Merge 🔗 См. Q22
> - [ ] Использовать HashMap<email, accountName> вместо UF | ❌ ПОСЛЕДСТВИЕ: не обрабатывает транзитивные случаи: acc1=[a,b], acc2=[b,c], acc3=[c,d] → один человек, но HashMap даст 3 отдельных entry
> - [ ] После union — смержить списки напрямую без TreeSet | ❌ ПОСЛЕДСТВИЕ: дублирующиеся emails в результате; задача требует уникальные отсортированные emails

## Q24. Redundant Connection?

Дано дерево с одним лишним ребром. Найти его (то, удаление которого восстановит дерево).

```java
int[] findRedundantConnection(int[][] edges) {
    UnionFind uf = new UnionFind(edges.length + 1);
    for (int[] e : edges) {
        if (!uf.union(e[0], e[1])) return e; // создаст цикл
    }
    return new int[]{};
}
```

Идём по рёбрам, первое создающее цикл — лишнее. `O(E · α(V))`.


> [!mcq]
> - [ ] Проверить все рёбра на наличие цикла через topological sort | ❌ ПОСЛЕДСТВИЕ: topological sort для directed graphs; задача undirected — нужен UF или DFS
> - [ ] Вернуть любое ребро с узлами степени > 2 | ❌ ПОСЛЕДСТВИЕ: избыточное ребро может соединять два узла степени 2; критерий — создание цикла, не степень
> - [x] Итерировать рёбра; первое у которого union(u,v) вернул false → redundant | ✓ ПРИМЕНЯТЬ: undirected graph с ровно одним лишним ребром O(E·α(V)) 📋 ПРАВИЛО: redundant = первое ребро создающее цикл = union вернул false 🔗 См. Q21
> - [ ] Найти вершину с наибольшей степенью — её ребро лишнее | ❌ ПОСЛЕДСТВИЕ: лишнее ребро необязательно у вершины с max degree; нужен structural анализ через UF

## Q25. (!) Где Trie используется в production?

1. **Поисковые системы** — autocomplete на Google/Yandex
2. **Spell checkers** — Microsoft Word, web browsers
3. **IP routing tables** — longest prefix match (`/24`, `/16` подсети)
4. **Code completion** — IntelliJ IDEA, VS Code
5. **Phone book** — T9 input на старых телефонах
6. **Predictive text** — мобильные клавиатуры (SwiftKey, GBoard)
7. **DNS resolution** — иерархия доменов
8. **Compilers** — таблица идентификаторов


> [!mcq]
> - [ ] Trie используется только для simple word lookup — HashMap быстрее | ❌ ПОСЛЕДСТВИЕ: HashMap O(1) lookup но не поддерживает prefix search; Trie даёт O(L) prefix queries vs O(N·L) scan HashMap
> - [ ] IP routing использует B-tree, а не Trie | ❌ ПОСЛЕДСТВИЕ: IP routing (longest prefix match) — классический use case Trie/Patricia Trie; B-tree не поддерживает prefix semantics
> - [x] Autocomplete (поисковики), IP routing (longest prefix match), spell checkers, code completion (IDE) | ✓ ПРИМЕНЯТЬ: когда нужен O(L) prefix search по большому словарю 📋 ПРАВИЛО: Trie = prefix tree = любой prefix-based retrieval 🔗 См. Q1
> - [ ] Trie не используется в DNS — там flat hashmap | ❌ ПОСЛЕДСТВИЕ: DNS hierarchy (com→example→www) — hierarchical prefix structure = Trie; каждый уровень домена = уровень в Trie

## Q26. Где Union-Find используется в production?

1. **Kruskal MST** — построение минимального остовного дерева (сетевые топологии)
2. **Image segmentation** — connected components в изображении
3. **Network connectivity** — кто с кем связан в сети
4. **Account merging** — Facebook (cross-device user identity)
5. **Type inference в компиляторах** — Hindley-Milner unification
6. **Percolation** — модели случайных систем
7. **Distributed systems** — leader election в gossip protocols
8. **Game development** — connected territories (Risk, Civilization)


> [!mcq]
> - [ ] Union-Find использован только в учебных задачах, в production применяют другие алгоритмы | ❌ ПОСЛЕДСТВИЕ: Kruskal MST (сетевые топологии), Hindley-Milner type inference (компиляторы), distributed gossip — всё реальный production
> - [x] Kruskal MST, network connectivity, image segmentation, account merging, type inference в компиляторах | ✓ ПРИМЕНЯТЬ: любая dynamic connectivity задача в production 📋 ПРАВИЛО: UF = dynamic connectivity ≈ O(1); применяется везде где нужно "connected?" 🔗 См. Q15
> - [ ] Union-Find не масштабируется — только для маленьких N | ❌ ПОСЛЕДСТВИЕ: UF O(α(n)) ≈ O(1) масштабируется до миллиардов nodes (Facebook identity graph)
> - [ ] MST строится только Dijkstra, не UF | ❌ ПОСЛЕДСТВИЕ: Kruskal MST = sort edges + UF для cycle detection; Dijkstra — shortest path, не MST

## Q27. Что такое Patricia Trie?

**Patricia trie (Practical Algorithm to Retrieve Information Coded in Alphanumeric)** — оптимизация Trie для **бинарных** ключей. Сжимает цепочки и хранит **позицию различающего бита**.

```
Patricia Trie для 0010, 0011, 1011:
        bit 0
       /     \
      0       1
   bit 3      └ 1011
   /    \
0010   0011
```

**Применения:**
- IP routing (BSD radix trees)
- File systems
- Ethereum state trie

Память: `O(n · key_length)`, поиск: `O(key_length)`.


> [!mcq]
> - [ ] Patricia Trie — то же что обычный Trie, только с другим названием | ❌ ПОСЛЕДСТВИЕ: Patricia сжимает linear chains (один потомок) в одну ветку; стандартный Trie хранит каждый символ как отдельный узел
> - [ ] Patricia Trie работает только с числовыми ключами | ❌ ПОСЛЕДСТВИЕ: Patricia Trie работает с любыми бинарными представлениями; применяется для строк, IP-адресов, любых ключей
> - [ ] Patricia Trie не применяется в IP routing — там используется B-tree | ❌ ПОСЛЕДСТВИЕ: BSD radix trees (Patricia Trie) — стандарт для IP routing longest prefix match в ядре Linux/BSD
> - [x] Оптимизация Trie: сжимает one-child цепочки, хранит позицию различающего бита; применяется в IP routing, Ethereum state trie | ✓ ПРИМЕНЯТЬ: бинарные ключи + нужна memory-efficient Trie с O(key_length) lookup 📋 ПРАВИЛО: Patricia = Path-Compressed Trie для бинарных ключей 🔗 См. Q1

## Q28. (!) Что такое Bloom filter и связь с Trie?

**Bloom filter** — компактная вероятностная структура для проверки членства. Может давать false positives, но не false negatives.

**Связь с Trie:** часто используются **вместе**:
- Bloom filter — быстрая проверка «может ли быть в Trie?»
- Если Bloom говорит «нет» — точно нет, не идём в Trie
- Если Bloom говорит «да» — проверяем Trie (может быть false positive)

Это паттерн в **LSM-trees (LevelDB, RocksDB, Cassandra)** — Bloom filter перед каждым SSTable.

Подробнее о Bloom filter — в [Hash Tables](hash-tables-interview.md).


> [!mcq]
> - [ ] Bloom filter заменяет Trie — можно использовать только Bloom | ❌ ПОСЛЕДСТВИЕ: Bloom только для membership check и даёт false positives; Trie хранит фактические данные и поддерживает prefix retrieval
> - [ ] Bloom filter + Trie нужны только в LSM-trees — нигде больше не применяются | ❌ ПОСЛЕДСТВИЕ: паттерн используется в любой системе с дорогим lookup: CDN cache, distributed caches, spell checkers
> - [x] Bloom filter — быстрая veto ("точно нет") перед дорогим Trie lookup; false positive → идём в Trie, false negative — невозможен | ✓ ПРИМЕНЯТЬ: LevelDB/RocksDB SSTable, когда lookup дорогой а miss-rate высокий 📋 ПРАВИЛО: Bloom = быстрый фильтр перед медленным Trie; no false negative 🔗 См. Q25
> - [ ] Bloom filter нельзя комбинировать с Trie из-за разных структур данных | ❌ ПОСЛЕДСТВИЕ: Bloom и Trie — независимые структуры; Bloom проверяет membership, Trie хранит данные; комбинирование стандартно в LSM

---

## See also

- [Алгоритмы (обзор)](../algorithms-interview.md) — карта алгоритмических тем
- [Деревья](trees-interview.md) — Trie как специализированное дерево
- [Хеш-таблицы](hash-tables-interview.md) — Bloom filter, alternative для membership
- [Графы](graphs-interview.md) — Kruskal использует Union-Find, cycle detection
- [Массивы и строки](arrays-strings-interview.md) — KMP и Rabin-Karp как альтернативы
- [Рекурсия](../algorithmic-paradigms/recursion-interview.md) — DFS обход Trie
- [Backtracking](../algorithmic-paradigms/backtracking-interview.md) — Word Search через Trie
- [Анализ сложности](../complexity/complexity-analysis-interview.md) — амортизированный O(α(n))
- [Java Collections](../../programming-languages/java/java-collections-interview.md) — нет встроенного Trie, но есть TreeMap
- [Redis](../../databases/redis-interview.md) — radix tree для streams
- [PostgreSQL](../../databases/postgresql-interview.md) — GIN/SP-GiST индексы используют tree-like structures
- [Кучи (Heaps)](heaps-interview.md)
- [Связные списки](linked-lists-interview.md)
- [Стеки и очереди](stacks-queues-interview.md)
