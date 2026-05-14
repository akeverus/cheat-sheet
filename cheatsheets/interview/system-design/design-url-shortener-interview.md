---
title: "Вопросы на собеседовании: Design URL Shortener"
description: "System design URL shortener (TinyURL, bit.ly): hashing, Base62, collisions, scaling reads, analytics, custom aliases, TTL, caching, DB schema, capacity estimation"
tags:
  - interview
  - system-design
  - design-url-shortener-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Design URL Shortener"
  - "URL Shortener design"
  - "TinyURL system design"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Design URL Shortener`

`URL Shortener` (TinyURL, bit.ly) — **classic system design interview**. Compact (feasible в 45 min), но covers multiple concepts: **hashing, encoding, caching, scale read-heavy, analytics**. Expected на middle/senior interviews.

## Полезные ссылки

- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [TinyURL on High Scalability](http://highscalability.com/)
- [bit.ly engineering blog](https://word.bitly.com/)
- [Hiredly: URL Shortener design](https://www.youtube.com/results?search_query=url+shortener+system+design)
- [Ben Cook — SystemsExpert walkthrough](https://www.algoexpert.io/systems)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Requirements**
- [Q1. (!) Functional и non-functional requirements?](#q1--functional-и-non-functional-requirements)
- [Q2. (!) Capacity estimation — сколько storage, QPS?](#q2--capacity-estimation--сколько-storage-qps)

**API**
- [Q3. (!) API endpoints и flow?](#q3--api-endpoints-и-flow)

**Encoding**
- [Q4. (!) Как генерировать short URL? (Base62, hash, counter)](#q4--как-генерировать-short-url-base62-hash-counter)
- [Q5. (!) Почему Base62, а не Base64?](#q5--почему-base62-а-не-base64)
- [Q6. Как избежать collisions?](#q6-как-избежать-collisions)

**Storage**
- [Q7. (!) Database schema?](#q7--database-schema)
- [Q8. SQL vs NoSQL — какой выбор?](#q8-sql-vs-nosql--какой-выбор)

**Scalability**
- [Q9. (!) Как scale reads? (cache, CDN)](#q9--как-scale-reads-cache-cdn)
- [Q10. (!) Cache strategy (write-through / lazy)?](#q10--cache-strategy-write-through--lazy)
- [Q11. Как shard DB?](#q11-как-shard-db)

**Features**
- [Q12. Custom aliases?](#q12-custom-aliases)
- [Q13. TTL / expiration?](#q13-ttl--expiration)
- [Q14. Analytics (click tracking)?](#q14-analytics-click-tracking)

**Production concerns**
- [Q15. (!) Reliability и single point of failure?](#q15--reliability-и-single-point-of-failure)
- [Q16. Security (spam, phishing)?](#q16-security-spam-phishing)
- [Q17. Rate limiting?](#q17-rate-limiting)

## Q1. (!) Functional и non-functional requirements?

**Functional:**
- Shorten long URL → short code (7 char typical)
- Redirect short → long
- Optional: custom alias (vanity URL)
- Optional: expiration (TTL)
- Optional: analytics (click counts)

**Non-functional:**
- **Read-heavy** (100:1 ratio reads:writes typical)
- High availability (99.9%+)
- **Low latency** (< 100ms redirect)
- Predictable (not overwhelmed by spike)
- **Unpredictable URLs** (can't guess next)
- No duplicate codes
- Scalability (billions URLs)

**Explicitly not required (scope tight):**
- User accounts (MVP no auth)
- Edit/delete URLs (later)
- Multi-region consistency — eventual OK

**Interview tip:** clarify с interviewer — scope matters greatly для design.


> [!mcq]
> - [ ] URL shortener — write-heavy сервис: оптимизировать запись, не чтение | ❌ ПОСЛЕДСТВИЕ: Reads:Writes ≈ 100:1; оптимизация записи без массивного caching reads = 100ms+ redirect latency
> - [x] Read-heavy (100:1), low latency (<100ms), HA 99.9%+, unpredictable URLs (security), billions scale | ✓ ПРИМЕНЯТЬ: NFR для URL shortener, формирующие архитектурные решения 📋 ПРАВИЛО: URL shortener = read-heavy + low latency + HA + unpredictable codes 🔗 См. Q9
> - [ ] Strong consistency между регионами обязательна | ❌ ПОСЛЕДСТВИЕ: глобальный consensus добавит 100-200ms на write; eventual consistency достаточно — короткий код доступен через секунды глобально
> - [ ] Custom aliases — обязательная функциональность для MVP | ❌ ПОСЛЕДСТВИЕ: vanity URLs усложняют collision detection и conflict resolution; в MVP лучше отложить, опционально для платных пользователей

## Q2. (!) Capacity estimation — сколько storage, QPS?

**Assumptions (bit.ly-scale):**
- 100M new URLs / month = ~40 writes/sec
- Read:write = 100:1 → ~4000 reads/sec
- Peak spike: 10x → 40K reads/sec

**Storage per URL:**
- `short_code`: 7 chars = 7B
- `long_url`: avg 100B
- `created_at`, `expires_at`, `user_id`: ~30B
- Total: ~150B per record

**5 years:**
- 100M × 12 × 5 = 6 billion URLs
- 6B × 150B = **900 GB** base data
- With indexes + replicas: ~3-5 TB

**Bandwidth:**
- Read: 4000 QPS × 100B (URL response) ≈ 400 KB/s
- Write: 40 QPS × 150B ≈ 6 KB/s
- Peak (10x) читается: 4 MB/s

**Memory для cache (80/20 rule):**
- 20% URLs get 80% traffic
- Top 20% of active links = ~200M URLs
- 200M × 150B = **30GB** — feasible on single large Redis instance

**Takeaway:** not a huge system, но requires careful caching для read latency.


> [!mcq]
> - [ ] Cache можно хранить на одной Redis instance — 30GB hot data | ❌ ПОСЛЕДСТВИЕ: single Redis = single point of failure; для HA нужен Redis Cluster или sentinel; 30GB на одной node — но без replication
> - [ ] Storage 5 лет ≈ 100GB — index не нужен | ❌ ПОСЛЕДСТВИЕ: реально 6B URLs × 150B + индексы на short_code и user_id ≈ 3-5TB; без планирования storage system падает на 50% capacity
> - [x] 6B URLs × 150B = 900GB raw + indexes ~3-5TB; cache 80/20 ⇒ 30GB Redis для hot URLs | ✓ ПРИМЕНЯТЬ: capacity estimation для read-heavy URL shortener 📋 ПРАВИЛО: 80/20 cache hits hot 20% URLs = огромный QPS hit rate 🔗 См. Q9
> - [ ] Bandwidth не критичен — URL короткие | ❌ ПОСЛЕДСТВИЕ: при peak 40k QPS × 100B response = 4MB/s; на ingress NIC можно превысить лимит при недостаточной аплинк-пропускной способности

## Q3. (!) API endpoints и flow?

**POST /shorten**
```http
POST /api/v1/shorten
{
  "url": "https://very/long/url",
  "custom_alias": "myalias",   // optional
  "ttl_seconds": 2592000        // optional
}
→ 200 OK
{
  "short_url": "https://tiny.url/abc1234"
}
```

**GET /{short_code} (redirect)**
```http
GET /abc1234
→ 302 Found
Location: https://very/long/url
```

**Use 301 vs 302?**
- **301** (permanent): browser caches → fewer server hits, но loses analytics; bad если need tracking
- **302** (temporary): no cache → each click hits server → track analytics
- bit.ly uses 301 with short cache (private, max-age=90)
- For analytics — typically 302 (force server hit)

**GET /api/v1/stats/{short_code}** — click analytics

**Flow (shorten):**
1. Validate URL
2. Check if already shortened (optional — dedup)
3. Generate short code
4. Insert to DB
5. Cache
6. Return short URL

**Flow (redirect):**
1. Lookup cache → hit → redirect
2. Miss → DB
3. Found → cache + redirect
4. Not found → 404


> [!mcq]
> - [ ] Использовать 301 (permanent) для всего — улучшает производительность | ❌ ПОСЛЕДСТВИЕ: 301 кэшируется браузером навсегда; redirect не дойдёт до сервера — analytics клик-трекинг не работает; bit.ly использует 301 с private max-age=90 для компромисса
> - [x] POST /shorten возвращает короткий URL; GET /{code} → 302 redirect (для analytics) или 301 (cached, лучше perf, без tracking) | ✓ ПРИМЕНЯТЬ: REST API дизайн URL shortener; выбор 301/302 зависит от tracking 📋 ПРАВИЛО: 301 = cached forever (no analytics); 302 = each click hits server 🔗 См. Q14
> - [ ] Flow shorten: validate → generate → return (без cache write) | ❌ ПОСЛЕДСТВИЕ: первый redirect промахнётся в кэше → DB hit → 50ms latency; pre-warm cache при создании = 5ms latency сразу
> - [ ] При создании короткого URL обязательно проверять что long_url существует | ❌ ПОСЛЕДСТВИЕ: HEAD request на каждый submit добавляет 200-500ms latency; spam-проверки лучше делать в backend offline

## Q4. (!) Как генерировать short URL? (Base62, hash, counter)

**Three main approaches:**

**1. Hash (MD5/SHA + Base62):**
```
hash = md5(long_url)
short = base62_encode(hash)[0:7]
```
- Pros: deterministic (same URL → same code — dedup!)
- Cons: collisions; need check + retry

**2. Counter + Base62:**
```
id = autoincrement_counter  // global: 1, 2, 3, ...
short = base62_encode(id)
```
- Counter 1 → "1", 100000000000 → "aZl8N0"
- Pros: no collisions; predictable
- Cons:
  - Enumerable (/1, /2 → discover all URLs)
  - Global counter = bottleneck (single point)
  - Solution: **distributed counter** (Snowflake, key ranges per server)

**3. Random (62^7):**
```
short = random_base62_string(7)
check_db_collision()
```
- 62^7 = 3.5 trillion combinations
- Pros: unpredictable, no global coordination
- Cons: probabilistic collisions (rare; ~few at billion scale)

**Hybrid (common in practice):**
- Pre-generate batches of random codes offline
- Service consumes from pool
- Pool refilled background
- No online random/collision work

**Short code length:**
- 6 chars: 62^6 = 56B — borderline
- 7 chars: 3.5T — safe for decade
- 8 chars: 218T — overkill но flexibility

**Picked approach:** counter + Base62 с key-range sharding, OR offline-generated random pool.


> [!mcq]
> - [ ] Hash MD5 без collision check — самый надёжный подход | ❌ ПОСЛЕДСТВИЕ: на billion entries birthday paradox даёт несколько collisions; без unique constraint два URL получат одинаковый код = один потеряется
> - [ ] Глобальный counter без шардинга — самое простое решение | ❌ ПОСЛЕДСТВИЕ: single counter — bottleneck при 40+ writes/sec; на peak load очередь к counter блокирует все writes; нужен distributed counter (Snowflake, key ranges)
> - [x] Counter+Base62 (no collisions, predictable, нужен distributed) ИЛИ random Base62 7chars (3.5T combos, low collision rate) ИЛИ pre-generated pool (offline batches) | ✓ ПРИМЕНЯТЬ: выбор зависит от scale и предсказуемости URL 📋 ПРАВИЛО: counter = predictable bottleneck; random = unpredictable независимое 🔗 См. Q5
> - [ ] 6 chars Base62 достаточно — 56 миллиардов комбинаций | ❌ ПОСЛЕДСТВИЕ: 56B комбинаций с 6B existing = пространство уже почти заполнено через 5 лет; collision rate растёт катастрофически; 7 chars = 3.5T = безопасно

## Q5. (!) Почему Base62, а не Base64?

**Base62 alphabet:** `[a-zA-Z0-9]` — 62 chars.

**Base64 alphabet:** Base62 + `+` и `/` (or `-`, `_` в URL-safe variant).

**Проблема Base64:**
- `+` и `/` require URL encoding (`%2B`, `%2F`) — уродливо
- URL-safe Base64 (`-`, `_`) лучше, но не universally supported

**Base62 advantages:**
- URL-safe inherently
- Double-click selects (в textbox) — `-`/`_` may split
- Aesthetic (clean)

**Why not just Base10 (decimal)?**
- Shorter encoding with Base62 (4.1x more compact bit-wise)
- 7 chars Base62 = 3.5T (enough)
- 7 chars Base10 = 10M (too few)

**Encoding:**
```python
ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

