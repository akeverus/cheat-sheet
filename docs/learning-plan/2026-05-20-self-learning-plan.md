# План самообучения (2026-05 — 2026-11)

> Дата создания: 2026-05-20
> Диагностика: `diagnostic/` (8 файлов с evidence)
> Спека: `../superpowers/specs/2026-05-20-learning-plan-design.md`

## 1. Контекст

- **Уровень:** senior, пробелы не самоназваны → выявлены через диагностику.
- **Цель:** комбинированная — закрытие пробелов + ритм роста + готовность к интервью senior+.
- **Бюджет:** **1 час × 5 будних дней = 5 ч/неделю.**
- **Горизонт:** ~6 месяцев, 9 циклов в backlog + refresh-сессии.
- **Жёсткое правило:** 1 цикл = 1 неделя = 5 часов = 1 тема. Не уложился — урезаем тему, а не растягиваем.

## 2. Диагностика

### Источники сигналов (snapshot: 2026-05-20)

| # | Источник | Что взято | Файл |
|---|---|---|---|
| 1 | `interview/TOC.md` | 25 категорий + 9 подкатегорий | `diagnostic/01-toc-categories.tsv` |
| 2 | `interview/*.md` | размеры (268 файлов) | `diagnostic/02-interview-sizes.tsv` |
| 3 | `interview/*.md` | даты правки | `diagnostic/03-interview-dates.tsv` |
| 4 | `cheatsheets/*` | asymmetry interview ↔ general | `diagnostic/04-asymmetry.tsv` |
| 5 | Chrome Profile 3 | 272 закладки, ключевые папки: AI(18), DevOps(26), Learning(12), Documentation(21+sub) | `diagnostic/05-bookmarks.tsv` |
| 6 | Singularity | 22 заметки с learning-intent | `diagnostic/06-singularity-hints.md` |
| 7 | quiz-app DB | 8366 вопросов сгенерировано, 0 прошёл; прокси через % важности | `diagnostic/07-quizapp-data.md` |

### Адаптации порогов

- Сигнал #2 (размер): «<200 строк» из исходной спеки не сработал — фактический минимум 331 строка. Использую относительный порог по avg категории: < 1200 строк = тонко.
- Сигнал #3 (даты): все файлы 2026-04-28 — 2026-05-16, дисперсии нет, исключён.
- Сигнал #7 (quiz): 0 сессий, использую прокси `% важных вопросов в теме`.

### Найденные пробелы (top-9)

Полная таблица с per-theme секциями — в `diagnostic/08-ranked-candidates.md`. Краткая сводка:

| # | Тема | Σ signals | Evidence summary |
|---|------|-----------|------------------|
| 1 | System Design & Highload | **5** | тонко (avg 1360) + 7/0 general + Singularity «читать DDIA» + payment-system 70% важности |
| 2 | Algorithms — Data Structures practice | **4** | 3 тренажёра в букмарках + 6 тем в топ-20 quiz + intent regular practice |
| 3 | AI/LLM Foundations | **4** | 18 закладок в AI + 9/0 general + llm-basics 73% важности |
| 4 | Kubernetes deep | **3** | Тренажёр + 6 doc-bmk + devops 13/0 + 53% важности |
| 5 | Data Engineering (Kafka Streams, Spark, Flink, Data Lake) | **4** | avg 948 (тонкие) + 8/0 + 4 темы в топ-15 quiz |
| 6 | Go programming | **3** | avg 970 + 3 темы в топ-20 quiz |
| 7 | Reactive Streams (Reactor + RxJava) | **2** | 6/0 general + rxjava 56% |
| 8 | Database Architecture deep | **2** | db-architecture 83% — ЛИДЕР всего распределения |
| 9 | Spring Cloud / Microservices | **2** | 17 doc-bmk + cloud 70% |
| 10 | Quiz-app drill rhythm (process) | мета | 0/8366 — встроено в weekly ritual |

## 3. Backlog (приоритизированный, 9 циклов)

### Cycle 1: System Design & Highload — *2026-05-25 → 05-29*

- **Why:** ранк #1. Прямой intent из Singularity (T-1930a052: «читать Высоконагруженные приложения»). 7/0 general покрытие. Senior-уровень требует уверенного SD.
- **Done when:** в `cheatsheets/interview/system-design/` появилась шпаргалка ≥ 600 строк по одной из ключевых тем (CAP/PACELC, sharding, replication, caching, event-sourcing). Пройдено 20 MCQ по system-design с ≥ 80% correct.
- **Источники:**
  - Свои: `cheatsheets/interview/system-design/system-design-interview.md` (2956 строк) — повторить базу
  - Книга: **«Высоконагруженные приложения» (Клеппманн / DDIA)**, главы 1-3 (Reliable/Scalable/Maintainable, Data Models, Storage)
  - Букмарки: «Backend Roadmap», backend-cheats README
  - quiz-app: тема `system-design` (165 вопросов из текущего распределения)

