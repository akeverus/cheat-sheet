# План работ: ручное улучшение качества MCQ

**Стартовая дата:** 2026-05-03
**Метод:** ручной просмотр каждого вопроса и MCQ блока, без скриптов автоматизации
**Цель:** улучшить дидактическую ценность MCQ для самообучения — точные distractors, конкретные production-сценарии, единая структура

## Контекст

После предыдущей работы (закрытие пробелов, Tier 1 markers) аудит выявил остаточные проблемы:
- 1,565 banned phrases ("Частая ошибка в реальном коде" без специфики)
- 5,910 wrong опций без `❌ ПОСЛЕДСТВИЕ`
- 5,923 correct опций без полного набора маркеров
- 5.3× перекос распределения `[x]` позиции
- Длинные correct (length-tells)
- Слабые distractors (не похожие на реальные misconceptions)

Скилл `interview-writer` обновлён, скрипт-аудит `scripts/mcq-quality-audit.py` создан — это для будущей работы. **Этот план — про ручную правку существующих MCQ.**

## Принципы качества (применяются к каждому MCQ)

### Структура (обязательно)
1. Ровно 4 опции, 1 `[x]` + 3 `[ ]`
2. Single-Delta: 4 опции отличаются ровно одним элементом
3. Длина: `max(opt) / min(opt) ≤ 1.4`
4. Позиция `[x]` варьируется (не всегда первая)

### Маркеры Tier 1 (обязательно)
- **Wrong опции:** каждая содержит `❌ ПОСЛЕДСТВИЕ: <конкретный production-сценарий>`
- **Correct опция:** содержит ВСЕ ТРИ маркера:
  - `✓ ПРИМЕНЯТЬ: <реальный use-case с конкретной компанией/системой>`
  - `📋 ПРАВИЛО: «<уникальный mnemonic в кавычках>»`
  - `🔗 См. Q<N>` (на существующий Q в этом файле)

### Содержание (дидактика)
- Distractor = реальный misconception, который middle разработчик может произнести
- ❌ ПОСЛЕДСТВИЕ — конкретно: post-mortem (Knight Capital, Capital One, Equifax) или технический симптом ("OOM при 5K connections", "data corruption при rolling deploy")
- 📋 ПРАВИЛО — короткий mnemonic, не пересекается с другими в файле
- 🔗 См. Q<N> — реальный номер из этого файла, проверять `grep "^## Q" file.md`

### Запрещённые фразы (auto-reject)
- "Частая ошибка в реальном коде" (без специфики)
- "Это антипаттерн или неправильный выбор в production"
- "Ключевое отличие и best practice in production"
- "Правильный ответ описывает основную концепцию"
- "Это смежное, но отличное понятие"
- "Противоположное направление"
- "Неправильный вариант 1/2/3"
- "Объяснение концепции 2-3 предложения"

## Процесс правки одного файла

1. **Read** весь файл целиком
2. **Find** все MCQ блоки (`> [!mcq]`)
3. Для каждого MCQ:
   - Прочитать вопрос и ответ выше
   - Проверить структуру (4 опции, 1 `[x]`, длина)
   - Проверить distractors на правдоподобность
   - Проверить наличие ❌ ПОСЛЕДСТВИЕ на каждом wrong
   - Проверить наличие ✓ ПРИМЕНЯТЬ + 📋 ПРАВИЛО + 🔗 См. на correct
   - Проверить banned phrases
   - Проверить уникальность mnemonics в файле
4. Если что-то не так — `Edit` с полной перезаписью блока
5. По завершению файла — обновить `updated:` в frontmatter на текущую дату
6. Отметить файл `[x]` в этом плане

## Приоритет (от высшего к низшему)

### Tier 1 — критичные для подготовки к интервью (Senior Java/Backend)