def encode(num):
    if num == 0: return ALPHABET[0]
    result = []
    while num:
        result.append(ALPHABET[num % 62])
        num //= 62
    return ''.join(reversed(result))

def decode(s):
    return sum(ALPHABET.index(c) * 62**i for i, c in enumerate(reversed(s)))
```


> [!mcq]
> - [ ] Base64 универсально лучше — больше алфавит = меньше длина кода | ❌ ПОСЛЕДСТВИЕ: Base64 содержит `+` и `/` — требуют URL encoding (`%2B`, `%2F`); URL уродлив; URL-safe Base64 (`-`,`_`) лучше но менее supported
> - [ ] Base10 (decimal) — самое читаемое для пользователей | ❌ ПОСЛЕДСТВИЕ: 7 chars Base10 = 10M URLs, исчерпается за месяц; Base62 7 chars = 3.5T, хватит на годы
> - [x] Base62 [a-zA-Z0-9] — URL-safe inherently, double-click selects, эстетично; 62^7 = 3.5T combinations | ✓ ПРИМЕНЯТЬ: short URL encoding, file IDs, любые URL-safe identifiers 📋 ПРАВИЛО: Base62 = URL-friendly без escape characters 🔗 См. Q4
> - [ ] Hex (Base16) лучше Base62 — стандартизированно | ❌ ПОСЛЕДСТВИЕ: Hex кодирует только 4 бита/symbol (vs ~6 Base62); 7 hex chars = 268M URLs; для billion масштаба нужно 10+ chars

## Q6. Как избежать collisions?

**В hash-based approach:**
- Collision rate низкий, но ≠ 0
- На billion entries: birthday paradox → few collisions expected

**Strategies:**

**1. Check-and-insert:**
```python
while True:
    code = generate_code()
    try:
        db.insert(code, url)  # unique constraint
        break
    except DuplicateError:
        continue  # try again