### Cycle 2: Algorithms — Data Structures (deep practice) — *2026-06-01 → 06-05*

- **Why:** ранк #2. 6 тем из топ-20 quiz importance + 3 тренажёра в букмарках + явный practice-intent.
- **Done when:** прорешать 25 LeetCode-Medium по одной DS (рекомендую Trees — самая весомая 71%). Пройти MCQ из `algorithms/data-structures/trees-interview` с ≥ 85%.
- **Источники:**
  - Свои: `cheatsheets/interview/algorithms/data-structures/trees-interview.md`
  - Тренажёры: VisuAlgo, Algorithm Visualizer (закладки)
  - LeetCode (внешнее)

### Cycle 3: AI/LLM Foundations — *2026-06-08 → 06-12*

- **Why:** ранк #3. 18 букмарок (самая большая learning-папка) + 9/0 general + 73% важности llm-basics.
- **Done when:** создан `cheatsheets/ai-ml/llm-fundamentals.md` ≥ 500 строк (токенизация, attention, context, embeddings, RAG, prompt engineering). 30 MCQ из ai-ml с ≥ 80%.
- **Источники:**
  - Свои: `cheatsheets/interview/ai-ml/*.md` (9 файлов)
  - Букмарки: awesome-ai-memory, system-prompts-and-models, Claude Code Docs

### Cycle 4: Kubernetes (deep) — *2026-06-15 → 06-19*

- **Why:** ранк #4. Используется на работе (Argo CD в букмарках), но без систематического self-study. 53% важности + тренажёр в Learning.
- **Done when:** `cheatsheets/devops/kubernetes-deep.md` ≥ 500 строк (контроллеры, scheduling, networking, RBAC, storage). Hands-on: kind/minikube + 5 манифестов разных типов.
- **Источники:**
  - Свои: `cheatsheets/interview/devops/kubernetes-*.md`
  - Тренажёр Kubernetes (Learning)
  - DevOps > Kubernetes букмарки (6 шт)

### Cycle 5: Data Engineering — Kafka Streams — *2026-06-22 → 06-26*

- **Why:** ранк #5. kafka-streams=82% важности (#2 в распределении). avg 948 строк в data-engineering (вторая самая тонкая).
- **Done when:** `kafka-streams-interview.md` расширен до ≥ 800 строк. Hands-on: минимальное Streams-приложение с windowing.
- **Источники:**
  - Свои: `cheatsheets/interview/data-engineering/kafka-streams-interview.md`
  - Apache Kafka docs (DevOps букмарки)

### Cycle 6: Go programming (concurrency) — *2026-06-29 → 07-03*

- **Why:** ранк #6. 3 темы в топ-20 quiz + avg 970 строк (тонко).
- **Done when:** `go-concurrency-interview.md` ≥ 500 строк (goroutines, channels, sync, context, patterns). Утилита на Go (например, CLI для парсинга своих диагностик).
- **Источники:**
  - Свои: `cheatsheets/interview/programming-languages/go/*.md`
  - Documentation > Go букмарки

### Cycle 7: Reactive Streams — *2026-07-06 → 07-10*

- **Why:** ранк #7. 6/0 general + rxjava 56% + используется со Spring.
- **Done when:** `project-reactor-interview.md` ≥ 600 строк (backpressure, operators, error handling, testing reactive code). 5 pipeline-задач.
- **Источники:**
  - Свои: `cheatsheets/interview/reactive/*.md`

### Cycle 8: Database Architecture deep — *2026-07-13 → 07-17*

- **Why:** ранк #8. db-architecture 83% — лидер распределения важности.
- **Done when:** `database-architecture-interview.md` ≥ 700 строк. 20 MCQ с ≥ 85%.
- **Источники:**
  - Свои: `cheatsheets/interview/databases/database-architecture-interview.md`
  - Documentation > Databases букмарки

### Cycle 9: Spring Cloud / Microservices ecosystem — *2026-07-20 → 07-24*

- **Why:** ранк #9. 17 букмарок Spring + cloud 70% важности.
- **Done when:** `spring-cloud-interview.md` ≥ 600 строк (service discovery, config server, circuit breakers, gateway, distributed tracing).
- **Источники:**
  - Свои: `cheatsheets/interview/frameworks/spring/spring-cloud-interview.md`
  - Spring Framework букмарки (17 шт)

---

После 9 циклов (~2.5 месяца чистого времени, 3-4 месяца с поправкой на пропуски/refresh) — повторить диагностику, обновить backlog.

## 4. Формат недельного цикла

Фиксированный 5-дневный паттерн. 1 час/день в будни.

