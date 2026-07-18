# MCQ Answer-Parity & Factual-Integrity Audit — AFTER (2026-07-11)

Итог рефакторинга пяти MCQ-сидеров по спецификации пользователя (§1–§8): сохранение структуры,
Option Parity, переписанные дистракторы, единственность ответа, фактология, fact-freshness,
расширённый QA-гейт и независимая повторная проверка reviewer-субагентами.

Машинные снимки: `gate-snapshot-before.json` / `gate-snapshot-after.json`.
Гейт: `scripts/mcq-answer-parity-gate.py` (детерминированные проверки §7 + новая
`CORRECT_POSITION_SEQUENCE`). BEFORE-аудит: `audit-2026-07-11-before.md`.

## 1. Итог гейта — 5/5 PASS

| Файл | Q | CLR ≤0.40 | LEN ≤1.35 | AVG 0.9–1.1 | SENT ≤1 | DETAIL ≤0.40 | ABS ≤0.25 | POS ≤2.0 | SEQ ≤5 | DUP ≤3 | STYLE ≤0.40 | SRC |
|---|--:|--:|--:|--:|--:|--:|--:|--:|--:|--:|--:|--|
| api-versioning | 20 | 0.15 | 1.33 | 0.99 | 0 | 0.00 | 0.00 | 1.0 | 3 | 0 | 0.29 | ✓ |
| hibernate-relationships | 15 | 0.00 | 1.33 | 0.96 | 1 | 0.00 | 0.09 | 1.33 | 3 | 0 | 0.33 | ✓ |
| linkerd | 20 | 0.20 | 1.35 | 1.00 | 0 | 0.05 | 0.02 | 1.0 | 3 | 0 | 0.25 | ✓ |
| resilience4j | 20 | 0.20 | 1.33 | 0.99 | 0 | 0.15 | 0.15 | 1.0 | 3 | 0 | 0.31 | ✓ |
| conflict-resolution | 20 | 0.20 | 1.29 | 1.04 | 1 | 0.20 | 0.00 | 1.0 | 3 | 0 | 0.36 | n/a |

`verify-mcq-json.sh` — все 5 valid (exit 0). Схема-инварианты (4 опции, 1 correct, ключи секций,
последовательный q_number, отсутствие `**bold**`) — целы.

## 2. Метрики до → после (ключевые проваленные проверки)

| Файл | CLR | LEN_RATIO | AVG | DETAIL | ABS_GAP | DUP | STYLE |
|---|--|--|--|--|--|--|--|
| api-versioning | 0.65 → **0.15** | 2.94 → **1.33** | 1.21 → **0.99** | 0.45 → **0.00** | 0.05 → 0.00 | 17 → **0** | 0.68 → **0.29** |
| hibernate | 0.67 → **0.00** | 2.80 → **1.33** | 1.43 → **0.96** | 1.00 → **0.00** | 0.09 → 0.09 | 0 → 0 | 1.00 → **0.33** |
| linkerd | 0.95 → **0.20** | 3.71 → **1.35** | 1.68 → **1.00** | 0.80 → **0.05** | 0.35 → **0.02** | 0 → 0 | 0.95 → **0.25** |
| resilience4j | 0.70 → **0.20** | 13.0 → **1.33** | 1.34 → **0.99** | 0.70 → **0.15** | 0.13 → 0.15 | 0 → 0 | 0.91 → **0.31** |
| conflict | 0.95 → **0.20** | 3.31 → **1.29** | 1.69 → **1.04** | 0.65 → **0.20** | 0.37 → **0.00** | 0 → 0 | 0.95 → **0.36** |

Главный сдвиг: правильный вариант больше не угадывается по форме. `STYLE_GUESSABILITY` (детерминированный
proxy «угадать correct без знания темы») упал с 0.68–1.00 до 0.25–0.36 при шансе 0.25.

## 3. Найденные и исправленные фактологические ошибки

**api-versioning** (Stripe / RFC 8594; sidecar Stripe `2026-06-24.dahlia`, RFC 8594):
- Версия у Stripe фиксируется HTTP-**заголовком** `Stripe-Version`, а не query-параметром (Q6).
- Убрано «Дефолт 2025» как безосновательный year-stamp (Q9).
- Версия аккаунта фиксируется при первом вызове + per-request override + у webhook-endpoint свой
  `api_version` (Q12).