```
- Simple
- Race condition: two requests same code → unique constraint handles

**2. Bloom filter + DB check:**
- Bloom filter: fast "probably seen" check
- Avoid DB hit if bloom says new (99.9% accurate)
- False positive → DB check anyway

**3. Pre-generate pool:**
- Background job fills pool с unique codes
- Online consumes from pool
- No collision check at request time

**4. Counter-based (no collisions):**
- Monotonic increment
- Distributed via Snowflake or Redis INCR or key ranges

**Suffix trick:**
- On collision, append char or increment
- Predictable growth

**Retry limit:**
- 3-5 retries; if all fail → increase length to 8 chars

**Probability calculation (birthday):**
- 3.5T space, 6B codes → P(collision) per new ≈ 6B/3.5T = 0.17%
- ~10M collisions at 6B codes
- Must handle gracefully


> [!mcq]
> - [ ] Generate random codes без проверки collision — birthday paradox низкий | ❌ ПОСЛЕДСТВИЕ: при 6B codes из 3.5T space P(collision)≈0.17%; это ~10M потерянных URL без unique constraint
> - [x] Strategies: check-and-insert (unique constraint), Bloom filter, pre-generate pool, counter-based; retry limit 3-5 then increase length | ✓ ПРИМЕНЯТЬ: production URL shortener должен иметь стратегию collision handling 📋 ПРАВИЛО: collision handling = unique constraint + retry с лимитом 🔗 См. Q4
> - [ ] Bloom filter гарантирует отсутствие collisions | ❌ ПОСЛЕДСТВИЕ: Bloom filter даёт false positives (1-2%); без последующей DB check возможен пропуск collision; Bloom = optimization, не guarantee
> - [ ] При collision лучше всегда возвращать 409 Conflict — пусть клиент попробует ещё раз | ❌ ПОСЛЕДСТВИЕ: латентность взлетает (RTT × число retries); клиент может не делать retry; сервер должен retry внутри generation logic

## Q7. (!) Database schema?

**Simple:**

```sql
CREATE TABLE urls (
  short_code VARCHAR(10) PRIMARY KEY,
  long_url TEXT NOT NULL,
  user_id BIGINT,
  created_at TIMESTAMP DEFAULT NOW(),
  expires_at TIMESTAMP,
  click_count BIGINT DEFAULT 0
);

CREATE INDEX idx_user ON urls(user_id);
CREATE INDEX idx_expires ON urls(expires_at);
```

**With analytics (separate table — high write volume):**
```sql
CREATE TABLE clicks (
  id BIGSERIAL PRIMARY KEY,
  short_code VARCHAR(10),
  clicked_at TIMESTAMP DEFAULT NOW(),
  referrer TEXT,
  user_agent TEXT,
  country VARCHAR(2),
  ip VARCHAR(45)
) PARTITION BY RANGE (clicked_at);
```

- Partition by month (easy to drop old)
- Index short_code для analytics queries

**Users table (если auth):**
```sql
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) UNIQUE,
  tier VARCHAR(20),
  created_at TIMESTAMP
);
```

**Considerations:**
- `short_code` primary key → fast lookup
- Don't join clicks на each redirect (denormalized counter или async batch)
- `click_count` eventually consistent (update from clicks stream every N sec)


> [!mcq]
> - [ ] click_count лучше обновлять синхронно при каждом redirect | ❌ ПОСЛЕДСТВИЕ: 40k QPS × UPDATE click_count = lock contention на short_code row; redirect latency взлетает; нужен async batch update
> - [x] short_code как PRIMARY KEY (fast lookup); clicks separate table partitioned by month; click_count denormalized eventually consistent | ✓ ПРИМЕНЯТЬ: schema дизайн URL shortener с разделением hot/cold paths 📋 ПРАВИЛО: redirect path = lookup-only; analytics path = separate async pipeline 🔗 См. Q14
> - [ ] Не нужен индекс на user_id если поиск по short_code | ❌ ПОСЛЕДСТВИЕ: "мои URL" страница запрашивает urls WHERE user_id=X; full table scan на 6B records = таймаут
> - [ ] PARTITION BY HASH лучше PARTITION BY RANGE для кликов | ❌ ПОСЛЕДСТВИЕ: HASH разбрасывает данные равномерно но не позволяет дропнуть старые партиции; RANGE по месяцам = TRUNCATE PARTITION для cleanup

## Q8. SQL vs NoSQL — какой выбор?

**SQL (PostgreSQL/MySQL):**
- Transactions (важно для counter?)
- Strong consistency
- Complex queries (analytics joins)
- ACID
- Familiar, battle-tested

**NoSQL (DynamoDB, Cassandra):**
- Key-value fits URL shortener perfectly (short_code → long_url)
- Unlimited scale
- Low latency reads at scale
- No-SQL eventual consistency acceptable

**Verdict:** **eivther works; key-value NoSQL preferred for scale**:
- Primary use case = lookup by key → exactly what KV store optimizes
- DynamoDB: 10ms p99 at any scale
- Redis is overkill but works for smaller

**Reality:**
- bit.ly уses **Redis + MySQL** (Redis cache, MySQL persistence)
- Google-scale: Spanner or BigTable

**Schema в DynamoDB:**
```
Table: urls
  PK: short_code (string)
  Attrs: long_url, created_at, expires_at (TTL), ...
