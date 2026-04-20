---
title: "Вопросы на собеседовании: Design Rate Limiter"
description: "System design rate limiter: token bucket, leaky bucket, fixed/sliding window, Redis INCR, distributed consistency, fairness, 429 response, Envoy RL"
tags:
  - interview
  - system-design
  - design-rate-limiter-interview
aliases:
  - "Rate Limiter design"
  - "Token bucket"
  - "Sliding window counter"
  - "Rate Limiter собеседование"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `Design Rate Limiter`

`Rate Limiter` — critical defensive component: защита от abuse, DoS, fair resource sharing, cost control. Classical system design вопрос: covers algorithms (token/leaky bucket, windows), data structures, distributed consistency, Redis patterns.

Дата последнего обновления: 2026-04-19

## Полезные ссылки

- [Stripe rate limiting blog](https://stripe.com/blog/rate-limiters)
- [Cloudflare rate limiting](https://developers.cloudflare.com/waf/rate-limiting-rules/)
- [Envoy rate limit](https://www.envoyproxy.io/docs/envoy/latest/intro/arch_overview/other_features/global_rate_limiting)
- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [Redis rate limiting patterns](https://redis.io/learn/develop/java/spring/rate-limiting)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Requirements**
- [Q1. (!) Зачем rate limiter, use cases?](#q1--зачем-rate-limiter-use-cases)
- [Q2. (!) Functional и non-functional requirements?](#q2--functional-и-non-functional-requirements)

**Algorithms**
- [Q3. (!) Token bucket?](#q3--token-bucket)
- [Q4. (!) Leaky bucket?](#q4--leaky-bucket)
- [Q5. (!) Fixed window counter?](#q5--fixed-window-counter)
- [Q6. (!) Sliding window log?](#q6--sliding-window-log)
- [Q7. (!) Sliding window counter (hybrid)?](#q7--sliding-window-counter-hybrid)
- [Q8. Сравнение алгоритмов?](#q8-сравнение-алгоритмов)

**Distribution**
- [Q9. (!) Distributed rate limiter — challenges?](#q9--distributed-rate-limiter--challenges)
- [Q10. (!) Redis-based implementation?](#q10--redis-based-implementation)
- [Q11. Lua script для atomicity?](#q11-lua-script-для-atomicity)

**Design**
- [Q12. (!) Где размещать rate limiter? (edge, gateway, service)](#q12--где-размещать-rate-limiter-edge-gateway-service)
- [Q13. Rate limit granularity (user, IP, API key)?](#q13-rate-limit-granularity-user-ip-api-key)
- [Q14. Multiple buckets (per-endpoint + global)?](#q14-multiple-buckets-per-endpoint--global)

**Production**
- [Q15. (!) Response format (429)?](#q15--response-format-429)
- [Q16. (!) Что делать при Redis outage?](#q16--что-делать-при-redis-outage)
- [Q17. Hot user / hot key problem?](#q17-hot-user--hot-key-problem)
- [Q18. Rate limit vs quota?](#q18-rate-limit-vs-quota)

## Q1. (!) Зачем rate limiter, use cases?

**Use cases:**

- **Prevent abuse:** attacker spams API → limiter blocks
- **DDoS mitigation:** layer 7 protection
- **Fair resource sharing:** no tenant monopolizes
- **Cost control:** cloud resources, API calls (OpenAI, Twilio)
- **Backpressure:** protect downstream (DB, slow service)
- **SLA enforcement:** paid tier gets higher limits
- **Compliance:** API gateway regulated quotas

**Real examples:**
- Twitter API: 500k/15min per app
- GitHub API: 5000/hr per authenticated user
- AWS API: service-specific TPS limits
- Google search: undocumented limits

**Typical policies:**
- 100 req/min per IP
- 1000 req/hour per API key
- 10 req/sec per endpoint globally

## Q2. (!) Functional и non-functional requirements?

**Functional:**
- Allow N requests в time window
- Reject over-limit (429 Too Many Requests)
- Different limits per user/IP/API key
- Support multiple policies (per-endpoint, global)

**Non-functional:**
- **Low latency** (rate check adds < 10ms)
- **High availability** (don't block legit traffic on failure)
- **Scalable** (millions of users)
- **Accurate** (or acceptable approximation)
- **Fair** (no user gets zeroed by another's traffic)
- **Distributed consistency** (user's limit honored across all instances)

**Trade-offs:**
- Strict accuracy vs performance
- Per-instance (fast, loose) vs shared Redis (accurate, adds RTT)
- Memory per user (sliding window log expensive)

## Q3. (!) Token bucket?

**Model:**
- Bucket holds tokens
- Tokens added at rate R per sec
- Each request consumes 1 token
- Bucket capacity = burst limit
- Empty bucket → reject

**Parameters:**
- `capacity`: max tokens (burst size)
- `refill_rate`: tokens per second

**Allows bursts** up to capacity, смоды to refill rate.

**Implementation:**
```python
class TokenBucket:
    def __init__(self, capacity, refill_rate):
        self.capacity = capacity
        self.refill_rate = refill_rate
        self.tokens = capacity
        self.last_refill = time.time()
    
    def allow(self):
        now = time.time()
        elapsed = now - self.last_refill
        self.tokens = min(self.capacity, self.tokens + elapsed * self.refill_rate)
        self.last_refill = now
        
        if self.tokens >= 1:
            self.tokens -= 1
            return True
        return False
```

**Pros:**
- Memory efficient (2 values per user)
- Allows bursts (realistic traffic patterns)
- Simple

**Cons:**
- Not strict (request bursts up to capacity)

**Widely used:** AWS API Gateway, Nginx, Stripe, cloud services.

## Q4. (!) Leaky bucket?

**Model:**
- Requests queue
- Processed at constant rate R
- Queue full → reject

**Analogy:** funnel; input spills when full; output trickle.

**Parameters:**
- `capacity`: queue size
- `leak_rate`: req/sec processed

**Implementation (queue-based):**
```python
class LeakyBucket:
    def __init__(self, capacity, leak_rate):
        self.capacity = capacity
        self.leak_rate = leak_rate
        self.queue = deque()
        self.last_leak = time.time()
    
    def allow(self):
        self._leak()
        if len(self.queue) < self.capacity:
            self.queue.append(time.time())
            return True
        return False
    
    def _leak(self):
        now = time.time()
        to_leak = int((now - self.last_leak) * self.leak_rate)
        for _ in range(min(to_leak, len(self.queue))):
            self.queue.popleft()
        self.last_leak = now
```

**Pros:**
- **Smooth output** (constant rate)
- Good для protect downstream (predictable load)

**Cons:**
- **No bursts allowed** (queue fills, then rejects)
- Request latency: waiting in queue

**Use:** traffic shaping (network equipment, message queues).

**Vs token bucket:** leaky smooths; token allows bursts. Most APIs use token bucket.

## Q5. (!) Fixed window counter?

**Model:**
- Current window: minute/hour aligned
- Counter per window
- Reset on window boundary

**Implementation (Redis):**
```python
def allow(user_id, limit=100):
    window = int(time.time() // 60)  # minute
    key = f"rl:{user_id}:{window}"
    count = redis.incr(key)
    if count == 1:
        redis.expire(key, 120)  # TTL
    return count <= limit
```

**Pros:**
- Simple
- Memory cheap (1 counter per window)
- Atomic с Redis INCR

**Cons:**
- **Boundary effect:** 100 requests at 1:59, 100 at 2:00 → 200 in 1 min (both windows intact)
- **Bursty traffic** at boundaries

**Used for:** simple systems, where burstiness OK (e.g., per-hour quotas).

## Q6. (!) Sliding window log?

**Model:**
- Store timestamp of each request
- On request: remove timestamps > window ago; count remaining
- Reject if count ≥ limit

**Implementation (Redis sorted set):**
```python
def allow(user_id, limit=100, window=60):
    now = time.time()
    key = f"rl:{user_id}"
    
    # Remove old
    redis.zremrangebyscore(key, 0, now - window)
    # Count current
    count = redis.zcard(key)
    
    if count < limit:
        redis.zadd(key, {now: now})
        redis.expire(key, window)
        return True
    return False
```

**Pros:**
- **Accurate** — exact count в sliding window
- No boundary effects

**Cons:**
- **Memory expensive:** O(limit) per user (100 timestamps)
- Large-scale (millions users × 100 timestamps) = gigabytes
- Redis sorted set operations slower than INCR

**Use:** critical correctness, low-volume APIs.

## Q7. (!) Sliding window counter (hybrid)?

**Model:**
- Maintain 2 fixed-window counters (current + previous)
- Estimate sliding: weighted sum

**Formula:**
```
count = prev_window_count × (1 - elapsed/window_size) + current_window_count
```

**Example:**
- Window = 60s; prev window (12:00-12:01) had 80 requests; current (12:01-12:02) 40s in has 30
- Estimate = 80 × (1 - 40/60) + 30 = 80 × 0.33 + 30 = 26.4 + 30 = 56.4
- Limit 100 → allow

**Implementation:**
```python
def allow(user_id, limit=100, window=60):
    now = time.time()
    cur_window = int(now // window)
    prev_window = cur_window - 1
    elapsed = now % window
    
    cur = int(redis.get(f"rl:{user_id}:{cur_window}") or 0)
    prev = int(redis.get(f"rl:{user_id}:{prev_window}") or 0)
    
    estimated = prev * (1 - elapsed / window) + cur
    
    if estimated < limit:
        redis.incr(f"rl:{user_id}:{cur_window}")
        redis.expire(f"rl:{user_id}:{cur_window}", window * 2)
        return True
    return False
```

**Pros:**
- Smooths boundary effect (unlike fixed window)
- Memory cheap (2 counters)
- Atomic с Redis

**Cons:**
- Approximate (assumes uniform distribution в prev window)
- Error < few % on typical traffic

**Best compromise** — Cloudflare uses this.

## Q8. Сравнение алгоритмов?

| Algorithm | Accuracy | Memory | Bursts | Smoothness | Complexity |
|-----------|----------|--------|--------|------------|------------|
| Token bucket | Good | O(1) | Allows | Medium | Low |
| Leaky bucket | Good | O(capacity) | Smoothed | Perfect | Medium |
| Fixed window | Poor (boundary) | O(1) | Allows at boundary | Low | Low |
| Sliding log | Perfect | O(limit) | Allows smooth | High | High |
| Sliding counter | Good | O(1) | Allows | High | Low |

**Production choice:**
- **Token bucket** — common for API (AWS, Stripe)
- **Sliding window counter** — accuracy + memory efficient (Cloudflare)
- **Leaky bucket** — traffic shaping, queue protection
- **Sliding log** — strict compliance (quota billing)

## Q9. (!) Distributed rate limiter — challenges?

**Problem:** N servers; user's requests hit different servers; must enforce global limit.

**Naive:** per-server limit = total / N.
- But traffic не uniform → some servers hit limit early, others underused
- Overall rate less than allowed

**Solutions:**

**1. Shared Redis:**
- All servers read/write same counter
- Accurate but adds RTT (~1ms per check)
- Redis is SPOF (cluster для HA)

**2. Token bucket с sync:**
- Each server has local tokens
- Periodic sync с central (every 100ms)
- Approximate, lower latency

**3. Consistent hashing:**
- User X always routed to server Y (sticky)
- Local check; no network
- Rebalance on scale events

**4. Gossip:**
- Servers exchange counters peer-to-peer
- Eventually consistent
- No central SPOF

**Consistency trade-off:**
- Strong consistency → slow + SPOF
- Eventual → fast + fuzzy

**Stripe:** redis + local cache; centralized для accuracy.

**Envoy RL:** external Redis, caches rules.

## Q10. (!) Redis-based implementation?

**Pattern 1: INCR + EXPIRE (fixed window):**
```python
pipe = redis.pipeline()
pipe.incr(key)
pipe.expire(key, 60)
count, _ = pipe.execute()
return count <= limit
```

Risk: race — EXPIRE may not set если key exists (older Redis). Modern:
```
SET key 1 EX 60 NX
# if exists → INCR
```

**Pattern 2: INCRBY + atomic Lua:**
```lua
-- rate_limit.lua
local key = KEYS[1]
local limit = tonumber(ARGV[1])
local ttl = tonumber(ARGV[2])

local current = redis.call("INCR", key)
if current == 1 then
    redis.call("EXPIRE", key, ttl)
end
if current > limit then
    return 0  -- deny
end
return 1  -- allow
```

**Pattern 3: Token bucket в Redis (Lua):**
```lua
local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

local data = redis.call("HMGET", key, "tokens", "last")
local tokens = tonumber(data[1]) or capacity
local last = tonumber(data[2]) or now

tokens = math.min(capacity, tokens + (now - last) * refill_rate)

if tokens >= 1 then
    tokens = tokens - 1
    redis.call("HMSET", key, "tokens", tokens, "last", now)
    redis.call("EXPIRE", key, 3600)
    return 1
else
    redis.call("HMSET", key, "tokens", tokens, "last", now)
    return 0
end
```

**Pattern 4: Sorted set (sliding log):**
```
ZADD key timestamp timestamp
ZREMRANGEBYSCORE key 0 (now - window)
ZCARD key
```

**Latency:** ~1-2ms per check Redis call.

**Scale:** Redis cluster, shard by key.

## Q11. Lua script для atomicity?

**Problem:** non-atomic операции → race conditions.

**Lua in Redis = atomic:**
- Single-threaded Redis executes Lua atomically
- No interleaving between commands

**EVAL:**
```python
script = """..."""
redis.eval(script, 1, key, limit, ttl)
```

**EVALSHA:**
- Load script once (`SCRIPT LOAD`), execute by SHA
- Reduces bandwidth

**Benefits:**
- Atomicity guarantees accurate counter
- Logic near data (no network between operations)

**Gotchas:**
- Lua blocks Redis; long script = pause
- Keep under few ms execution time
- `redis.call()` for commands; errors propagate

**Libraries:**
- `resilience4j-ratelimiter` (Java) — local, not distributed
- `bucket4j-redis` (Java) — distributed, Lua-based
- `redis-cell` (Redis module) — дedicated rate limit commands

## Q12. (!) Где размещать rate limiter? (edge, gateway, service)

**Layers:**

**1. CDN / WAF (Cloudflare, AWS WAF):**
- Protects edge
- Block malicious before reaching origin
- Basic IP-based

**2. API Gateway (Kong, Envoy, AWS API Gateway):**
- Per-route rate limits
- Centralized policy
- Good for public APIs

**3. Load balancer:**
- NGINX `limit_req` module
- Per-IP rate limit

**4. Service layer:**
- In-code (Spring `@RateLimiter`, Bucket4j)
- Fine-grained (per-user, per-operation)
- Expensive to scale но flexible

**5. Database / backend:**
- Connection-level limits (HikariCP)
- Query-level (DB-specific)

**Defense in depth:** multiple layers.

**Typical design:**
- CDN: 10k req/s per IP (DDoS)
- API Gateway: 100 req/s per API key
- Service: per-feature limits
- DB: connection pool limits

**Centralized vs distributed:**
- Centralized — accurate, adds latency
- Distributed — fast, approximate
- Match to use case

## Q13. Rate limit granularity (user, IP, API key)?

**Dimensions:**

**IP:** anonymous users.
- Gotcha: NAT / corporate proxy — many users share IP
- Don't limit too low for reasonable networks

**User ID:** authenticated.
- Fair per-person
- Requires auth first

**API key:** service accounts.
- Billing alignment

**Endpoint:** per-URL.
- `/search` stricter than `/health`

**Method:** GET vs POST (POST often stricter — write).

**Combinations (composite key):**
- `{user_id}:{endpoint}` — user + endpoint
- Independent buckets

**Multi-tier (premium vs free):**
- Bucket per user; rate depends on subscription tier
- Free: 100/hour; paid: 10000/hour

**Implementation:**
```
key = f"rl:{user_id}:{endpoint}"
limit = user.tier.limit_for(endpoint)
```

**Metadata lookup:** user → tier → limit (cached).

## Q14. Multiple buckets (per-endpoint + global)?

**Problem:** user can DoS one endpoint with full limit, starving others.

**Solution:** layered limits:
- Per-endpoint: each endpoint has limit
- Per-service: user has overall limit across endpoints

**Check both:** request allowed only if both buckets have tokens.

**Example:**
- User: 1000 req/min total
- `/search`: 100 req/min
- `/profile`: 50 req/min

User can do 100 searches + 50 profile fetches + 850 other = within 1000 total.

**Implementation:** check multiple keys; if all allow → pass.

**Cost:** multiple Redis calls per request; batch with pipeline.

## Q15. (!) Response format (429)?

**Standard response:**
```http
HTTP/1.1 429 Too Many Requests
Content-Type: application/json
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1640000000
Retry-After: 60

{
  "error": "rate_limit_exceeded",
  "message": "You've exceeded the 1000 req/hr limit",
  "retry_after": 60
}
```

**Headers:**
- `X-RateLimit-Limit` — max allowed
- `X-RateLimit-Remaining` — remaining in window
- `X-RateLimit-Reset` — unix timestamp when resets
- `Retry-After` — seconds до retry (standard)

**Client behavior:**
- Exponential backoff + jitter
- Honor `Retry-After`
- Don't retry immediately (thundering herd)

**Common headers:**
- GitHub uses `X-RateLimit-*`
- Twitter `x-rate-limit-*`
- Standardization in progress (RFC draft)

**Include on 2xx too:** let clients monitor remaining.

## Q16. (!) Что делать при Redis outage?

**Fail-open (default for most):**
- Redis unavailable → allow request (degraded mode)
- Serves traffic; some abuse may pass
- **Preferred** — don't block customers due to infra

**Fail-closed:**
- Redis down → deny всё
- Safer for strict quotas (billing)
- Risk: outage → massive 429s to customers

**Hybrid:**
- Local in-memory fallback
- Counter approximated в process
- Smaller window, looser
- When Redis back → reconcile

**Detection:**
- Redis timeout → switch mode
- Circuit breaker wraps Redis calls
- Metric: rate-limiter-errors

**Multi-AZ Redis:** failover seamless; minimal downtime.

**Design decision:** fail-open typical для SaaS (don't reject customers); fail-closed для billing/quota.

## Q17. Hot user / hot key problem?

**Hot user:** one user hits rate limit endpoint millions/sec.

**Problem:**
- Their counter key gets every request
- Redis single slot → single node overloaded
- Other users affected

**Solutions:**

**1. Random sharding:**
- Split counter across N shards: `rl:user:{X}:{0..9}`
- Read all, sum, check
- Allow if total < limit
- Write to random shard

**2. Local rate limit + sync:**
- Each app instance limits locally (say 1/Nth of global)
- Sync to shared periodically
- Approximate but scalable

**3. CDN-level early rejection:**
- If user flagged as abuser → CDN drops before origin
- Reduces load на rate limiter itself

**4. Separate infrastructure:**
- Dedicated Redis for hot tenants
- Isolation from general traffic

**5. Quota tier limits:**
- Very high-volume users → special tier
- Dedicated resources, isolated

## Q18. Rate limit vs quota?

**Rate limit:** short-term req/sec / req/min.
- Smooths traffic
- Prevents bursts

**Quota:** long-term req/day / req/month.
- Billing / subscription
- Plan boundaries

**Both often enforced:**
- 100 req/min (rate) AND 100k req/month (quota)

**Storage:**
- Rate: short TTL (few min) — Redis
- Quota: persistent — DB (daily aggregate)

**Reset:**
- Rate: rolling window
- Quota: monthly boundary (1st of month)

**Visibility:**
- Rate: ephemeral
- Quota: dashboard для users, billing

---

## See also

- [System Design](system-design-interview.md) — общие принципы
- [API Gateway](../architecture/api-gateway-interview.md) — where rate limiter lives
- [Caching](../architecture/caching-strategies-interview.md) — Redis patterns
- [Redis](../databases/redis-interview.md) — INCR, Lua, sorted sets
- [Distributed Systems](../architecture/distributed-systems-interview.md) — consistency trade-offs
- [Resilience Patterns](../architecture/resilience-patterns-interview.md) — circuit breaker, backpressure
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — load management
- [Load Balancing](../architecture/load-balancing-interview.md) — request distribution
- [HTTP/REST](../api/http-rest-interview.md) — 429, Retry-After
- [Design URL Shortener](design-url-shortener-interview.md) — rate limiter component

- [[design-chat-system-interview|Design Chat System]]
- [[design-feed-system-interview|Design Feed System]]
- [[design-payment-system-interview|Design Payment System]]
- [[design-search-interview|Design Search System]]
- [[design-url-shortener-interview|Design URL Shortener]]
- [[system-design-interview|System Design]]