- «Любое аддитивное изменение всегда безопасно» переведено в дистрактор: новые коды/enum безопасны
  только для терпимых к незнакомым значениям клиентов (Q3).
- Сроки deprecation и «держать 3–4 версии» переформулированы как вендорская политика, а не стандарт
  (Q16/Q19). RFC 8594 ограничен `Sunset`-заголовком ответа + `sunset` link-rel; `Deprecation`/
  successor-version вынесены как практика поверх RFC (Q17). DUPLICATE_NGRAMS 17 → 0.

**hibernate-relationships** (Hibernate ORM 6.6.x):
- `EAGER` не гарантирует JOIN (в JPQL без `JOIN FETCH` — отдельный SELECT); `LAZY` — подсказка,
  `EAGER` — требование (Q3).
- `Set` vs `List` — контекстно; `List`→`Set` лечит только `MultipleBagFetchException`, а не N+1/картезиан
  в общем случае (реальные средства: `@BatchSize`, отдельные запросы, `@EntityGraph`, `JOIN FETCH`) (Q8).
- Де-абсолютизированы equals/hashCode (business/natural ID **или** константа), Statistics — диагностика,
  а не постоянная prod-настройка (Q15).

**linkerd** (v2.20, проверено 2026-07-11; факты post-cutoff — на цитированных источниках):
- Не-K8s workloads — mesh expansion (`ExternalWorkload` CRD, ~2.15).
- Retries/timeouts — аннотации `retry.linkerd.io/*` / `timeout.linkerd.io/*` на `HTTPRoute`/`GRPCRoute`
  (с 2.16), `ServiceProfile` — legacy; traffic split — взвешенные `backendRefs` `HTTPRoute`, SMI
  `TrafficSplit` вынесен в `linkerd-smi` (legacy).
- Трейсинг — `OpenTelemetry`/`OTLP` (рекомендация `linkerd-jaeger` убрана).
- Модель релизов с фев-2024: OSS перестал публиковать stable-артефакты, бесплатные edge — Apache-2.0,
  Buoyant позиционирует их production-ready; stable/LTS — коммерческий Buoyant Enterprise (без утверждения,
  что «любая stable-сборка обязательно платная»). Бенчмарки несут mode+source+version.
- **Пост-ревью:** дата-слип «в 2025 поддерживается только 2.x» → 2026 (Q3-D).

**resilience4j** (v2.2.0):
- CircuitBreaker ≠ thread-изоляция (SEMAPHORE/THREADPOOL — это Bulkhead) (Q1/Q13).
- 6 состояний CircuitBreaker.State + `METRICS_ONLY` (Q4).
- `RateLimiter`/`AtomicRateLimiter` — фиксированные permits за период, НЕ token bucket (Q2/Q10).
- `slowCallDurationThreshold` только классифицирует медленный вызов для статистики, это не sync-таймаут;
  реальный лимит времени — `TimeLimiter` (async) (Q6/Q14).
- Порядок аспектов `Retry(CircuitBreaker(RateLimiter(TimeLimiter(Bulkhead()))))` + `*AspectOrder` (Q15/Q20).
- `EventPublisher` — push/observer, не pull (Q18). Пределы Spring AOP без ложных абсолютов (Q20).
- **Пост-ревью:** убран устаревший `(Vavr)` из explanation Q1 (в 2.x Vavr выпилен); Q18-A ужесточён,
  чтобы убрать частичную правду (ядро реально имеет собственный `EventPublisher`).

**conflict-resolution** (soft-skill, sidecar n/a):
- Все correct-эссе ужаты до ~2 предложений; дистракторы подняты до профессиональных ментальных моделей,
  ошибающихся по приоритету/последовательности/уровню эскалации/пропущенному шагу.
- Убраны токсичные strawman и выдуманные универсальные дедлайны (ABSOLUTE_MARKER_GAP 0.367 → 0.00).
  Q9 (STAR) — самодостаточные ответы кандидата от первого лица.