```

**Analytics separate** regardless of main DB:
- Kafka → Flink/Spark → data warehouse (BigQuery, Redshift)
- Heavy aggregation off main path


> [!mcq]
>
> **Вопрос:** Для URL shortener выбирают между PostgreSQL и DynamoDB. Какое утверждение лучше всего обосновывает выбор технологии хранения?
>
> ---
>
> #### A) PostgreSQL обязателен — нужны транзакции и ACID для целостности short_code → long_url — ❌ Неверно
>
> **Что на самом деле:** Запись short_code → long_url — это идемпотентная single-row операция. Никаких multi-table транзакций не требуется: либо INSERT с UNIQUE constraint проходит, либо нет. ACID нужен для финансовых транзакций или сложных бизнес-инвариантов, а не для key-value lookup.
> **Откуда путаница:** Привычка "DB = ACID = PostgreSQL по умолчанию". Многие backend-разработчики не различают bounded-context: какое именно гарантирование нужно в этой схеме.
> **Если бы это было правдой:** Bit.ly и TinyURL работали бы на PostgreSQL primary, упирались бы в vertical scale на 50k+ QPS, тратили inflated $$$ на RDS Multi-AZ вместо DynamoDB on-demand с автоматическим горизонтальным split.
>
> ---
>
> #### B) Redis — единственный нужный store; persistence через AOF достаточно вместо отдельной DB — ❌ Неверно
>
> **Что на самом деле:** Redis с AOF теряет 0-1с данных при crash; RDB snapshot — до минут. Для URL shortener это означает потерю свежесозданных коротких ссылок — user отправил клиенту ссылку, через секунду crash → ссылка не разрешается → user-visible 404 на собственную ссылку. Redis нужен как cache layer поверх durable store (MySQL/DynamoDB), а не как единственный source of truth.
> **Откуда путаница:** "Bit.ly использует Redis" → читают как "Redis вместо DB", хотя там Redis + MySQL.
> **Если бы это было правдой:** При memory eviction (LRU) старые short_code теряются навсегда; SLA на durability падает до уровня AOF fsync (~99.9% в лучшем случае); compliance аудит (GDPR right-to-erasure logs) проваливается.
>
> ---
>
> #### C) Key-value NoSQL (DynamoDB/Cassandra) — оптимальный выбор: схема доступа = lookup by PK, нужна горизонтальная масштабируемость и predictable p99 latency — ✓ Верно
>
> **Развёрнутое объяснение:** URL shortener — это **canonical key-value workload**: 99% запросов = `GET long_url WHERE short_code = ?`. DynamoDB даёт p99 ≈ 10ms на любом масштабе с auto-partitioning по hash(short_code); on-demand pricing освобождает от capacity planning. Транзакции не нужны — short_code генерируется уникальным (counter+Base62, Snowflake, или INSERT с retry на conflict). Analytics (clicks) идут в отдельный pipeline (Kafka → ClickHouse), не нагружая redirect path.
> **Пример:**
> ```
> Table: urls (DynamoDB)
>   PK: short_code (S)
>   Attrs: long_url, created_at, expires_at (TTL attr), user_id
>   GSI: user_id-created_at-index (для "My links")
> ```
> Bit.ly: MySQL + Redis (legacy stack); modern (Yandex Cloud Shortener, Hootsuite Owly) — DynamoDB-style KV. Google goo.gl (до закрытия) — Bigtable.
> **Когда применять:** Read-heavy workload (100:1 reads:writes), schema-on-read (атрибуты могут эволюционировать без миграций), нужен global secondary index для query patterns кроме PK, multi-region replication из коробки (DynamoDB Global Tables).
> **Подводные камни:** Cross-partition queries (например, "top-100 URLs by clicks") требуют scan — нужен отдельный analytics store. Hot partition при единичном viral URL — митигировать write sharding (suffix к ключу) или CDN edge cache. Стоимость GSI = удвоение write capacity.
> **Связанные вопросы:** [[Q7]] — Database schema; [[Q9]] — scale reads через cache/CDN; [[Q11]] — sharding strategies для самой DB.
>
> ---
>
> #### D) Cassandra — лучший выбор потому что поддерживает SQL через CQL — ❌ Неверно
>
> **Что на самом деле:** CQL похож на SQL **синтаксически**, но семантически ограничен: нет JOIN, нет subqueries, WHERE только по partition key (или с ALLOW FILTERING — антипаттерн). Cassandra оптимизирована для write-heavy workload (LSM-tree, append-only), а URL shortener — read-heavy. DynamoDB или Redis-as-primary дадут лучший cost/latency для 100:1 read ratio.
> **Откуда путаница:** "CQL = SQL = удобно" — обманчивое сходство имени.
> **Если бы это было правдой:** Tombstones от expired URLs накапливаются в SSTables, compaction overhead растёт; redirect latency через Cassandra p99 ≈ 30-50ms против DynamoDB 10ms; операционная сложность (управление nodetool, repair schedules) выше чем у managed DynamoDB.

**Read amplification problem:** 1 URL може serve billions of redirects.

**Layers:**

**L0 — Browser cache (via 301):**
- Permanent redirect → browser caches
- Loses analytics (can't track)
- Use only if no analytics needed

**L1 — CDN:**
- Edge cache для redirect response
- `Cache-Control: public, max-age=86400`
- Massive offload для top URLs
- Works with 302 too (configurable)

**L2 — Redis cache:**
- Hot URLs в memory
- ~1ms lookup
- Cache everything, evict LRU/LFU

**L3 — DB read replicas:**
- MySQL replicas читают без primary load
- Geo-distributed replicas для multi-region

**Architecture:**
```
User → CDN (edge) → Load Balancer → App → Redis → DB (primary+replicas)
```

**Hit ratios typical:**
- CDN: 50-70% (long tail)
- Redis: 95%+ (most popular)
- DB: rarely touched for reads

**80/20 rule:** 20% URLs get 80% traffic → cache effective.


> [!mcq]
>
> **Вопрос:** При scale reads для URL shortener (40k QPS, 100:1 read:write ratio) какая стратегия наиболее эффективна для снижения нагрузки на origin?
>
> ---
>
> #### A) Single Redis instance перед DB достаточен — отдаст все hot ключи из памяти — ❌ Неверно
>
> **Что на самом деле:** Single Redis instance с 30GB RAM покрывает hot set, но даёт single point of failure и upper bound ~100k QPS на инстанс. При 40k QPS глобально это работает, но при viral spike (топ-URL × миллион кликов) Redis instance насыщает NIC раньше, чем CPU; нужен Redis Cluster или multi-tier (in-proc → Redis → DB). Также Redis не решает географическую latency: user из Бразилии всё равно идёт в US-East datacenter.
> **Откуда путаница:** "Redis = решение проблемы reads" — но single-tier cache игнорирует edge caching и geo-distribution.
> **Если бы это было правдой:** При DDoS на популярный short_code single Redis колено-в-колено с DB; cross-region users получают 200ms RTT вместо 20ms через CDN edge; Redis OOM при unbounded growth.
>
> ---
>
> #### B) 301 Permanent Redirect полностью решает проблему — браузер закеширует — ❌ Неверно
>
> **Что на самом деле:** 301 действительно кешируется браузером надолго, но даёт два критических минуса: (1) теряется аналитика — последующие переходы не доходят до сервера, нет click_count; (2) нельзя отозвать ссылку — если URL заблокирован за phishing, у миллионов пользователей в браузере останется кеш 301 на месяцы. Большинство shortener используют **302 Found** (не кешируется) + CDN с короткой TTL для контроля.
> **Откуда путаница:** "Permanent = постоянный = хорошо для производительности" — но в business context каждый клик должен быть наблюдаемым.
> **Если бы это было правдой:** Аналитика клик-стрима ломается; revocation malicious URL невозможен без X-User-Agent fingerprint; conversion tracking партнёров (UTM-параметры) теряется на повторных кликах.
>
> ---
>
> #### C) Sharding DB по short_code решает read scaling без cache — ❌ Неверно
>
> **Что на самом деле:** Sharding распределяет write load и storage capacity, но **не решает read amplification**: один популярный short_code (viral video link) генерирует миллионы redirect на один shard → этот shard становится hot partition. Cache (Redis/CDN) — единственный способ обработать read amplification, потому что копирует hot ключи близко к edge, минуя origin. Sharding и cache решают **разные** проблемы и применяются вместе.
> **Откуда путаница:** Смешение write scale (sharding) и read scale (caching) — обе называются "scale", но техники разные.
> **Если бы это было правдой:** При viral URL один shard упирается в IOPS, остальные простаивают; добавление shard'ов не помогает, потому что трафик идёт в один partition; нужен либо cache, либо write-sharding популярного ключа (suffix trick).
>
> ---
>
> #### D) Multi-tier cache (CDN edge → Redis cluster → DB read replicas) + 302 redirect — обрабатывает read amplification на каждом уровне — ✓ Верно
>
> **Развёрнутое объяснение:** **L0 Browser** — не кешировать (302) для аналитики. **L1 CDN edge** (CloudFront/Cloudflare) — top URLs закешированы в 200+ POP по всему миру, hit ratio 50-70%, latency 5-20ms close to user. **L2 Redis cluster** — hot set (20% URLs = 80% трафика), p99 1ms, hit ratio 95%+ от того что прошло через CDN. **L3 DB read replicas** (Aurora replicas или DynamoDB Global Tables) — rarely touched, обрабатывают cache miss + cold tail. Каждый уровень снижает нагрузку на следующий экспоненциально — origin DB видит < 0.1% исходного трафика.
> **Пример:**
> ```
> 40k QPS глобально
>   ↓ CDN edge (hit 60%)
> 16k QPS на app servers
>   ↓ Redis cluster (hit 95%)
> 800 QPS на DB
>   ↓ read replicas split
> ~200 QPS на каждую replica
> ```
> Cache-Control: `public, max-age=300, s-maxage=3600` — короткий browser TTL (revocation), длинный CDN TTL (offload).
> **Когда применять:** Любой read-heavy KV сервис: bit.ly (Akamai + Redis), TikTok shortlinks (Cloudflare + own KV), Twitter t.co (Fastly + Manhattan KV store). 100:1 read:write — caching обязателен; 10:1 — рекомендован; 1:1 — необязателен.
> **Подводные камни:** Cache invalidation при revocation — short TTL + explicit purge через CDN API. Stale cache при rolling restart — warm-up через replay logs. CDN cost — CloudFront $0.085/GB egress; для viral URLs можно превысить DB cost.
> **Связанные вопросы:** [[Q10]] — cache-aside vs write-through trade-offs; [[Q15]] — graceful degradation при cache outage; [[Q11]] — sharding для write capacity (orthogonal к read scaling).

**Write-through:**
- On shorten: write DB + Redis atomically
- Read always hits Redis
- Potential issue: Redis full → evict; next read DB cache miss

**Cache-aside (lazy):**
- On read: check Redis → miss → DB → store in Redis
- Simpler; DB is source of truth
- First read after write uncached

**Write-behind:**
- Not applicable — short URLs rarely deleted/changed

**Recommended:** cache-aside с write-through для hot data.

**TTL:**
- Cache TTL = 24h typical
- Long TTL OK (URLs immutable after creation)
- Invalidate on delete (rare)

**Eviction:**
- LFU (Redis `allkeys-lfu`) — keeps popular
- Memory sized для hot set (30GB для 200M URLs)

**Local cache (L1 in-proc):**
- Super-hot URLs (< 1k) in-proc Caffeine
- Nanosecond access
- Invalidate periodically


> [!mcq]
>
> **Вопрос:** Какая cache strategy + eviction policy наиболее уместна для Redis-слоя URL shortener?
>
> ---
>
> #### A) Write-through на все ключи + LRU eviction + TTL=infinity — гарантирует 100% cache hit — ❌ Неверно
>
> **Что на самом деле:** Write-through на ВСЕ shorten операции означает write amplification 2x (DB + Redis для каждой записи), включая URL которые никогда не будут прочитаны (90% URLs — long tail, < 1 click). Это раздувает Redis memory: 6B URLs × 100 bytes = 600GB Redis-кластер вместо 30GB для hot set. LRU без TTL = ключи никогда не expire сами; при memory pressure eviction случайных old-but-popular ключей.
> **Откуда путаница:** "Cache hit 100% — святой грааль" — но cost этого выше, чем экономия от cache miss-ов на cold tail.
> **Если бы это было правдой:** Redis cost вырастает в 20x; write latency на shorten растёт (await Redis write); при partial Redis outage write fails, хотя DB здорова.
>
> ---
>
> #### B) Cache-aside (lazy loading) + LFU eviction + TTL=24h — populates только реально востребованное, хранит самые популярные — ✓ Верно
>
> **Развёрнутое объяснение:** **Cache-aside** означает: read → Redis miss → DB → store в Redis. Это автоматически фильтрует cold tail — URLs которые никто не кликает не занимают cache memory. **LFU** (Least Frequently Used, `maxmemory-policy allkeys-lfu` в Redis) лучше LRU для URL shortener потому что популярные ссылки кликают долго (виральные YouTube-shortlinks 6+ месяцев), а LRU выбросит "старый но часто кликаемый" в пользу "недавно созданного но единожды прочитанного". **TTL 24h** — компромисс: длинный TTL экономит DB reads, короткий — даёт revocation window для phishing URLs.
> **Пример:**
> ```
> # redis.conf
> maxmemory 32gb
> maxmemory-policy allkeys-lfu
> 
> # Application
> long_url = redis.get(short_code)
> if long_url is None:
>     long_url = db.lookup(short_code)
>     if long_url:
>         redis.setex(short_code, 86400, long_url)  # TTL 24h
>     else:
>         redis.setex(f"miss:{short_code}", 60, "1")  # negative cache 1 min
> return long_url
> ```
> **Когда применять:** Read-heavy KV workload с power-law распределением (Pareto 20/80). Twitter t.co, bit.ly, Yandex Cloud Object Storage metadata — все используют cache-aside + LFU. Negative caching (короткий TTL на 404) защищает от amplified DB load при scanning attacks.
> **Подводные камни:** Thundering herd — cache expiry на популярном ключе → миллион concurrent DB queries; митигировать через probabilistic early refresh (XFetch algorithm) или single-flight pattern. Cold start — после Redis restart первые минуты hit ratio 0%; warm-up через replay top-1000 ключей. Stale data при DB update — invalidate через `DEL short_code` или короткий TTL.
> **Связанные вопросы:** [[Q9]] — multi-tier cache architecture; [[Q13]] — TTL и expiration semantics; [[Q15]] — graceful degradation при Redis outage.
>
> ---
>
> #### C) Write-behind (async DB write) ускоряет shorten endpoint — ❌ Неверно
>
> **Что на самом деле:** Write-behind означает: write только в Redis, async flush в DB через batch. Для URL shortener это **опасно**: после shorten user получает короткую ссылку и отправляет другу; если Redis crashed до flush — ссылка не разрешается → 404 для user-visible action. Write-behind применим только для idempotent-able loss tolerant данных (analytics counters, кэш hit metrics), но не для durable mapping.
> **Откуда путаница:** "Async = быстро" — но без durability gates это потеря данных.
> **Если бы это было правдой:** SLA на "созданная ссылка работает" падает до Redis persistence (~99.5%); compliance аудит (GDPR data retention) ломается потому что DB не имеет полной истории; recovery procedure после Redis crash требует replay из app logs.
>
> ---
>
> #### D) LRU eviction всегда лучше LFU для cache — ❌ Неверно
>
> **Что на самом деле:** LRU оптимален для **recency-based** workloads (file system cache, session cache), но URL shortener имеет **frequency-based** распределение — viral URL популярна месяцами с пиками. LRU выбросит "стабильно популярный" URL когда в кеш попадёт burst of new shorten requests; LFU защищает long-term hot ключи. Redis 4.0+ предлагает оба, но `allkeys-lfu` явно рекомендуется для CDN-style workload (см. Redis docs).
> **Откуда путаница:** LRU исторически был дефолтом в Redis (до 4.0) и memcached → "стандартный выбор".
> **Если бы это было правдой:** При viral burst (Black Friday, новостной shortlink) hot set эвакуируется новыми ссылками; hit ratio падает с 95% до 60%; DB read load вырастает в 8x в самый неудобный момент.

**Sharding strategies:**

**By short_code hash:**
- `shard_id = hash(short_code) % N`
- Even distribution
- Lookup: hash → shard → query
- Resharding pain (см consistent hashing)

**By short_code range:**
- Shard A: codes a-i, shard B: j-r, ...
- Prone to hot spots if not uniform

**By user_id:**
- User's URLs co-located (good for "my links" page)
- Hot user = hot shard

**Hybrid (common):**
- Codes generated by shard ID prefix: "aB" prefix → shard 0, "cD" → shard 1
- Each shard generates own codes (no collision across)
- Lookup: prefix → shard

**Replication:**
- Each shard: primary + 2 replicas
- Multi-AZ / multi-region для durability

**Consistent hashing:**
- Add/remove shards: rebalance few keys
- Virtual nodes for evenness

**DynamoDB:** hash-based partitioning built-in; auto-split hot partitions (>1000 WCU).


> [!mcq]
>
> **Вопрос:** Как лучше всего шардировать DB для URL shortener с 6B записей, чтобы избежать hot partitions и упростить resharding?
>
> ---
>
> #### A) Consistent hashing по short_code с virtual nodes (vnodes) + shard-prefix encoding для co-location генерации — ✓ Верно
>
> **Развёрнутое объяснение:** **Consistent hashing** минимизирует rebalancing при изменении числа shards: добавили shard — мигрирует только 1/N ключей, а не все. **Virtual nodes** (каждый физический shard представлен 100-500 vnodes на hash-кольце) выравнивают распределение даже при разной ёмкости физических shards. **Shard-prefix encoding** (первые 1-2 символа short_code = shard ID) ускоряет lookup: app server знает shard по prefix без consultation с metadata service; каждый shard генерирует свои коды независимо → нет distributed counter contention. DynamoDB делает это автоматически (auto-split partitions при > 1000 WCU), Cassandra — через token ranges + vnodes.
> **Пример:**
> ```
> # Code generation: shard-prefix encoded
> shard_id = host_local_counter % NUM_SHARDS
> suffix = base62(snowflake_id)
> short_code = base62(shard_id) + suffix   # e.g. "aB7xK2p"
> 
> # Lookup
> shard = decode_prefix(short_code[0])      # O(1)
> long_url = shards[shard].get(short_code)
> 
> # Resharding (add shard N+1)
> consistent_hash_ring.add_vnodes(N+1, count=200)
> # Migrate only ~1/(N+1) of vnodes from old shards
> ```
> DynamoDB hash partitioning: hash(short_code) → 10GB/3000 RCU partition; auto-split at threshold. Cassandra: `PARTITION KEY = short_code` + `num_tokens: 256`.
> **Когда применять:** Любая горизонтально-масштабируемая KV-система с unpredictable growth. Используется в DynamoDB, Cassandra, Riak, Twitter Manhattan, Bit.ly's MySQL sharding layer. Особенно важно когда reshard происходит без downtime (online cluster expansion).
> **Подводные камни:** Hot partition при viral URL — даже с perfect hashing один short_code = один shard; митигировать через write sharding (suffix к ключу — `viral_url#0`, `viral_url#1`, ..., aggregation на read) или CDN edge cache. Cross-shard query ("count URLs created today") требует scatter-gather; для analytics использовать отдельный store (Kafka → ClickHouse). Vnode count tradeoff: больше vnodes = равномернее, но больше metadata overhead.
> **Связанные вопросы:** [[Q4]] — distributed code generation (Snowflake); [[Q14]] — analytics в отдельной системе; [[Q15]] — replication внутри shard для durability.
>
> ---
>
> #### B) Range-based sharding по алфавиту short_code (a-i → shard 0, j-r → shard 1, ...) — проще для debug — ❌ Неверно
>
> **Что на самом деле:** Range-based sharding по prefix создаёт **hot shards**: Base62 distribution неравномерна — реальный traffic зависит от scheme generation. Если counter monotonically растёт и base62-encoded младший разряд меняется быстрее старшего, новые URLs концентрируются на одном shard, пока range не "продвинется". Также resharding range partitions требует физического move половины данных при split.
> **Откуда путаница:** Range partitioning из PostgreSQL/MySQL `PARTITION BY RANGE` — проще для timeseries (по дате), но для random keys создаёт hotspots.
> **Если бы это было правдой:** Все новые short_code попадают на shard 0; через месяц shard 0 имеет 80% данных, shard 5 — 5%; resharding split shard 0 = migration 600GB данных = часы downtime или сложный online split.
>
> ---
>
> #### C) Sharding по user_id co-locates URLs одного пользователя — лучший выбор — ❌ Неверно
>
> **Что на самом деле:** Sharding по user_id оптимизирует **"My links" query** (все URLs пользователя на одном shard), но проваливает основной use case — **redirect by short_code**: чтобы найти long_url нужно либо scatter-gather по всем shards, либо вторичный индекс short_code → user_id (extra lookup). Большинство трафика — redirects (100:1), а не "My links" view; primary access pattern диктует shard key.
> **Откуда путаница:** Принцип "shard by what you query" применён без учёта весов запросов.
> **Если бы это было правдой:** Каждый redirect делает либо N parallel queries (scatter), либо 2 sequential lookup (index + shard); p99 latency растёт с 10ms до 30-50ms; system upper bound по QPS падает в N раз для основного use case.
>
> ---
>
> #### D) Modulo hashing `shard = hash(short_code) % N` без consistent hashing — простейшее решение — ❌ Неверно
>
> **Что на самом деле:** `mod N` distribution равномерна, но при изменении N (добавили shard) **почти все ключи** меняют свой shard: `hash(x) % 4 ≠ hash(x) % 5` для большинства x. Это требует full reshuffle = миграция 100% данных. Consistent hashing решает именно эту проблему — переселяется только 1/N ключей. Также `mod N` не поддерживает разные capacity per shard (heterogeneous cluster).
> **Откуда путаница:** Mod hashing — стандартная техника в учебниках, минимальный код; работает до первой попытки масштабировать кластер.
> **Если бы это было правдой:** Добавление shard превращается в недельную миграционную операцию; "live resharding" невозможен без сложного proxy-layer (Vitess, ProxySQL); при failure shard невозможно временно redirect трафик на neighbours без перекеширования всего hash space.

