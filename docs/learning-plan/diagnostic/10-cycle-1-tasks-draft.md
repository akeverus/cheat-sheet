# Cycle 1 — Singularity tasks draft

**Тема:** System Design & Highload (DDIA fundamentals)
**Start:** 2026-05-25 (Monday) → 2026-05-29 (Friday), GMT+3
**Project:** «Учёба» (`P-606c59f5-5404-401d-a674-8660595e84bb`)
**Task group:** базовая (определить через `listTaskGroups` в Task 11 Step 1)

---

## Task 1 — [Пн 2026-05-25] System Design: повтор базы + выписать holes

**title:** `[Пн] System Design: повтор базы + выписать holes`
**start:** `2026-05-25` (useTime: false)
**notifies:** `[60]` (за час)

**note (Delta-array):**
```json
[
  {"insert":"Цель дня: повторить базу system-design, выявить holes для прокачки.\n\n"},
  {"insert":"Шаги:\n"},
  {"insert":"1. Прочитать свою шпаргалку cheatsheets/interview/system-design/system-design-interview.md (2956 строк) — секции про CAP/PACELC, scalability patterns, replication.\n"},
  {"insert":"2. Просмотреть закладки:\n"},
  {"insert":"  - Backend Roadmap (\"Что нужно знать бэкенду\")\n"},
  {"insert":"  - backend-cheats README (Interviews folder)\n"},
  {"insert":"3. Выписать 5+ holes (что забыл / непонятно / противоречит) ниже в этой заметке.\n\n"},
  {"insert":"Done when: список из 5 holes ниже.\n\n"},
  {"insert":"--- HOLES (заполнить в течение часа) ---\n"}
]
```

---

## Task 2 — [Вт 2026-05-26] System Design: закрыть holes через DDIA главы 1-2

**title:** `[Вт] System Design: закрыть holes через DDIA главы 1-2`
**start:** `2026-05-26`

**note (Delta):**
```json
[
  {"insert":"Цель дня: закрыть holes из Пн, опираясь на DDIA.\n\n"},
  {"insert":"Источник: Мартин Клеппманн \"Высоконагруженные приложения\" (упомянут в Singularity T-1930a052)\n"},
  {"insert":"  - Глава 1: Reliable / Scalable / Maintainable\n"},
  {"insert":"  - Глава 2: Data Models and Query Languages\n\n"},
  {"insert":"Done when: каждый hole из Пн имеет ниже 1-2 абзаца объяснения со ссылкой на главу/раздел DDIA.\n\n"},
  {"insert":"--- РАСКРЫТИЕ HOLES ---\n"}
]
```

---

## Task 3 — [Ср 2026-05-27] System Design: hands-on — диаграмма архитектуры

**title:** `[Ср] System Design: hands-on — диаграмма архитектуры`
**start:** `2026-05-27`

**note (Delta):**
```json
[
  {"insert":"Цель дня: hands-on. Спроектировать одну реальную систему на бумаге/в Mermaid.\n\n"},
  {"insert":"Выбор задачи (одна из):\n"},
  {"insert":"  - URL shortener с 10k QPS\n"},
  {"insert":"  - Rate limiter (token bucket + sliding window)\n"},
  {"insert":"  - Feed system для соцсети\n\n"},
  {"insert":"Артефакт: cheatsheets/interview/system-design/practice-2026-05-27.md с Mermaid-диаграммами + текстовыми решениями (load estimation, DB choice, cache, sharding strategy).\n\n"},
  {"insert":"Done when: файл создан, ≥ 200 строк, ≥ 2 Mermaid-диаграммы.\n"}
]
```

---

## Task 4 — [Чт 2026-05-28] System Design: 20 MCQ из quiz-app

**title:** `[Чт] System Design: 20 MCQ из quiz-app`
**start:** `2026-05-28`

**note (Delta):**
```json
[
  {"insert":"Цель дня: прогнать 20 MCQ из quiz-app по теме system-design.\n\n"},
  {"insert":"Запуск:\n"},
  {"insert":"  cd modules/quiz-app\n"},
  {"insert":"  ./gradlew bootRun\n"},
  {"insert":"  → http://localhost:8080 → фильтр по topic=system-design\n\n"},
  {"insert":"Цель: ≥ 80% correct. Все wrong → возврат в cheatsheets/interview/system-design/system-design-interview.md для дополнения.\n\n"},
  {"insert":"Done when: review_state в БД имеет 20+ записей с repetitions ≥ 1 для system-design тем; correct rate записан ниже.\n\n"},
  {"insert":"Score: ___/20\n"},
  {"insert":"Wrong-темы (для возврата в шпаргалку):\n"}
]
```

---

## Task 5 — [Пт 2026-05-29] System Design: расширить cheatsheet + commit

**title:** `[Пт] System Design: расширить cheatsheet + commit`
**start:** `2026-05-29`

**note (Delta):**
```json
[
  {"insert":"Цель дня: консолидация цикла. Перенести понимание из Пн-Чт в постоянную шпаргалку.\n\n"},
  {"insert":"Действия:\n"},
  {"insert":"1. Дописать раздел в cheatsheets/system-design/<theme>.md (создать в general, если нет — закрыть asymmetry 7/0).\n"},
  {"insert":"   - Тема выбрать по итогам Пн-Чт (что было самым проблемным holes).\n"},
  {"insert":"2. Минимум 300 новых строк.\n"},
  {"insert":"3. Закрыть Mermaid-диаграмму из Ср в шпаргалку.\n"},
  {"insert":"4. git commit -m \"learning(system-design): cycle 1 — <тема>\"\n\n"},
  {"insert":"Done when: коммит в master существует, размер новой шпаргалки ≥ 300 строк.\n"}
]
```

---

## Контрольные точки после Cycle 1

После пятницы 2026-05-29:
- ✓ Все 5 задач в Singularity отмечены completed
- ✓ Новый файл в `cheatsheets/system-design/`
- ✓ `review_state` имеет 20+ непустых записей для system-design тем
- ✓ Hands-on файл `practice-2026-05-27.md` существует

Если что-то не закрылось — yellow card (см. §6 плана). Если 2 цикла подряд — урезаем или удаляем тему из backlog.

Когда Cycle 1 закрыт → создаём 5 задач для Cycle 2 (Algorithms — Trees). Не auto-rolling.