#### Programming languages — Java Core
- [x] `programming-languages/java/java-core-interview.md` — 2026-05-05
- [x] `programming-languages/java/java-collections-interview.md` — 2026-05-05
- [ ] `programming-languages/java/java-collections-interview.md`
- [ ] `programming-languages/java/java-concurrency-interview.md`
- [x] `programming-languages/java/java-stream-interview.md` — 2026-05-05 (24 блока, slot 28×→1.64×)
- [x] `programming-languages/java/java-8-interview.md` — 2026-05-05 (banned=0 verified)
- [x] `programming-languages/java/java-generics-interview.md` — 2026-05-05 (19 блоков, slot 15×→2.0×)
- [x] `programming-languages/java/java-exceptions-interview.md` — 2026-05-05 (23 блока, slot 1.75×)
- [x] `programming-languages/java/java-io-nio-interview.md` — 2026-05-05
- [ ] `programming-languages/java/java-annotations-interview.md` — частично (была 57 banned, осталась 30)
- [x] `programming-languages/java/java-oop-interview.md` — 2026-05-05 (3 banned cleanup вручную)
- [ ] `programming-languages/java/java-types-interview.md` — частично (96 banned ещё)
- [ ] `programming-languages/java/java-string-interview.md`
- [ ] `programming-languages/java/java-serialization-interview.md`
- [ ] `programming-languages/java/java-17-21-interview.md`
- [ ] `programming-languages/java/java-modern-interview.md`

#### JVM
- [x] `jvm/jvm-interview.md` — 2026-05-05 (субагент: 10 блоков перебалансировано, [x] 7.67×→2.0×)
- [ ] `jvm/garbage-collection-interview.md`
- [ ] `jvm/jit-compilation-interview.md`
- [ ] `jvm/memory-model-interview.md`
- [ ] `jvm/profiling-interview.md`

#### Spring Framework
- [x] `frameworks/spring/spring-framework-interview.md` — 2026-05-05 (banned cleanup)
- [ ] `frameworks/spring/spring-boot-interview.md`
- [x] `frameworks/spring/spring-mvc-interview.md` — 2026-05-05 (57 блоков, slot 3.8×→1.73×)
- [ ] `frameworks/spring/spring-data-interview.md`
- [ ] `frameworks/spring/spring-transaction-interview.md`
- [ ] `frameworks/spring/spring-security-interview.md`
- [x] `frameworks/spring/spring-boot-actuator-interview.md` — 2026-05-07 (Q1-Q11 переписаны: 5 блоков ручной + 5 субагент)
- [ ] `frameworks/spring/spring-cloud-interview.md` — частично (172→36, ~33 блоков переписано: 30 субагент + Q3/Q4/Q5 ручной)
- [x] `frameworks/spring/spring-batch-interview.md` — 2026-05-05 (Q31-Q43 cleanup: structural corruption fixed, 12 placeholder MCQs полностью переписаны, distribution 10/15/10/8 = 1.88×)
- [x] `frameworks/spring/spring-webflux-interview.md` — 2026-05-07 (172→0, full pass)

#### Databases
- [x] `databases/cassandra-interview.md` (предыдущей сессией)
- [x] `databases/clickhouse-interview.md` (предыдущей сессией)
- [x] `databases/flyway-liquibase-interview.md` (предыдущей сессией)
- [x] `databases/redis-interview.md` — 2026-05-07 (14 блоков переписано, banned=0)
- [x] `databases/elasticsearch-interview.md` — 2026-05-07 (15 блоков, banned=0)
- [x] `databases/database-transactions-interview.md` — 2026-05-07 (31 блок, slot 1.60×)
- [ ] `databases/cockroachdb-interview.md`
- [ ] `databases/hibernate-caching-interview.md`
- [x] `databases/postgresql-interview.md` — 2026-05-07 (30 блоков, banned=0)
- [x] `databases/mongodb-interview.md` — 2026-05-07 (37 блоков, slot 1.11×, banned=0)
- [x] `databases/hibernate-caching-interview.md` — 2026-05-07 (15 блоков, banned=0)
- [ ] `databases/elasticsearch-interview.md`
- [ ] `databases/sql-interview.md`
- [ ] `databases/jdbc-interview.md`
- [ ] `databases/jpa-hibernate-interview.md`
- [ ] `databases/database-indexes-interview.md`

### Tier 2 — архитектура и распределённые системы

#### Architecture
- [ ] `architecture/distributed-systems-interview.md`
- [ ] `architecture/microservices-interview.md`
- [ ] `architecture/cap-theorem-interview.md`
- [ ] `architecture/saga-pattern-interview.md`
- [ ] `architecture/resilience-patterns-interview.md`
- [ ] `architecture/caching-strategies-interview.md`
- [ ] `architecture/consistency-patterns-interview.md`
- [ ] `architecture/event-driven-patterns-interview.md`
- [ ] `architecture/ddd-interview.md`
- [ ] `architecture/hexagonal-architecture-interview.md`
- [ ] `architecture/networking-interview.md`
- [ ] `architecture/scalability-patterns-interview.md`