**User requests:** `/myalias` instead of `/abc1234`.

**Implementation:**
```sql
-- Reuse schema: custom aliases stored as short_code
INSERT INTO urls (short_code, long_url) VALUES ('myalias', '...');
```

- Check uniqueness (same as regular)
- Reserve common words (admin, login)
- Length constraints (3-30 chars)

**Conflict с generated codes:**
- Reserve namespace: random codes 7+ chars; customs 3-20 chars
- Or check uniqueness across same table

**Pricing:** often paid feature (vanity URLs premium).

**Abuse:**
- Reserved list (trademarks, offensive)
- Premium namespaces


> [!mcq]
>
> **Вопрос:** Два пользователя одновременно пытаются забронировать custom alias `/promo2026`. Какая реализация корректно обрабатывает race condition?
>
> ---
>
> #### A) Сначала SELECT WHERE short_code='promo2026', если нет — INSERT — проще всего — ❌ Неверно
>
> **Что на самом деле:** SELECT-then-INSERT — классический **check-then-act race condition** (TOCTOU). Между SELECT (миллисекунды) и INSERT две параллельные транзакции могут оба прочитать "не существует", оба INSERT — и одна выиграет, другая упадёт на UNIQUE constraint (если он есть) или (хуже) перепишет (если нет). Без UNIQUE constraint один из пользователей получит "успех", а в DB будет данные другого. Защита через application-level lock не работает в multi-instance deployment.
> **Откуда путаница:** Изоляция READ COMMITTED не предотвращает phantom write; SERIALIZABLE дороже и часто не используется.
> **Если бы это было правдой:** Под нагрузкой race возможен ежеминутно для популярных alias-имён ("admin", "login"); user видит "alias забронирован", но через секунду открывает свою ссылку — она ведёт на чужой URL; legal/PR issue если premium-аккаунт оплатил vanity URL.
>
> ---
>
> #### B) Просто хранить custom_alias в отдельной таблице, FK на urls — race решится сама — ❌ Неверно
>
> **Что на самом деле:** Отдельная таблица не решает проблему — race condition существует на любой структуре, где не используется constraint или atomic-операция. FK защищает только от orphan references, но не от concurrent claim одинакового alias-значения. Без UNIQUE constraint на `aliases.name` две параллельные транзакции вставят одинаковый alias.
> **Откуда путаница:** Думают "разделил на таблицы = снял проблему", хотя проблема в отсутствии явного constraint.
> **Если бы это было правдой:** Двойное хранение (urls + aliases) удваивает write amplification; FK contention на parent row при concurrent insert; всё равно нужен UNIQUE constraint — и можно было сделать без отдельной таблицы.
>
> ---
>
> #### C) Использовать distributed lock (Redis SETNX или ZooKeeper) на alias name перед INSERT — ❌ Неверно
>
> **Что на самом деле:** Distributed lock работает, но это **over-engineering**: добавляет dependency (Redis/ZK availability), сложность с lock TTL и release-on-crash, и performance overhead (round-trip к lock service на каждый INSERT). DB UNIQUE constraint решает ту же задачу atomic-ally за одну операцию без extra dependencies. Distributed locks нужны для координации **между разными ресурсами** (cross-DB state), не для single-row uniqueness.
> **Откуда путаница:** Гипертрофированное применение "distributed systems" подходов к локальной проблеме.
> **Если бы это было правдой:** Latency на shorten растёт +5-20ms (lock RTT); при Redis outage shorten ломается, хотя DB здорова; lock timeout misconfigure → stale lock блокирует валидный alias на минуты.
>
> ---
>
> #### D) UNIQUE constraint на short_code + INSERT с обработкой conflict (`ON CONFLICT DO NOTHING` / catch DuplicateKeyException) — atomic, durable — ✓ Верно
>
> **Развёрнутое объяснение:** **UNIQUE constraint** даёт **atomic check-and-insert** на уровне DB — DB engine гарантирует что только одна транзакция выиграет race, остальные получат ошибку конфликта. Это работает идентично для random-generated short_code и custom aliases (один столбец, одна constraint). PostgreSQL: `INSERT ... ON CONFLICT (short_code) DO NOTHING RETURNING id` — если RETURNING вернул строку, выиграл я; иначе alias занят. DynamoDB: `PutItem` с `ConditionExpression: attribute_not_exists(short_code)` — фейлится с `ConditionalCheckFailedException`, если ключ существует.
> **Пример:**
> ```sql
> -- Schema
> CREATE TABLE urls (
>   short_code VARCHAR(30) PRIMARY KEY,  -- UNIQUE implicit
>   long_url   TEXT NOT NULL,
>   user_id    BIGINT,
>   created_at TIMESTAMPTZ DEFAULT NOW(),
>   is_custom  BOOLEAN DEFAULT FALSE,
>   CHECK (LENGTH(short_code) BETWEEN 3 AND 30)
> );
> 
> -- Claim custom alias (atomic)
> INSERT INTO urls (short_code, long_url, user_id, is_custom)
> VALUES ('promo2026', $1, $2, TRUE)
> ON CONFLICT (short_code) DO NOTHING
> RETURNING short_code;
> -- 0 rows returned → 409 Conflict to user
> ```
> **Когда применять:** Любой scenario с unique resource claim: usernames (Twitter @handle), email addresses, ticket reservations, vanity URLs (Bit.ly Pro, Rebrandly, TinyURL custom domains). UNIQUE — single source of truth для concurrent integrity.
> **Подводные камни:** Reserved namespace — заранее INSERT системных alias ("admin", "api", "login", "support", "terms") при bootstrap, чтобы user не мог их claim. Case sensitivity — `ProMo2026` vs `promo2026` должны считаться одинаковыми; либо normalize на write (lowercase), либо `UNIQUE INDEX ON LOWER(short_code)`. Cross-shard uniqueness — при sharding по short_code constraint работает per-shard, но т.к. lookup тоже идёт через hash → один alias = один shard, всё ок.
> **Связанные вопросы:** [[Q6]] — collision avoidance для generated codes; [[Q4]] — code generation с гарантией uniqueness; [[Q16]] — abuse prevention (reserved trademarks).

