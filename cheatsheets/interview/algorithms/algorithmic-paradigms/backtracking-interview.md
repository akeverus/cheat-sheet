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
updated: "2026-04-25"
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
- [Q10. (!) Combinations C(n, k)?](#q10--combinations-cn-k)
- [Q11. Combination Sum — комбинации с заданной суммой?](#q11-combination-sum--комбинации-с-заданной-суммой)

**Grid задачи**
- [Q12. (!) N-Queens?](#q12--n-queens)
- [Q13. (!) Sudoku Solver?](#q13--sudoku-solver)
- [Q14. (!) Word Search?](#q14--word-search)
- [Q15. Rat in a Maze?](#q15-rat-in-a-maze)

**Графы**
- [Q16. (!) Hamiltonian Path / Cycle?](#q16--hamiltonian-path--cycle)
- [Q17. Graph Coloring?](#q17-graph-coloring)
- [Q18. (!) Generate Parentheses?](#q18--generate-parentheses)

**Строки**
- [Q19. (!) Letter Combinations of a Phone Number?](#q19--letter-combinations-of-a-phone-number)
- [Q20. (!) Palindrome Partitioning?](#q20--palindrome-partitioning)
- [Q21. Restore IP Addresses?](#q21-restore-ip-addresses)

**Pruning и оптимизации**
- [Q22. (!) Что такое pruning и зачем?](#q22--что-такое-pruning-и-зачем)
- [Q23. (!) Branch and Bound — что это?](#q23--branch-and-bound--что-это)
- [Q24. Memoization в backtracking?](#q24-memoization-в-backtracking)

**Применения и подводные камни**
- [Q25. (!) Какова сложность backtracking?](#q25--какова-сложность-backtracking)
- [Q26. (!) Где backtracking в production?](#q26--где-backtracking-в-production)
- [Q27. Iterative backtracking — реально?](#q27-iterative-backtracking--реально)

## Q1. (!) Что такое backtracking?

**Backtracking** — алгоритмическая техника, которая исследует все возможные решения **рекурсивно**, отказываясь («**возвращаясь назад**»), когда текущий частичный результат не может привести к валидному решению.

**Сценарий:**
1. Делаем выбор (расширяем частичное решение)
2. Рекурсивно ищем дальше
3. Если решение найдено — возвращаем
4. Если тупик — откатываем выбор и пробуем другое

**Пример:** N-Queens — ставим ферзей по очереди; если очередной не помещается — снимаем предыдущего и пробуем другое поле.


> [!mcq]
> - [ ] BFS с visited-множеством — перебираем все уровни без отката | ❌ ПОСЛЕДСТВИЕ: BFS не откатывает выбор; нет undo → не может восстановить состояние для следующего варианта
> - [ ] Greedy: всегда берём лучший доступный выбор без возврата | ❌ ПОСЛЕДСТВИЕ: greedy не пробует альтернативы; если оптимальное локальное решение ведёт в тупик — алгоритм зависает
> - [x] Рекурсивно делаем выбор → идём глубже → если тупик — откатываем (undo) → пробуем следующий выбор | ✓ ПРИМЕНЯТЬ: N-Queens, permutations, subsets с constraints 📋 ПРАВИЛО: backtrack = choose → explore → unchoose 🔗 См. Q2
> - [ ] Мемоизация: кэшируем подзадачи чтобы не перевычислять | ❌ ПОСЛЕДСТВИЕ: мемоизация — техника DP; backtracking не кэширует, а откатывает состояние при тупике

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

Три вопроса:
1. **Когда добавить в результат?** (base case)
2. **Какие выборы доступны?** (constraints)
3. **Как откатить выбор?** (undo)


> [!mcq]
> - [ ] Шаблон: for(choice) { apply; recurse; } — без undo | ❌ ПОСЛЕДСТВИЕ: без undo следующая итерация цикла работает с изменённым состоянием → неверные результаты
> - [x] backtrack(state): if(complete) save; for(choice) { apply; backtrack; undo; } — make/unmake вокруг рекурсии | ✓ ПРИМЕНЯТЬ: любая задача с пространством состояний: permutations, N-Queens, subsets 📋 ПРАВИЛО: apply → recurse → undo = симметрия вокруг рекурсии 🔗 См. Q1
> - [ ] Шаблон: while(!stack.empty) { pop; process; push neighbors; } — итеративно | ❌ ПОСЛЕДСТВИЕ: итеративный подход не реализует backtracking без явного стека состояний; значительно сложнее
> - [ ] Шаблон: memoize результаты subproblems, return если уже посчитано | ❌ ПОСЛЕДСТВИЕ: мемоизация — это DP, не backtracking; backtracking не кэширует частичные состояния

## Q3. (!) Чем backtracking отличается от brute force?

Brute force перебирает **все** комбинации без раннего отказа. Backtracking — **отсекает** неперспективные ветви.

```java
// Brute force: 2^n проверок для subsets
for (int mask = 0; mask < (1 << n); mask++) { ... }

// Backtracking: те же 2^n в худшем, но с pruning часто намного меньше
backtrack(state) {
    if (notValid(state)) return; // PRUNE — не идём глубже
    ...
}
```

В худшем случае backtracking = brute force. На практике с хорошими constraints — драматически быстрее.


> [!mcq]
> - [ ] Backtracking = brute force; pruning только замедляет из-за дополнительных проверок | ❌ ПОСЛЕДСТВИЕ: pruning сокращает search space экспоненциально; для N-Queens без pruning 8! попыток, с pruning ~88
> - [ ] Brute force и backtracking идентичны — оба перебирают все варианты с undo | ❌ ПОСЛЕДСТВИЕ: brute force не делает undo; он генерирует все комбинации заранее (bitmask); backtracking строит дерево решений с откатом
> - [ ] Backtracking всегда медленнее brute force из-за overhead на undo | ❌ ПОСЛЕДСТВИЕ: undo — O(1) для большинства задач; backtracking с pruning драматически быстрее при хороших constraints
> - [x] Backtracking = brute force + pruning: отсекает ветви при нарушении constraints; в худшем = brute force | ✓ ПРИМЕНЯТЬ: constraint-heavy задачи где pruning эффективен (N-Queens, Sudoku) 📋 ПРАВИЛО: backtrack = BF + early termination при constraint violation 🔗 См. Q22

## Q4. (!) Чем backtracking отличается от DFS?

**Backtracking — это DFS** по **дереву решений**. Отличие — **есть** unmake step.

| Критерий | DFS (graph) | Backtracking |
|----------|-------------|--------------|
| Где работает | Граф | Дерево решений (state space tree) |
| Visited tracking | `boolean[] visited` (постоянно) | Локальное состояние (откатывается) |
| Цель | Обход или поиск | Найти все/любое валидное решение |
| Undo step | Нет | Есть (после рекурсивного вызова) |

В DFS на графе мы помечаем посещённые вершины и **никогда** их не «отпускаем». В backtracking мы откатываем модификацию, чтобы попробовать другие выборы.


> [!mcq]
> - [ ] Backtracking и DFS идентичны — оба обходят граф рекурсивно с visited | ❌ ПОСЛЕДСТВИЕ: DFS на графе помечает visited постоянно; backtracking откатывает состояние после каждой ветви — это ключевое отличие
> - [ ] Backtracking не может применяться к графам — только к деревьям | ❌ ПОСЛЕДСТВИЕ: backtracking работает с деревьями решений (state space tree), которые могут быть построены для любой задачи, включая графовые
> - [x] Backtracking — DFS по дереву решений с undo step после рекурсии; DFS на графе — постоянная пометка visited без отката | ✓ ПРИМЕНЯТЬ: задачи где нужно исследовать все варианты (permutations, combinations) 📋 ПРАВИЛО: backtrack = DFS + unmake; DFS = DFS + visited (permanent) 🔗 См. Q1
> - [ ] DFS всегда быстрее backtracking т.к. нет overhead на undo | ❌ ПОСЛЕДСТВИЕ: DFS и backtracking решают разные задачи; DFS — обход графа, backtracking — перебор решений; скорость несравнима

## Q5. (!) Чем backtracking отличается от DP?

| Критерий | Backtracking | DP |
|----------|--------------|-----|
| Подзадачи | Уникальные пути | Перекрывающиеся |
| Memoization | Обычно нет | Обязательно |
| Возвращает | Все/один валидный путь | Оптимум (число способов, мин/макс) |
| Сложность | `O(branchFactor^depth)` | Полиномиальная |

Если в backtracking-задаче подзадачи перекрываются — добавляем мемоизацию, и она становится DP.

**Пример:** наивная рекурсия для Fibonacci — backtracking-стиль; с мемоизацией — DP.


> [!mcq]
> - [ ] Backtracking и DP — взаимозаменяемы; выбирай по синтаксической удобности | ❌ ПОСЛЕДСТВИЕ: DP требует overlapping subproblems + optimal substructure; backtracking — для enumeration задач без перекрытий
> - [ ] DP с мемоизацией всегда быстрее backtracking — добавить кэш к любому backtracking | ❌ ПОСЛЕДСТВИЕ: если подзадачи не перекрываются, кэш никогда не даёт hit; лишняя память без выигрыша
> - [ ] Backtracking возвращает только одно решение, DP — все | ❌ ПОСЛЕДСТВИЕ: backtracking возвращает все/любое решение в зависимости от задачи; DP возвращает оптимум (число, не список)
> - [x] Backtracking — для enumeration (все пути, все решения); DP — для оптимизации/подсчёта с перекрывающимися подзадачами | ✓ ПРИМЕНЯТЬ: задача требует «все способы» → backtrack; «минимум/максимум/количество» → DP 📋 ПРАВИЛО: backtrack = enumerate; DP = optimize + memoize 🔗 См. Q24

## Q6. (!) Все подмножества (Subsets)?

Для `[1, 2, 3]` — `[[], [1], [2], [1,2], [3], [1,3], [2,3], [1,2,3]]`. Всего `2^n`.

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

`O(n · 2^n)` — `2^n` подмножеств, копирование каждого `O(n)`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q7. Subsets с дубликатами? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Если в `nums` есть дубликаты — пропускаем их в одной позиции (но не в разных уровнях рекурсии).

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

Сортировка + skip — стандартный паттерн для задач с дубликатами.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q8. (!) Permutations — все перестановки? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Для `[1, 2, 3]` — `n!` перестановок.

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

`O(n · n!)`. **Used array** — стандартный приём для permutations.

**Альтернатива — swap-based:**

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. Permutations с дубликатами? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. (!) Combinations C(n, k)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Все сочетания `k` элементов из `[1..n]`.

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

**Pruning** через `n - (k - size) + 1` — не идём в ветви, где остатка не хватит.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Combination Sum — комбинации с заданной суммой? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`candidates`, `target`. Найти все комбинации (можно повторять элементы), дающие target.

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

`i` (не `i + 1`) — потому что элемент можно использовать несколько раз. **Combination Sum II** — каждый раз; **Combination Sum III** — фиксированное `k` элементов.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. (!) N-Queens? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Поставить `n` ферзей на `n×n` доску так, чтобы они не били друг друга.

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

`O(n!)`. Для `n = 8` — 92 решения, для `n = 12` — 14200.

**Оптимизация через bitsets:** `column`, `diag1`, `diag2` — `O(1)` проверка.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. (!) Sudoku Solver? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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

**Оптимизации:**
- Битмаски для row/col/box (вместо проверки 27 ячеек)
- Сначала ячейки с минимумом валидных вариантов (MRV — Minimum Remaining Values heuristic)
- Constraint propagation (если в строке только одна ячейка может быть `5` — там и ставим)


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. (!) Word Search? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

В grid из букв найти слово (по соседним клеткам, без повторного посещения).

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

`O(R · C · 4^L)`, где `L` — длина слова. Альтернатива — отдельный `boolean[][]` visited, но in-place быстрее по памяти.

Для **множества** слов — Trie + backtracking, см. [Trie](../data-structures/tries-interview.md).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Rat in a Maze? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Лабиринт с препятствиями. Найти все пути из (0,0) в (n-1, n-1).

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. (!) Hamiltonian Path / Cycle? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Hamiltonian path:** путь, посещающий каждую вершину **ровно один раз**. NP-hard в общем случае → backtracking.

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

`O(n!)` в худшем случае.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. Graph Coloring? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Раскрасить вершины графа `m` цветами так, чтобы соседние имели разные цвета.

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

Применения: распределение частот в радиосвязи, register allocation в компиляторах.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q18. (!) Generate Parentheses? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Сгенерировать все валидные комбинации `n` пар скобок.

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

Всего `Catalan(n)` валидных комбинаций. Constraint: `close < open` (не закрывать больше, чем открыли).


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q19. (!) Letter Combinations of a Phone Number? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

`"23"` → буквы клавиатуры — все комбинации.

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

`O(4^n)`. На каждой цифре до 4 букв.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q20. (!) Palindrome Partitioning? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Разделить строку на палиндромные подстроки.

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

С DP-предвычислением `isPalindrome[i][j]` — ускорение проверки до `O(1)`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q21. Restore IP Addresses? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Из строки цифр восстановить все валидные IPv4 (4 части по 1-255, без leading zero).

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q22. (!) Что такое pruning и зачем? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Pruning (отсечение)** — отказ от исследования ветвей, которые точно не приведут к решению. Главный инструмент эффективности backtracking.

**Виды pruning:**
1. **Constraint check** — проверка валидности до рекурсивного вызова (N-Queens isSafe)
2. **Symmetry pruning** — игнорирование симметричных решений
3. **Lower/upper bound** — если текущая оценка хуже лучшего найденного — отказ (Branch and Bound)
4. **Look-ahead** — проверка, есть ли хоть один валидный ход дальше

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q23. (!) Branch and Bound — что это? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Branch and Bound (B&B)** — backtracking с **оценкой lower/upper bound**. На каждом шаге считаем оптимистичную оценку и отсекаем, если она хуже текущего best.

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

Применяется в **TSP, Knapsack 0/1, integer linear programming**. Используется в LP-solver'ах вроде CPLEX, Gurobi.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q24. Memoization в backtracking? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Если подзадачи **повторяются** — добавляем мемоизацию. Это превращает backtracking в DP.

**Word Break** — пример:

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

Без мемоизации — экспоненциально. С — `O(n²)`.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q25. (!) Какова сложность backtracking? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

В худшем случае — экспоненциальная: `O(branchFactor^depth)`.

| Задача | Сложность |
|--------|-----------|
| Subsets | `O(2^n)` |
| Permutations | `O(n!)` |
| N-Queens | `O(n!)` |
| Sudoku | до `O(9^81)` без pruning, на практике быстро |
| TSP | `O(n!)` |

**С pruning** — на практике гораздо быстрее. Sudoku с хорошими эвристиками решается мгновенно для большинства задач.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q26. (!) Где backtracking в production? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

1. **SAT-solvers** (Z3, Glucose) — boolean satisfiability
2. **Constraint Satisfaction Problems** — Sudoku, scheduling, planning
3. **Game AI** — chess, Go (minimax + alpha-beta pruning — это backtracking)
4. **Compiler register allocation** — graph coloring backtracking
5. **Crossword/puzzle generators**
6. **Routing с constraints** — VLSI design
7. **Resource allocation** — bin packing, scheduling
8. **DNA sequence alignment** (некоторые методы)
9. **Web crawlers** с фильтрацией
10. **Theorem provers** — proof search


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q27. Iterative backtracking — реально? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

Да, через явный стек состояний. Полезно для **очень глубокой** рекурсии (риск StackOverflow).

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

**Минусы:**
- Сложнее undo (нужно копировать состояние или хранить undo info)
- Больше памяти (состояния в стеке)

В большинстве случаев рекурсия проще и достаточно. Итеративный — только для production-критичной глубокой рекурсии.

---

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [Алгоритмы (обзор)](../algorithms-interview.md) — карта алгоритмических тем ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
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
