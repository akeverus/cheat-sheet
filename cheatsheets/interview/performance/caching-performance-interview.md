---
title: "Вопросы на собеседовании: Caching Performance"
description: "Cache performance tuning: hit ratio, eviction, TTL, stampede, warming, Redis/Memcached tuning, CDN, multi-level, Caffeine, metrics, troubleshooting"
tags:
  - interview
  - performance
  - caching-performance-interview
aliases:
  - "Caching Performance interview"
  - "Cache tuning"
  - "Cache hit ratio"
  - "Caching Performance собеседование"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `Caching Performance`

`Caching Performance` — метрики и tuning cache: **hit ratio**, eviction, TTL, thundering herd. Отличие от [стратегий кэширования](../architecture/caching-strategies-interview.md) — здесь фокус на **measurement и troubleshooting** производительности. Ошибки: low hit ratio, stampede, cache bloat, stale serving.

Дата последнего обновления: 2026-04-19

## Полезные ссылки

### Официальная документация

- [Redis performance](https://redis.io/docs/manual/performance/)
- [Memcached wiki](https://github.com/memcached/memcached/wiki)
- [Caffeine benchmarks](https://github.com/ben-manes/caffeine/wiki/Benchmarks)
- [Netflix EVCache](https://netflixtechblog.com/ephemeral-volatile-caching-in-the-cloud-8eabc6acbf05)
- [Google SRE Book — caching](https://sre.google/sre-book/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Метрики**
- [Q1. (!) Ключевые метрики cache?](#q1--ключевые-метрики-cache)
- [Q2. (!) Hit ratio — что считается "хорошо"?](#q2--hit-ratio--что-считается-хорошо)
- [Q3. (!) Как измерить cache impact на latency?](#q3--как-измерить-cache-impact-на-latency)

**Eviction и memory**
- [Q4. (!) LRU / LFU / TinyLFU — performance разница?](#q4--lru--lfu--tinylfu--performance-разница)
- [Q5. (!) Redis `maxmemory-policy` — выбор?](#q5--redis-maxmemory-policy--выбор)
- [Q6. Memory fragmentation Redis?](#q6-memory-fragmentation-redis)

**Cache stampede**
- [Q7. (!) Thundering herd / cache stampede?](#q7--thundering-herd--cache-stampede)
- [Q8. (!) Защита: mutex, probabilistic early expiration?](#q8--защита-mutex-probabilistic-early-expiration)
- [Q9. Request coalescing?](#q9-request-coalescing)

**TTL strategies**
- [Q10. (!) Как выбрать TTL?](#q10--как-выбрать-ttl)
- [Q11. Jittered TTL (prevent mass expiration)?](#q11-jittered-ttl-prevent-mass-expiration)

**Warming и invalidation**
- [Q12. (!) Cold start — cache warming strategies?](#q12--cold-start--cache-warming-strategies)
- [Q13. Invalidation performance (patterns)?](#q13-invalidation-performance-patterns)

**Multi-level**
- [Q14. (!) L1 (in-proc) + L2 (Redis) — зачем?](#q14--l1-in-proc--l2-redis--зачем)
- [Q15. Caffeine tuning?](#q15-caffeine-tuning)

**Network и serialization**
- [Q16. (!) Redis cluster overhead vs single node?](#q16--redis-cluster-overhead-vs-single-node)
- [Q17. Pipeline / MGET batching?](#q17-pipeline--mget-batching)
- [Q18. Serialization overhead (JSON vs MessagePack vs protobuf)?](#q18-serialization-overhead-json-vs-messagepack-vs-protobuf)

**CDN performance**
- [Q19. (!) CDN hit ratio и cache headers?](#q19--cdn-hit-ratio-и-cache-headers)
- [Q20. CDN tiered caching?](#q20-cdn-tiered-caching)

**Troubleshooting**
- [Q21. (!) Hot key problem?](#q21--hot-key-problem)
- [Q22. (!) Big keys problem?](#q22--big-keys-problem)
- [Q23. Cache not scaling — что проверить?](#q23-cache-not-scaling--что-проверить)

## Q1. (!) Ключевые метрики cache?

**Must-have метрики:**

**Hit ratio:**
```
hit_ratio = hits / (hits + misses)
```
- Главная метрика; target зависит от workload (80-99%)
- Низкое значение = cache бесполезен

**Eviction rate:**
- Rate of keys evicted (LRU/LFU)
- High = memory under pressure → increase size или tune TTL

**Latency:**
- Get / Set time p50/p95/p99
- Redis single-digit ms norm; > 10ms = problem (network, big keys)

**Throughput (ops/sec):**
- GET/SET per second
- Watch для capacity planning

**Memory usage:**
- `used_memory` vs `maxmemory`
- > 80% = eviction pressure

**Connection count:**
- Open connections (limit на server side)
- Too many = connection exhaustion

**Network bandwidth:**
- Can hit NIC limits before CPU

**Error rate:**
- Failed ops (connection refused, timeout, OOM)

**Redis-specific:**
- `instantaneous_ops_per_sec`
- `keyspace_hits` / `keyspace_misses`
- `evicted_keys`
- `expired_keys`
- `blocked_clients`

**Memcached:**
- `get_hits` / `get_misses`
- `bytes` / `limit_maxbytes`
- `evictions`

**Export:** `redis_exporter` / `memcached_exporter` → Prometheus → Grafana.

## Q2. (!) Hit ratio — что считается "хорошо"?

**Зависит от use case:**

**Tier 1 (hot data, e.g. session, config):** **99%+**
- Almost everything cached; miss rare
- Short TTL, pre-warmed

**Tier 2 (DB query cache, API response):** **90-95%**
- Most requests cached; некоторая invalidation
- Typical web app

**Tier 3 (CDN, static content):** **95-99%**
- Static rarely changes

**Tier 4 (long-tail data, e.g. user-generated):** **50-80%**
- Many unique keys, few repeats (Zipfian)
- Cache still useful, но не доминирует

**Metric calculation:**
- **Instantaneous:** last 1 min — volatile
- **5-15 min avg** — stable for alerting
- **Daily** — trend analysis

**Poor hit ratio causes:**
- TTL too short (eviction before reuse)
- Cache size too small (LRU eviction)
- Many unique keys (long tail, cache useless)
- Invalidation too aggressive
- Cold start (after deploy / restart)

**Fix workflow:**
1. Measure current hit ratio per cache
2. If < target → investigate
3. Check top-N misses (if possible) → patterns
4. Adjust: size, TTL, or restructure keys

**Hit ratio ≠ always "higher = better":**
- 100% hit можно achieve by caching garbage forever
- Balance с freshness (stale serving)

## Q3. (!) Как измерить cache impact на latency?

**Метод 1: direct measurement:**
```java
long start = System.nanoTime();
Value v = cache.get(key);
long cacheLatency = System.nanoTime() - start;

if (v == null) {
    start = System.nanoTime();
    v = db.query(key);  // slow path
    long dbLatency = System.nanoTime() - start;
    cache.put(key, v);
}
```

Log/metric both.

**Метод 2: A/B / feature flag:**
- Disable cache для subset of traffic
- Compare end-to-end latency
- Measures "real" impact (including cache overhead)

**Метод 3: p99 analysis:**
- Sort requests by latency
- P99 = slowest 1% — often cache misses
- Delta (P99 vs P50) = cache effectiveness

**Calculate theoretical max:**
```
avg_latency = hit_ratio × cache_lat + (1 - hit_ratio) × miss_lat
```

Example:
- Cache hit = 1ms
- DB = 100ms
- Hit ratio 95% → avg = 0.95×1 + 0.05×100 = 5.95ms
- Hit ratio 90% → avg = 10.9ms (2x slower!)
- **5% drop in hit ratio nearly doubled latency**

**Показывает why hit ratio is king.**

**Instrumentation:**
- OpenTelemetry spans с `cache.hit=true/false` attribute
- Split p99 по attribute → clear picture

**Watch:** cache itself может add latency:
- Network RTT for Redis (1-2ms)
- Serialization
- In-proc cache (Caffeine) — microseconds, essentially free

## Q4. (!) LRU / LFU / TinyLFU — performance разница?

**LRU (Least Recently Used):**
- Evict oldest touched
- Pros: simple, good для sequential workloads
- Cons: one-hit wonders pollute cache (recent but low-value)

**LFU (Least Frequently Used):**
- Evict lowest count
- Pros: keeps popular items
- Cons: "classic" LFU never forgets → stale popular displaces new

**TinyLFU (Caffeine, modern Redis `allkeys-lfu`):**
- Frequency estimator (Count-Min Sketch) + windowed LRU
- Approximates LFU с low memory
- Handles both recency + frequency
- **Benchmark winner** на realistic workloads

**Hit ratio comparison (realistic workload, e.g. database/search):**
- LRU: ~70-80% hit ratio
- LFU: ~75-82%
- **TinyLFU: 85-95%** — noticeably better

**Admission filter:**
- New item замещает incumbent only if estimated frequency > incumbent
- Prevents cold-hit flushing warm cache

**Redis modes:**
- `allkeys-lru` (default in many configs)
- `allkeys-lfu` (since 4.0) — approximated LFU
- Combine с `maxmemory-samples` (default 5; higher = better approx, more CPU)

**Caffeine:** uses TinyLFU — usually right choice for JVM in-proc.

**Verdict:** на типичном Zipfian web workload — **TinyLFU superior** к LRU by 5-15% hit ratio.

## Q5. (!) Redis `maxmemory-policy` — выбор?

**Policies:**

- **`noeviction`** — return error on OOM (default in some configs)
- **`allkeys-lru`** — evict LRU across all keys
- **`allkeys-lfu`** — evict LFU across all keys (4.0+)
- **`allkeys-random`** — random eviction
- **`volatile-lru`** — LRU only among keys with TTL
- **`volatile-lfu`** — LFU with TTL
- **`volatile-random`** — random with TTL
- **`volatile-ttl`** — shortest TTL first

**Выбор:**

**Pure cache (all data cacheable, no distinction):**
- `allkeys-lfu` — usually best hit ratio
- `allkeys-lru` — simpler, ~ as good

**Cache + session store (mixed):**
- `volatile-lru` or `volatile-lfu` — sessions have TTL; evict cache; preserve session
- But! Persistent keys (no TTL) never evicted → can fill memory — monitor!

**Write-through persistence:**
- `noeviction` — app must manage; accept OOM errors
- Use Redis как **storage**, not cache

**Tuning:**
```
maxmemory 10gb
maxmemory-policy allkeys-lfu
maxmemory-samples 10  # default 5; higher = better approximation
```

**Anti-pattern:** `noeviction` with unlimited TTL — grows until OOM → crash.

**Monitor:** `evicted_keys` counter; spikes indicate under-provisioned.

## Q6. Memory fragmentation Redis?

**Fragmentation ratio:**
```
mem_fragmentation_ratio = used_memory_rss / used_memory
```

- 1.0 = no frag
- 1.0-1.5 = normal
- \> 1.5 = high frag (waste)
- < 1.0 = swapping (BAD!) — OS has swapped Redis memory → terrible latency

**Причины:**
- Variable-sized keys/values (jemalloc arenas)
- Deletes leave holes
- Long-running process с lots writes

**Fix:**

**Active defrag (Redis 4.0+):**
```
activedefrag yes
active-defrag-ignore-bytes 100mb
active-defrag-threshold-lower 10
active-defrag-threshold-upper 100
active-defrag-cycle-min 5
active-defrag-cycle-max 75
```
- Background process relocates values
- CPU overhead (tune cycle-max)

**Restart:**
- Nuclear option; clears cache
- Failover to replica, restart primary

**Allocator:** jemalloc (Redis default) — best fragmentation; don't use libc malloc.

**Monitor:** alert if `mem_fragmentation_ratio > 1.5` for extended period.

## Q7. (!) Thundering herd / cache stampede?

**Scenario:**
1. Hot key expires
2. 1000 concurrent requests miss cache
3. All 1000 rebuild value (hit DB/compute)
4. DB crushed; latencies spike

**Problem:** cache was serving at 1000 RPS; miss → 1000 RPS slams DB.

**Consequences:**
- DB overload
- Cascade failure
- Latency spike
- Worse if rebuild takes seconds

**Prevention:**

**1. Distributed lock (mutex):**
- First miss acquires lock; others wait
- One rebuilds; others read fresh value
- Code sketch:
```java
if (cache.get(key) == null) {
    if (lock.tryLock(key, 5s)) {
        try {
            val = db.load();
            cache.put(key, val);
        } finally { lock.unlock(); }
    } else {
        waitAndRetry();
    }
}
```

**2. Probabilistic early expiration:**
- Rebuild probabilistically before expiration
- Near expiry → higher chance to rebuild
- Formula (Vattani et al. 2015):
```
now - (delta × β × ln(random())) >= expiry
```
- **Pros:** no lock, smooths rebuild load

**3. Stale-while-revalidate:**
- Serve stale, trigger async rebuild
- OK if slight staleness acceptable (typical)

**4. Request coalescing (in-process):**
- Single-flight: deduplicate concurrent requests
- Go `singleflight`, Java `CompletableFuture` cache

**5. Warmup:**
- Pre-populate cache before releasing traffic

## Q8. (!) Защита: mutex, probabilistic early expiration?

**Distributed mutex (Redis):**

```python
def get_with_lock(key):
    val = redis.get(key)
    if val:
        return val
    
    lock_key = f"lock:{key}"
    if redis.set(lock_key, 1, nx=True, ex=10):  # 10s lock
        try:
            val = db.query(key)
            redis.set(key, val, ex=300)
            return val
        finally:
            redis.delete(lock_key)
    else:
        # Another is rebuilding; wait or serve stale
        time.sleep(0.1)
        return redis.get(key) or get_with_lock(key)  # recursion limit
```

**Pros:** simple; guarantees single rebuilder.
**Cons:** waiters block; lock release critical (use short TTL).

**Redlock (Redis multi-master):** more robust distributed lock; overkill для most cache use.

**Probabilistic (XFetch) algorithm:**
```python
def xfetch(key, ttl, compute_fn):
    data, expiry, delta = redis.get_with_meta(key)
    now = time.time()
    if data and now - delta * BETA * math.log(random.random()) < expiry:
        return data  # serve cached
    
    # Probabilistically refresh early
    start = time.time()
    data = compute_fn()
    delta = time.time() - start
    redis.set_with_meta(key, data, ttl, delta)
    return data
```

- `delta` = time to recompute (stored alongside)
- `BETA` = tuning constant (1.0 typical)
- As near expiry → probability rebuild → 1
- No herd: requests spread over rebuild window

**Caffeine** (Java in-proc) — built-in `refreshAfterWrite`: async refresh while returning cached.

**Comparison:**

| Approach | Simplicity | Effectiveness | Latency |
|----------|-----------|---------------|---------|
| Mutex | Medium | High | Waiters blocked |
| Probabilistic | Complex | High | None blocked |
| Stale-while-revalidate | Easy | High (for tolerant apps) | None blocked |
| Warmup | Easy | High (if feasible) | One-time |

## Q9. Request coalescing?

**Coalescing** — de-duplicate **concurrent identical requests** within single process.

**Без coalescing:**
- Request A: miss, starts DB query
- Request B (1ms later): miss, starts another identical DB query
- 2 DB queries for same result

**С coalescing:**
- Request A starts task
- Request B detects A's in-flight task, **awaits same future**
- 1 DB query; both get result

**Go `singleflight`:**
```go
var group singleflight.Group
result, _, _ := group.Do(key, func() (interface{}, error) {
    return db.Query(key)
})
```

**Java (CompletableFuture):**
```java
ConcurrentHashMap<String, CompletableFuture<Value>> inflight = new ConcurrentHashMap<>();

public Value get(String key) {
    Value v = cache.get(key);
    if (v != null) return v;
    
    return inflight.computeIfAbsent(key, k -> 
        CompletableFuture.supplyAsync(() -> {
            Value val = db.query(k);
            cache.put(k, val);
            inflight.remove(k);
            return val;
        })
    ).join();
}
```

**Caffeine** does automatic coalescing when using `.build(CacheLoader)`.

**Limits:**
- Per-process only (не помогает если 100 pods miss одновременно)
- Combine с distributed mutex для cross-process

**Gotcha:** exception in one request → all sharing waiters fail. Often OK but know it.

## Q10. (!) Как выбрать TTL?

**Factors:**

**1. Freshness requirement:**
- Real-time (stock prices) → seconds
- Product details → minutes-hours
- User profile → hours-days
- Config → hours-day

**2. Backend cost of miss:**
- Cheap (in-memory app) → short TTL OK
- Expensive (multi-join DB query, 500ms) → long TTL

**3. Rate of change:**
- Rarely changes → long TTL (days)
- Frequently changes → short TTL или event-driven invalidation

**4. Invalidation capability:**
- Если умеете invalidate (pub/sub, event bus) → long TTL acceptable
- Без invalidation → TTL = staleness tolerance

**Pragmatic defaults:**
- API responses: 5-60 seconds
- DB query cache: 1-5 min
- Config / reference data: 1h
- Sessions: 24h

**Calculation:**
```
hit_ratio = TTL_length / (TTL_length + request_interval)
```

Example:
- Key requested every 100ms
- TTL = 5s → ratio = 5 / 5.1 ≈ 98%
- TTL = 1s → ratio = 1 / 1.1 ≈ 91%
- Increasing TTL 5x → hit ratio +7 points

**Tune by workload:**
- Measure hit ratio at different TTLs
- Plot → find elbow

**Gotcha:** too long TTL + no invalidation → user sees stale. Test: does 1-hour stale OK?

## Q11. Jittered TTL (prevent mass expiration)?

**Problem:**
- 10,000 keys set at same time with TTL=60s
- All expire simultaneously
- 10,000 misses → stampede on DB

**Fix:** add random jitter to TTL.

**Implementation:**
```python
TTL_BASE = 60
TTL_JITTER = 10  # ±10s
ttl = TTL_BASE + random.randint(-TTL_JITTER, TTL_JITTER)
redis.set(key, val, ex=ttl)
```

- Keys now expire over 50-70s window
- Smooth load instead of spike

**Why commonly needed:**
- Batch warming / cache build → all TTLs sync
- Daily dump / hourly refresh → sync TTLs
- Deploy event (все pods warm up cache concurrently)

**Caffeine:**
- `expireAfterWrite` with `Expiry` interface can return random duration
- Or add jitter to `Duration.ofSeconds(60 + random())`

**Another pattern — pre-expiry refresh:**
- Refresh at 80% of TTL (async)
- Acts как natural jitter + avoids mass miss

**Effect:** smooth rebuild rate instead of spike — DB usage graph changes from sawtooth to flat.

## Q12. (!) Cold start — cache warming strategies?

**Cold cache after:**
- Deploy / restart
- Cache cluster failover
- Scaling up (new instance)
- TTL expires en masse

**Impact:** fraction (or all) requests hit DB → latency spike, potential overload.

**Strategies:**

**1. Eager warmup (pre-load):**
- Before exposing new instance to traffic, populate cache с most-requested keys
- Query top-N from access logs
- Readiness probe succeeds after warmup done

**2. Gradual traffic ramp:**
- Load balancer: new instance gets 1%, 5%, 10%... traffic over minutes
- Cache fills naturally without overload

**3. Replication:**
- Cache replica alongside primary (Redis replica)
- On primary restart → promote replica (pre-warmed)
- L2 cache remote (Redis) + L1 local (Caffeine): L2 survives L1 restart

**4. Persistent cache:**
- Redis AOF / RDB — survives restart (though load takes time)
- Memcached — не persistent; cold always

**5. Background refresh from sources of truth:**
- ETL/batch job writes cache
- Event stream (Kafka) → cache update

**6. Don't cache everything:**
- Accept first-request slowness
- SLO allows, maybe OK

**Измерение:**
- Time to reach target hit ratio (e.g. 95%)
- "Warmup time" должен быть < max allowable cold period

**K8s:** readiness probe returns healthy only после warmup. Otherwise load balancer sends traffic to cold pod.

## Q13. Invalidation performance (patterns)?

**"There are only two hard things in Computer Science: cache invalidation and naming things."** — Phil Karlton.

**Patterns и performance:**

**1. TTL-based:**
- Simplest, no explicit invalidation
- Accept staleness (up to TTL)
- **Cost:** zero overhead; **inconsistency:** up to TTL window

**2. Write-through:**
- On DB write → update cache synchronously
- **Cost:** write latency = max(DB, cache); fail if cache down
- **Consistency:** strong

**3. Write-behind:**
- Update cache; async DB write
- **Cost:** cheap; **risk:** data loss on crash

**4. Event-based (pub/sub):**
- DB change → event → subscribers invalidate caches
- **Cost:** infra (Kafka/Redis pub/sub); **consistency:** seconds
- Good fit for CQRS, event sourcing

**5. CDC (Change Data Capture):**
- Debezium reads DB WAL → topic → cache invalidation consumer
- **Cost:** complex; **consistency:** near-realtime
- Scales well

**6. Tag-based (Varnish, Fastly):**
- Group keys by tag; invalidate tag = invalidate all
- Example: tag "user:42" on all user 42 pages; write → tag invalidation → all caches drop

**7. Version-based:**
- Include version in cache key: `product:v5:123`
- Update DB → increment version → effectively new cache entry
- Old keys eventually expire

**Performance考量:**
- Broadcast invalidation = N×M messages (N caches × M keys) — scale?
- Invalidation lag = stale serving
- Too aggressive = low hit ratio

**Best practice:**
- Start с TTL (simple)
- Add invalidation where staleness unacceptable
- Not "invalidate everything on write" — be targeted

## Q14. (!) L1 (in-proc) + L2 (Redis) — зачем?

**L1: in-process** (Caffeine, Guava, local HashMap):
- Latency: nanoseconds-microseconds
- Capacity: limited by JVM heap (100MB-1GB typical)
- No network

**L2: distributed** (Redis, Memcached):
- Latency: 0.5-2ms (network)
- Capacity: terabytes
- Shared across app instances

**Hierarchy:**
```
Request → L1 (check in-proc) → L2 (check Redis) → DB
         hit → return (μs)    hit → return (ms)    miss → rebuild
```

**Benefits:**
- **Ultra-fast** для hot keys (L1 hits)
- **Relieve Redis load** (Redis CPU freed)
- **Network savings** (fewer Redis GETs)
- **Resilient** к Redis outage (L1 keeps serving)

**Trade-off: consistency:**
- L1 и L2 могут desync
- L2 updated → L1 stale (for its TTL)
- OK для most use cases (short L1 TTL)

**L1 TTL:** shorter than L2 (e.g. L1=1min, L2=10min).

**Invalidation:**
- Can publish invalidation event (Redis pub/sub) → all L1s drop key
- Or just accept L1 staleness for brief period

**When use:**
- Read-heavy (10,000+ RPS to one key) → L1 critical
- Hot keys (Pareto distribution) — 20% keys = 80% requests
- Latency-sensitive (SLO in sub-millisecond)

**When not:**
- Low RPS (L1 overhead > benefit)
- Every read needs exact latest value

## Q15. Caffeine tuning?

**Caffeine** — high-performance Java cache lib (SLF4J-style simplicity).

**Config:**
```java
Cache<K, V> cache = Caffeine.newBuilder()
    .maximumSize(10_000)         // LFU-based eviction
    .expireAfterWrite(5, MINUTES) // TTL
    .expireAfterAccess(10, MINUTES) // TTI (time-to-idle)
    .refreshAfterWrite(1, MINUTES)  // async refresh for freshness
    .recordStats()                  // enable metrics
    .build(key -> db.load(key));
```

**`maximumSize` vs `maximumWeight`:**
- Size: count entries
- Weight: custom weigher function (`entry.size()` for bytes)
- Pick based на whether values vary significantly

**`expireAfterWrite` vs `expireAfterAccess`:**
- Write: fixed TTL from creation
- Access: reset on read (hot keys stay)
- Combine with `expireAfter(Expiry)` для custom logic

**`refreshAfterWrite`:**
- After X, async reload in background
- Return stale while loading → no miss penalty
- Critical for stampede prevention

**Stats:**
```java
CacheStats stats = cache.stats();
stats.hitRate();       // e.g., 0.94
stats.evictionCount();
stats.missRate();
```

Export via Micrometer to Prometheus:
```java
CaffeineCacheMetrics.monitor(registry, cache, "user_cache");
```

**Sizing:**
- Start с 10x expected unique keys
- Monitor hit ratio → adjust
- Too large → GC pressure (old-gen fills)

**Loading cache:**
- `CacheLoader` — sync load on miss
- `AsyncCacheLoader` — non-blocking
- Coalesces concurrent requests for same key (no herd)

**Spring Boot:**
```yaml
spring.cache.type: caffeine
spring.cache.caffeine.spec: maximumSize=1000,expireAfterWrite=10m
```

## Q16. (!) Redis cluster overhead vs single node?

**Single-node Redis:**
- Lowest latency
- Simple
- Limit: 1 CPU core (mostly) + single-machine RAM

**Redis Cluster:**
- Shard across N masters
- 16384 hash slots
- Horizontal scale (data + writes)

**Overhead:**

**1. Network hops:**
- Client → wrong node? → redirect (MOVED) → correct node
- SMART client caches slot map; avoids

**2. Cross-slot operations:**
- `MGET key1 key2` — fails if keys in different slots
- Fix: **hash tags** `{user:42}:session`, `{user:42}:orders` → same slot
- Or use Lua / pipeline per-slot batching

**3. Resharding:**
- Moving slots = brief MOVED redirects
- Well-handled by cluster-aware clients (Lettuce, Jedis JedisCluster)

**4. Multi-key transactions (MULTI/EXEC):**
- Cross-slot = impossible
- Plan data colocation

**Measurement:**
- Single-node p99: ~1ms (localhost) / 2-3ms (within VPC)
- Cluster p99: similar if slot map cached; worse if not

**Sentinel (HA, не shard):**
- Auto-failover на replica
- Single-master performance + HA
- Still 1 node capacity

**Cluster won't help:**
- Single hot key (one slot = one node)
- Low data volume that fits на single machine

**Cluster helps:**
- Total dataset > RAM limit
- Write throughput > single node CPU
- Spread load

**Alternatives:**
- Partition at app layer (consistent hashing across Redis instances)
- Redis Enterprise (commercial) — transparent sharding

## Q17. Pipeline / MGET batching?

**Problem:** 100 Redis GETs = 100 RTTs (500ms + if cross-region).

**Pipeline:**
```python
p = redis.pipeline()
for key in keys:
    p.get(key)
results = p.execute()  # 1 RTT for all
```

**MGET:**
```python
results = redis.mget(keys)  # single command, 1 RTT
```

**Differences:**

| Aspect | Pipeline | MGET |
|--------|----------|------|
| RTT | 1 (all cmds batched) | 1 |
| Command types | Any (GET, SET, ...) | GET only |
| Atomicity | No (unless MULTI wrapped) | Single command |
| Cluster support | Needs cluster-aware client | Keys must share slot |

**Pipeline for mixed:**
```python
p = redis.pipeline()
p.get(key1)
p.set(key2, val)
p.incr(counter)
res1, _, counter = p.execute()
```

**Cluster pipeline:**
- Keys split across nodes by slot
- Lettuce: `RedisClusterAsyncCommands.mget(keys...)` splits automatically
- Jedis: manual split

**Performance gain:**
- 100 ops, 1ms RTT → 100ms sequential vs 2ms pipelined = **50x faster**
- Savings grow with cross-region (20+ ms RTT)

**Rule of thumb:** batch when you know multiple keys upfront; latency dominated by RTT.

**Limit:** big pipelines consume memory (client + server); keep batches sensible (100-1000).

## Q18. Serialization overhead (JSON vs MessagePack vs protobuf)?

**Measurement** (1KB object):

| Format | Size | Ser speed | Deser speed | Human readable |
|--------|------|-----------|-------------|----------------|
| JSON | 1000B | Fast | Fast | Yes |
| MessagePack | 700B | Fast | Fast | No |
| Protobuf | 600B | Fast | Fast | No |
| Kryo (Java) | 550B | Fastest JVM | Fastest JVM | No |
| Avro | 600B | Medium | Medium | No (schema) |
| Fury (Apache) | 500B | Fastest | Fastest | No |
| Gzip(JSON) | 400B | Slow | Slow | No |
| Java Serialization | 1500B | Slow | Slow | No |

**Trade-offs:**
- JSON: universal, readable, но largest + slowest
- MessagePack: binary JSON, easy adoption
- Protobuf: schema upfront → version-safe + compact
- Kryo: JVM-specific; fast; backward compat questions
- Fury: 2020s benchmark leader
- **Avoid:** Java Serialization (slow, CVE-prone, bloated)

**Cache-specific:**
- Small value (< 1KB): serialization overhead dominates
- Large value (> 10KB): network transfer dominates → compression helps
- **Hot key:** serialize once, cache serialized bytes

**Redis specifics:**
- Store as binary (bytes)
- Compression at serialization (gzip, lz4, zstd) — trade CPU for network
- LZ4 — fast decompression; Zstd — better ratio

**Benchmark своего workload:**
```java
// ~ 5k ops/sec JSON vs 15k ops/sec protobuf типично
```

**Don't over-optimize:**
- If cache takes 0.5% of request time, 2× faster ser = 0.25% win
- Profile before switching

## Q19. (!) CDN hit ratio и cache headers?

**CDN hit ratio** — % of requests served from edge vs origin.

**Goal:** 90%+ typical; static-heavy >98%.

**Влияющие headers:**

**`Cache-Control`:**
- `max-age=3600` — cache 1 hour
- `s-maxage=7200` — CDN-specific TTL (vs browser)
- `public` / `private` — CDN cacheable vs not
- `no-cache` — revalidate each time
- `no-store` — don't cache at all
- `must-revalidate` — strict после expiry
- `stale-while-revalidate=60` — serve stale для 60s while revalidating

**`Vary`:**
- List headers that vary cached response
- `Vary: Accept-Encoding` — separate cache for gzip/br
- `Vary: User-Agent` — disaster (one entry per UA) — avoid!
- `Vary: Cookie` — also often disaster

**`ETag` / `Last-Modified`:**
- Revalidation: `If-None-Match: "etag"` → 304 Not Modified (no body)
- Saves bandwidth, не latency

**Common mistakes:**
- `Set-Cookie` — by default most CDNs don't cache responses with Set-Cookie
- Query strings (CDN may treat `?ts=1`, `?ts=2` as different objects)
- `Cache-Control: private` — skip CDN; want `public`

**Optimizations:**
- **Normalize query strings:** strip analytics params (`utm_*`)
- **Cache key engineering:** include only важные params
- **Tiered caching:** see next question

**Measurement:**
- CDN dashboard (CloudFront, Fastly) shows hit ratio
- Low hit ratio → check headers, cache key config, expiration

**Cache-Control best practices:**
- Static assets (JS/CSS/images): `max-age=31536000, immutable` + fingerprint filename
- HTML: `max-age=0, s-maxage=60` (clients always re-fetch, CDN caches briefly)
- API: per-endpoint; often `private, max-age=0`

## Q20. CDN tiered caching?

**Without tiered:**
```
100 edge POPs → origin
All 100 miss same object → origin slammed with 100 requests
```

**Tiered / shield POP:**
```
Edge POPs → shield POP (regional) → origin
First edge miss fetches from shield; shield fetches from origin ONCE
Other edges hit shield (shield pre-populated)
```

**Benefits:**
- **Origin offload** (9x to 100x reduction)
- Faster miss resolution для later edges (shield closer than origin)

**Vendors:**
- **Fastly:** shielding — choose a POP as shield
- **CloudFront:** Origin Shield — enable per-distribution
- **Cloudflare:** Argo Tiered Cache — auto-select best shield

**Configuration:**
- Pick shield closer to origin than edges (e.g., origin in us-east-1 → shield in us-east-1)
- Or closer to users (depends on pattern)

**When helps:**
- Large number of edge POPs (100+)
- Long-tail content (not everything popular)
- High origin load bills

**When not:**
- Small CDN footprint (few POPs)
- Cache TTL очень short (shield also misses often)

**Cost:** enable costs slightly more (shield request billed), but offset by origin savings.

## Q21. (!) Hot key problem?

**Hot key:** single key receiving disproportionate load (e.g., 80% of GETs для one product).

**Problem:**
- Redis node serving that key CPU-bound
- Network NIC to that node bottleneck
- Cluster rebalancing won't help (key can't split)

**Detection:**
- `redis-cli --hotkeys` (sampling-based)
- `MONITOR` — capture commands (careful, high overhead)
- Application metrics: per-key hit count

**Solutions:**

**1. In-process L1 cache (Caffeine):**
- Hot key mostly served from process memory
- Redis load drops dramatically

**2. Read replicas:**
- Route reads к replica for that key
- Redis Cluster replica reads (RE-only) — application opt-in

**3. Key sharding (multi-key):**
- Duplicate key across N instances: `product:123:shard0`...`product:123:shard9`
- App chooses random shard for read; writes must update all
- Good for read-heavy static

**4. Pre-compute / denormalize:**
- If hot key = expensive query → materialize elsewhere

**5. Client-side sampling:**
- 1% of reads hit Redis; 99% served from L1 cache в process
- Invalidate L1 on Redis pub/sub

**Monitor:** alert if single key > 5% of total ops.

**Real-world:** Twitter "Justin Bieber problem" — single user timeline caused hot shard; solution custom sharding.

## Q22. (!) Big keys problem?

**Big key:** single value very large (> 100KB, especially MB).

**Problems:**
- **Latency spike:** GET на 10MB key = 10MB network transfer each request
- **Blocking:** Redis single-threaded; reading big key blocks other ops
- **Memory fragmentation:** allocation/deallocation pain
- **Replication lag:** big key write sends 10MB к replicas
- **Hash slot imbalance:** if large key in one slot, that node bigger

**Detection:**
- `redis-cli --bigkeys` (sampling)
- `MEMORY USAGE key` (exact size)
- `DEBUG OBJECT key` (detailed)

**Common causes:**
- Large list (millions of elements)
- Large hash (thousands of fields)
- Large set
- Large serialized blob

**Solutions:**

**1. Split into smaller chunks:**
- Instead of `SET user:42 {big_json}` → `HSET user:42 field1 val1` (access patterns may improve)
- Or shard manually: `chunk1`, `chunk2`...

**2. Use collections instead of blobs:**
- Instead of JSON list → Redis List (RPUSH/LRANGE) — O(1) per element

**3. Pagination:**
- `LRANGE list 0 99` instead of `LRANGE list 0 -1` (all)

**4. Compression:**
- Store compressed blob; decompress client-side

**5. Move to real DB / object store:**
- Redis not storage for big blobs; use S3 / DB + pointer in cache

**6. Scan instead of MEMBERS:**
- `SMEMBERS huge_set` → blocks; use `SSCAN` cursor

**Monitor:** alert on `MEMORY USAGE > 1MB` for any key.

## Q23. Cache not scaling — что проверить?

**Symptoms:**
- Latency rising with load
- Hit ratio dropping
- CPU / memory saturation

**Debug checklist:**

**1. Single node bottleneck:**
- Redis single-threaded — CPU core saturated at ~100k ops/sec
- Fix: cluster, replica reads

**2. Network NIC:**
- 1 Gbps saturated; 10 Gbps available?
- Big values → multiply RPS × size = bandwidth need

**3. Connection limit:**
- `CONFIG GET maxclients` — hit limit?
- App side connection pool too small → queueing

**4. Hot key (see Q21):**
- One key overwhelming
- Spread via L1

**5. Big keys (see Q22):**
- Blocking ops

**6. Slow commands:**
- `SLOWLOG GET 10` — recent slow commands
- `KEYS *` (never use in prod!), `HGETALL` on big hash, `SMEMBERS`

**7. Persistence:**
- `BGSAVE` spike? `RDB` sync? `AOF` rewrite?
- Fork() on large heap: Copy-on-Write pressure
- Disable persistence на pure cache

**8. Memory fragmentation:**
- `mem_fragmentation_ratio > 1.5` — see Q6

**9. Swap:**
- `mem_fragmentation_ratio < 1` — swapped out; BAD; disable swap

**10. Client-side issue:**
- Connection pool too small
- Serialization CPU bound
- GC pauses in app (cache look-ups blocked)

**Tool:**
- `redis-cli --latency`, `redis-cli --latency-history`
- Grafana dashboard for Redis

**Scaling options:**
- Vertical: bigger box (limited by single-thread)
- Horizontal: cluster (data sharding) или replication (read scaling)
- Caching layers (L1, CDN, more levels)

---

## See also

- [Caching Strategies](../architecture/caching-strategies-interview.md) — patterns (write-through, aside)
- [Database Performance](database-performance-interview.md) — cache reduces DB load
- [Redis](../databases/redis-interview.md) — deeper Redis internals
- [JVM Performance Tuning](jvm-performance-tuning-interview.md) — L1 cache memory
- [Memory Management](memory-management-interview.md) — in-proc cache GC impact
- [Application Profiling](application-profiling-interview.md) — how to measure cache overhead
- [Network Performance](network-performance-interview.md) — RTT к Redis
- [Performance Testing](performance-testing-interview.md) — load test with cache
- [Load Balancing](../architecture/load-balancing-interview.md) — session affinity, cache locality
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — caching as scale tool