**DB expiration:**
- `expires_at` timestamp
- Application checks on read → 404 if expired
- Lazy cleanup (cron deletes expired rows)

**Redis TTL:**
```
SET short_code long_url EX 86400
```
- Auto-expire в cache

**DynamoDB TTL:**
- Built-in TTL attribute
- Auto-delete (eventually) after expiration

**Scheduled deletion:**
- Cron: `DELETE FROM urls WHERE expires_at < NOW()` (batched)
- Or soft-delete: mark deleted, purge later

**Analytics consideration:**
- Even expired URLs — preserve click history? Depends on product


> [!mcq]
>
> **Вопрос:** Какая стратегия expiration / TTL для URL shortener оптимальна по cost и UX?
>
> ---
>
> #### A) Hard DELETE из DB сразу при истечении expires_at через synchronous trigger — гарантирует чистоту — ❌ Неверно
>
> **Что на самом деле:** Synchronous trigger на каждый redirect (проверять `expires_at < NOW()`, DELETE если истёк) — antipattern: redirect path должен быть read-only, любая запись добавляет lock contention и удваивает latency. Также при concurrent reads тот же expired URL может попасть в N DELETE — wasted work. Hard delete сразу теряет историю clicks и audit trail.
> **Откуда путаница:** "Истёк → удалить" — интуитивно, но смешивает logical expiration с physical cleanup.
> **Если бы это было правдой:** p99 redirect latency растёт; concurrent DELETE conflicts; нет возможности восстановить ссылку при ошибочном expiration; analytics за expired URLs ломается.
>
> ---
>
> #### B) Не использовать TTL вообще — URLs живут вечно, проще — ❌ Неверно
>
> **Что на самом деле:** Без TTL DB растёт неограниченно: бесплатные ссылки от анонимных пользователей (90% URLs) живут годами без cleanup. Storage cost растёт linearly, индексы становятся медленнее (B-tree depth, cache footprint), backup/restore времени-затратнее. Также phishing/spam URLs накапливаются — даже после revocation запись остаётся forever. Business model большинства shortener (Bit.ly, TinyURL, Short.io) включает TTL: 30 дней для anonymous, 1-5 лет для paid.
> **Откуда путаница:** "Storage cheap, не парься" — но при 6B+ записях каждая копейка × миллиарды = реальные деньги.
> **Если бы это было правдой:** Через 5 лет 60B+ записей в DB, многие никогда не читались; backup time 8+ часов; cold storage tier неприменим без TTL gate; GDPR compliance ("right to be forgotten") требует ручного процесса.
>
> ---
>
> #### C) Soft delete (флаг `deleted_at` или `is_expired`) + native DB TTL (DynamoDB TTL attribute) + background sweep с batch DELETE — двухфазный cleanup — ✓ Верно
>
> **Развёрнутое объяснение:** **Логически** URL считается expired когда `expires_at < NOW()` — app проверяет на read и возвращает 404 (не DELETE), сохраняя audit trail и возможность undo. **Физически** cleanup делается асинхронно: DynamoDB TTL автоматически удаляет items в течение 48 часов после `expires_at` (без cost — фоновая операция); для MySQL/Postgres — batched scheduled DELETE через cron в low-traffic window. Между logical expiration и physical delete — grace window (7-30 дней) когда ссылку можно восстановить (важно для accidental expiration или legal hold).
> **Пример:**
> ```sql
> -- Read path (no write)
> SELECT long_url FROM urls
> WHERE short_code = $1
>   AND (expires_at IS NULL OR expires_at > NOW())
>   AND deleted_at IS NULL;
> -- Returns 0 rows → 404 Gone
> 
> -- Background sweep (cron, hourly, batch=10k)
> DELETE FROM urls
> WHERE deleted_at < NOW() - INTERVAL '30 days'
>   AND short_code IN (
>     SELECT short_code FROM urls
>     WHERE deleted_at IS NOT NULL
>     ORDER BY deleted_at ASC LIMIT 10000
>   );
> ```
> ```yaml
> # DynamoDB
> Table: urls
>   TimeToLiveSpecification:
>     AttributeName: expires_at
>     Enabled: true
> # AWS auto-deletes within 48h of expiration, free
> ```
> **Когда применять:** Bit.ly использует DynamoDB TTL для free-tier ссылок (30 days). Twitter t.co — soft delete с 90-day retention для compliance. Любой mass-storage сервис где cleanup нельзя блокировать main flow: S3 Lifecycle Policies, Cloudflare KV namespace expiration, Redis EXPIRE.
> **Подводные камни:** DynamoDB TTL — eventual (до 48 часов задержки), не подходит для time-critical revocation; в этом случае дополнительная app-level проверка. Index on `expires_at` для sweep — занимает место, но без него full scan. Cache invalidation — expired URL в Redis должен быть удалён или иметь TTL <= DB expires_at, иначе serve expired content из cache.
> **Связанные вопросы:** [[Q9]] — cache TTL coordination; [[Q14]] — analytics для expired URLs (preserve history); [[Q16]] — revocation для malicious URLs (immediate, not lazy).
>
> ---
>
> #### D) Использовать Redis EXPIRE на DB-записях — Redis сам управляет TTL для всех слоёв — ❌ Неверно
>
> **Что на самом деле:** Redis EXPIRE работает только для Redis keys — не для DB rows. Redis — это **cache layer**, durable store (MySQL/DynamoDB) держит данные независимо. Если Redis expired key — cache miss приведёт к DB lookup, который вернёт URL (потому что DB не знает что Redis expired). Cross-layer TTL coordination требует **одного source of truth** (`expires_at` колонка в DB) + cache TTL <= DB TTL.
> **Откуда путаница:** Смешивание ролей cache и persistent storage; "Redis имеет TTL — давайте использовать только его".
> **Если бы это было правдой:** Несогласованность: Redis истёк, но DB вернула долговечный URL — expiration logic некорректна; при Redis restart все TTL теряются — все URLs становятся "вечными"; невозможно сделать revocation на DB-уровне (legal hold) без extra invalidation logic.