| День | Что делаем | Артефакт |
|------|------------|----------|
| **Пн** | Прочитать свою шпаргалку темы (раздел) + 2-3 букмарки → выписать **holes** (что непонятно / противоречит / не закрыто). | Список holes в `note` Singularity-задачи Пн |
| **Вт** | Закрыть holes — точечное чтение из закладок и/или книги. | Текст в `note` Singularity-задачи Вт |
| **Ср** | **Hands-on**: написать код / нарисовать диаграмму / разобрать конкретный кейс. | Файл `cheatsheets/<theme>/practice-YYYY-MM-DD.md` или git-репо с кодом |
| **Чт** | Прогнать **20 MCQ из quiz-app** для темы цикла (уже сгенерированные!). Цель ≥ 80% correct. Wrong → возврат в `cheatsheets/<theme>/<file>.md` для дополнения. | Сессия в quiz-app (`review_state` заполняется) |
| **Пт** | Расширить cheatsheet (свои новые понимания). Commit. | `git commit -m "learning(<theme>): ..."` |

## 5. Singularity integration

- **Проект:** «Учёба» (`P-606c59f5-5404-401d-a674-8660595e84bb`)
- **Таск-группа:** базовая (по правилу CLAUDE.md — `Singularity API → пункт 1`)
- **Шаблон названий:** `[Пн|Вт|Ср|Чт|Пт] <Theme>: <короткое описание дня>`
- **Свойства задач:** `start` = соответствующий день недели в GMT+3, `useTime: false`, `notify: 1`, `notifies: [60]`, `priority: 1`, `isNote: false`.
- **В note-поле:** Delta-array со ссылками: путь к шпаргалке в репе + URL букмарок + ожидаемый «done».
- **Правило обновления:** когда цикл закрыт (все 5 задач отмечены) → создать следующую пятёрку из backlog. **Не auto-rolling**, делается вручную.

## 6. Правила пересмотра

- **Жёлтая карточка:** если цикл не закрыт за 2 недели подряд — урезаем тему до 50% объёма ИЛИ удаляем из backlog. Честность важнее объёма.
- **Месячный re-run:** перезапускать `IMPLEMENTATION_PLAN.md` Tasks 1-8 → обновлять `08-ranked-candidates.md`. Новые темы в топ-12 → добавлять в конец backlog.
- **Закрытая тема ≠ выученная.** Каждые 2-3 месяца — refresh-цикл по старой закрытой теме (одна пятница = одна refresh-сессия, прогон MCQ).
- **Quiz-app data growth:** через 4 недели в `user_topic_stats` будут реальные данные → следующая диагностика использует #7 как настоящий signal, а не прокси.

## 7. Источники

### Свои шпаргалки (по темам backlog)

```
cheatsheets/interview/
├── system-design/        — Cycle 1 (7 файлов, avg 1360)
├── algorithms/data-structures/  — Cycle 2 (8 файлов)
├── ai-ml/                — Cycle 3 (9 файлов)
├── devops/kubernetes-*  — Cycle 4
├── data-engineering/     — Cycle 5 (8 файлов, avg 948)
├── programming-languages/go/  — Cycle 6 (7 файлов)
├── reactive/             — Cycle 7 (6 файлов)
├── databases/database-architecture-interview.md  — Cycle 8
└── frameworks/spring/spring-cloud-interview.md   — Cycle 9
```

### Chrome bookmarks (по темам)

- **System Design (Cycle 1):** «Backend Roadmap», backend-cheats README, общий roadmap.sh
- **Algorithms (Cycle 2):** Тренажёр VisuAlgo, Algorithm Visualizer, Big O Cheat Sheet
- **AI/LLM (Cycle 3):** AI folder (18 ссылок) — awesome-ai-memory, system-prompts, awesome-cursorrules, Claude Code Docs, и др.
- **Kubernetes (Cycle 4):** Тренажёр Kubernetes + DevOps > Kubernetes (6 ссылок)
- **Data Engineering (Cycle 5):** DevOps > Kafka (2) + Apache Kafka docs
- **Go (Cycle 6):** Documentation > Go (2 ссылки)
- **Reactive (Cycle 7):** Spring Framework (17, частично применимо)
- **Database Architecture (Cycle 8):** Documentation > Databases (5)
- **Spring Cloud (Cycle 9):** Spring Framework (17 ссылок)

### Внешнее (за пределами букмарок — только то, что упомянуто пользователем)

- **«Высоконагруженные приложения» (М. Клеппманн, DDIA)** — упомянуто в Singularity-заметке T-1930a052. Cycle 1.
- LeetCode / Codewars для Cycle 2 — стандартная DSA-практика (не в букмарках, но необходимо).
- Книги из заметки T-03ba7a25 (Атомные привычки, Гибкое сознание, Дневник стоика) — не учебный план, а личная разработка. Не включаю.