- **Пост-ревью:** опечатки «компромис» → «компромисс» (Q1, Q18); грамматика «против техническом
  направлении» → «против технического направления» (Q8).

## 4. Повторная проверка (reviewer-субагенты, независимо, read-only)

Пять независимых reviewer-субагентов прогнали три LLM-проверки на каждый файл: **single-correctness**
(состязательно защитить каждый дистрактор как «тоже верный»), **factual accuracy** (сверка version-sensitive
фактов с sidecar-источниками), **blind-style guessability**.

**Вердикт по всем 5: approve. Ни одного BLOCKER/MAJOR по содержанию. Single-correctness и фактология —
чисто во всех файлах.** Все замечания были MINOR; действенные из них применены (см. §3, пост-ревью).

## 5. Изменения в QA и SKILL

- **Новая проверка `CORRECT_POSITION_SEQUENCE`** (порог: арифметический прогон позиций ≤ 5). Ловит то,
  что `CORRECT_POSITION_DISTRIBUTION` пропускала: идеальный баланс A/B/C/D при строгом цикле
  A→B→C→D угадывается по предыдущим ответам. Два ревьюера независимо это отметили (api-versioning,
  resilience4j). Позиции во всех 5 файлах перемешаны (прогон 3), баланс сохранён; **текст, секции и флаг
  correct не тронуты — переставлены только метки/order опций.**
- Порог внесён в таблицу гейта в `mcq-quality-fixer/SKILL.md`; selftest гейта — OK.

## 6. Кросс-артефактные правки теории (.md, только проза, без MCQ-маркеров)

- `linkerd-interview.md`: Q20 переписан на нейтральную формулировку (edge = Apache-2.0/production-ready
  по Buoyant, stable/LTS — коммерческий BEL, спор в сообществе явно указан); трейсинг — `OpenTelemetry`/`OTLP`
  вместо устаревшего `linkerd-jaeger`. Раньше теория противоречила исправленному JSON.
- `api-versioning-interview.md`: «Дефолт на 2025:» → «Практичный дефолт:» (снят year-stamp).
  «Stale Q6 (query-param)» из промежуточного отчёта — **ложная тревога**: Q6 в .md — это общая стратегия
  query-parameter versioning (корректна), а Stripe-секция уже использует заголовок `Stripe-Version`.

## 7. Оставшиеся спорные места (не блокеры, осознанно приняты)

- **Систематический тон-tell (MINOR)** в hibernate/linkerd/conflict: correct-варианты чаще «зависит /
  несколько инструментов / modern+legacy», дистракторы — единичные абсолютные claim'ы. Длины и код-плотность
  уже сбалансированы; чистый тон-tell снижается медленно и риск «деревянности» выше пользы. Кандидат на
  будущий мягкий проход, не бага.
- **linkerd Q5/Q6 бенчмарки** (~30 GB vs ~80 GB; ~30 MB vs ~50–100 MB) поданы как факт, тогда как Q19
  корректно квалифицирует такие числа как version/mode/source-зависимые. `~` смягчает; выравнивать не стали,
  чтобы не раздувать длину опции. Числа — на источниках sidecar.
- **api-versioning Q4–Q8** («плюсы и минусы») частично угадываются по форме «единственный сбалансированный
  ответ с честным минусом» — отчасти неотъемлемо от постановки stem'а.
- **linkerd** факты — post knowledge-cutoff (2.20 анонс 2026-06-23); достоверность держится на цитированных
  первичных источниках в sidecar, не на памяти модели.

## 8. Источники version-sensitive утверждений (fact-freshness sidecars)

`docs/mcq-quality/fact-freshness/*.json`, `checked_date=2026-07-11`:
- **resilience4j** — `product_version=2.2.0`, 5 источников (офиц. docs resilience4j).
- **linkerd** — `product_version=2.20`, 5 источников (linkerd.io, Buoyant, CNCF).
- **hibernate-relationships** — `Hibernate ORM 6.6.x`, 4 источника (офиц. User Guide / Jakarta Persistence).
- **api-versioning** — `Stripe 2026-06-24.dahlia` + RFC 8594, 6 источников (Stripe API changelog, RFC).
- **conflict-resolution** — sidecar отсутствует (soft-skill, не version-sensitive).