**On redirect:**
- Emit event async (don't block redirect)
- Kafka topic: `url_clicks`
- Don't write to DB synchronously (slow redirect)

**Event format:**
```json
{
  "short_code": "abc1234",
  "clicked_at": "2024-...",
  "ip": "1.2.3.4",
  "ua": "Mozilla/...",
  "referrer": "facebook.com"
}
```

**Processing:**
- Flink/Spark stream → aggregates (count per code per day)
- Store в time-series DB (InfluxDB, ClickHouse) or data warehouse
- Real-time dashboard reads aggregates

**Counter в main DB:**
- Not incrementing per-click (contention)
- Batch update every 1 min from aggregates
- Eventually consistent view of count

**Privacy:**
- IP anonymization (strip last octet)
- User consent (GDPR)

**Country lookup:**
- IP → country via MaxMind GeoIP DB


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. (!) Reliability и single point of failure? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**SPOFs to eliminate:**

**Load balancer:** multiple LBs (ELB, DNS round-robin).

**App servers:** horizontal scale (stateless); auto-scaling group.

**Cache (Redis):** cluster with replication + sentinel/cluster mode.

**DB:**
- Primary + replicas (automatic failover — AWS RDS Multi-AZ)
- Cross-region replication для DR

**CDN:** multi-vendor (CloudFront + Cloudflare) — rare, но possible.

**Counter (for code generation):**
- Single counter = SPOF
- Snowflake distributed IDs
- Or key ranges per host (host A uses codes 1-1M, host B 1M-2M)

**Circuit breakers:**
- Cache down → fall back to DB (degraded perf но works)
- DB down → serve from cache only (reads), reject writes

**Availability target:**
- 99.99% = ~50 min/year downtime
- Requires multi-region для planned maintenance too


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. Security (spam, phishing)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Malicious use:** shortener obscures destination → phishing via trusted domain.

**Defenses:**

**1. URL scanning:**
- At creation: check Google Safe Browsing API, Phishtank
- Block known malicious

**2. Moderation:**
- Human review flagged URLs
- User reporting mechanism

**3. Banned domains list:**
- Known scam domains blocklisted

**4. Preview page:**
- First click → interstitial "redirecting to X. Proceed?"
- Users can abort

**5. Rate limiting per user:**
- Anonymous user: 10 URLs/hour
- Authenticated: higher limit

**6. Abuse detection:**
- Many URLs to same domain → flag
- Patterns (bot activity)

**7. Post-creation monitoring:**
- Periodically re-scan existing URLs (target may become malicious later)
- Revoke if target changed to malicious

**Terms of service:** clear prohibited use; disable on violation.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. Rate limiting? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

**Why:** prevent abuse, DoS, cost control.

**Layers:**

**Global:**
- Per-IP: 100 requests/min (redirects)
- Per-user: 10 shortens/hour (unauth), 1000/hr (auth)

**Per-feature:**
- API keys: based on tier

**Algorithms:**
- Token bucket (flexible)
- Leaky bucket (smooth)
- Fixed window (simple)
- See [[rate-limiter-interview|Rate Limiter design]]

**Storage:** Redis (distributed counter).

**Headers:**
```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 987
X-RateLimit-Reset: 1640000000
Retry-After: 60
```

**429 Too Many Requests** — standard response.

**DDoS:** CDN (CloudFront) + WAF handle layer 7 attacks.

---

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [System Design](system-design-interview.md) — общие принципы ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Design Rate Limiter](design-rate-limiter-interview.md) — компонент
- [Caching Strategies](../architecture/caching-strategies-interview.md) — Redis, CDN
- [Database Architecture](../databases/database-architecture-interview.md) — SQL vs NoSQL
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — sharding, replication
- [Load Balancing](../architecture/load-balancing-interview.md) — fronting app servers
- [CAP Theorem](../architecture/cap-theorem-interview.md) — consistency trade-offs
- [Redis](../databases/redis-interview.md) — cache layer
- [DynamoDB](../databases/dynamodb-interview.md) — NoSQL option
- [Distributed Systems](../architecture/distributed-systems-interview.md) — sharding, consensus
