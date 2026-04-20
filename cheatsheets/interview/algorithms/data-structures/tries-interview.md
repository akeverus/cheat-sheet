---
title: "Вопросы на собеседовании: Trie и специальные структуры"
description: "Trie (префиксное дерево), Compressed trie (Radix tree), Suffix tree/array, Union-Find (DSU), применение в автодополнении, IP-routing, поиске подстрок"
tags:
  - interview
  - algorithms
  - tries-interview
aliases:
  - "Trie interview"
  - "Префиксное дерево собеседование"
  - "Radix tree interview"
  - "Suffix tree interview"
  - "Union-Find interview"
  - "DSU interview"
difficulty: "intermediate"
updated: "2026-04-18"
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

## Q4. (!) Сложности операций Trie?

| Операция | Сложность | Память |
|----------|-----------|--------|
| `insert(word)` | `O(L)`, L = длина | `O(L · ALPHABET)` |
| `search(word)` | `O(L)` | — |
| `startsWith(prefix)` | `O(L)` | — |
| `delete(word)` | `O(L)` | — |
| `autocomplete(prefix)` | `O(L + K)`, K = ответов | — |

**Память:** `O(N · L · ALPHABET)` в худшем случае (где N — число слов). Compressed trie уменьшает это.

## Q5. (!) Зачем Trie вместо HashMap?

Если нужно искать слово целиком — HashMap проще и быстрее (`O(L)` vs `O(L)`, но с меньшими константами).

**Trie выигрывает когда:**
1. **Префиксный поиск** — `startsWith`, autocomplete: `O(L + K)` vs HashMap `O(N · L)` (нужно перебрать все)
2. **Меньше памяти при общих префиксах** — `cat, car, card` — общий путь `ca` хранится один раз
3. **Lexicographic ordering** — естественный порядок обхода
4. **Prefix-based deletion** — удалить все слова с префиксом
5. **Spell-check / fuzzy search** — поиск с расстоянием Левенштейна

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

## Q15. (!) Что такое Union-Find?

**Union-Find (Disjoint Set Union, DSU)** — структура для отслеживания разбиения элементов на **непересекающиеся группы**. Поддерживает:
- `find(x)` — найти представителя (корень) группы элемента `x`
- `union(x, y)` — объединить группы элементов `x` и `y`

С оптимизациями **path compression** + **union by rank** обе операции работают за **амортизированный `O(α(n)) ≈ O(1)`** (где `α` — обратная функция Аккермана).

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

## Q19. Какова амортизированная сложность с обеими оптимизациями?

С **path compression + union by rank** последовательность из `m` операций над `n` элементами выполняется за **`O(m · α(n))`**, где `α(n)` — обратная функция Аккермана.

`α(n) < 5` для всех практических `n` (до 2^65536). Поэтому каждая операция считается **константной** на практике.

Без оптимизаций — `O(m · n)` в худшем случае.

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

## Q22. (!) Connected Components в графе?

```java
int countComponents(int n, int[][] edges) {
    UnionFind uf = new UnionFind(n);
    for (int[] e : edges) uf.union(e[0], e[1]);
    return uf.getComponents();
}
```

`O((V + E) · α(V))`. Простота реализации делает Union-Find популярным выбором.

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

## Q25. (!) Где Trie используется в production?

1. **Поисковые системы** — autocomplete на Google/Yandex
2. **Spell checkers** — Microsoft Word, web browsers
3. **IP routing tables** — longest prefix match (`/24`, `/16` подсети)
4. **Code completion** — IntelliJ IDEA, VS Code
5. **Phone book** — T9 input на старых телефонах
6. **Predictive text** — мобильные клавиатуры (SwiftKey, GBoard)
7. **DNS resolution** — иерархия доменов
8. **Compilers** — таблица идентификаторов

## Q26. Где Union-Find используется в production?

1. **Kruskal MST** — построение минимального остовного дерева (сетевые топологии)
2. **Image segmentation** — connected components в изображении
3. **Network connectivity** — кто с кем связан в сети
4. **Account merging** — Facebook (cross-device user identity)
5. **Type inference в компиляторах** — Hindley-Milner unification
6. **Percolation** — модели случайных систем
7. **Distributed systems** — leader election в gossip protocols
8. **Game development** — connected territories (Risk, Civilization)

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

## Q28. (!) Что такое Bloom filter и связь с Trie?

**Bloom filter** — компактная вероятностная структура для проверки членства. Может давать false positives, но не false negatives.

**Связь с Trie:** часто используются **вместе**:
- Bloom filter — быстрая проверка «может ли быть в Trie?»
- Если Bloom говорит «нет» — точно нет, не идём в Trie
- Если Bloom говорит «да» — проверяем Trie (может быть false positive)

Это паттерн в **LSM-trees (LevelDB, RocksDB, Cassandra)** — Bloom filter перед каждым SSTable.

Подробнее о Bloom filter — в [Hash Tables](hash-tables-interview.md).

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

- [Массивы и строки](arrays-strings-interview.md)
- [Графы](graphs-interview.md)
- [Хеш-таблицы](hash-tables-interview.md)
- [Кучи (Heaps)](heaps-interview.md)
- [Связные списки](linked-lists-interview.md)
- [Стеки и очереди](stacks-queues-interview.md)
