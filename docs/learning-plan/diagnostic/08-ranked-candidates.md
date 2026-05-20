# Ранжированные кандидаты для backlog

**Дата синтеза:** 2026-05-20
**Источники:** все файлы из `diagnostic/01–07`
**Правило включения:** ≥ 2 сильных сигнала. Если меньше — в backlog не идёт.

## Адаптация порогов по фактическим данным

Сигнал #2 (размеры): порог «<200 строк» из спеки оказался не работающим — самый тонкий файл 331 строка. Фактический порог: avg категории < 1200 строк = тонко, < 1000 = очень тонко.

Сигнал #3 (даты): все файлы за 2026-04-28 — 2026-05-16. Дисперсии нет → исключаю.

Сигнал #7 (quiz-app): прямого session-evidence нет (0 из 8366 прошёл). Использую прокси: `% важных вопросов по теме` — топ темы по этому показателю обработаны как сильный сигнал приоритета.

## Сводная таблица

| Rank | Тема | s2 (size) | s4 (asym) | s5 (bmk) | s6 (intent) | s7 (quiz) | Σ |
|------|------|-----------|-----------|----------|-------------|-----------|---|
| 1 | System Design & Highload | ✓ (avg 1360, 7 файлов) | ✓ (7/0 general) | ◦ (general roadmap) | ✓✓ ("Высоконагруженные") | ✓ (payment=70%) | **5** |
| 2 | Algorithms — Data Structures practice | ◦ | – | ✓✓ (3 тренажёра) | ✓ (общая практика) | ✓✓ (6 тем в топ-20) | **4** |
| 3 | AI/LLM Foundations | – | ✓ (9/0 general) | ✓✓ (18 в AI folder) | ◦ | ✓ (llm=73%, emb=66%) | **4** |
| 4 | Kubernetes deep | ◦ | ✓ (devops 13/0) | ✓ (Тренажёр + 6 doc) | – | ✓ (k8s=53%) | **3** |
| 5 | Data Engineering (Kafka Streams, Spark, Flink) | ✓ (avg 948) | ✓ (8/0 general) | – | – | ✓✓ (4 темы в топе) | **4** |
| 6 | Go programming | ✓ (avg 970) | – (2 doc bmk) | ◦ | – | ✓✓ (3 темы в топ-20) | **3** |
| 7 | Reactive Streams (Reactor + RxJava) | ◦ | ✓ (6/0 general) | – | – | ✓ (rxjava=56%) | **2** |
| 8 | Database Architecture deep | – | – (есть general) | ✓ (5 doc bmk) | – | ✓✓ (db-arch=83% — ЛИДЕР) | **2** |
| 9 | Spring Cloud / Microservices ecosystem | – | – (есть general) | ✓✓ (17 doc bmk) | – | ✓ (cloud=70%) | **2** |
| 10 | Quiz-app drill rhythm (process, not topic) | мета | мета | – | – | ✓ (0 прошёл из 8366!) | **2** |

Легенда: ✓ = сигнал есть; ✓✓ = сильный сигнал; ◦ = частичный; – = нет.

---

## 1. System Design & Highload

**Evidence:**
- **s2 (size):** `cheatsheets/interview/system-design/` — 7 файлов, avg 1360 строк. Меньше среднего по проекту.
- **s4 (asymmetry):** `cheatsheets/system-design/` — НЕТ. Есть только в interview/.
- **s5 (bookmarks):** Profile 3 → «Что нужно знать бэкенд-разработчику. Backend Roadmap», «backend-cheats README» в Interviews — общие roadmap, прямого SD нет.
- **s6 (Singularity intent):** **Самый сильный сигнал**: заметка T-1930a052 явно говорит «Читать книгу про Высоконагруженные приложения» (это книга Мартина Клеппманна — DDIA, классика SD).
- **s7 (quiz):** `system-design/design-payment-system-interview` = 70% важности.

**Why this matters:** Senior-уровень предполагает уверенный SD. Пользователь прямо в заметках выразил намерение читать DDIA. Шпаргалок по SD меньше, чем по другим топикам, при том что эта тема постоянно встречается на интервью senior+.

**Done when:** В `cheatsheets/interview/system-design/` появилась шпаргалка по 1 из ключевых тем (CAP/PACELC, sharding, replication, caching strategies, или event-sourcing patterns) ≥ 600 строк; пройдено 20 MCQ из соответствующей темы с ≥ 80% correct.

