# Roadmap — атомарный чек-лист самообучения

> **Модель:** плоский атомарный бэклог. 1 пункт = 1 тема (1 interview-файл). Темы расположены в **логическом порядке** — от фундамента к продвинутому; идёшь сверху вниз.
> Микро-петля внутри темы: 📖 изучить → 🧠 тест в quiz-app (≥80%) → 🏋️ практика на тренажёре *или* 🔎 ресурсы (доки + видео).
> 🔴 — пробелы из диагностики (приоритет внутри потока). Бюджет ~1 ч/будний день (recurring slot + habit «Учусь сегодня» в Singularity).
> Зеркало прогресса — Singularity, мастер-нот `T-84507082` (1 чек-бокс на тему). История/диагностика — `docs/learning-plan/diagnostic/`.
> 🧠 Тест: `./gradlew bootRun` → http://localhost:8080 → выбрать тему по `topic`-ключу.


## Оглавление

1. [Алгоритмы и структуры данных](#1) — 18 тем
2. [Языки (Java / Kotlin / Go / Scala)](#2) — 46 тем
3. [JVM Internals](#3) — 2 темы
4. [Производительность](#4) — 7 тем
5. [Фреймворки (Spring / JVM-alt)](#5) — 36 тем
6. [Реактивное программирование](#6) — 6 тем
7. [Базы данных и хранилища](#7) — 20 тем
8. [Брокеры сообщений и стриминг](#8) — 7 тем
9. [API](#9) — 8 тем
10. [Архитектура и паттерны](#10) — 24 темы
11. [System Design (проектирование систем)](#11) — 22 темы
12. [Data Engineering](#12) — 8 тем
13. [Облака](#13) — 6 тем
14. [DevOps и контейнеры](#14) — 13 тем
15. [CI/CD](#15) — 2 темы
16. [Наблюдаемость (метрики и трейсинг)](#16) — 9 тем
17. [Логирование](#17) — 1 тема
18. [Безопасность](#18) — 10 тем
19. [Тестирование](#19) — 14 тем
20. [Качество кода](#20) — 7 тем
21. [Паттерны проектирования](#21) — 1 тема
22. [AI / LLM](#22) — 25 тем
23. [Behavioral](#23) — 6 тем
24. [Лидерство](#24) — 7 тем
25. [Подготовка к интервью (мета)](#25) — 1 тема


<a id="1"></a>
## 1. Алгоритмы и структуры данных
*`cheatsheets/interview/algorithms/` — 18 тем*

- [ ] **Algorithms** — `algorithms/algorithms-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/algorithms-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/algorithms-interview` ≥80%
  - [ ] 🏋️ Практика: 10 задач на [LeetCode](https://leetcode.com/problemset/); визуализация на [VisuAlgo](https://visualgo.net/en)

### complexity

- [ ] **Complexity analysis** — `algorithms/complexity/complexity-analysis-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/complexity/complexity-analysis-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/complexity/complexity-analysis-interview` ≥80%
  - [ ] 🏋️ Практика: разобрать сложности по [Big-O Cheat Sheet](https://www.bigocheatsheet.com/); оценить Big-O для 10 знакомых алгоритмов без кода

### data-structures

- [ ] **Arrays strings** — `algorithms/data-structures/arrays-strings-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/data-structures/arrays-strings-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/data-structures/arrays-strings-interview` ≥80%
  - [ ] 🏋️ Практика: визуализировать в [VisuAlgo](https://visualgo.net/en) + 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по этой структуре; разбор на [Algorithm Visualizer](https://algorithm-visualizer.org/)
- [ ] **Linked lists** — `algorithms/data-structures/linked-lists-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/data-structures/linked-lists-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/data-structures/linked-lists-interview` ≥80%
  - [ ] 🏋️ Практика: визуализировать в [VisuAlgo](https://visualgo.net/en) + 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по этой структуре; разбор на [Algorithm Visualizer](https://algorithm-visualizer.org/)
- [ ] **Stacks queues** — `algorithms/data-structures/stacks-queues-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/data-structures/stacks-queues-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/data-structures/stacks-queues-interview` ≥80%
  - [ ] 🏋️ Практика: визуализировать в [VisuAlgo](https://visualgo.net/en) + 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по этой структуре; разбор на [Algorithm Visualizer](https://algorithm-visualizer.org/)
- [ ] **Hash tables** — `algorithms/data-structures/hash-tables-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/data-structures/hash-tables-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/data-structures/hash-tables-interview` ≥80%
  - [ ] 🏋️ Практика: визуализировать в [VisuAlgo](https://visualgo.net/en) + 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по этой структуре; разбор на [Algorithm Visualizer](https://algorithm-visualizer.org/)
- [ ] **Trees** 🔴 — `algorithms/data-structures/trees-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/data-structures/trees-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/data-structures/trees-interview` ≥80%
  - [ ] 🏋️ Практика: визуализировать в [VisuAlgo](https://visualgo.net/en) + 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по этой структуре; разбор на [Algorithm Visualizer](https://algorithm-visualizer.org/)
- [ ] **Heaps** — `algorithms/data-structures/heaps-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/data-structures/heaps-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/data-structures/heaps-interview` ≥80%
  - [ ] 🏋️ Практика: визуализировать в [VisuAlgo](https://visualgo.net/en) + 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по этой структуре; разбор на [Algorithm Visualizer](https://algorithm-visualizer.org/)
- [ ] **Tries** — `algorithms/data-structures/tries-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/data-structures/tries-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/data-structures/tries-interview` ≥80%
  - [ ] 🏋️ Практика: визуализировать в [VisuAlgo](https://visualgo.net/en) + 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по этой структуре; разбор на [Algorithm Visualizer](https://algorithm-visualizer.org/)
- [ ] **Graphs** — `algorithms/data-structures/graphs-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/data-structures/graphs-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/data-structures/graphs-interview` ≥80%
  - [ ] 🏋️ Практика: визуализировать в [VisuAlgo](https://visualgo.net/en) + 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по этой структуре; разбор на [Algorithm Visualizer](https://algorithm-visualizer.org/)

### sorting-searching

- [ ] **Sorting algorithms** — `algorithms/sorting-searching/sorting-algorithms-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/sorting-searching/sorting-algorithms-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/sorting-searching/sorting-algorithms-interview` ≥80%
  - [ ] 🏋️ Практика: [VisuAlgo](https://visualgo.net/en) (sorting/searching) + 5–10 задач binary-search/sorting на [LeetCode](https://leetcode.com/problemset/)
- [ ] **Searching algorithms** — `algorithms/sorting-searching/searching-algorithms-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/sorting-searching/searching-algorithms-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/sorting-searching/searching-algorithms-interview` ≥80%
  - [ ] 🏋️ Практика: [VisuAlgo](https://visualgo.net/en) (sorting/searching) + 5–10 задач binary-search/sorting на [LeetCode](https://leetcode.com/problemset/)

### algorithmic-paradigms

- [ ] **Recursion** — `algorithms/algorithmic-paradigms/recursion-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/algorithmic-paradigms/recursion-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/algorithmic-paradigms/recursion-interview` ≥80%
  - [ ] 🏋️ Практика: 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по тегу (DP / Backtracking / Greedy / …); прогон на [Algorithm Visualizer](https://algorithm-visualizer.org/)
- [ ] **Two pointers sliding window** — `algorithms/algorithmic-paradigms/two-pointers-sliding-window-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/algorithmic-paradigms/two-pointers-sliding-window-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/algorithmic-paradigms/two-pointers-sliding-window-interview` ≥80%
  - [ ] 🏋️ Практика: 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по тегу (DP / Backtracking / Greedy / …); прогон на [Algorithm Visualizer](https://algorithm-visualizer.org/)
- [ ] **Divide and conquer** — `algorithms/algorithmic-paradigms/divide-and-conquer-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/algorithmic-paradigms/divide-and-conquer-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/algorithmic-paradigms/divide-and-conquer-interview` ≥80%
  - [ ] 🏋️ Практика: 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по тегу (DP / Backtracking / Greedy / …); прогон на [Algorithm Visualizer](https://algorithm-visualizer.org/)
- [ ] **Backtracking** — `algorithms/algorithmic-paradigms/backtracking-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/algorithmic-paradigms/backtracking-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/algorithmic-paradigms/backtracking-interview` ≥80%
  - [ ] 🏋️ Практика: 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по тегу (DP / Backtracking / Greedy / …); прогон на [Algorithm Visualizer](https://algorithm-visualizer.org/)
- [ ] **Greedy algorithms** — `algorithms/algorithmic-paradigms/greedy-algorithms-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/algorithmic-paradigms/greedy-algorithms-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/algorithmic-paradigms/greedy-algorithms-interview` ≥80%
  - [ ] 🏋️ Практика: 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по тегу (DP / Backtracking / Greedy / …); прогон на [Algorithm Visualizer](https://algorithm-visualizer.org/)
- [ ] **Dynamic programming** — `algorithms/algorithmic-paradigms/dynamic-programming-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/algorithms/algorithmic-paradigms/dynamic-programming-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=algorithms/algorithmic-paradigms/dynamic-programming-interview` ≥80%
  - [ ] 🏋️ Практика: 10–15 Medium-задач на [LeetCode](https://leetcode.com/problemset/) по тегу (DP / Backtracking / Greedy / …); прогон на [Algorithm Visualizer](https://algorithm-visualizer.org/)

<a id="2"></a>
## 2. Языки (Java / Kotlin / Go / Scala)
*`cheatsheets/interview/programming-languages/` — 46 тем*


### java

- [ ] **Java core** — `programming-languages/java/java-core-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-core-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-core-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+core+explained) — каналы: Defog Tech, Java
- [ ] **Java oop** — `programming-languages/java/java-oop-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-oop-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-oop-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+oop+explained) — каналы: Defog Tech, Java
- [ ] **Java types** — `programming-languages/java/java-types-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-types-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-types-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+types+explained) — каналы: Defog Tech, Java
- [ ] **Java conditional statements** — `programming-languages/java/java-conditional-statements-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-conditional-statements-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-conditional-statements-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+conditional+statements+explained) — каналы: Defog Tech, Java
- [ ] **Java initialization** — `programming-languages/java/java-initialization-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-initialization-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-initialization-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+initialization+explained) — каналы: Defog Tech, Java
- [ ] **Java string** — `programming-languages/java/java-string-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-string-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-string-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+string+explained) — каналы: Defog Tech, Java
- [ ] **Java exceptions** — `programming-languages/java/java-exceptions-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-exceptions-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-exceptions-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+exceptions+explained) — каналы: Defog Tech, Java
- [ ] **Java collections** — `programming-languages/java/java-collections-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-collections-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-collections-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+collections+explained) — каналы: Defog Tech, Java
- [ ] **Java generics** — `programming-languages/java/java-generics-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-generics-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-generics-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+generics+explained) — каналы: Defog Tech, Java
- [ ] **Java functional interface** — `programming-languages/java/java-functional-interface-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-functional-interface-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-functional-interface-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+functional+interface+explained) — каналы: Defog Tech, Java
- [ ] **Java stream** — `programming-languages/java/java-stream-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-stream-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-stream-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/api/streams/) · [видео ▶](https://www.youtube.com/results?search_query=Java+stream+explained) — каналы: Defog Tech, Java
- [ ] **Java optional** — `programming-languages/java/java-optional-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-optional-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-optional-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+optional+explained) — каналы: Defog Tech, Java
- [ ] **Java 8** — `programming-languages/java/java-8-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-8-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-8-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+8+explained) — каналы: Defog Tech, Java
- [ ] **Java records** — `programming-languages/java/java-records-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-records-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-records-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+records+explained) — каналы: Defog Tech, Java
- [ ] **Java pattern matching** — `programming-languages/java/java-pattern-matching-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-pattern-matching-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-pattern-matching-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+pattern+matching+explained) — каналы: Defog Tech, Java
- [ ] **Java 17 21** — `programming-languages/java/java-17-21-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-17-21-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-17-21-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+17+21+explained) — каналы: Defog Tech, Java
- [ ] **Java annotations** — `programming-languages/java/java-annotations-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-annotations-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-annotations-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+annotations+explained) — каналы: Defog Tech, Java
- [ ] **Java reflection** — `programming-languages/java/java-reflection-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-reflection-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-reflection-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+reflection+explained) — каналы: Defog Tech, Java
- [ ] **Java io nio** — `programming-languages/java/java-io-nio-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-io-nio-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-io-nio-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+io+nio+explained) — каналы: Defog Tech, Java
- [ ] **Java serialization** — `programming-languages/java/java-serialization-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-serialization-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-serialization-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+serialization+explained) — каналы: Defog Tech, Java
- [ ] **Java modules** — `programming-languages/java/java-modules-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-modules-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-modules-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+modules+explained) — каналы: Defog Tech, Java
- [ ] **Java concurrency** — `programming-languages/java/java-concurrency-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-concurrency-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-concurrency-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.oracle.com/javase/tutorial/essential/concurrency/) · [видео ▶](https://www.youtube.com/results?search_query=Java+concurrency+explained) — каналы: Defog Tech, Java
- [ ] **Java completable future** — `programming-languages/java/java-completable-future-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-completable-future-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-completable-future-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+completable+future+explained) — каналы: Defog Tech, Java
- [ ] **Java virtual threads** — `programming-languages/java/java-virtual-threads-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-virtual-threads-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-virtual-threads-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html) · [видео ▶](https://www.youtube.com/results?search_query=Java+virtual+threads+explained) — каналы: Defog Tech, Java
- [ ] **Java lombok** — `programming-languages/java/java-lombok-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-lombok-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-lombok-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+lombok+explained) — каналы: Defog Tech, Java
- [ ] **Java mapstruct** — `programming-languages/java/java-mapstruct-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-mapstruct-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-mapstruct-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+mapstruct+explained) — каналы: Defog Tech, Java
- [ ] **Java jackson** — `programming-languages/java/java-jackson-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/java/java-jackson-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/java/java-jackson-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://dev.java/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Java+jackson+explained) — каналы: Defog Tech, Java

### kotlin

- [ ] **Kotlin** — `programming-languages/kotlin/kotlin-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/kotlin/kotlin-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/kotlin/kotlin-interview` ≥80%
  - [ ] 🏋️ Практика: [Kotlin Koans](https://play.kotlinlang.org/koans/) — пройти все упражнения в Playground
- [ ] **Kotlin collections** — `programming-languages/kotlin/kotlin-collections-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/kotlin/kotlin-collections-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/kotlin/kotlin-collections-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://kotlinlang.org/docs/home.html) · [видео ▶](https://www.youtube.com/results?search_query=Kotlin+collections+explained) — каналы: Philipp Lackner, Kotlin by JetBrains
- [ ] **Kotlin exceptions** — `programming-languages/kotlin/kotlin-exceptions-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/kotlin/kotlin-exceptions-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/kotlin/kotlin-exceptions-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://kotlinlang.org/docs/home.html) · [видео ▶](https://www.youtube.com/results?search_query=Kotlin+exceptions+explained) — каналы: Philipp Lackner, Kotlin by JetBrains
- [ ] **Kotlin sealed classes** — `programming-languages/kotlin/kotlin-sealed-classes-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/kotlin/kotlin-sealed-classes-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/kotlin/kotlin-sealed-classes-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://kotlinlang.org/docs/home.html) · [видео ▶](https://www.youtube.com/results?search_query=Kotlin+sealed+classes+explained) — каналы: Philipp Lackner, Kotlin by JetBrains
- [ ] **Kotlin value classes** — `programming-languages/kotlin/kotlin-value-classes-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/kotlin/kotlin-value-classes-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/kotlin/kotlin-value-classes-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://kotlinlang.org/docs/home.html) · [видео ▶](https://www.youtube.com/results?search_query=Kotlin+value+classes+explained) — каналы: Philipp Lackner, Kotlin by JetBrains
- [ ] **Kotlin coroutines** — `programming-languages/kotlin/kotlin-coroutines-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/kotlin/kotlin-coroutines-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/kotlin/kotlin-coroutines-interview` ≥80%
  - [ ] 🏋️ Практика: [Kotlin Coroutines hands-on](https://kotlinlang.org/docs/coroutines-guide.html) — прогнать примеры в [Playground](https://play.kotlinlang.org/)
- [ ] **Kotlin flow** — `programming-languages/kotlin/kotlin-flow-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/kotlin/kotlin-flow-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/kotlin/kotlin-flow-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://kotlinlang.org/docs/flow.html) · [видео ▶](https://www.youtube.com/results?search_query=Kotlin+flow+explained) — каналы: Philipp Lackner, Kotlin by JetBrains
- [ ] **Kotlin interop java** — `programming-languages/kotlin/kotlin-interop-java-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/kotlin/kotlin-interop-java-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/kotlin/kotlin-interop-java-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://kotlinlang.org/docs/home.html) · [видео ▶](https://www.youtube.com/results?search_query=Kotlin+interop+java+explained) — каналы: Philipp Lackner, Kotlin by JetBrains
- [ ] **Kotlin serialization** — `programming-languages/kotlin/kotlin-serialization-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/kotlin/kotlin-serialization-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/kotlin/kotlin-serialization-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://kotlinlang.org/docs/home.html) · [видео ▶](https://www.youtube.com/results?search_query=Kotlin+serialization+explained) — каналы: Philipp Lackner, Kotlin by JetBrains
- [ ] **Kotlin dsl** — `programming-languages/kotlin/kotlin-dsl-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/kotlin/kotlin-dsl-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/kotlin/kotlin-dsl-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://kotlinlang.org/docs/home.html) · [видео ▶](https://www.youtube.com/results?search_query=Kotlin+dsl+explained) — каналы: Philipp Lackner, Kotlin by JetBrains
- [ ] **Kotlin spring** — `programming-languages/kotlin/kotlin-spring-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/kotlin/kotlin-spring-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/kotlin/kotlin-spring-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://kotlinlang.org/docs/home.html) · [видео ▶](https://www.youtube.com/results?search_query=Kotlin+spring+explained) — каналы: Philipp Lackner, Kotlin by JetBrains

### go

- [ ] **Go** — `programming-languages/go/go-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/go/go-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/go/go-interview` ≥80%
  - [ ] 🏋️ Практика: [A Tour of Go](https://go.dev/tour/) — пройти все разделы; закрепить на [Go by Example](https://gobyexample.com/)
- [ ] **Go stdlib** — `programming-languages/go/go-stdlib-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/go/go-stdlib-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/go/go-stdlib-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://go.dev/doc/) · [видео ▶](https://www.youtube.com/results?search_query=Go+stdlib+explained) — каналы: TutorialEdge, Go
- [ ] **Go modules** — `programming-languages/go/go-modules-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/go/go-modules-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/go/go-modules-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://go.dev/doc/) · [видео ▶](https://www.youtube.com/results?search_query=Go+modules+explained) — каналы: TutorialEdge, Go
- [ ] **Go generics** — `programming-languages/go/go-generics-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/go/go-generics-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/go/go-generics-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://go.dev/doc/) · [видео ▶](https://www.youtube.com/results?search_query=Go+generics+explained) — каналы: TutorialEdge, Go
- [ ] **Go concurrency** 🔴 — `programming-languages/go/go-concurrency-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/go/go-concurrency-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/go/go-concurrency-interview` ≥80%
  - [ ] 🏋️ Практика: [Go by Example](https://gobyexample.com/): goroutines / channels / select + раздел Concurrency в [Tour](https://go.dev/tour/)
- [ ] **Go memory gc** — `programming-languages/go/go-memory-gc-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/go/go-memory-gc-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/go/go-memory-gc-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://go.dev/doc/) · [видео ▶](https://www.youtube.com/results?search_query=Go+memory+gc+explained) — каналы: TutorialEdge, Go
- [ ] **Go testing** — `programming-languages/go/go-testing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/go/go-testing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/go/go-testing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://go.dev/doc/) · [видео ▶](https://www.youtube.com/results?search_query=Go+testing+explained) — каналы: TutorialEdge, Go

### scala

- [ ] **Scala** — `programming-languages/scala/scala-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/programming-languages/scala/scala-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=programming-languages/scala/scala-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.scala-lang.org/) · [видео ▶](https://www.youtube.com/results?search_query=Scala+explained) — каналы: Rock the JVM

<a id="3"></a>
## 3. JVM Internals
*`cheatsheets/interview/jvm/` — 2 темы*

- [ ] **JVM** — `jvm/jvm-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/jvm/jvm-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=jvm/jvm-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.oracle.com/javase/specs/jvms/se21/html/) · [видео ▶](https://www.youtube.com/results?search_query=JVM+explained) — каналы: JEP Café, Java
- [ ] **GraalVM Native** — `jvm/graalvm-native-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/jvm/graalvm-native-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=jvm/graalvm-native-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.graalvm.org/latest/reference-manual/native-image/) · [видео ▶](https://www.youtube.com/results?search_query=GraalVM+Native+explained) — каналы: JEP Café, Java

<a id="4"></a>
## 4. Производительность
*`cheatsheets/interview/performance/` — 7 тем*

- [ ] **Memory management** — `performance/memory-management-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/performance/memory-management-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=performance/memory-management-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.oracle.com/en/java/javase/21/gctuning/introduction-garbage-collection-tuning.html) · [видео ▶](https://www.youtube.com/results?search_query=Memory+management+explained) — каналы: JEP Café, Java
- [ ] **Jvm performance tuning** — `performance/jvm-performance-tuning-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/performance/jvm-performance-tuning-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=performance/jvm-performance-tuning-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.oracle.com/en/java/javase/21/gctuning/) · [видео ▶](https://www.youtube.com/results?search_query=Jvm+performance+tuning+explained) — каналы: JEP Café, Java
- [ ] **Application profiling** — `performance/application-profiling-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/performance/application-profiling-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=performance/application-profiling-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/async-profiler/async-profiler) · [видео ▶](https://www.youtube.com/results?search_query=Application+profiling+explained) — каналы: JEP Café, Java
- [ ] **Database performance** — `performance/database-performance-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/performance/database-performance-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=performance/database-performance-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://use-the-index-luke.com/) · [видео ▶](https://www.youtube.com/results?search_query=Database+performance+explained) — каналы: JEP Café, Java
- [ ] **Caching performance** — `performance/caching-performance-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/performance/caching-performance-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=performance/caching-performance-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://aws.amazon.com/caching/) · [видео ▶](https://www.youtube.com/results?search_query=Caching+performance+explained) — каналы: JEP Café, Java
- [ ] **Network performance** — `performance/network-performance-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/performance/network-performance-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=performance/network-performance-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://hpbn.co/) · [видео ▶](https://www.youtube.com/results?search_query=Network+performance+explained) — каналы: JEP Café, Java
- [ ] **Performance testing** — `performance/performance-testing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/performance/performance-testing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=performance/performance-testing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.gatling.io/) · [видео ▶](https://www.youtube.com/results?search_query=Performance+testing+explained) — каналы: JEP Café, Java

<a id="5"></a>
## 5. Фреймворки (Spring / JVM-alt)
*`cheatsheets/interview/frameworks/` — 36 тем*


### spring

- [ ] **Spring framework** — `frameworks/spring/spring-framework-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-framework-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-framework-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-framework/reference/) · [видео ▶](https://www.youtube.com/results?search_query=Spring+framework+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring boot** — `frameworks/spring/spring-boot-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-boot-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-boot-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+boot+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring mvc** — `frameworks/spring/spring-mvc-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-mvc-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-mvc-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-framework/reference/web/webmvc.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+mvc+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring data jpa** — `frameworks/spring/spring-data-jpa-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-data-jpa-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-data-jpa-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-data/jpa/reference/) · [видео ▶](https://www.youtube.com/results?search_query=Spring+data+jpa+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring transaction** — `frameworks/spring/spring-transaction-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-transaction-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-transaction-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+transaction+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring aop** — `frameworks/spring/spring-aop-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-aop-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-aop-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+aop+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring validation** — `frameworks/spring/spring-validation-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-validation-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-validation-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+validation+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring security** — `frameworks/spring/spring-security-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-security-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-security-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-security/reference/) · [видео ▶](https://www.youtube.com/results?search_query=Spring+security+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring data jdbc** — `frameworks/spring/spring-data-jdbc-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-data-jdbc-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-data-jdbc-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+data+jdbc+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring r2dbc** — `frameworks/spring/spring-r2dbc-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-r2dbc-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-r2dbc-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+r2dbc+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring webflux** — `frameworks/spring/spring-webflux-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-webflux-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-webflux-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-framework/reference/web/webflux.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+webflux+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring cache** — `frameworks/spring/spring-cache-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-cache-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-cache-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+cache+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring async** — `frameworks/spring/spring-async-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-async-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-async-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+async+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring scheduling** — `frameworks/spring/spring-scheduling-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-scheduling-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-scheduling-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+scheduling+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring events** — `frameworks/spring/spring-events-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-events-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-events-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+events+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring messaging** — `frameworks/spring/spring-messaging-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-messaging-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-messaging-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+messaging+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring kafka** — `frameworks/spring/spring-kafka-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-kafka-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-kafka-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-kafka/reference/) · [видео ▶](https://www.youtube.com/results?search_query=Spring+kafka+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring integration** — `frameworks/spring/spring-integration-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-integration-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-integration-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+integration+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring batch** — `frameworks/spring/spring-batch-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-batch-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-batch-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-batch/reference/) · [видео ▶](https://www.youtube.com/results?search_query=Spring+batch+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring cloud** 🔴 — `frameworks/spring/spring-cloud-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-cloud-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-cloud-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://spring.io/projects/spring-cloud) · [видео ▶](https://www.youtube.com/results?search_query=Spring+cloud+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring boot actuator** — `frameworks/spring/spring-boot-actuator-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-boot-actuator-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-boot-actuator-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+boot+actuator+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring rest client** — `frameworks/spring/spring-rest-client-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-rest-client-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-rest-client-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+rest+client+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring graphql** — `frameworks/spring/spring-graphql-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-graphql-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-graphql-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+graphql+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring session** — `frameworks/spring/spring-session-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-session-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-session-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+session+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring retry** — `frameworks/spring/spring-retry-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-retry-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-retry-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+retry+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Resilience4j** — `frameworks/spring/resilience4j-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/resilience4j-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/resilience4j-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://resilience4j.readme.io/docs) · [видео ▶](https://www.youtube.com/results?search_query=Resilience4j+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring modulith** — `frameworks/spring/spring-modulith-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-modulith-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-modulith-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+modulith+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring state machine** — `frameworks/spring/spring-state-machine-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-state-machine-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-state-machine-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+state+machine+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring vault** — `frameworks/spring/spring-vault-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-vault-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-vault-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+vault+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring testing** — `frameworks/spring/spring-testing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-testing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-testing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+testing+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring boot 3 migration** — `frameworks/spring/spring-boot-3-migration-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-boot-3-migration-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-boot-3-migration-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-boot/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Spring+boot+3+migration+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Spring ai** — `frameworks/spring/spring-ai-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/spring/spring-ai-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/spring/spring-ai-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-ai/reference/) · [видео ▶](https://www.youtube.com/results?search_query=Spring+ai+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper

### jvm-alternatives

- [ ] **Quarkus** — `frameworks/jvm-alternatives/quarkus-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/jvm-alternatives/quarkus-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/jvm-alternatives/quarkus-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://quarkus.io/guides/) · [видео ▶](https://www.youtube.com/results?search_query=Quarkus+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Micronaut** — `frameworks/jvm-alternatives/micronaut-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/jvm-alternatives/micronaut-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/jvm-alternatives/micronaut-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://micronaut.io/docs/) · [видео ▶](https://www.youtube.com/results?search_query=Micronaut+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Ktor** — `frameworks/jvm-alternatives/ktor-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/jvm-alternatives/ktor-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/jvm-alternatives/ktor-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://ktor.io/docs/) · [видео ▶](https://www.youtube.com/results?search_query=Ktor+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper
- [ ] **Vertx** — `frameworks/jvm-alternatives/vertx-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/frameworks/jvm-alternatives/vertx-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=frameworks/jvm-alternatives/vertx-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://vertx.io/docs/) · [видео ▶](https://www.youtube.com/results?search_query=Vertx+explained) — каналы: Dan Vega, Java Brains, SpringDeveloper

<a id="6"></a>
## 6. Реактивное программирование
*`cheatsheets/interview/reactive/` — 6 тем*

- [ ] **Reactive streams** — `reactive/reactive-streams-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/reactive/reactive-streams-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=reactive/reactive-streams-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.reactive-streams.org/) · [видео ▶](https://www.youtube.com/results?search_query=Reactive+streams+explained) — каналы: Josh Long, SpringDeveloper
- [ ] **Reactive patterns** — `reactive/reactive-patterns-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/reactive/reactive-patterns-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=reactive/reactive-patterns-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://projectreactor.io/docs/core/release/reference/) · [видео ▶](https://www.youtube.com/results?search_query=Reactive+patterns+explained) — каналы: Josh Long, SpringDeveloper
- [ ] **Project reactor** 🔴 — `reactive/project-reactor-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/reactive/project-reactor-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=reactive/project-reactor-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://projectreactor.io/docs/core/release/reference/) · [видео ▶](https://www.youtube.com/results?search_query=Project+reactor+explained) — каналы: Josh Long, SpringDeveloper
- [ ] **WebFlux** — `reactive/webflux-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/reactive/webflux-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=reactive/webflux-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.spring.io/spring-framework/reference/web/webflux.html) · [видео ▶](https://www.youtube.com/results?search_query=WebFlux+explained) — каналы: Josh Long, SpringDeveloper
- [ ] **RxJava** — `reactive/rxjava-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/reactive/rxjava-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=reactive/rxjava-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/ReactiveX/RxJava/wiki) · [видео ▶](https://www.youtube.com/results?search_query=RxJava+explained) — каналы: Josh Long, SpringDeveloper
- [ ] **Reactive testing** — `reactive/reactive-testing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/reactive/reactive-testing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=reactive/reactive-testing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://projectreactor.io/docs/test/release/reference/) · [видео ▶](https://www.youtube.com/results?search_query=Reactive+testing+explained) — каналы: Josh Long, SpringDeveloper

<a id="7"></a>
## 7. Базы данных и хранилища
*`cheatsheets/interview/databases/` — 20 тем*

- [ ] **Database architecture** 🔴 — `databases/database-architecture-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/database-architecture-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/database-architecture-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://use-the-index-luke.com/) · [видео ▶](https://www.youtube.com/results?search_query=Database+architecture+explained) — каналы: Hussein Nasser
- [ ] **SQL** — `databases/sql-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/sql-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/sql-interview` ≥80%
  - [ ] 🏋️ Практика: запросы на [SQLZoo](https://sqlzoo.net/) + [sql-ex.ru](https://www.sql-ex.ru/) + раздел [LeetCode Database](https://leetcode.com/problemset/database/)
- [ ] **Postgresql** — `databases/postgresql-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/postgresql-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/postgresql-interview` ≥80%
  - [ ] 🏋️ Практика: запросы на [SQLZoo](https://sqlzoo.net/) + [sql-ex.ru](https://www.sql-ex.ru/) + раздел [LeetCode Database](https://leetcode.com/problemset/database/)
- [ ] **Database transactions** — `databases/database-transactions-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/database-transactions-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/database-transactions-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.postgresql.org/docs/current/tutorial-transactions.html) · [видео ▶](https://www.youtube.com/results?search_query=Database+transactions+explained) — каналы: Hussein Nasser
- [ ] **Database replication** — `databases/database-replication-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/database-replication-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/database-replication-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.postgresql.org/docs/current/high-availability.html) · [видео ▶](https://www.youtube.com/results?search_query=Database+replication+explained) — каналы: Hussein Nasser
- [ ] **Database sharding** — `databases/database-sharding-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/database-sharding-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/database-sharding-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.mongodb.com/docs/manual/sharding/) · [видео ▶](https://www.youtube.com/results?search_query=Database+sharding+explained) — каналы: Hussein Nasser
- [ ] **Hibernate** — `databases/hibernate-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/hibernate-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/hibernate-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://hibernate.org/orm/documentation/) · [видео ▶](https://www.youtube.com/results?search_query=Hibernate+explained) — каналы: Hussein Nasser
- [ ] **Hibernate relationships** — `databases/hibernate-relationships-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/hibernate-relationships-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/hibernate-relationships-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://hibernate.org/orm/documentation/) · [видео ▶](https://www.youtube.com/results?search_query=Hibernate+relationships+explained) — каналы: Hussein Nasser
- [ ] **Hibernate jpql criteria** — `databases/hibernate-jpql-criteria-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/hibernate-jpql-criteria-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/hibernate-jpql-criteria-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://hibernate.org/orm/documentation/) · [видео ▶](https://www.youtube.com/results?search_query=Hibernate+jpql+criteria+explained) — каналы: Hussein Nasser
- [ ] **Hibernate caching** — `databases/hibernate-caching-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/hibernate-caching-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/hibernate-caching-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://hibernate.org/orm/documentation/) · [видео ▶](https://www.youtube.com/results?search_query=Hibernate+caching+explained) — каналы: Hussein Nasser
- [ ] **Flyway liquibase** — `databases/flyway-liquibase-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/flyway-liquibase-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/flyway-liquibase-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://documentation.red-gate.com/flyway/) · [видео ▶](https://www.youtube.com/results?search_query=Flyway+liquibase+explained) — каналы: Hussein Nasser
- [ ] **Redis** — `databases/redis-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/redis-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/redis-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://redis.io/docs/latest/) · [видео ▶](https://www.youtube.com/results?search_query=Redis+explained) — каналы: Hussein Nasser
- [ ] **Mongodb** — `databases/mongodb-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/mongodb-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/mongodb-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.mongodb.com/docs/manual/) · [видео ▶](https://www.youtube.com/results?search_query=Mongodb+explained) — каналы: Hussein Nasser
- [ ] **Elasticsearch** — `databases/elasticsearch-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/elasticsearch-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/elasticsearch-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html) · [видео ▶](https://www.youtube.com/results?search_query=Elasticsearch+explained) — каналы: Hussein Nasser
- [ ] **Cassandra** — `databases/cassandra-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/cassandra-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/cassandra-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://cassandra.apache.org/doc/latest/) · [видео ▶](https://www.youtube.com/results?search_query=Cassandra+explained) — каналы: Hussein Nasser
- [ ] **Scylladb** — `databases/scylladb-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/scylladb-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/scylladb-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.scylladb.com/) · [видео ▶](https://www.youtube.com/results?search_query=Scylladb+explained) — каналы: Hussein Nasser
- [ ] **Clickhouse** — `databases/clickhouse-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/clickhouse-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/clickhouse-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://clickhouse.com/docs) · [видео ▶](https://www.youtube.com/results?search_query=Clickhouse+explained) — каналы: Hussein Nasser
- [ ] **Cockroachdb** — `databases/cockroachdb-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/cockroachdb-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/cockroachdb-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.cockroachlabs.com/docs/) · [видео ▶](https://www.youtube.com/results?search_query=Cockroachdb+explained) — каналы: Hussein Nasser
- [ ] **Dynamodb** — `databases/dynamodb-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/dynamodb-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/dynamodb-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html) · [видео ▶](https://www.youtube.com/results?search_query=Dynamodb+explained) — каналы: Hussein Nasser
- [ ] **Neo4j** — `databases/neo4j-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/databases/neo4j-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=databases/neo4j-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://neo4j.com/docs/) · [видео ▶](https://www.youtube.com/results?search_query=Neo4j+explained) — каналы: Hussein Nasser

<a id="8"></a>
## 8. Брокеры сообщений и стриминг
*`cheatsheets/interview/messaging/` — 7 тем*

- [ ] **Message brokers comparison** — `messaging/message-brokers-comparison-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/messaging/message-brokers-comparison-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=messaging/message-brokers-comparison-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.confluent.io/learn/message-queue/) · [видео ▶](https://www.youtube.com/results?search_query=Message+brokers+comparison+explained) — каналы: Confluent, Hussein Nasser
- [ ] **Kafka** — `messaging/kafka-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/messaging/kafka-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=messaging/kafka-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://kafka.apache.org/documentation/) · [видео ▶](https://www.youtube.com/results?search_query=Kafka+explained) — каналы: Confluent, Hussein Nasser
- [ ] **Rabbitmq** — `messaging/rabbitmq-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/messaging/rabbitmq-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=messaging/rabbitmq-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.rabbitmq.com/docs) · [видео ▶](https://www.youtube.com/results?search_query=Rabbitmq+explained) — каналы: Confluent, Hussein Nasser
- [ ] **AWS SQS / SNS** — `messaging/aws-sqs-sns-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/messaging/aws-sqs-sns-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=messaging/aws-sqs-sns-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.aws.amazon.com/sqs/) · [видео ▶](https://www.youtube.com/results?search_query=AWS+SQS+%2F+SNS+explained) — каналы: Confluent, Hussein Nasser
- [ ] **NATS** — `messaging/nats-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/messaging/nats-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=messaging/nats-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.nats.io/) · [видео ▶](https://www.youtube.com/results?search_query=NATS+explained) — каналы: Confluent, Hussein Nasser
- [ ] **Pulsar** — `messaging/pulsar-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/messaging/pulsar-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=messaging/pulsar-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://pulsar.apache.org/docs/) · [видео ▶](https://www.youtube.com/results?search_query=Pulsar+explained) — каналы: Confluent, Hussein Nasser
- [ ] **Redpanda** — `messaging/redpanda-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/messaging/redpanda-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=messaging/redpanda-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.redpanda.com/) · [видео ▶](https://www.youtube.com/results?search_query=Redpanda+explained) — каналы: Confluent, Hussein Nasser

<a id="9"></a>
## 9. API
*`cheatsheets/interview/api/` — 8 тем*

- [ ] **Http rest** — `api/http-rest-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/api/http-rest-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=api/http-rest-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://developer.mozilla.org/en-US/docs/Web/HTTP) · [видео ▶](https://www.youtube.com/results?search_query=Http+rest+explained) — каналы: Hussein Nasser
- [ ] **REST Maturity (Richardson)** — `api/rest-maturity-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/api/rest-maturity-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=api/rest-maturity-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/microsoft/api-guidelines) · [видео ▶](https://www.youtube.com/results?search_query=REST+Maturity+%28Richardson%29+explained) — каналы: Hussein Nasser
- [ ] **Api design best practices** — `api/api-design-best-practices-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/api/api-design-best-practices-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=api/api-design-best-practices-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/microsoft/api-guidelines) · [видео ▶](https://www.youtube.com/results?search_query=Api+design+best+practices+explained) — каналы: Hussein Nasser
- [ ] **Api versioning** — `api/api-versioning-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/api/api-versioning-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=api/api-versioning-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/microsoft/api-guidelines) · [видео ▶](https://www.youtube.com/results?search_query=Api+versioning+explained) — каналы: Hussein Nasser
- [ ] **OpenAPI / Swagger** — `api/openapi-swagger-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/api/openapi-swagger-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=api/openapi-swagger-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://swagger.io/docs/specification/v3_0/about/) · [видео ▶](https://www.youtube.com/results?search_query=OpenAPI+%2F+Swagger+explained) — каналы: Hussein Nasser
- [ ] **gRPC** — `api/grpc-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/api/grpc-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=api/grpc-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://grpc.io/docs/) · [видео ▶](https://www.youtube.com/results?search_query=gRPC+explained) — каналы: Hussein Nasser
- [ ] **GraphQL** — `api/graphql-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/api/graphql-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=api/graphql-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://graphql.org/learn/) · [видео ▶](https://www.youtube.com/results?search_query=GraphQL+explained) — каналы: Hussein Nasser
- [ ] **Websocket** — `api/websocket-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/api/websocket-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=api/websocket-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API) · [видео ▶](https://www.youtube.com/results?search_query=Websocket+explained) — каналы: Hussein Nasser

<a id="10"></a>
## 10. Архитектура и паттерны
*`cheatsheets/interview/architecture/` — 24 темы*

- [ ] **Distributed systems** — `architecture/distributed-systems-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/distributed-systems-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/distributed-systems-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://microservices.io/patterns/) · [видео ▶](https://www.youtube.com/results?search_query=Distributed+systems+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Networking** — `architecture/networking-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/networking-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/networking-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.cloudflare.com/learning/network-layer/how-does-the-internet-work/) · [видео ▶](https://www.youtube.com/results?search_query=Networking+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **DNS** — `architecture/dns-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/dns-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/dns-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.cloudflare.com/learning/dns/what-is-dns/) · [видео ▶](https://www.youtube.com/results?search_query=DNS+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Latency numbers** — `architecture/latency-numbers-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/latency-numbers-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/latency-numbers-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://gist.github.com/jboner/2841832) · [видео ▶](https://www.youtube.com/results?search_query=Latency+numbers+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Load balancing** — `architecture/load-balancing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/load-balancing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/load-balancing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.cloudflare.com/learning/performance/what-is-load-balancing/) · [видео ▶](https://www.youtube.com/results?search_query=Load+balancing+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Reverse proxy** — `architecture/reverse-proxy-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/reverse-proxy-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/reverse-proxy-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://microservices.io/patterns/) · [видео ▶](https://www.youtube.com/results?search_query=Reverse+proxy+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Caching strategies** — `architecture/caching-strategies-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/caching-strategies-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/caching-strategies-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://aws.amazon.com/caching/best-practices/) · [видео ▶](https://www.youtube.com/results?search_query=Caching+strategies+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **CDN** — `architecture/cdn-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/cdn-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/cdn-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://developer.mozilla.org/en-US/docs/Glossary/CDN) · [видео ▶](https://www.youtube.com/results?search_query=CDN+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Scalability patterns** — `architecture/scalability-patterns-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/scalability-patterns-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/scalability-patterns-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://microservices.io/patterns/) · [видео ▶](https://www.youtube.com/results?search_query=Scalability+patterns+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Cap theorem** — `architecture/cap-theorem-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/cap-theorem-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/cap-theorem-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://microservices.io/patterns/) · [видео ▶](https://www.youtube.com/results?search_query=Cap+theorem+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Consistency patterns** — `architecture/consistency-patterns-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/consistency-patterns-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/consistency-patterns-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://microservices.io/patterns/) · [видео ▶](https://www.youtube.com/results?search_query=Consistency+patterns+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Microservices** — `architecture/microservices-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/microservices-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/microservices-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://microservices.io/) · [видео ▶](https://www.youtube.com/results?search_query=Microservices+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Service discovery** — `architecture/service-discovery-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/service-discovery-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/service-discovery-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://microservices.io/patterns/) · [видео ▶](https://www.youtube.com/results?search_query=Service+discovery+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Api gateway** — `architecture/api-gateway-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/api-gateway-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/api-gateway-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://microservices.io/patterns/) · [видео ▶](https://www.youtube.com/results?search_query=Api+gateway+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **BFF Pattern** — `architecture/bff-pattern-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/bff-pattern-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/bff-pattern-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://microservices.io/patterns/) · [видео ▶](https://www.youtube.com/results?search_query=BFF+Pattern+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Resilience patterns** — `architecture/resilience-patterns-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/resilience-patterns-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/resilience-patterns-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://resilience4j.readme.io/docs) · [видео ▶](https://www.youtube.com/results?search_query=Resilience+patterns+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Event driven patterns** — `architecture/event-driven-patterns-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/event-driven-patterns-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/event-driven-patterns-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://microservices.io/patterns/) · [видео ▶](https://www.youtube.com/results?search_query=Event+driven+patterns+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Saga pattern** — `architecture/saga-pattern-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/saga-pattern-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/saga-pattern-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://microservices.io/patterns/data/saga.html) · [видео ▶](https://www.youtube.com/results?search_query=Saga+pattern+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **CQRS / Event Sourcing** — `architecture/cqrs-event-sourcing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/cqrs-event-sourcing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/cqrs-event-sourcing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://martinfowler.com/bliki/CQRS.html) · [видео ▶](https://www.youtube.com/results?search_query=CQRS+%2F+Event+Sourcing+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **DDD** — `architecture/ddd-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/ddd-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/ddd-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://martinfowler.com/tags/domain%20driven%20design.html) · [видео ▶](https://www.youtube.com/results?search_query=DDD+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Clean architecture** — `architecture/clean-architecture-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/clean-architecture-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/clean-architecture-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) · [видео ▶](https://www.youtube.com/results?search_query=Clean+architecture+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Hexagonal architecture** — `architecture/hexagonal-architecture-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/hexagonal-architecture-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/hexagonal-architecture-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://alistair.cockburn.us/hexagonal-architecture/) · [видео ▶](https://www.youtube.com/results?search_query=Hexagonal+architecture+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Strangler fig** — `architecture/strangler-fig-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/strangler-fig-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/strangler-fig-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://microservices.io/patterns/) · [видео ▶](https://www.youtube.com/results?search_query=Strangler+fig+explained) — каналы: ByteByteGo, Hussein Nasser
- [ ] **Edge computing** — `architecture/edge-computing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/architecture/edge-computing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=architecture/edge-computing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://microservices.io/patterns/) · [видео ▶](https://www.youtube.com/results?search_query=Edge+computing+explained) — каналы: ByteByteGo, Hussein Nasser

<a id="11"></a>
## 11. System Design (проектирование систем)
*`cheatsheets/interview/system-design/` — 22 темы*

- [ ] **System design** 🔴 — `system-design/system-design-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/system-design-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/system-design-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=System+design+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design url shortener** — `system-design/design-url-shortener-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-url-shortener-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-url-shortener-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+url+shortener+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design pastebin** — `system-design/design-pastebin-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-pastebin-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-pastebin-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+pastebin+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design rate limiter** — `system-design/design-rate-limiter-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-rate-limiter-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-rate-limiter-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+rate+limiter+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design key value store** — `system-design/design-key-value-store-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-key-value-store-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-key-value-store-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+key+value+store+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design typeahead** — `system-design/design-typeahead-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-typeahead-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-typeahead-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+typeahead+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design twitter** — `system-design/design-twitter-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-twitter-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-twitter-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+twitter+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design instagram** — `system-design/design-instagram-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-instagram-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-instagram-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+instagram+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design feed system** — `system-design/design-feed-system-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-feed-system-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-feed-system-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+feed+system+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design chat system** — `system-design/design-chat-system-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-chat-system-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-chat-system-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+chat+system+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design web crawler** — `system-design/design-web-crawler-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-web-crawler-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-web-crawler-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+web+crawler+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design search** — `system-design/design-search-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-search-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-search-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+search+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design youtube** — `system-design/design-youtube-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-youtube-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-youtube-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+youtube+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design netflix** — `system-design/design-netflix-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-netflix-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-netflix-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+netflix+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design dropbox** — `system-design/design-dropbox-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-dropbox-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-dropbox-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+dropbox+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design uber** — `system-design/design-uber-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-uber-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-uber-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+uber+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design google maps** — `system-design/design-google-maps-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-google-maps-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-google-maps-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+google+maps+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design ecommerce delivery** — `system-design/design-ecommerce-delivery-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-ecommerce-delivery-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-ecommerce-delivery-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+ecommerce+delivery+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design payment system** — `system-design/design-payment-system-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-payment-system-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-payment-system-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+payment+system+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design parking lot oo** — `system-design/design-parking-lot-oo-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-parking-lot-oo-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-parking-lot-oo-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+parking+lot+oo+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design vending machine oo** — `system-design/design-vending-machine-oo-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-vending-machine-oo-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-vending-machine-oo-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+vending+machine+oo+explained) — каналы: ByteByteGo, Gaurav Sen
- [ ] **Design elevator oo** — `system-design/design-elevator-oo-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/system-design/design-elevator-oo-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=system-design/design-elevator-oo-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://github.com/donnemartin/system-design-primer) · [видео ▶](https://www.youtube.com/results?search_query=Design+elevator+oo+explained) — каналы: ByteByteGo, Gaurav Sen

<a id="12"></a>
## 12. Data Engineering
*`cheatsheets/interview/data-engineering/` — 8 тем*

- [ ] **Data warehousing** — `data-engineering/data-warehousing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/data-engineering/data-warehousing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=data-engineering/data-warehousing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.snowflake.com/guides/what-data-warehouse/) · [видео ▶](https://www.youtube.com/results?search_query=Data+warehousing+explained) — каналы: Confluent, Andreas Kretz
- [ ] **Data lake lakehouse** — `data-engineering/data-lake-lakehouse-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/data-engineering/data-lake-lakehouse-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=data-engineering/data-lake-lakehouse-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.databricks.com/glossary/data-lakehouse) · [видео ▶](https://www.youtube.com/results?search_query=Data+lake+lakehouse+explained) — каналы: Confluent, Andreas Kretz
- [ ] **Apache spark** — `data-engineering/apache-spark-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/data-engineering/apache-spark-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=data-engineering/apache-spark-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://spark.apache.org/docs/latest/) · [видео ▶](https://www.youtube.com/results?search_query=Apache+spark+explained) — каналы: Confluent, Andreas Kretz
- [ ] **Apache flink** — `data-engineering/apache-flink-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/data-engineering/apache-flink-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=data-engineering/apache-flink-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://nightlies.apache.org/flink/flink-docs-stable/) · [видео ▶](https://www.youtube.com/results?search_query=Apache+flink+explained) — каналы: Confluent, Andreas Kretz
- [ ] **Stream processing** — `data-engineering/stream-processing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/data-engineering/stream-processing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=data-engineering/stream-processing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.confluent.io/learn/stream-processing/) · [видео ▶](https://www.youtube.com/results?search_query=Stream+processing+explained) — каналы: Confluent, Andreas Kretz
- [ ] **Kafka streams** 🔴 — `data-engineering/kafka-streams-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/data-engineering/kafka-streams-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=data-engineering/kafka-streams-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://kafka.apache.org/documentation/streams/) · [видео ▶](https://www.youtube.com/results?search_query=Kafka+streams+explained) — каналы: Confluent, Andreas Kretz
- [ ] **Apache airflow** — `data-engineering/apache-airflow-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/data-engineering/apache-airflow-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=data-engineering/apache-airflow-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://airflow.apache.org/docs/) · [видео ▶](https://www.youtube.com/results?search_query=Apache+airflow+explained) — каналы: Confluent, Andreas Kretz
- [ ] **dbt** — `data-engineering/dbt-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/data-engineering/dbt-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=data-engineering/dbt-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.getdbt.com/docs/introduction) · [видео ▶](https://www.youtube.com/results?search_query=dbt+explained) — каналы: Confluent, Andreas Kretz

<a id="13"></a>
## 13. Облака
*`cheatsheets/interview/cloud/` — 6 тем*

- [ ] **Cloud native patterns** — `cloud/cloud-native-patterns-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/cloud/cloud-native-patterns-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=cloud/cloud-native-patterns-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://learn.microsoft.com/azure/architecture/) · [видео ▶](https://www.youtube.com/results?search_query=Cloud+native+patterns+explained) — каналы: TechWorld with Nana, freeCodeCamp
- [ ] **AWS** — `cloud/aws-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/cloud/aws-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=cloud/aws-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.aws.amazon.com/) · [видео ▶](https://www.youtube.com/results?search_query=AWS+explained) — каналы: TechWorld with Nana, freeCodeCamp
- [ ] **Azure** — `cloud/azure-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/cloud/azure-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=cloud/azure-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://learn.microsoft.com/azure/) · [видео ▶](https://www.youtube.com/results?search_query=Azure+explained) — каналы: TechWorld with Nana, freeCodeCamp
- [ ] **GCP** — `cloud/gcp-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/cloud/gcp-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=cloud/gcp-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://cloud.google.com/docs) · [видео ▶](https://www.youtube.com/results?search_query=GCP+explained) — каналы: TechWorld with Nana, freeCodeCamp
- [ ] **Serverless** — `cloud/serverless-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/cloud/serverless-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=cloud/serverless-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://martinfowler.com/articles/serverless.html) · [видео ▶](https://www.youtube.com/results?search_query=Serverless+explained) — каналы: TechWorld with Nana, freeCodeCamp
- [ ] **AWS Lambda** — `cloud/aws-lambda-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/cloud/aws-lambda-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=cloud/aws-lambda-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.aws.amazon.com/lambda/latest/dg/welcome.html) · [видео ▶](https://www.youtube.com/results?search_query=AWS+Lambda+explained) — каналы: TechWorld with Nana, freeCodeCamp

<a id="14"></a>
## 14. DevOps и контейнеры
*`cheatsheets/interview/devops/` — 13 тем*

- [ ] **Linux** — `devops/linux-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/devops/linux-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=devops/linux-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://linuxjourney.com/) · [видео ▶](https://www.youtube.com/results?search_query=Linux+explained) — каналы: TechWorld with Nana
- [ ] **Git** — `devops/git-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/devops/git-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=devops/git-interview` ≥80%
  - [ ] 🏋️ Практика: [Learn Git Branching](https://learngitbranching.js.org/?locale=ru_RU): пройти rebase / cherry-pick / bisect / reflog
- [ ] **Docker** — `devops/docker-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/devops/docker-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=devops/docker-interview` ≥80%
  - [ ] 🏋️ Практика: [Play with Docker](https://www.docker.com/play-with-docker/): собрать multi-stage образ, прогнать compose
- [ ] **Kubernetes** 🔴 — `devops/kubernetes-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/devops/kubernetes-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=devops/kubernetes-interview` ≥80%
  - [ ] 🏋️ Практика: [Play with Kubernetes](https://labs.play-with-k8s.com/) (или kind/minikube): развернуть 5 манифестов, отладить упавший pod
- [ ] **Helm** — `devops/helm-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/devops/helm-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=devops/helm-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://helm.sh/docs/) · [видео ▶](https://www.youtube.com/results?search_query=Helm+explained) — каналы: TechWorld with Nana
- [ ] **Argocd** — `devops/argocd-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/devops/argocd-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=devops/argocd-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://argo-cd.readthedocs.io/) · [видео ▶](https://www.youtube.com/results?search_query=Argocd+explained) — каналы: TechWorld with Nana
- [ ] **Terraform** — `devops/terraform-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/devops/terraform-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=devops/terraform-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://developer.hashicorp.com/terraform/docs) · [видео ▶](https://www.youtube.com/results?search_query=Terraform+explained) — каналы: TechWorld with Nana
- [ ] **Ansible** — `devops/ansible-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/devops/ansible-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=devops/ansible-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.ansible.com/) · [видео ▶](https://www.youtube.com/results?search_query=Ansible+explained) — каналы: TechWorld with Nana
- [ ] **Gradle maven** — `devops/gradle-maven-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/devops/gradle-maven-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=devops/gradle-maven-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.gradle.org/current/userguide/userguide.html) · [видео ▶](https://www.youtube.com/results?search_query=Gradle+maven+explained) — каналы: TechWorld with Nana
- [ ] **Vault** — `devops/vault-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/devops/vault-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=devops/vault-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://developer.hashicorp.com/vault/docs) · [видео ▶](https://www.youtube.com/results?search_query=Vault+explained) — каналы: TechWorld with Nana
- [ ] **Consul** — `devops/consul-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/devops/consul-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=devops/consul-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://developer.hashicorp.com/consul/docs) · [видео ▶](https://www.youtube.com/results?search_query=Consul+explained) — каналы: TechWorld with Nana
- [ ] **Istio service mesh** — `devops/istio-service-mesh-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/devops/istio-service-mesh-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=devops/istio-service-mesh-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://istio.io/latest/docs/) · [видео ▶](https://www.youtube.com/results?search_query=Istio+service+mesh+explained) — каналы: TechWorld with Nana
- [ ] **Linkerd** — `devops/linkerd-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/devops/linkerd-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=devops/linkerd-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://linkerd.io/docs/) · [видео ▶](https://www.youtube.com/results?search_query=Linkerd+explained) — каналы: TechWorld with Nana

<a id="15"></a>
## 15. CI/CD
*`cheatsheets/interview/cicd/` — 2 темы*

- [ ] **Pipeline design** — `cicd/pipeline-design-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/cicd/pipeline-design-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=cicd/pipeline-design-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://martinfowler.com/articles/continuousIntegration.html) · [видео ▶](https://www.youtube.com/results?search_query=Pipeline+design+explained) — каналы: TechWorld with Nana
- [ ] **Deployment strategies** — `cicd/deployment-strategies-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/cicd/deployment-strategies-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=cicd/deployment-strategies-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://martinfowler.com/bliki/BlueGreenDeployment.html) · [видео ▶](https://www.youtube.com/results?search_query=Deployment+strategies+explained) — каналы: TechWorld with Nana

<a id="16"></a>
## 16. Наблюдаемость (метрики и трейсинг)
*`cheatsheets/interview/monitoring/` — 9 тем*

- [ ] **Observability** — `monitoring/observability-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/monitoring/observability-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=monitoring/observability-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://opentelemetry.io/docs/concepts/observability-primer/) · [видео ▶](https://www.youtube.com/results?search_query=Observability+explained) — каналы: TechWorld with Nana, Grafana
- [ ] **Metrics tracing** — `monitoring/metrics-tracing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/monitoring/metrics-tracing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=monitoring/metrics-tracing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://opentelemetry.io/docs/concepts/signals/) · [видео ▶](https://www.youtube.com/results?search_query=Metrics+tracing+explained) — каналы: TechWorld with Nana, Grafana
- [ ] **Prometheus / Grafana** — `monitoring/prometheus-grafana-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/monitoring/prometheus-grafana-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=monitoring/prometheus-grafana-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://prometheus.io/docs/introduction/overview/) · [видео ▶](https://www.youtube.com/results?search_query=Prometheus+%2F+Grafana+explained) — каналы: TechWorld with Nana, Grafana
- [ ] **Micrometer** — `monitoring/micrometer-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/monitoring/micrometer-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=monitoring/micrometer-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.micrometer.io/micrometer/reference/) · [видео ▶](https://www.youtube.com/results?search_query=Micrometer+explained) — каналы: TechWorld with Nana, Grafana
- [ ] **Opentelemetry** — `monitoring/opentelemetry-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/monitoring/opentelemetry-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=monitoring/opentelemetry-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://opentelemetry.io/docs/) · [видео ▶](https://www.youtube.com/results?search_query=Opentelemetry+explained) — каналы: TechWorld with Nana, Grafana
- [ ] **Jaeger / Zipkin** — `monitoring/jaeger-zipkin-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/monitoring/jaeger-zipkin-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=monitoring/jaeger-zipkin-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.jaegertracing.io/docs/latest/) · [видео ▶](https://www.youtube.com/results?search_query=Jaeger+%2F+Zipkin+explained) — каналы: TechWorld with Nana, Grafana
- [ ] **Loki / Grafana** — `monitoring/loki-grafana-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/monitoring/loki-grafana-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=monitoring/loki-grafana-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://grafana.com/docs/loki/latest/) · [видео ▶](https://www.youtube.com/results?search_query=Loki+%2F+Grafana+explained) — каналы: TechWorld with Nana, Grafana
- [ ] **ELK Stack** — `monitoring/elk-stack-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/monitoring/elk-stack-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=monitoring/elk-stack-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.elastic.co/guide/index.html) · [видео ▶](https://www.youtube.com/results?search_query=ELK+Stack+explained) — каналы: TechWorld with Nana, Grafana
- [ ] **Logging strategies** — `monitoring/logging-strategies-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/monitoring/logging-strategies-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=monitoring/logging-strategies-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://12factor.net/logs) · [видео ▶](https://www.youtube.com/results?search_query=Logging+strategies+explained) — каналы: TechWorld with Nana, Grafana

<a id="17"></a>
## 17. Логирование
*`cheatsheets/interview/logging/` — 1 тема*

- [ ] **Logging** — `logging/logging-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/logging/logging-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=logging/logging-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://12factor.net/logs) · [видео ▶](https://www.youtube.com/results?search_query=Logging+explained) — каналы: TechWorld with Nana

<a id="18"></a>
## 18. Безопасность
*`cheatsheets/interview/security/` — 10 тем*

- [ ] **Application security** — `security/application-security-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/security/application-security-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=security/application-security-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://owasp.org/www-project-top-ten/) · [видео ▶](https://www.youtube.com/results?search_query=Application+security+explained) — каналы: OWASP, PwnFunction
- [ ] **OWASP Top 10** — `security/owasp-top10-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/security/owasp-top10-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=security/owasp-top10-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://owasp.org/www-project-top-ten/) · [видео ▶](https://www.youtube.com/results?search_query=OWASP+Top+10+explained) — каналы: OWASP, PwnFunction
- [ ] **Authentication authorization patterns** — `security/authentication-authorization-patterns-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/security/authentication-authorization-patterns-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=security/authentication-authorization-patterns-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.okta.com/identity-101/authentication-vs-authorization/) · [видео ▶](https://www.youtube.com/results?search_query=Authentication+authorization+patterns+explained) — каналы: OWASP, PwnFunction
- [ ] **OAuth2** — `security/oauth2-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/security/oauth2-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=security/oauth2-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://oauth.net/2/) · [видео ▶](https://www.youtube.com/results?search_query=OAuth2+explained) — каналы: OWASP, PwnFunction
- [ ] **JWT** — `security/jwt-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/security/jwt-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=security/jwt-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://jwt.io/introduction) · [видео ▶](https://www.youtube.com/results?search_query=JWT+explained) — каналы: OWASP, PwnFunction
- [ ] **TLS/SSL** — `security/tls-ssl-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/security/tls-ssl-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=security/tls-ssl-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.cloudflare.com/learning/ssl/what-is-ssl/) · [видео ▶](https://www.youtube.com/results?search_query=TLS%2FSSL+explained) — каналы: OWASP, PwnFunction
- [ ] **mTLS** — `security/mtls-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/security/mtls-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=security/mtls-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.cloudflare.com/learning/access-management/what-is-mutual-tls/) · [видео ▶](https://www.youtube.com/results?search_query=mTLS+explained) — каналы: OWASP, PwnFunction
- [ ] **Secrets management** — `security/secrets-management-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/security/secrets-management-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=security/secrets-management-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://developer.hashicorp.com/vault/docs) · [видео ▶](https://www.youtube.com/results?search_query=Secrets+management+explained) — каналы: OWASP, PwnFunction
- [ ] **Zero trust** — `security/zero-trust-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/security/zero-trust-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=security/zero-trust-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.cloudflare.com/learning/security/glossary/what-is-zero-trust/) · [видео ▶](https://www.youtube.com/results?search_query=Zero+trust+explained) — каналы: OWASP, PwnFunction
- [ ] **Supply chain security** — `security/supply-chain-security-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/security/supply-chain-security-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=security/supply-chain-security-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://slsa.dev/) · [видео ▶](https://www.youtube.com/results?search_query=Supply+chain+security+explained) — каналы: OWASP, PwnFunction

<a id="19"></a>
## 19. Тестирование
*`cheatsheets/interview/testing/` — 14 тем*

- [ ] **Test strategies** — `testing/test-strategies-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/test-strategies-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/test-strategies-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://martinfowler.com/articles/practical-test-pyramid.html) · [видео ▶](https://www.youtube.com/results?search_query=Test+strategies+explained) — каналы: Java Brains, Amigoscode
- [ ] **Unit testing** — `testing/unit-testing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/unit-testing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/unit-testing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://martinfowler.com/bliki/UnitTest.html) · [видео ▶](https://www.youtube.com/results?search_query=Unit+testing+explained) — каналы: Java Brains, Amigoscode
- [ ] **Junit** — `testing/junit-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/junit-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/junit-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://junit.org/junit5/docs/current/user-guide/) · [видео ▶](https://www.youtube.com/results?search_query=Junit+explained) — каналы: Java Brains, Amigoscode
- [ ] **Mockito** — `testing/mockito-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/mockito-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/mockito-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://site.mockito.org/) · [видео ▶](https://www.youtube.com/results?search_query=Mockito+explained) — каналы: Java Brains, Amigoscode
- [ ] **Integration testing** — `testing/integration-testing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/integration-testing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/integration-testing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://martinfowler.com/testing/) · [видео ▶](https://www.youtube.com/results?search_query=Integration+testing+explained) — каналы: Java Brains, Amigoscode
- [ ] **Testcontainers** — `testing/testcontainers-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/testcontainers-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/testcontainers-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://testcontainers.com/guides/) · [видео ▶](https://www.youtube.com/results?search_query=Testcontainers+explained) — каналы: Java Brains, Amigoscode
- [ ] **Rest assured** — `testing/rest-assured-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/rest-assured-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/rest-assured-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://rest-assured.io/) · [видео ▶](https://www.youtube.com/results?search_query=Rest+assured+explained) — каналы: Java Brains, Amigoscode
- [ ] **Contract testing** — `testing/contract-testing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/contract-testing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/contract-testing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.pact.io/) · [видео ▶](https://www.youtube.com/results?search_query=Contract+testing+explained) — каналы: Java Brains, Amigoscode
- [ ] **Test automation** — `testing/test-automation-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/test-automation-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/test-automation-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://martinfowler.com/testing/) · [видео ▶](https://www.youtube.com/results?search_query=Test+automation+explained) — каналы: Java Brains, Amigoscode
- [ ] **Selenium** — `testing/selenium-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/selenium-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/selenium-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.selenium.dev/documentation/) · [видео ▶](https://www.youtube.com/results?search_query=Selenium+explained) — каналы: Java Brains, Amigoscode
- [ ] **Load testing** — `testing/load-testing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/load-testing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/load-testing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.gatling.io/) · [видео ▶](https://www.youtube.com/results?search_query=Load+testing+explained) — каналы: Java Brains, Amigoscode
- [ ] **Property based testing** — `testing/property-based-testing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/property-based-testing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/property-based-testing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://jqwik.net/docs/current/user-guide.html) · [видео ▶](https://www.youtube.com/results?search_query=Property+based+testing+explained) — каналы: Java Brains, Amigoscode
- [ ] **Mutation testing** — `testing/mutation-testing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/mutation-testing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/mutation-testing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://pitest.org/) · [видео ▶](https://www.youtube.com/results?search_query=Mutation+testing+explained) — каналы: Java Brains, Amigoscode
- [ ] **Chaos engineering** — `testing/chaos-engineering-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/testing/chaos-engineering-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=testing/chaos-engineering-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://principlesofchaos.org/) · [видео ▶](https://www.youtube.com/results?search_query=Chaos+engineering+explained) — каналы: Java Brains, Amigoscode

<a id="20"></a>
## 20. Качество кода
*`cheatsheets/interview/code-quality/` — 7 тем*

- [ ] **Clean code practices** — `code-quality/clean-code-practices-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/code-quality/clean-code-practices-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=code-quality/clean-code-practices-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://refactoring.guru/refactoring) · [видео ▶](https://www.youtube.com/results?search_query=Clean+code+practices+explained) — каналы: ArjanCodes, CodeAesthetic
- [ ] **Code smells** — `code-quality/code-smells-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/code-quality/code-smells-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=code-quality/code-smells-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://refactoring.guru/refactoring/smells) · [видео ▶](https://www.youtube.com/results?search_query=Code+smells+explained) — каналы: ArjanCodes, CodeAesthetic
- [ ] **Refactoring patterns** — `code-quality/refactoring-patterns-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/code-quality/refactoring-patterns-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=code-quality/refactoring-patterns-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://refactoring.guru/refactoring/techniques) · [видео ▶](https://www.youtube.com/results?search_query=Refactoring+patterns+explained) — каналы: ArjanCodes, CodeAesthetic
- [ ] **Code review** — `code-quality/code-review-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/code-quality/code-review-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=code-quality/code-review-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://google.github.io/eng-practices/review/) · [видео ▶](https://www.youtube.com/results?search_query=Code+review+explained) — каналы: ArjanCodes, CodeAesthetic
- [ ] **Static analysis** — `code-quality/static-analysis-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/code-quality/static-analysis-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=code-quality/static-analysis-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.sonarsource.com/learn/) · [видео ▶](https://www.youtube.com/results?search_query=Static+analysis+explained) — каналы: ArjanCodes, CodeAesthetic
- [ ] **Code coverage** — `code-quality/code-coverage-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/code-quality/code-coverage-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=code-quality/code-coverage-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://martinfowler.com/bliki/TestCoverage.html) · [видео ▶](https://www.youtube.com/results?search_query=Code+coverage+explained) — каналы: ArjanCodes, CodeAesthetic
- [ ] **Technical debt** — `code-quality/technical-debt-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/code-quality/technical-debt-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=code-quality/technical-debt-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://martinfowler.com/bliki/TechnicalDebt.html) · [видео ▶](https://www.youtube.com/results?search_query=Technical+debt+explained) — каналы: ArjanCodes, CodeAesthetic

<a id="21"></a>
## 21. Паттерны проектирования
*`cheatsheets/interview/design-patterns/` — 1 тема*

- [ ] **Design patterns** — `design-patterns/design-patterns-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/design-patterns/design-patterns-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=design-patterns/design-patterns-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://refactoring.guru/design-patterns) · [видео ▶](https://www.youtube.com/results?search_query=Design+patterns+explained) — каналы: Christopher Okhravi

<a id="22"></a>
## 22. AI / LLM
*`cheatsheets/interview/ai-ml/` — 25 тем*

- [ ] **Llm basics** 🔴 — `ai-ml/llm-basics-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/llm-basics-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/llm-basics-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/learn/nlp-course/) · [видео ▶](https://www.youtube.com/results?search_query=Llm+basics+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Prompt engineering** — `ai-ml/prompt-engineering-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/prompt-engineering-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/prompt-engineering-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.promptingguide.ai/) · [видео ▶](https://www.youtube.com/results?search_query=Prompt+engineering+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Embeddings** — `ai-ml/embeddings-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/embeddings-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/embeddings-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/blog/getting-started-with-embeddings) · [видео ▶](https://www.youtube.com/results?search_query=Embeddings+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Vector databases** — `ai-ml/vector-databases-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/vector-databases-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/vector-databases-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.pinecone.io/learn/vector-database/) · [видео ▶](https://www.youtube.com/results?search_query=Vector+databases+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **RAG** — `ai-ml/rag-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/rag-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/rag-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.pinecone.io/learn/retrieval-augmented-generation/) · [видео ▶](https://www.youtube.com/results?search_query=RAG+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Function calling** — `ai-ml/function-calling-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/function-calling-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/function-calling-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://platform.openai.com/docs/guides/function-calling) · [видео ▶](https://www.youtube.com/results?search_query=Function+calling+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Llm integration patterns** — `ai-ml/llm-integration-patterns-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/llm-integration-patterns-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/llm-integration-patterns-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/learn) · [видео ▶](https://www.youtube.com/results?search_query=Llm+integration+patterns+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Long context vs rag** — `ai-ml/long-context-vs-rag-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/long-context-vs-rag-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/long-context-vs-rag-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/learn) · [видео ▶](https://www.youtube.com/results?search_query=Long+context+vs+rag+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Ai agents** — `ai-ml/ai-agents-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/ai-agents-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/ai-agents-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.anthropic.com/engineering/building-effective-agents) · [видео ▶](https://www.youtube.com/results?search_query=Ai+agents+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Agentic patterns** — `ai-ml/agentic-patterns-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/agentic-patterns-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/agentic-patterns-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.anthropic.com/engineering/building-effective-agents) · [видео ▶](https://www.youtube.com/results?search_query=Agentic+patterns+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Multi agent orchestration** — `ai-ml/multi-agent-orchestration-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/multi-agent-orchestration-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/multi-agent-orchestration-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://langchain-ai.github.io/langgraph/) · [видео ▶](https://www.youtube.com/results?search_query=Multi+agent+orchestration+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Code agents** — `ai-ml/code-agents-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/code-agents-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/code-agents-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/learn) · [видео ▶](https://www.youtube.com/results?search_query=Code+agents+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **MCP** — `ai-ml/mcp-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/mcp-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/mcp-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://modelcontextprotocol.io/docs) · [видео ▶](https://www.youtube.com/results?search_query=MCP+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Reasoning models** — `ai-ml/reasoning-models-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/reasoning-models-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/reasoning-models-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/learn) · [видео ▶](https://www.youtube.com/results?search_query=Reasoning+models+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Multimodal ai** — `ai-ml/multimodal-ai-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/multimodal-ai-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/multimodal-ai-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/learn) · [видео ▶](https://www.youtube.com/results?search_query=Multimodal+ai+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Fine tuning llm** — `ai-ml/fine-tuning-llm-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/fine-tuning-llm-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/fine-tuning-llm-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/learn/nlp-course/chapter3) · [видео ▶](https://www.youtube.com/results?search_query=Fine+tuning+llm+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Open source llms** — `ai-ml/open-source-llms-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/open-source-llms-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/open-source-llms-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/learn) · [видео ▶](https://www.youtube.com/results?search_query=Open+source+llms+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Model serving** — `ai-ml/model-serving-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/model-serving-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/model-serving-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://docs.vllm.ai/) · [видео ▶](https://www.youtube.com/results?search_query=Model+serving+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Inference optimization** — `ai-ml/inference-optimization-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/inference-optimization-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/inference-optimization-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/docs/transformers/llm_optims) · [видео ▶](https://www.youtube.com/results?search_query=Inference+optimization+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Llm evaluation** — `ai-ml/llm-evaluation-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/llm-evaluation-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/llm-evaluation-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/learn/cookbook/llm_judge) · [видео ▶](https://www.youtube.com/results?search_query=Llm+evaluation+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Ai application architecture** — `ai-ml/ai-application-architecture-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/ai-application-architecture-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/ai-application-architecture-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/learn) · [видео ▶](https://www.youtube.com/results?search_query=Ai+application+architecture+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Ai observability** — `ai-ml/ai-observability-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/ai-observability-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/ai-observability-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/learn) · [видео ▶](https://www.youtube.com/results?search_query=Ai+observability+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Ai safety guardrails** — `ai-ml/ai-safety-guardrails-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/ai-safety-guardrails-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/ai-safety-guardrails-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/learn) · [видео ▶](https://www.youtube.com/results?search_query=Ai+safety+guardrails+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **Ai compliance governance** — `ai-ml/ai-compliance-governance-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/ai-compliance-governance-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/ai-compliance-governance-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://huggingface.co/learn) · [видео ▶](https://www.youtube.com/results?search_query=Ai+compliance+governance+explained) — каналы: Andrej Karpathy, 3Blue1Brown
- [ ] **MLOps** — `ai-ml/mlops-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/ai-ml/mlops-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=ai-ml/mlops-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://ml-ops.org/) · [видео ▶](https://www.youtube.com/results?search_query=MLOps+explained) — каналы: Andrej Karpathy, 3Blue1Brown

<a id="23"></a>
## 23. Behavioral
*`cheatsheets/interview/behavioral/` — 6 тем*

- [ ] **Behavioral** — `behavioral/behavioral-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/behavioral/behavioral-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=behavioral/behavioral-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.techinterviewhandbook.org/behavioral-interview/) · [видео ▶](https://www.youtube.com/results?search_query=Behavioral+explained) — каналы: Jeff H Sipe, Exponent
- [ ] **STAR-метод** — `behavioral/star-method-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/behavioral/star-method-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=behavioral/star-method-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.techinterviewhandbook.org/behavioral-interview/) · [видео ▶](https://www.youtube.com/results?search_query=STAR-%D0%BC%D0%B5%D1%82%D0%BE%D0%B4+explained) — каналы: Jeff H Sipe, Exponent
- [ ] **Conflict stories** — `behavioral/conflict-stories-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/behavioral/conflict-stories-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=behavioral/conflict-stories-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.techinterviewhandbook.org/behavioral-interview/) · [видео ▶](https://www.youtube.com/results?search_query=Conflict+stories+explained) — каналы: Jeff H Sipe, Exponent
- [ ] **Failure stories** — `behavioral/failure-stories-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/behavioral/failure-stories-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=behavioral/failure-stories-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.techinterviewhandbook.org/behavioral-interview/) · [видео ▶](https://www.youtube.com/results?search_query=Failure+stories+explained) — каналы: Jeff H Sipe, Exponent
- [ ] **Leadership stories** — `behavioral/leadership-stories-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/behavioral/leadership-stories-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=behavioral/leadership-stories-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.techinterviewhandbook.org/behavioral-interview/) · [видео ▶](https://www.youtube.com/results?search_query=Leadership+stories+explained) — каналы: Jeff H Sipe, Exponent
- [ ] **Culture fit** — `behavioral/culture-fit-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/behavioral/culture-fit-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=behavioral/culture-fit-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.techinterviewhandbook.org/behavioral-interview/) · [видео ▶](https://www.youtube.com/results?search_query=Culture+fit+explained) — каналы: Jeff H Sipe, Exponent

<a id="24"></a>
## 24. Лидерство
*`cheatsheets/interview/leadership/` — 7 тем*

- [ ] **Team leadership** — `leadership/team-leadership-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/leadership/team-leadership-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=leadership/team-leadership-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://staffeng.com/guides/) · [видео ▶](https://www.youtube.com/results?search_query=Team+leadership+explained) — каналы: StaffEng, Engineers Codex
- [ ] **Mentoring** — `leadership/mentoring-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/leadership/mentoring-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=leadership/mentoring-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://staffeng.com/guides/) · [видео ▶](https://www.youtube.com/results?search_query=Mentoring+explained) — каналы: StaffEng, Engineers Codex
- [ ] **Technical decisions** — `leadership/technical-decisions-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/leadership/technical-decisions-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=leadership/technical-decisions-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://adr.github.io/) · [видео ▶](https://www.youtube.com/results?search_query=Technical+decisions+explained) — каналы: StaffEng, Engineers Codex
- [ ] **Estimations planning** — `leadership/estimations-planning-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/leadership/estimations-planning-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=leadership/estimations-planning-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://staffeng.com/guides/) · [видео ▶](https://www.youtube.com/results?search_query=Estimations+planning+explained) — каналы: StaffEng, Engineers Codex
- [ ] **Conflict resolution** — `leadership/conflict-resolution-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/leadership/conflict-resolution-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=leadership/conflict-resolution-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://staffeng.com/guides/) · [видео ▶](https://www.youtube.com/results?search_query=Conflict+resolution+explained) — каналы: StaffEng, Engineers Codex
- [ ] **Code review practices** — `leadership/code-review-practices-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/leadership/code-review-practices-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=leadership/code-review-practices-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://google.github.io/eng-practices/review/) · [видео ▶](https://www.youtube.com/results?search_query=Code+review+practices+explained) — каналы: StaffEng, Engineers Codex
- [ ] **Tech interviewing** — `leadership/tech-interviewing-interview`
  - [ ] 📖 Изучить: `cheatsheets/interview/leadership/tech-interviewing-interview.md`
  - [ ] 🧠 Тест: quiz-app `topic=leadership/tech-interviewing-interview` ≥80%
  - [ ] 🔎 Ресурсы: [доки](https://www.techinterviewhandbook.org/) · [видео ▶](https://www.youtube.com/results?search_query=Tech+interviewing+explained) — каналы: StaffEng, Engineers Codex

<a id="25"></a>
## 25. Подготовка к интервью (мета)
*`cheatsheets/interview/preparation/` — 1 тема*

- [ ] **Interview preparation** — `preparation/interview-preparation`
  - [ ] 📖 Изучить: `cheatsheets/interview/preparation/interview-preparation.md`
  - [ ] 🧠 Тест: _квиз ещё не сгенерирован_ (`topic=preparation/interview-preparation`)
  - [ ] 🔎 Ресурсы: [доки](https://www.techinterviewhandbook.org/) · [видео ▶](https://www.youtube.com/results?search_query=Interview+preparation+explained) — каналы: freeCodeCamp