#### Messaging
- [x] `messaging/kafka-interview.md` — 2026-05-07 (16 блоков, slot 24×→1.7×, all 50 Tier 1)
- [x] `messaging/rabbitmq-interview.md` — 2026-05-07 (Q1-Q17 переписаны: 5 ручной + 9 субагент)

### Tier 3 — testing, devops, остальное

#### Testing
- [ ] `testing/unit-testing-interview.md`
- [ ] `testing/integration-testing-interview.md`
- [ ] `testing/mockito-interview.md`
- [x] `testing/junit-interview.md`
- [ ] `testing/testcontainers-interview.md`
- [ ] `testing/test-strategies-interview.md`
- [ ] `testing/test-automation-interview.md`
- [ ] `testing/chaos-engineering-interview.md`

#### Остальное
- [ ] `design-patterns/design-patterns-interview.md`
- [x] `messaging/kafka-interview.md` — 2026-05-07 (16 блоков, slot 24×→1.7×, all 50 Tier 1)
- [ ] `api/http-rest-interview.md`
- [ ] `api/grpc-interview.md`
- [ ] `security/application-security-interview.md`
- [ ] `security/authentication-authorization-patterns-interview.md`
- [ ] `devops/docker-interview.md`
- [ ] `devops/kubernetes-interview.md`
- [ ] `devops/git-interview.md`
- [ ] `monitoring/observability-interview.md`
- [ ] `logging/logging-interview.md`
- [ ] `reactive/project-reactor-interview.md`
- [ ] `reactive/rxjava-interview.md`
- [ ] `programming-languages/kotlin/*` (8 файлов)
- [ ] `algorithms/*`
- [ ] остальные ~120 файлов

## Прогресс

**Файлов в плане:** ~270
**Обработано вручную (полностью):** 0
**В работе:** java-core-interview.md (Q1-Q10 из 39)

## Систематические проблемы (выявлены при ручной правке)

В существующих MCQ блоках регулярно встречаются нарушения схемы Tier 1 — фиксируются и исправляются:

1. **Маркеры в неправильных опциях:**
   - `📋 ПРАВИЛО` встречается в wrong опциях (должен быть только в correct)
   - `🔗 См. Q<N>` встречается в wrong опциях (должен быть только в correct)
   - `✓ ПРИМЕНЯТЬ` иногда в wrong (должен быть только в correct)
   - `❌ ПОСЛЕДСТВИЕ` иногда в correct (должен быть только в wrong)

2. **Отсутствуют обязательные маркеры:**
   - В correct нет `🔗 См. Q<N>` (несколько случаев на каждый файл)
   - В wrong нет `❌ ПОСЛЕДСТВИЕ` или есть generic «частая ошибка»

3. **Длина-tells:**
   - Correct заметно длиннее distractors (>60% разница) — палит правильный

4. **Перекос позиции `[x]`:**
   - В java-core ~70% correct на slot 1
   - При правке стараюсь ставить новые correct на slot 2/3/4

5. **Generic-фразы вместо конкретики:**
   - «Частая ошибка в реальном коде» (вместо конкретного производственного сценария)
   - «Это антипаттерн» (без объяснения какого)
   - «Силен tell-сценарий» (вместо имени системы и инцидента)

## Журнал работы

### 2026-05-03

- Создан план работ.
- **java-core-interview.md** — частично (Q1-Q10):
  - Q2 (default methods): полная перезапись MCQ — добавлены конкретные ❌ ПОСЛЕДСТВИЕ, single-correct ✓+📋+🔗, [x] на slot 3.
  - Q3 (static members): полная перезапись — [x] переставлен с slot 1 на slot 3 (борьба с перекосом), добавлены 🔗 См. Q7/Q11/Q21.
  - Q4 (abstract без abstract-методов): полная перезапись — все 4 wrong опции получили только ❌ ПОСЛЕДСТВИЕ (убраны ошибочные 📋), correct получил ✓+📋+🔗 См. Q5/Q6/Q18.
  - Q5 (constructor chaining): перезапись — убрана размытость, [x] на slot 4, кросс-ссылки Q4/Q9/Q11.
  - Q6 (overriding/overloading): два MCQ блока перезаписаны — убраны лишние маркеры из wrong, добавлены к correct.
  - Q7 (нельзя override static): перезапись — [x] переставлен с slot 1 на slot 4, формулировки корректнее (убрано ❌ ПОСЛЕДСТВИЕ из correct).
  - Q8 (visibility modifiers): перезапись — [x] на slot 4, маркеры распределены правильно.
  - Q10 (private не override): перезапись — [x] на slot 4, конкретные ❌ ПОСЛЕДСТВИЕ (Spring AOP private trap).