**Suggested sources:**
- Свои: `cheatsheets/interview/system-design/*.md` (7 файлов)
- Внешнее (из закладок): «Backend Roadmap», backend-cheats README
- Книга: «Высоконагруженные приложения» (Клеппманн, DDIA) — упомянутая в заметке T-1930a052

---

## 2. Algorithms — Data Structures (deep practice)

**Evidence:**
- **s2 (size):** algorithms/data-structures avg 2076 — НЕ тонко. Но многие темы из топ-важности — структурные (stacks-queues=76%, trees=71%, backtracking=70%, linked-lists=66%, sorting=68%, recursion=67%).
- **s5 (bookmarks):** Learning folder содержит «Тренажёр VisuAlgo», «Тренажёр Algorithm Visualizer», + «Big O Cheat Sheet» — 3 практических инструмента. Намерение прокачивать практику, а не учить теорию.
- **s6 (intent):** заметка T-0064643c («Просмотреть все темы по Java») + T-1930a052 («practice по будням») = регулярная DSA-практика.
- **s7 (quiz):** 6 тем в топ-20 по % важности. Самый частый кластер.

**Why this matters:** Пользователь много структурирует алгоритмы (134 файла в general!), но никогда не прогонял MCQ. Бутылочное горлышко — не знание, а муторная практика.

**Done when:** За неделю прорешать 25 задач LeetCode уровня Medium по одной структуре данных (например, Trees), пройти соответствующие MCQ в quiz-app с ≥ 85% correct.

**Suggested sources:**
- Свои: `cheatsheets/algorithms/`, `cheatsheets/interview/algorithms/`
- Тренажёры: VisuAlgo, Algorithm Visualizer (закладки)
- LeetCode/Codewars (внешнее — не в букмарках, но стандартно)

---

## 3. AI/LLM Foundations

**Evidence:**
- **s4 (asymmetry):** `cheatsheets/interview/ai-ml/` 9 файлов, в general — НЕТ.
- **s5 (bookmarks):** Profile 3 → AI folder = **18 ссылок** (Claude Code docs, awesome-ai-memory, prompt engineering, LLM madness, и т.д.). Самая большая «учебная» папка.
- **s7 (quiz):** llm-basics=73% важности, embeddings=66%.

**Why this matters:** Очень активный интерес (18 букмарок), много шпаргалок только для interview, ничего систематического в general. Senior-разработчик в 2026 году без LLM-fluency — пробел.

**Done when:** Создать `cheatsheets/ai-ml/llm-fundamentals.md` (≥ 500 строк) покрывающий: токенизация, attention, context window, embedding spaces, RAG patterns, prompt engineering best practices. Пройти 30 MCQ по ai-ml с ≥ 80%.

**Suggested sources:**
- Свои: `cheatsheets/interview/ai-ml/*.md`
- Букмарки: awesome-ai-memory, system-prompts-and-models, awesome-cursorrules
- Claude Code Docs

---

## 4. Kubernetes (deep)

**Evidence:**
- **s4 (asymmetry):** devops 13/0 general — НЕТ.
- **s5 (bookmarks):** Learning folder → «Тренажёр Kubernetes» + DevOps folder → подпапка Kubernetes 6 ссылок. Намерение и references.
- **s7 (quiz):** devops/kubernetes-interview = 53% важности.

**Why this matters:** Уже работает с K8s на работе (Argo CD, dev-кластеры в букмарках), но систематического self-study не было. Нужен переход от «использую» к «понимаю изнутри».

**Done when:** Создать `cheatsheets/devops/kubernetes-deep.md` ≥ 500 строк: контроллеры, scheduling, networking, storage, RBAC. Hands-on: задеплоить мини-кластер локально (kind/minikube) и сделать 5 манифестов разных типов.

**Suggested sources:**
- Свои: `cheatsheets/interview/devops/kubernetes-*.md`
- Тренажёр Kubernetes (закладка)
- Подпапка DevOps > Kubernetes в букмарках

---

## 5. Data Engineering (Kafka Streams / Spark / Flink / Data Lake)

**Evidence:**
- **s2 (size):** data-engineering avg 948 строк — **второй самый тонкий**.
- **s4 (asymmetry):** 8/0 general — НЕТ.
- **s7 (quiz):** **4 темы из топ-15 по важности**: kafka-streams=82%, spark=69%, data-lake=68%, flink=68%.

**Why this matters:** Кластер очень важных по мнению автора тем, при этом самые тонкие шпаргалки в репе. Это классический паттерн «знаю что важно, но не дописал».

