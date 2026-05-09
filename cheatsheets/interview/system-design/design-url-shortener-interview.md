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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q9. (!) Как scale reads? (cache, CDN) ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q10. (!) Cache strategy (write-through / lazy)? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. Как shard DB? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. Custom aliases? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. TTL / expiration? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. Analytics (click tracking)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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