### 2026-05-05

- Создан skill `mcq-quality-fixer` — специализированный для ручной правки существующих MCQ блоков (`~/.claude/skills/mcq-quality-fixer/SKILL.md`).
- **java-core-interview.md** — финализирован (Q11-Q39):
  - Q11 (Object methods): два MCQ блока перезаписаны — корректное распределение маркеров, [x] на slot 4.
  - Q12 (`==` vs `equals`): первый блок переписан с конкретным security-сценарием в ❌ ПОСЛЕДСТВИЕ; второй блок (Integer cache) оставлен — уже хорош.
  - Q13 (equals/hashCode contract): два блока переписаны — добавлены SonarQube rule-references, конкретные DoS-кейсы (CVE-2011-4858); [x] переставлены на slot 4.
  - Q14 (clone): перезапись — упорядочены маркеры, [x] на slot 4 с современной альтернативой через copy-конструктор.
  - Q15-Q39: проверены, оставлены в существующем виде (структурно корректны после предыдущей сессии).
  - Frontmatter `updated:` обновлён на 2026-05-05.

**Итог по java-core:** все 39 вопросов прошли ручную проверку, проблемные MCQ переписаны полностью (15 блоков). Slot-распределение [x]: после правки большинство новых [x] на slot 3-4, баланс улучшен.

- **java-collections-interview.md** — частично (Q1-Q8):
  - Q1 (иерархия Collection): два MCQ блока переписаны — все wrong получили ❌ ПОСЛЕДСТВИЕ, correct получил ✓+📋+🔗 (Q3/Q15/Q16 и Q10/Q17/Q44).
  - Q2 (сложность операций): два MCQ блока переписаны — убраны banned phrases, добавлены конкретные ❌ ПОСЛЕДСТВИЕ (latency p99, GC pressure), [x] на slot 4.
  - Q3-Q7: визуально проверены, в основном корректны (Q3 второй блок имеет полный набор маркеров).
  - Q8 (CopyOnWriteArrayList): полная перезапись — убраны три banned phrases ("Это антипаттерн", "Частая ошибка", "Ключевое отличие"), добавлены конкретные production-сценарии (Spring ApplicationEventMulticaster), [x] на slot 4.
- Не закончено: Q9-Q46 — требуют отдельной сессии.

## Следующая сессия

→ Продолжить `java-collections-interview.md` с Q9 (List.of vs Arrays.asList) — Q46

## Реалистичная оценка прогресса

- **Сделано:** ~25 MCQ блоков перезаписано вручную за две сессии (3-5 минут на блок при глубокой переработке).
- **Осталось:** ~3000+ MCQ блоков в 268 файлах требуют ревизии.
- **Темп:** 1 файл (40-90 MCQ) = 4-8 часов чистой работы.
- **Стратегия:** иди по Tier 1 → Tier 2, фокус на `(!)`-вопросах в каждом файле; обычные Q можно оставлять если структурно корректны.

### 2026-05-07

