---
title: "Вопросы на собеседовании: Backtracking"
description: "Backtracking как DFS с возвратом, классические задачи (N-Queens, Sudoku, permutations, combinations, subsets, word search), pruning, разница с brute force и DP"
tags:
  - interview
  - algorithms
  - backtracking-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Backtracking"
  - "Backtracking interview"
  - "Backtracking собеседование"
prerequisites:
  - "[[backtracking]]"
next: []
updated: "2026-05-19"
---
# Вопросы на собеседовании: `Backtracking`

Backtracking — DFS по дереву решений с **отказом** (откатом), когда текущий путь не ведёт к решению. Классические задачи: N-Queens, Sudoku, permutations/combinations/subsets, Word Search. Главное — научиться **prune** (отсекать) бесперспективные ветви.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Backtracking Algorithms — Baeldung](https://www.baeldung.com/cs/backtracking-algorithms)
- [N-Queens Problem — Baeldung](https://www.baeldung.com/cs/n-queens-problem)
- [Sudoku Solver — Baeldung](https://www.baeldung.com/java-sudoku)
- [Permutations Algorithm — Baeldung](https://www.baeldung.com/java-array-permutations)
- [Backtracking — GeeksforGeeks](https://www.geeksforgeeks.org/backtracking-algorithms/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое backtracking?](#q1--что-такое-backtracking)
- [Q2. (!) Шаблон backtracking?](#q2--шаблон-backtracking)
- [Q3. (!) Чем backtracking отличается от brute force?](#q3--чем-backtracking-отличается-от-brute-force)
- [Q4. (!) Чем backtracking отличается от DFS?](#q4--чем-backtracking-отличается-от-dfs)
- [Q5. (!) Чем backtracking отличается от DP?](#q5--чем-backtracking-отличается-от-dp)

**Subsets, Permutations, Combinations**
- [Q6. (!) Все подмножества (Subsets)?](#q6--все-подмножества-subsets)
- [Q7. Subsets с дубликатами?](#q7-subsets-с-дубликатами)
- [Q8. (!) Permutations — все перестановки?](#q8--permutations--все-перестановки)
- [Q9. Permutations с дубликатами?](#q9-permutations-с-дубликатами)
- [Q10. (!) Как сгенерировать все сочетания Combinations C(n, k)?](#q10--как-сгенерировать-все-сочетания-combinations-cn-k)
- [Q11. Combination Sum — комбинации с заданной суммой?](#q11-combination-sum--комбинации-с-заданной-суммой)

**Grid задачи**
- [Q12. (!) Как решить задачу N-Queens?](#q12--как-решить-задачу-n-queens)
- [Q13. (!) Как написать Sudoku Solver?](#q13--как-написать-sudoku-solver)
- [Q14. (!) Как решить задачу Word Search?](#q14--как-решить-задачу-word-search)
- [Q15. Как решить задачу Rat in a Maze (крыса в лабиринте)?](#q15-как-решить-задачу-rat-in-a-maze-крыса-в-лабиринте)

**Графы**
- [Q16. (!) Как найти Hamiltonian Path / Cycle?](#q16--как-найти-hamiltonian-path--cycle)
- [Q17. Как решить задачу Graph Coloring (раскраска графа)?](#q17-как-решить-задачу-graph-coloring-раскраска-графа)
- [Q18. (!) Как решить задачу Generate Parentheses?](#q18--как-решить-задачу-generate-parentheses)

**Строки**
- [Q19. (!) Как решить задачу Letter Combinations of a Phone Number?](#q19--как-решить-задачу-letter-combinations-of-a-phone-number)
- [Q20. (!) Как решить задачу Palindrome Partitioning?](#q20--как-решить-задачу-palindrome-partitioning)
- [Q21. Как решить задачу Restore IP Addresses?](#q21-как-решить-задачу-restore-ip-addresses)

**Pruning и оптимизации**
- [Q22. (!) Что такое pruning и зачем?](#q22--что-такое-pruning-и-зачем)
- [Q23. (!) Branch and Bound — что это?](#q23--branch-and-bound--что-это)
- [Q24. Memoization в backtracking?](#q24-memoization-в-backtracking)

**Применения и подводные камни**
- [Q25. (!) Какова сложность backtracking?](#q25--какова-сложность-backtracking)
- [Q26. (!) Где backtracking в production?](#q26--где-backtracking-в-production)
- [Q27. Iterative backtracking — реально?](#q27-iterative-backtracking--реально)

## Q1. (!) Что такое backtracking?

**Backtracking** — это перебор решений в глубину, в котором мы строим ответ по шагам и **откатываем последний шаг**, как только частичное решение становится бесперспективным. По сути это DFS по дереву всех вариантов с обязательным шагом отмены (undo).

Ключевая идея — не доводить заведомо тупиковый путь до конца. Как только понятно, что текущее частичное решение не может привести к валидному ответу, мы отрезаем **всё поддерево** под ним и возвращаемся к предыдущему выбору. Именно этот ранний откат отличает backtracking от слепого перебора и даёт основную экономию.

**Цикл из четырёх шагов:**
1. Делаем выбор — расширяем частичное решение
2. Рекурсивно идём глубже
3. Если решение собрано — фиксируем его
4. Если тупик — отменяем выбор и пробуем следующий вариант

**Пример:** в N-Queens ставим ферзей построчно. Если для очередной строки нет безопасной клетки — снимаем ферзя в предыдущей строке и переставляем его, вместо того чтобы перебирать все расстановки доски целиком.


## Q2. (!) Шаблон backtracking?

```java
void backtrack(State state, Result result) {
    if (isComplete(state)) {
        result.add(makeCopy(state));
        return;
    }

    for (Choice choice : getValidChoices(state)) {
        state.apply(choice);     // make
        backtrack(state, result);
        state.undo(choice);      // unmake — это и есть "backtrack"
    }
}
```

Весь шаблон держится на трёх симметричных строках внутри цикла: `apply` (сделать выбор), рекурсивный вызов, `undo` (откатить выбор). Откат обязан **точно** отменять то, что сделал `apply`, иначе соседние ветви унаследуют чужое состояние и решения будут неверными.

Чтобы решить любую backtracking-задачу, ответьте на три вопроса:
1. **Когда фиксировать результат?** — условие готового решения (base case)
2. **Какие выборы доступны из текущего состояния?** — ограничения (constraints)
3. **Как откатить выбор?** — обратная операция к `apply` (undo)

Полезно завести привычку писать `undo` сразу под `apply` — так труднее забыть откат, а пара «сделал/отменил» читается как единое целое.


## Q3. (!) Чем backtracking отличается от brute force?

Разница в одном: brute force перебирает **все** комбинации до конца, а backtracking **отсекает** ветвь, как только частичное состояние стало невалидным, и не тратит время на её достройку.

```java
// Brute force: 2^n проверок для subsets
for (int mask = 0; mask < (1 << n); mask++) { ... }

// Backtracking: те же 2^n в худшем, но с pruning часто намного меньше
backtrack(state) {
    if (notValid(state)) return; // PRUNE — не идём глубже
    ...
}
```

**Компромисс:** в худшем случае, когда ни одно ограничение не срабатывает рано, backtracking вырождается ровно в brute force — те же `2^n`. Выигрыш появляется только от качественных ограничений: чем раньше определяется тупик, тем большее поддерево удаётся срезать. На практике с хорошими constraints разница в скорости — на порядки.


## Q4. (!) Чем backtracking отличается от DFS?

Формально **backtracking — это и есть DFS**, но по **дереву решений** (state space tree), а не по заданному графу. Узлы этого дерева — частичные решения, рёбра — отдельные выборы. Главное практическое отличие — наличие шага отмены (unmake) после возврата из рекурсии.

| Критерий | DFS (graph) | Backtracking |
|----------|-------------|--------------|
| Где работает | Граф | Дерево решений (state space tree) |
| Visited tracking | `boolean[] visited` (постоянно) | Локальное состояние (откатывается) |
| Цель | Обход или поиск | Найти все/любое валидное решение |
| Undo step | Нет | Есть (после рекурсивного вызова) |

Почему так: в DFS на графе пометка `visited` глобальна — вершину достаточно обойти один раз, поэтому её **никогда** не «отпускают». В backtracking состояние локально для текущего пути: пометив клетку или заняв колонку, мы обязаны снять пометку при возврате, иначе следующий вариант увидит ложно занятый ресурс. Откат и нужен, чтобы одно и то же состояние можно было переиспользовать в соседних ветвях.


## Q5. (!) Чем backtracking отличается от DP?

| Критерий | Backtracking | DP |
|----------|--------------|-----|
| Подзадачи | Уникальные пути | Перекрывающиеся |
| Memoization | Обычно нет | Обязательно |
| Возвращает | Все/один валидный путь | Оптимум (число способов, мин/макс) |
| Сложность | `O(branchFactor^depth)` | Полиномиальная |

Граница между ними тоньше, чем кажется. Backtracking перечисляет конкретные решения, проходя **уникальные** комбинации выборов, и обычно ничего не кэширует. DP применим, когда одни и те же подзадачи **перекрываются**: их результат запоминают (memoization) и возвращают оптимум — число способов, минимум или максимум, — а не сами пути.

**Эмпирическое правило:** если в backtracking-задаче одно и то же частичное состояние достигается разными путями и от него зависит только итоговое число/оптимум, добавьте мемоизацию — и backtracking превращается в DP.

**Пример:** наивная рекурсия для Fibonacci пишется в backtracking-стиле и пересчитывает одни и те же значения экспоненциально; добавили кэш — получили DP за линейное время.


## Q6. (!) Все подмножества (Subsets)?

Нужно сгенерировать все подмножества массива. Для `[1, 2, 3]` это `[[], [1], [2], [1,2], [3], [1,3], [2,3], [1,2,3]]` — всего `2^n` штук, потому что каждый элемент независимо либо входит в подмножество, либо нет.

Идея решения: для каждого элемента, начиная с позиции `start`, делаем выбор «взять его», уходим в рекурсию (откуда возьмутся все подмножества, продолжающие текущее) и откатываемся. Параметр `start` гарантирует, что элементы добавляются только слева направо, поэтому каждое подмножество порождается ровно один раз без повторов.

```java
List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), result);
    return result;
}

void backtrack(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
    result.add(new ArrayList<>(current)); // добавляем КАЖДЫЙ shape — даже пустой
    for (int i = start; i < nums.length; i++) {
        current.add(nums[i]);
        backtrack(nums, i + 1, current, result);
        current.remove(current.size() - 1); // backtrack
    }
}
```

**Сложность:** `O(n · 2^n)` — всего `2^n` подмножеств, и копирование каждого в результат стоит `O(n)`. Ключевая деталь — `result.add(...)` стоит в самом начале функции, до цикла: в результат попадает **каждый** узел дерева (любое частичное решение), включая пустое подмножество, а не только листья.


## Q7. Subsets с дубликатами?

Если в `nums` есть повторяющиеся числа, наивное решение из Q6 выдаст одинаковые подмножества несколько раз. Чтобы этого не было, массив **сортируют** (одинаковые элементы встают рядом) и на каждом уровне рекурсии берут лишь первый из группы дубликатов. Остальные одинаковые значения в этой же позиции пропускаются — иначе ветви, отличающиеся только порядком равных элементов, дали бы идентичные подмножества.

```java
List<List<Integer>> subsetsWithDup(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), result);
    return result;
}

void backtrack(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
    result.add(new ArrayList<>(current));
    for (int i = start; i < nums.length; i++) {
        if (i > start && nums[i] == nums[i - 1]) continue; // skip duplicates
        current.add(nums[i]);
        backtrack(nums, i + 1, current, result);
        current.remove(current.size() - 1);
    }
}
```

Условие пропуска именно `i > start` (а не `i > 0`): первый элемент группы на этом уровне (`i == start`) брать **нужно** — он-то и порождает подмножества с этим значением. Пропускаем только его повторы. Связка «сортировка + skip соседних дубликатов» — стандартный паттерн для всех задач с повторами (subsets, permutations, combination sum).


## Q8. (!) Permutations — все перестановки?

Нужно сгенерировать все перестановки массива — для `[1, 2, 3]` их `n!`. В отличие от subsets, здесь важен порядок и используются все элементы, поэтому идти по `start` уже нельзя: на каждом шаге можно поставить любой ещё **не использованный** элемент. Какой именно занят — отслеживает массив `used`.

```java
List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, new ArrayList<>(), new boolean[nums.length], result);
    return result;
}

void backtrack(int[] nums, List<Integer> current, boolean[] used, List<List<Integer>> result) {
    if (current.size() == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        current.add(nums[i]);
        used[i] = true;
        backtrack(nums, current, used, result);
        used[i] = false;
        current.remove(current.size() - 1);
    }
}
```

**Сложность:** `O(n · n!)` — `n!` перестановок, копирование каждой `O(n)`. Решение через `used`-массив — стандартный приём для перестановок: он явно отвечает на вопрос «какие элементы ещё доступны».

**Альтернатива — swap-based.** Вместо отдельного флага-массива переставляем элементы прямо в `nums`: фиксируем позицию `start`, по очереди ставим туда каждый из оставшихся через `swap`, рекурсируем и возвращаем элемент на место. Память чуть экономнее (нет `used` и нового списка), но порядок перестановок другой и in-place мутации легче испортить, если забыть обратный swap:

```java
void permuteSwap(int[] nums, int start, List<List<Integer>> result) {
    if (start == nums.length) {
        result.add(Arrays.stream(nums).boxed().toList());
        return;
    }
    for (int i = start; i < nums.length; i++) {
        swap(nums, start, i);
        permuteSwap(nums, start + 1, result);
        swap(nums, start, i); // backtrack
    }
}
```


## Q9. Permutations с дубликатами?

Та же логика, что и для уникальных перестановок, но с защитой от повторов. Массив сортируют, а внутри цикла добавляют условие пропуска: одинаковые значения на одном уровне рекурсии берём строго в порядке слева направо.

```java
List<List<Integer>> permuteUnique(int[] nums) {
    Arrays.sort(nums); // важно для пропуска дубликатов
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, new ArrayList<>(), new boolean[nums.length], result);
    return result;
}

void backtrack(int[] nums, List<Integer> current, boolean[] used, List<List<Integer>> result) {
    if (current.size() == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        // Пропуск дубликата только если предыдущий такой же не использован на этом уровне
        if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) continue;
        used[i] = true;
        current.add(nums[i]);
        backtrack(nums, current, used, result);
        used[i] = false;
        current.remove(current.size() - 1);
    }
}
```

**Почему именно `!used[i - 1]`.** Ключ условия `nums[i] == nums[i - 1] && !used[i - 1]`: если предыдущий равный элемент **не** использован на текущем пути, значит мы стоим в той же позиции, где он мог бы быть, — это начало второго дубля той же перестановки, его и пропускаем. Если же `used[i - 1] == true`, равный элемент уже стоит выше по пути, и текущий — законное следующее вхождение того же числа. Сортировка обязательна, иначе дубликаты не окажутся рядом и условие не сработает.


## Q10. (!) Как сгенерировать все сочетания Combinations C(n, k)?

Нужно перечислить все сочетания `k` элементов из диапазона `[1..n]` — порядок внутри сочетания не важен, повторов нет.

```java
List<List<Integer>> combine(int n, int k) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(1, n, k, new ArrayList<>(), result);
    return result;
}

void backtrack(int start, int n, int k, List<Integer> current, List<List<Integer>> result) {
    if (current.size() == k) {
        result.add(new ArrayList<>(current));
        return;
    }
    // Pruning: остатка должно хватить на (k - current.size())
    for (int i = start; i <= n - (k - current.size()) + 1; i++) {
        current.add(i);
        backtrack(i + 1, n, k, current, result);
        current.remove(current.size() - 1);
    }
}
```

**Подводный камень и оптимизация.** Верхняя граница цикла `n - (k - size) + 1` — это pruning: она запрещает стартовать с элемента, после которого оставшихся чисел уже не хватит, чтобы добрать сочетание до `k`. Без этого отсечения мы бы заходили в заведомо тупиковые ветви и откатывались только у самого `base case`. Параметр `start` (передаём `i + 1`) обеспечивает строго возрастающий порядок и тем самым отсутствие перестановочных дубликатов.


## Q11. Combination Sum — комбинации с заданной суммой?

Дан массив `candidates` и число `target`. Нужны все комбинации, дающие в сумме `target`, причём **каждый элемент можно брать сколько угодно раз**. Именно повторное использование и отличает задачу от обычных combinations.

```java
List<List<Integer>> combinationSum(int[] candidates, int target) {
    List<List<Integer>> result = new ArrayList<>();
    Arrays.sort(candidates); // для pruning
    backtrack(candidates, 0, target, new ArrayList<>(), result);
    return result;
}

void backtrack(int[] candidates, int start, int remaining,
               List<Integer> current, List<List<Integer>> result) {
    if (remaining == 0) {
        result.add(new ArrayList<>(current));
        return;
    }
    for (int i = start; i < candidates.length; i++) {
        if (candidates[i] > remaining) break; // pruning (sorted)
        current.add(candidates[i]);
        backtrack(candidates, i, remaining - candidates[i], current, result); // i, не i+1
        current.remove(current.size() - 1);
    }
}
```

Две детали решают всё. Во-первых, рекурсия вызывается с `i`, а **не** `i + 1`: так тот же элемент остаётся доступным на следующем шаге и его можно взять повторно. Во-вторых, `Arrays.sort` плюс `break` при `candidates[i] > remaining` — это pruning: как только отсортированный кандидат превысил остаток, дальше по массиву идут только бо́льшие числа, и весь хвост цикла можно отбросить.

**Вариации задачи:**
- **Combination Sum II** — каждый элемент используется не более одного раза (рекурсия с `i + 1` плюс skip дубликатов).
- **Combination Sum III** — нужно ровно `k` элементов с фиксированной суммой.


## Q12. (!) Как решить задачу N-Queens?

Расставить `n` ферзей на доске `n×n` так, чтобы ни один не бил другого (по строке, колонке и обеим диагоналям). Канонический пример backtracking.

Ключевое упрощение: ферзей ставим **построчно**, по одному на строку. Тогда конфликт по строке исключён автоматически, а `queens[row] = column` компактно кодирует всю доску. На каждом шаге перебираем колонки текущей строки, проверяем `isSafe` (нет ферзя в той же колонке и на диагоналях) и рекурсивно переходим к следующей строке. Если безопасной колонки нет — рекурсия просто не находит вариантов и откатывается к предыдущей строке.

```java
List<List<String>> solveNQueens(int n) {
    List<List<String>> result = new ArrayList<>();
    int[] queens = new int[n]; // queens[row] = column
    backtrack(0, n, queens, result);
    return result;
}

void backtrack(int row, int n, int[] queens, List<List<String>> result) {
    if (row == n) {
        result.add(buildBoard(queens, n));
        return;
    }
    for (int col = 0; col < n; col++) {
        if (isSafe(queens, row, col)) {
            queens[row] = col;
            backtrack(row + 1, n, queens, result);
            // queens[row] не обнуляем — перезапишется или останется неиспользуемым
        }
    }
}

boolean isSafe(int[] queens, int row, int col) {
    for (int i = 0; i < row; i++) {
        if (queens[i] == col) return false; // та же колонка
        if (Math.abs(queens[i] - col) == row - i) return false; // диагональ
    }
    return true;
}
```

**Сложность:** `O(n!)` — в первой строке `n` вариантов колонки, во второй не больше `n − 1` и так далее. Для `n = 8` существует 92 решения, для `n = 12` — 14200.

**Оптимизация через bitsets.** Вместо линейного `isSafe` за `O(row)` держим три битовые маски занятых ресурсов — `column`, `diag1`, `diag2`. Проверка и постановка ферзя становятся `O(1)` битовыми операциями, что заметно ускоряет перебор для больших `n`. В проверке диагоналей здесь работает свойство: на одной диагонали `row − col` (или `row + col`) постоянно.


## Q13. (!) Как написать Sudoku Solver?

Заполнить поле `9×9` цифрами так, чтобы в каждой строке, колонке и блоке `3×3` все цифры были разными. Решаем backtracking: находим первую пустую клетку, по очереди пробуем цифры `1..9`, ставим первую допустимую (`isValid`) и рекурсивно решаем остаток. Если до конца дойти не удалось — стираем цифру (`board[r][c] = '.'`, это и есть откат) и пробуем следующую.

Важная деталь — `return false` после цикла по цифрам: если для пустой клетки **ни одна** цифра не подошла, текущая ветвь тупиковая, и мы сигналим об этом наверх, чтобы вышестоящий вызов поменял свой выбор. Функция возвращает `true`, только когда пустых клеток не осталось.

```java
boolean solveSudoku(char[][] board) {
    for (int r = 0; r < 9; r++) {
        for (int c = 0; c < 9; c++) {
            if (board[r][c] != '.') continue;
            for (char d = '1'; d <= '9'; d++) {
                if (isValid(board, r, c, d)) {
                    board[r][c] = d;
                    if (solveSudoku(board)) return true;
                    board[r][c] = '.'; // backtrack
                }
            }
            return false;
        }
    }
    return true;
}

boolean isValid(char[][] board, int row, int col, char digit) {
    for (int i = 0; i < 9; i++) {
        if (board[row][i] == digit) return false;
        if (board[i][col] == digit) return false;
        int br = 3 * (row / 3) + i / 3;
        int bc = 3 * (col / 3) + i % 3;
        if (board[br][bc] == digit) return false;
    }
    return true;
}
```

**Оптимизации (от простой к мощной):**
- **Битмаски для row/col/box** — хранить занятые цифры тремя наборами битов и проверять допустимость за `O(1)` вместо обхода 27 ячеек на каждую попытку.
- **MRV (Minimum Remaining Values)** — заполнять не первую попавшуюся пустую клетку, а ту, где меньше всего допустимых вариантов. Это раньше упирается в противоречие и режет дерево перебора.
- **Constraint propagation** — выводить вынужденные значения без перебора: если в строке (колонке, блоке) цифру `5` может принять только одна клетка, сразу ставим её туда. Так часть судоку «дорешивается» логикой ещё до backtracking.


## Q14. (!) Как решить задачу Word Search?

В сетке букв найти заданное слово, двигаясь по соседним клеткам (вверх/вниз/влево/вправо) и не используя одну клетку дважды в пределах пути. Запускаем DFS из каждой клетки, совпавшей с первой буквой, и идём по четырём направлениям, пока буквы совпадают.

Трюк с пометкой: вместо отдельного массива `visited` временно затираем текущую клетку символом `'#'`, чтобы рекурсия не вернулась в неё, а **на откате** восстанавливаем исходную букву (`board[r][c] = tmp`). Это и есть backtracking в чистом виде — состояние меняется на время спуска и аккуратно откатывается при подъёме.

```java
boolean exist(char[][] board, String word) {
    int rows = board.length, cols = board[0].length;
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            if (dfs(board, r, c, word, 0)) return true;
        }
    }
    return false;
}

boolean dfs(char[][] board, int r, int c, String word, int i) {
    if (i == word.length()) return true;
    if (r < 0 || r >= board.length || c < 0 || c >= board[0].length
        || board[r][c] != word.charAt(i)) return false;

    char tmp = board[r][c];
    board[r][c] = '#'; // mark visited
    boolean found = dfs(board, r + 1, c, word, i + 1)
                 || dfs(board, r - 1, c, word, i + 1)
                 || dfs(board, r, c + 1, word, i + 1)
                 || dfs(board, r, c - 1, word, i + 1);
    board[r][c] = tmp; // backtrack
    return found;
}
```

**Сложность:** `O(R · C · 4^L)`, где `R·C` — клеток-стартов, а `4^L` — ветвление по четырём направлениям на каждую из `L` букв. Альтернатива пометке `'#'` — отдельный `boolean[][] visited`, но in-place вариант экономит память и не требует выделять матрицу под каждый запуск.

**Сценарий для множества слов** (Word Search II): искать каждое слово по отдельности дорого; вместо этого складывают все слова в Trie и обходят сетку один раз, отсекая ветви, для которых в Trie нет продолжения — см. [Trie](../data-structures/tries-interview.md).


## Q15. Как решить задачу Rat in a Maze (крыса в лабиринте)?

В лабиринте с препятствиями (`0` — стена, `1` — проход) нужно найти **все** пути из `(0,0)` в `(n-1, n-1)`. В отличие от Word Search, где достаточно факта «слово существует», здесь собираем сами маршруты, поэтому накапливаем `path` и при достижении выхода копируем его в результат.

Логика DFS: входим в клетку, помечаем `visited[r][c] = true` и добавляем её в путь, рекурсивно идём по четырём направлениям, а на выходе из клетки **снимаем** пометку и убираем её из пути. Откат двух вещей — `visited` и `path` — обязателен: без него клетка осталась бы навечно занятой и пути, проходящие через неё под другим углом, потерялись бы.

```java
List<List<int[]>> findPaths(int[][] maze) {
    List<List<int[]>> paths = new ArrayList<>();
    int n = maze.length;
    boolean[][] visited = new boolean[n][n];
    dfs(maze, 0, 0, n, visited, new ArrayList<>(), paths);
    return paths;
}

void dfs(int[][] maze, int r, int c, int n, boolean[][] visited,
         List<int[]> path, List<List<int[]>> paths) {
    if (r < 0 || r >= n || c < 0 || c >= n
        || maze[r][c] == 0 || visited[r][c]) return;

    path.add(new int[]{r, c});
    visited[r][c] = true;

    if (r == n - 1 && c == n - 1) {
        paths.add(new ArrayList<>(path));
    } else {
        dfs(maze, r + 1, c, n, visited, path, paths);
        dfs(maze, r, c + 1, n, visited, path, paths);
        dfs(maze, r - 1, c, n, visited, path, paths);
        dfs(maze, r, c - 1, n, visited, path, paths);
    }

    visited[r][c] = false; // backtrack
    path.remove(path.size() - 1);
}
```


## Q16. (!) Как найти Hamiltonian Path / Cycle?

**Hamiltonian path** — путь, проходящий через каждую вершину графа **ровно один раз** (если он ещё и замыкается в старт — это Hamiltonian cycle). Задача NP-hard, эффективного полиномиального алгоритма не известно, поэтому на практике перебирают порядок вершин с backtracking.

Идея: строим путь как массив вершин, на позицию `pos` пробуем поставить любую вершину `v`, которая (а) смежна с предыдущей в пути и (б) ещё не использована — это и проверяет `isSafeHamilton`. Если из неё не удалось достроить полный путь, ставим `path[pos] = -1` (откат) и пробуем следующую вершину.

```java
boolean hamiltonianPath(int[][] graph, int start) {
    int n = graph.length;
    int[] path = new int[n];
    Arrays.fill(path, -1);
    path[0] = start;
    return findHamilton(graph, path, 1);
}

boolean findHamilton(int[][] graph, int[] path, int pos) {
    if (pos == graph.length) return true;
    for (int v = 0; v < graph.length; v++) {
        if (isSafeHamilton(graph, path, pos, v)) {
            path[pos] = v;
            if (findHamilton(graph, path, pos + 1)) return true;
            path[pos] = -1; // backtrack
        }
    }
    return false;
}

boolean isSafeHamilton(int[][] graph, int[] path, int pos, int v) {
    if (graph[path[pos - 1]][v] == 0) return false; // нет ребра
    for (int i = 0; i < pos; i++) if (path[i] == v) return false;
    return true;
}
```

**Сложность:** `O(n!)` в худшем случае — по сути перебор всех порядков вершин, отсекаемый проверкой смежности и занятости.


## Q17. Как решить задачу Graph Coloring (раскраска графа)?

Раскрасить вершины графа `m` цветами так, чтобы любые две соседние вершины получили разные цвета (задача о `m`-раскрашиваемости). Идём по вершинам подряд: для очередной вершины пробуем цвета `1..m`, ставим первый, не конфликтующий с уже раскрашенными соседями (`isSafeColor`), и рекурсивно красим следующую. Если ни один цвет не подошёл — откатываем (`color[v] = 0`) и возвращаемся к предыдущей вершине за другим вариантом.

```java
boolean graphColoring(int[][] graph, int m) {
    int n = graph.length;
    int[] color = new int[n];
    return tryColor(graph, m, color, 0);
}

boolean tryColor(int[][] graph, int m, int[] color, int v) {
    if (v == graph.length) return true;
    for (int c = 1; c <= m; c++) {
        if (isSafeColor(graph, color, v, c)) {
            color[v] = c;
            if (tryColor(graph, m, color, v + 1)) return true;
            color[v] = 0; // backtrack
        }
    }
    return false;
}
```

**Сценарии применения:** распределение частот в радиосвязи (соседние передатчики не должны делить частоту), составление расписаний без конфликтов и register allocation в компиляторах, где «цвета» — это регистры процессора.


## Q18. (!) Как решить задачу Generate Parentheses?

Сгенерировать все правильные скобочные последовательности из `n` пар скобок. Вместо того чтобы порождать все `2^(2n)` строк и фильтровать валидные, строим **только корректные**: на каждом шаге решаем, добавить `(` или `)`, и сразу отсекаем неправильные варианты.

Двигают перебор два счётчика — `open` (сколько открывающих уже поставили) и `close` (закрывающих):
- открыть `(` можно, пока `open < n`;
- закрыть `)` можно, только пока `close < open` — нельзя закрыть скобку, которой ещё не открыли.

Эти два ограничения и есть встроенный pruning: строка по построению всегда остаётся валидным префиксом, поэтому достраивать невалидные ветви просто не приходится.

```java
List<String> generateParenthesis(int n) {
    List<String> result = new ArrayList<>();
    backtrack(result, new StringBuilder(), 0, 0, n);
    return result;
}

void backtrack(List<String> result, StringBuilder current, int open, int close, int n) {
    if (current.length() == n * 2) {
        result.add(current.toString());
        return;
    }
    if (open < n) {
        current.append('(');
        backtrack(result, current, open + 1, close, n);
        current.deleteCharAt(current.length() - 1);
    }
    if (close < open) {
        current.append(')');
        backtrack(result, current, open, close + 1, n);
        current.deleteCharAt(current.length() - 1);
    }
}
```

Число результатов равно `n`-му числу Каталана `Catalan(n)` — столько существует правильных скобочных последовательностей. Главное ограничение, которое стоит проговорить на собеседовании: `close < open` — нельзя закрыть больше скобок, чем открыто.


## Q19. (!) Как решить задачу Letter Combinations of a Phone Number?

Дана строка цифр, как на кнопочном телефоне; каждой цифре соответствует набор букв (`2 → "abc"`, `3 → "def"` и т. д.). Нужно собрать все буквенные комбинации — для `"23"` это `ad, ae, af, bd, be, bf, cd, ce, cf`.

Это перебор «декартова произведения»: идём по цифрам слева направо, для текущей цифры по очереди подставляем каждую её букву, рекурсируем к следующей цифре и откатываем символ. Когда обработаны все цифры (`idx == digits.length()`), накопленная строка — готовая комбинация.

```java
List<String> letterCombinations(String digits) {
    if (digits.isEmpty()) return List.of();
    String[] map = {"", "", "abc", "def", "ghi", "jkl",
                    "mno", "pqrs", "tuv", "wxyz"};
    List<String> result = new ArrayList<>();
    backtrack(digits, 0, new StringBuilder(), map, result);
    return result;
}

void backtrack(String digits, int idx, StringBuilder current,
               String[] map, List<String> result) {
    if (idx == digits.length()) {
        result.add(current.toString());
        return;
    }
    String letters = map[digits.charAt(idx) - '0'];
    for (char c : letters.toCharArray()) {
        current.append(c);
        backtrack(digits, idx + 1, current, map, result);
        current.deleteCharAt(current.length() - 1);
    }
}
```

**Сложность:** `O(4^n)` для `n` цифр — на каждую цифру приходится до 4 букв (`7` и `9` дают по 4), поэтому общее число комбинаций ограничено `4^n`.


## Q20. (!) Как решить задачу Palindrome Partitioning?

Разбить строку всеми способами на куски, каждый из которых — палиндром. Например, `"aab"` даёт `[["a","a","b"], ["aa","b"]]`.

Перебираем точку разреза: от позиции `start` пробуем все концы `end`; если подстрока `[start..end]` — палиндром, фиксируем её как очередной кусок и рекурсивно разбиваем хвост `[end+1..]`. Невалидные (не-палиндромные) куски сразу отсекаются проверкой `isPalindrome`, поэтому в дерево перебора попадают только осмысленные разрезы. Когда `start` дошёл до конца строки — текущий список кусков образует полное разбиение.

```java
List<List<String>> partition(String s) {
    List<List<String>> result = new ArrayList<>();
    backtrack(s, 0, new ArrayList<>(), result);
    return result;
}

void backtrack(String s, int start, List<String> current, List<List<String>> result) {
    if (start == s.length()) {
        result.add(new ArrayList<>(current));
        return;
    }
    for (int end = start; end < s.length(); end++) {
        if (isPalindrome(s, start, end)) {
            current.add(s.substring(start, end + 1));
            backtrack(s, end + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}

boolean isPalindrome(String s, int l, int r) {
    while (l < r) {
        if (s.charAt(l++) != s.charAt(r--)) return false;
    }
    return true;
}
```

**Оптимизация:** проверка `isPalindrome` за `O(len)` вызывается многократно для одних и тех же отрезков. Если заранее построить DP-таблицу `isPalindrome[i][j]`, каждая проверка станет `O(1)`-чтением ячейки — типичный приём «backtracking + предвычисление» для ускорения отсечения.


## Q21. Как решить задачу Restore IP Addresses?

Из сплошной строки цифр восстановить все корректные адреса IPv4. Адрес — это ровно 4 части (октета), каждая в диапазоне `0..255` и без ведущего нуля (`"01"`, `"00"` запрещены, а одиночный `"0"` допустим).

Перебираем длину очередного октета — 1, 2 или 3 цифры. Берём кусок, проверяем два правила (нет лишнего ведущего нуля и значение `≤ 255`); невалидный кусок пропускаем. Решение фиксируем только в `base case`: набралось ровно `parts == 4` октета **и** строка израсходована до конца (`start == s.length()`). Если октетов уже 4, а строка не кончилась (или наоборот) — ветвь тупиковая, выходим. В конце убираем хвостовую точку, которую добавляли после каждого октета.

```java
List<String> restoreIpAddresses(String s) {
    List<String> result = new ArrayList<>();
    backtrack(s, 0, 0, "", result);
    return result;
}

void backtrack(String s, int start, int parts, String current, List<String> result) {
    if (parts == 4 && start == s.length()) {
        result.add(current.substring(0, current.length() - 1)); // убрать последнюю '.'
        return;
    }
    if (parts == 4 || start == s.length()) return;

    for (int len = 1; len <= 3 && start + len <= s.length(); len++) {
        String part = s.substring(start, start + len);
        if ((part.startsWith("0") && part.length() > 1) || Integer.parseInt(part) > 255) continue;
        backtrack(s, start + len, parts + 1, current + part + ".", result);
    }
}
```


## Q22. (!) Что такое pruning и зачем?

**Pruning (отсечение)** — отказ от спуска в ветви, которые гарантированно не приведут к решению. Это главный рычаг эффективности backtracking: само по себе дерево перебора экспоненциально, и вся практическая скорость берётся именно из того, сколько поддеревьев удаётся отрезать как можно раньше.

**Виды pruning:**
1. **Constraint check** — проверка валидности **до** рекурсивного вызова. Классика — `isSafe` в N-Queens: не ставим ферзя в бьющуюся клетку и не уходим в заведомо мёртвую ветвь.
2. **Symmetry pruning** — пропуск симметричных решений (например, зеркальных расстановок), чтобы не перебирать эквивалентные варианты повторно.
3. **Lower/upper bound** — для оптимизационных задач: если оптимистичная оценка текущей ветви уже хуже лучшего найденного ответа, ветвь отбрасывают. Это основа Branch and Bound.
4. **Look-ahead** — заглядывание вперёд: если у какой-то будущей переменной не осталось ни одного допустимого значения, текущий частичный путь тупиковый, и его обрывают сразу.

```java
// Без pruning — медленно
for (int i = 0; i < n; i++) {
    state.add(i);
    backtrack(state, result);
    state.remove();
}

// С pruning — быстро
for (int i = 0; i < n; i++) {
    if (!canExtend(state, i)) continue; // PRUNE
    state.add(i);
    backtrack(state, result);
    state.remove();
}
```


## Q23. (!) Branch and Bound — что это?

**Branch and Bound (B&B)** — это backtracking для задач **оптимизации**, усиленный оценкой границы (bound). На каждом узле считаем оптимистичную оценку лучшего, что вообще достижимо в этом поддереве, и если она уже не лучше текущего рекордного решения `best` — поддерево целиком отсекаем.

Разница с обычным backtracking: там мы отсекаем ветви, нарушающие **жёсткие ограничения** (невалидные состояния), а в B&B — ещё и ветви, которые валидны, но **бесперспективны по качеству**. Чем точнее (ближе к реальности) оценка границы, тем сильнее отсечение и тем быстрее сходится поиск.

```java
int best = Integer.MAX_VALUE;

void bnb(State state) {
    if (isComplete(state)) {
        best = Math.min(best, state.cost());
        return;
    }
    if (state.lowerBound() >= best) return; // PRUNE — не сможем улучшить
    for (Choice c : choices(state)) {
        state.apply(c);
        bnb(state);
        state.undo(c);
    }
}
```

**Сценарии применения:** TSP (задача коммивояжёра), Knapsack 0/1, целочисленное линейное программирование (integer linear programming). Именно B&B лежит в основе промышленных оптимизационных решателей вроде CPLEX и Gurobi.


## Q24. Memoization в backtracking?

Если в ходе перебора одно и то же подсостояние возникает многократно, его результат можно кэшировать — это и есть мемоизация. По сути такой шаг превращает backtracking в нисходящий (top-down) DP.

**Word Break** — наглядный пример. Проверяем, разбивается ли строка на слова из словаря. Без кэша одна и та же позиция `start` пересчитывается по многу раз через разные префиксы, и время растёт экспоненциально:

```java
Map<Integer, Boolean> memo = new HashMap<>();

boolean wordBreak(String s, int start, Set<String> dict) {
    if (start == s.length()) return true;
    if (memo.containsKey(start)) return memo.get(start);

    for (int end = start + 1; end <= s.length(); end++) {
        if (dict.contains(s.substring(start, end)) && wordBreak(s, end, dict)) {
            memo.put(start, true);
            return true;
        }
    }
    memo.put(start, false);
    return false;
}
```

**Когда это работает:** мемоизация помогает, только если состояние компактно описывается (здесь — одним числом `start`) и результат зависит **лишь** от него, а не от всего пройденного пути. Тогда различных подзадач всего `O(n)`, и с учётом внутреннего цикла время падает с экспоненциального до `O(n²)`. В задачах же, где надо перечислить **все** конкретные решения (subsets, permutations), кэшировать обычно нечего — все пути уникальны.


## Q25. (!) Какова сложность backtracking?

В худшем случае — экспоненциальная: `O(branchFactor^depth)`, где `branchFactor` — число выборов на шаге, а `depth` — глубина дерева решений. Точная оценка зависит от формы дерева конкретной задачи.

| Задача | Сложность |
|--------|-----------|
| Subsets | `O(2^n)` |
| Permutations | `O(n!)` |
| N-Queens | `O(n!)` |
| Sudoku | до `O(9^81)` без pruning, на практике быстро |
| TSP | `O(n!)` |

**Важная оговорка:** эти оценки — для **худшего случая**, когда отсечений почти нет. С хорошим pruning реальное число посещённых узлов на порядки меньше теоретического. Колонка про Sudoku показательна: формально верхняя граница `O(9^81)`, но с проверкой ограничений и эвристиками типичная головоломка решается практически мгновенно — потому что подавляющая часть дерева отсекается на ранних уровнях.


## Q26. (!) Где backtracking в production?

Backtracking — это не только олимпиадная техника: всюду, где нужно перебрать варианты под ограничениями и рано отсекать тупики, под капотом работает он (часто как часть SAT/CSP-движков).

1. **SAT-solvers** (Z3, Glucose) — проверка выполнимости булевых формул; в основе DPLL — это backtracking с отсечениями.
2. **Constraint Satisfaction Problems** — Sudoku, составление расписаний, planning.
3. **Game AI** — шахматы, Go: minimax с alpha-beta pruning — по сути backtracking по дереву игры с отсечением бесперспективных ходов.
4. **Register allocation в компиляторах** — раскраска графа интерференций перебором (см. Q17).
5. **Генераторы кроссвордов и головоломок** — заполнение сетки словами под ограничения пересечений.
6. **Routing с ограничениями** — трассировка в VLSI-проектировании.
7. **Resource allocation** — упаковка (bin packing), распределение ресурсов и расписаний.
8. **Выравнивание ДНК-последовательностей** — в части методов биоинформатики.
9. **Web crawlers** с фильтрацией обходимых ссылок.
10. **Theorem provers** — поиск доказательства как перебор шагов вывода.


## Q27. Iterative backtracking — реально?

Да — рекурсию можно заменить явным стеком состояний, который вручную моделирует то, что обычно делает стек вызовов. Это имеет смысл при **очень глубокой** рекурсии, где есть риск `StackOverflow`, или когда нужен полный контроль над порядком обхода.

```java
void iterativeBacktrack(State initial) {
    Deque<State> stack = new ArrayDeque<>();
    stack.push(initial);

    while (!stack.isEmpty()) {
        State curr = stack.pop();
        if (isComplete(curr)) {
            handleSolution(curr);
            continue;
        }
        for (Choice c : choices(curr)) {
            if (isValid(curr, c)) {
                stack.push(applyCopy(curr, c));
            }
        }
    }
}
```

**Минусы итеративного варианта:**
- Сложнее реализовать undo: в рекурсии откат происходит «бесплатно» при возврате, а здесь приходится либо класть в стек копии состояний, либо отдельно хранить undo-информацию.
- Выше расход памяти — частичные состояния копятся в стеке вместо лёгких кадров рекурсии.

**Рекомендация:** в большинстве задач рекурсивная версия короче, читабельнее и её достаточно. К явному стеку стоит переходить только когда глубина рекурсии реально угрожает переполнением стека в production.

---

## See also


- [Рекурсия](recursion-interview.md) — основа backtracking
- [Графы](../data-structures/graphs-interview.md) — DFS = тот же подход
- [DP](dynamic-programming-interview.md) — backtracking + memoization
- [Divide and Conquer](divide-and-conquer-interview.md) — другая парадигма
- [Деревья](../data-structures/trees-interview.md) — обходы через DFS
- [Массивы и строки](../data-structures/arrays-strings-interview.md) — Word Search, Palindrome Partition
- [Trie](../data-structures/tries-interview.md) — Word Search II через Trie + backtracking
- [Хеш-таблицы](../data-structures/hash-tables-interview.md) — used set для permutations
- [Анализ сложности](../complexity/complexity-analysis-interview.md) — экспоненциальная vs полиномиальная
- [Шпаргалка: Поиск с возвратом (Backtracking)](../../../algorithms/algorithmic-paradigms/backtracking.md) — теория