**Done when:** Расширить `cheatsheets/interview/data-engineering/kafka-streams-interview.md` до ≥ 800 строк (текущая ~948 на категорию, надо одну глубокую). Hands-on: написать минимальное Streams-приложение с windowing.

**Suggested sources:**
- Свои: `cheatsheets/interview/data-engineering/*.md`
- Документация Apache Kafka (в DevOps закладках)

---

## 6. Go programming

**Evidence:**
- **s2 (size):** programming-languages/go avg 970 — тонко (7 файлов).
- **s5 (bookmarks):** Documentation > Go = 2 ссылки.
- **s7 (quiz):** **3 темы в топ-20**: go-testing=68%, go=67%, go-concurrency=66%.

**Why this matters:** Видимо новый язык в стеке (Java-разработчик добавляет Go). 7 шпаргалок начато, % важности в quiz-app высокий — это «учу новый язык» паттерн.

**Done when:** Расширить `cheatsheets/interview/programming-languages/go/go-concurrency-interview.md` ≥ 500 строк: goroutines, channels, sync, context, паттерны. Написать 1 утилиту на Go (например, CLI парсер своих собственных диагностик).

**Suggested sources:**
- Свои: `cheatsheets/interview/programming-languages/go/*.md`
- Go docs в Documentation/Go букмарках

---

## 7. Reactive Streams (Reactor + RxJava)

**Evidence:**
- **s4 (asymmetry):** reactive 6/0 general — НЕТ.
- **s7 (quiz):** rxjava=56% важности, project-reactor=38%.

**Why this matters:** Использует Spring (32 файла), значит наверняка работает с WebFlux/Reactor. Reactive-программирование — частая тема на senior-собеседованиях, есть шпаргалки только для interview.

**Done when:** Расширить project-reactor-interview.md ≥ 600 строк: backpressure, operators deep dive, error handling, testing reactive code. Написать 5 практических pipeline-задач.

**Suggested sources:**
- Свои: `cheatsheets/interview/reactive/*.md`
- Project Reactor docs (вероятно в Spring Framework букмарках)

---

## 8. Database Architecture (deep)

**Evidence:**
- **s5 (bookmarks):** Documentation > Databases = 5 ссылок.
- **s7 (quiz):** **database-architecture = 83% важности — ЛИДЕР всего распределения.**

**Why this matters:** Самый высокий % важности (34 из 41 вопроса помечены важными). Это тема, в которую автор инвестировал больше всего «мыслей о важности», но не закрыл прохождением.

**Done when:** Расширить `database-architecture-interview.md` (текущий размер проверить) ≥ 700 строк. Пройти 20 MCQ с ≥ 85%.

**Suggested sources:**
- Свои: `cheatsheets/interview/databases/database-architecture-interview.md`
- Databases закладки (Documentation)

---

## 9. Spring Cloud / Microservices ecosystem

**Evidence:**
- **s5 (bookmarks):** Documentation > Spring Framework = 17 ссылок (самая большая doc-папка).
- **s7 (quiz):** spring-cloud=70%, spring-framework=70%.

**Why this matters:** Используется на работе, но микросервисный аспект может быть менее закрыт чем core Spring.

**Done when:** Расширить `cheatsheets/interview/frameworks/spring/spring-cloud-interview.md` ≥ 600 строк: service discovery, config server, circuit breakers, gateway, sleuth/tracing.

---

## 10. Quiz-app drill rhythm (мета-тема — процесс, не топик)

**Evidence:**
- **s7 (quiz):** 0 из 8366 вопросов прошёл. Огромный неиспользованный инструмент.

**Why this matters:** Создан мощный self-evaluation tool, но не используется как self-check. Это не пробел в знаниях, а пробел в процессе.

**Done when:** В weekly cycle (Чт-стадия) встроено прохождение MCQ через quiz-app. После 4 недель — `user_topic_stats` имеет данные по 4 темам.

**Recommendation:** Не выделяем отдельный цикл, а **встраиваем в недельный ритуал** (Phase 5 спеки).

---

## Решение по backlog

Включаем в backlog темы 1–9 (10-я — встроена в ритуал). Это 9 циклов = 9 недель = ~2.5 месяца. С запасом на refresh-сессии и пропуски — это 4 месяца реальной работы при 5h/week.

**Cycle 1** (rank 1): **System Design & Highload** — старт по DDIA (Клеппманн).