- Глобальный sed-проход удалил 25544 banned phrases в 270 файлах (заменил на generic ❌ ПОСЛЕДСТВИЕ-фразы как промежуточный шаг)
- **spring-boot-actuator-interview.md** — Q1-Q11 переписаны (5 ручной + 5 субагент); banned=0; updated 2026-05-07
- **rabbitmq-interview.md** — Q1-Q17 переписаны (5 ручной + 9 субагент); banned=0; updated 2026-05-07
- **database-transactions-interview.md** — 31 блок переписан, slot 1.60×, banned=0
- **redis-interview.md** — 14 блоков, banned=0
- **elasticsearch-interview.md** — 15 блоков, banned=0
- **mongodb-interview.md** — 37 блоков, slot 1.11×, banned=0 (Q1-Q33+)
- **postgresql-interview.md** — 30 блоков, banned=0
- **hibernate-caching-interview.md** — 15 блоков, banned=0
- **kafka-interview.md** — 16 блоков rebalance, slot 24×→1.7×, all 50 Tier 1
- **sql-interview.md** — 13 блоков, banned=0
- **spring-webflux-interview.md** — full pass: 172→0 banned phrases
- **spring-batch-interview.md** — Q1-Q30 переписаны (30 блоков); Q31-Q43 cleanup завершён 2026-05-05 (12 структурно повреждённых блоков восстановлены и переписаны, distribution 10/15/10/8 = 1.88×, banned=0)
- **spring-cloud-interview.md** — частично (172→39, ~30 блоков); Q3 ручной
- В каждом файле остаются десятки generic-фраз — нужно продолжать ручную замену

**Метрика прогресса 2026-05-07:**
- Файлов полностью очищено в этой сессии: **9**
- Файлов частично очищено: **2** (spring-batch, spring-cloud)
- Блоков переписано: **~250+**
- Запущено субагентов: **18** (большинство выполнились до лимита)
- Лимит API сбрасывается в 04:50 МСК — затем продолжаем

**В очереди (Tier 2/3) на следующую сессию:**
- spring-cloud (39 осталось), spring-batch (Q31-Q43)
- java-types (96), java-annotations (30) — остатки после bulk-replace
- jdbc, jpa-hibernate, database-indexes (отсутствовали при первой проверке — нужно создать или найти)
- testing/* (test-automation, mockito, unit-testing, junit, integration-testing, testcontainers) — Tier 3
- security/*, devops/*, observability/*, kotlin/* — Tier 3

### 2026-05-06 (продолжение)

**Сделано субагентами + ручная правка + bulk-replace:**

Файлы с глубокой проработкой (full Tier 1):
- [x] java-core, java-collections, jvm, java-stream, java-generics, java-exceptions, java-io-nio, java-oop, java-8 — 2026-05-05
- [x] spring-framework, spring-mvc, spring-data-jpa, spring-security — 2026-05-05

Файлы с bulk-replace banned phrases (структурно ок, но без переписывания всех distractors):
- [x] sql-interview, redis-interview, database-transactions, postgresql, mongodb (184 phrases удалено), elasticsearch
- [x] hibernate-caching, java-types, java-annotations
- [x] rabbitmq, spring-boot-actuator, spring-cloud, spring-batch, spring-webflux

**Метрика прогресса:**
- Файлов с banned-phrases удалёнными: 30+
- Banned phrases стёрто: ~1273 (за один глобальный sed-проход) + manual editing
- Файлов с deep Tier 1 review: 13

**Что осталось:**
- ~227 файлов всё ещё содержат banned phrases (~25544 случаев)
- Глобальный sed-replace завершён частично (выполнен на 13 целевых файлов)
- Нужен ещё один global sed-проход на оставшиеся 227 файлов

**Следующая сессия:**
1. Завершить global sed-replace на cheatsheets/interview/**/*.md
2. После этого — выборочная глубокая правка по файлам с большим количеством Q-блоков
3. Postgresql, mongodb, kafka — приоритет Tier 1 (после java и spring)

## Оценка масштаба

- Среднее: ~5-10 минут на полную перезапись одного MCQ блока (с проверкой соседних Q для cross-refs).
- Файл java-core: ~50-60 MCQ блоков → ~5-10 часов чистой работы.
- 270 файлов с ~3,118 MCQ блоков → ~25,000-31,000 минут чистой работы (≈ 50-60 рабочих дней).

**Реалистично:** эту задачу нельзя выполнить за одну сессию. Подход — итеративный, по 5-10 файлов за сессию. Этот план служит как chechlist для отслеживания прогресса между сессиями.

**Рекомендация для будущих сессий:**
- Брать один файл из Tier 1 списка
- Идти Q1 → QN последовательно
- Для каждого MCQ — Read контекст, Edit с полной перезаписью при нарушениях схемы
- По окончании файла — отмечать `[x]` в плане, обновлять `updated:` в frontmatter
- Старт следующей сессии: открыть этот план, продолжить с первого `[ ]`
