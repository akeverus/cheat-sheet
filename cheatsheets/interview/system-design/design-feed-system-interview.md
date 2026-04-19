---
title: "Вопросы на собеседовании: Design Feed System"
description: "System design news feed (Twitter, Facebook, Instagram): fan-out on write vs read, timeline generation, ranking, celebrity problem, caching, Redis sorted sets"
tags:
  - interview
  - system-design
  - design-feed-system-interview
aliases:
  - "News Feed design"
  - "Twitter timeline"
  - "Feed architecture"
  - "Feed System собеседование"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `Design Feed System`

`Feed System` (Twitter timeline, Facebook News Feed, Instagram) — classic system design. Fundamental trade-off: **fan-out on write** vs **fan-out on read**. Celebrity problem, ranking, caching. Интересный challenge для middle/senior ролей.

Дата последнего обновления: 2026-04-19

## Полезные ссылки

- [Twitter — Timelines at scale](https://www.infoq.com/presentations/Twitter-Timeline-Scalability/)
- [Facebook News Feed (Haystack)](https://engineering.fb.com/)
- [Instagram engineering](https://instagram-engineering.com/)
- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [High Scalability](http://highscalability.com/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Requirements**
- [Q1. (!) Functional и non-functional requirements?](#q1--functional-и-non-functional-requirements)
- [Q2. (!) Capacity estimation?](#q2--capacity-estimation)

**Fan-out**
- [Q3. (!) Fan-out on write vs fan-out on read?](#q3--fan-out-on-write-vs-fan-out-on-read)
- [Q4. (!) Celebrity problem?](#q4--celebrity-problem)
- [Q5. (!) Hybrid approach?](#q5--hybrid-approach)

**Timeline storage**
- [Q6. (!) Storage для user timeline?](#q6--storage-для-user-timeline)
- [Q7. Redis sorted set для timeline?](#q7-redis-sorted-set-для-timeline)

**Ranking**
- [Q8. (!) Chronological vs algorithmic feed?](#q8--chronological-vs-algorithmic-feed)
- [Q9. Ranking features и signals?](#q9-ranking-features-и-signals)
- [Q10. ML pipeline для ranking?](#q10-ml-pipeline-для-ranking)

**Architecture**
- [Q11. (!) High-level architecture?](#q11--high-level-architecture)
- [Q12. Пост creation flow?](#q12-пост-creation-flow)
- [Q13. Read (timeline fetch) flow?](#q13-read-timeline-fetch-flow)

**Scalability**
- [Q14. (!) Как handle millions of followers?](#q14--как-handle-millions-of-followers)
- [Q15. Cache strategy?](#q15-cache-strategy)
- [Q16. DB sharding?](#q16-db-sharding)

**Production**
- [Q17. (!) Как справиться с viral posts?](#q17--как-справиться-с-viral-posts)
- [Q18. Feed freshness vs latency trade-off?](#q18-feed-freshness-vs-latency-trade-off)

## Q1. (!) Functional и non-functional requirements?

**Functional:**
- User posts tweet/update
- User sees feed из posts followed people
- Like, comment, share (optional)
- Rank by recency or algorithm
- Infinite scroll

**Non-functional:**
- **Read-heavy** (100:1 reads:writes)
- **Low latency** (< 200ms для feed load)
- **High availability** (99.99%)
- **Consistency:** eventual OK (few seconds lag fine)
- **Scalable** (billions users)

**Scope excluded (interview tip):**
- Direct messaging — separate
- Video streaming
- Complex moderation

## Q2. (!) Capacity estimation?

**Twitter-scale assumptions:**
- 500M active users
- 200M write posts daily → ~2300 posts/sec
- Each user follows ~200 others
- Average reads: 10 timeline fetches/user/day = 5B reads/day = 58K/sec

**Fan-out impact:**
- Avg post reaches 200 followers
- 2300 posts/sec × 200 = **460K timeline writes/sec** (if write-side fanout)
- 460K × 50B/record = 23 MB/s
- Per day: ~2TB timeline write volume

**Storage:**
- Posts: 200M/day × 300B = 60GB/day raw
- 5 years: ~110 TB (raw); indexes + replicas: 300-400TB

**Cache:**
- Hot users' timelines (top 1M) × 1000 posts × 100B = 100GB Redis

**Bandwidth:**
- Timeline load: 60K/s × 20KB (100 posts × 200B) = 1.2 GB/s

## Q3. (!) Fan-out on write vs fan-out on read?

**Fan-out on write (push model):**
- User posts → system writes copy to each follower's timeline
- Timeline pre-computed
- Read: just fetch from user's timeline cache

```
A posts → fanout worker → write to each follower's timeline
             |
             → Follower1's timeline (append)
             → Follower2's timeline (append)
             → ...
```

**Pros:**
- Fast reads (O(1) lookup)
- Consistent low read latency

**Cons:**
- Expensive writes (celebrity with 10M followers = 10M writes per post)
- Wasted work (many followers inactive)
- Storage amplification

**Fan-out on read (pull model):**
- User posts → just write to their own posts
- Read: fetch recent posts from all followed users, merge, sort

```
A posts → write to A's posts table
User B reads feed → fetch posts from each of 200 followed users → merge sort
```

**Pros:**
- Cheap writes
- No amplification
- Celebrity posts handled naturally

**Cons:**
- Slow reads (especially with many followees)
- Complex ranking на read time

**Real:** hybrid (see Q5).

## Q4. (!) Celebrity problem?

**Problem:** user with millions of followers (celebrity, brand).
- Fan-out on write: 10M follower × every post = massive write amplification
- System bottleneck

**Solutions:**

**1. Detect celebrities:**
- Threshold: > 100K followers = "celebrity"
- Skip write-side fanout for their posts
- Readers pull celebrity posts at read time

**2. Hybrid fanout:**
- Non-celebrity posts → push to followers' timelines
- Celebrity posts → pull from celebrity's post store on feed load

**3. Async fanout:**
- Write-side fan-out eventually; prioritize active followers
- Queue with backpressure (Kafka)

**Twitter approach:**
- Used SPOF-ish "fanout" system; celebrities handled separately
- Some systems just cache celebrity timeline в CDN

**At read time:**
```
user_feed = read_own_timeline_cache() + read_celebrity_posts(followed_celebrities)
merge_sort_by_time()
```

## Q5. (!) Hybrid approach?

**Pragmatic production:**

**Write side:**
- Regular users (< 10K followers): write-side fanout to followers' timelines
- Celebrities: write only to own posts store; skip fanout

**Read side:**
- Fetch user's precomputed timeline (from cache) — fast
- Additionally fetch posts from followed celebrities (pull)
- Merge, sort, return

**Optimization:**
- Only fanout to **active** followers (logged in последние N days)
- Inactive followers: pull timeline on login

**Further:**
- Split fanout: immediate for priority followers (close friends), delayed for others
- Caching at multiple layers (Redis, CDN)

**Decision matrix:**
- Simple system, moderate scale: write fanout
- Huge scale + celebrities: hybrid
- Very dynamic network (Twitter): heavy caching + hybrid

## Q6. (!) Storage для user timeline?

**Options:**

**Per-user timeline cache (Redis):**
```
LIST user:42:timeline = [post_id_1, post_id_2, ...]
```
- Keep recent N (500-1000) items
- Trim older
- Fast reads

**SQL/NoSQL backing:**
- Cassandra: partition by user_id, cluster by post_time
- DynamoDB: PK user_id + SK time

**Post storage (separate):**
- Posts master table (by post_id)
- Denormalized в timelines (post_id only; fetch body from posts on read)

**Read flow:**
1. `LRANGE user:42:timeline 0 49` (50 post IDs from Redis)
2. `MGET post:id1 post:id2 ...` (fetch bodies)
3. Apply ranking (if algorithmic)
4. Return

**Space:**
- Timeline cache (top 1M users × 1000 IDs × 8B) = 8GB — fits в memory

## Q7. Redis sorted set для timeline?

**Sorted set:**
```
ZADD user:42:timeline <timestamp> <post_id>
ZRANGE user:42:timeline 0 49 WITHSCORES
```

**Pros:**
- Ordered by score (timestamp) natively
- Easy range queries by time
- Fast O(log N) insert

**Usage:**
- On post: `ZADD user:X:timeline <now> <post_id>` for each follower
- On read: `ZREVRANGE user:X:timeline 0 49` (top 50 by time)
- Trim: `ZREMRANGEBYRANK user:X:timeline 0 -1001` (keep 1000)

**Memory:**
- Sorted set entry: ~64-80 bytes
- 1000 items × 80 = 80KB per user
- 1M users × 80KB = 80GB (doable)

**Eviction:**
- TTL on set или LRU eviction globally
- Inactive users' timelines expire; regenerate on next visit

## Q8. (!) Chronological vs algorithmic feed?

**Chronological (reverse chrono):**
- Most recent first
- Simple, predictable
- Pros: user control, no "why did I see this?" confusion
- Cons: posts buried если you follow many; less engagement

**Algorithmic:**
- ML-ranked by relevance
- Personal signals (past likes, friends' activity)
- Higher engagement
- Cons: opaque, filter bubble

**Example — Twitter (historically):**
- Chronological → algorithmic "Home" → back to chronological "Latest" tab

**Instagram:** algorithmic since 2016.

**Facebook:** algorithmic (EdgeRank, then ML).

**Modern:**
- Hybrid — users can switch
- Recent + ranked sections

## Q9. Ranking features и signals?

**Typical features:**

- **Recency** (older decays)
- **Engagement** (likes, comments, shares)
- **Friend interactions** (from close friends scores higher)
- **User's past behavior** (viewed similar content)
- **Author's history** (quality score)
- **Media type** (video/image engagement)
- **Time spent viewing** (dwell time)
- **Clicks** on the post
- **Negative signals:** hide, unfollow, "not interesting"

**Example (EdgeRank simplified):**
```
score = affinity × weight × time_decay
```

**Modern ML:**
- Features: 100s (user embeddings, post embeddings)
- Model: deep learning (DNN, transformer)
- Train offline, serve online

## Q10. ML pipeline для ranking?

**Architecture:**

```
Offline:
  Training data (click logs, engagement) → Feature store → Train model (TensorFlow/PyTorch) → Model registry

Online:
  Feed request → Candidate generation (pool of ~1000 posts) →
  Ranking model (score each) → Top 50 returned → User sees
  → Log interaction → feedback to training
```

**Candidate generation:**
- Recent posts from follows
- Trending posts
- Posts similar to past interactions
- Ads

**Ranking model:**
- Input: (user features, post features, context) → score
- Output: top-K по score
- Inference: few ms per request

**A/B testing:**
- New model → small % traffic → metrics (DAU, time-on-app)
- Ramp up если winning

**Scale:**
- TensorFlow Serving / TorchServe / SageMaker
- GPU inference for deep models
- Edge optimization (quantization, distillation)

## Q11. (!) High-level architecture?

```
Clients
    ↓
[Load Balancer]
    ↓
[API Gateway]
    ↓
[Feed Service] — read timeline
[Post Service] — write post
[User Service] — profile, follows
[Ranking Service] — ML model
    ↓
[Fanout Service] — background worker (Kafka)
    ↓
[Storage]
- Posts DB (Cassandra)
- User DB (MySQL/Postgres)
- Timeline cache (Redis)
- Graph DB (follows — Neo4j / custom)
- Media (S3)
    ↓
[Search] — Elasticsearch
[Analytics] — stream → Kafka → Flink → data warehouse
```

## Q12. Пост creation flow?

1. User writes post → POST `/posts`
2. Post Service:
   - Validate
   - Insert to Posts DB
   - Return success to user (optimistic UI)
3. Async:
   - Publish to Kafka `posts_created`
4. Fanout workers (consumers):
   - Read post
   - Lookup user's followers (User Service / Graph DB)
   - If non-celebrity: write post_id to each follower's Redis timeline
   - If celebrity: skip (readers pull)
   - Index post in Elasticsearch

**Latency target:**
- User sees own post: immediate (< 200ms)
- Followers see post: within 1-5 sec typically

**Backpressure:**
- Fanout lag monitored; scaling workers horizontally

## Q13. Read (timeline fetch) flow?

**User loads feed:**

1. GET `/feed` с pagination (`?cursor=<timestamp>`)
2. Feed Service:
   a. Fetch user's timeline from Redis: `ZREVRANGEBYSCORE user:42:timeline <cursor> -inf LIMIT 0 50`
   b. Fetch celebrity posts (if user follows any): query Celebrity Posts store
   c. Merge + rank (ML)
   d. Fetch post bodies: batch MGET
   e. Enrich (author name/avatar) from cache/User Service
3. Return to client

**Latency budget (200ms):**
- Timeline fetch: 5ms (Redis)
- Celebrity pull: 30ms (DB/cache)
- Ranking: 50ms (ML inference)
- Enrichment: 20ms
- Rest: network, serialize

**Caching:**
- Enriched posts cached (TTL 1 min)
- User data cached (1 hr)

## Q14. (!) Как handle millions of followers?

**Options:**

**1. Async fanout с priority:**
- High-priority followers (active в last 24h) fanout first
- Low-priority — later или skip
- Async queue (Kafka)

**2. Pull for low-priority:**
- Don't fanout to all; they pull on read

**3. Timeline trimming:**
- Only keep most relevant / recent 1000 items
- Old pushed out

**4. Sharded fanout workers:**
- Parallel workers process subsets of followers
- Kafka partitions (by follower_id hash)

**5. Precompute at off-peak:**
- Batch fanout during low load

**Twitter example:**
- Fanout service "Timeline Service" — Scala/Java
- Horizontally scaled, backed by Redis
- Asynchronous; eventual consistency OK

## Q15. Cache strategy?

**Multi-layer caching:**

**L1 (client / app memory):**
- Recent posts viewed
- Short TTL

**L2 (CDN для public content):**
- Public profiles, media
- TTL hours

**L3 (Redis):**
- User's timeline (sorted set)
- Post bodies (hash) — TTL 1hr
- User profile data (hash)

**L4 (DB):**
- Persistent store; cache miss последний

**Invalidation:**
- Post edit: invalidate post cache, re-push to timelines
- Follow change: rebuild affected user's timeline (async)

**Cache warming:**
- New user on login: async build initial timeline
- Celebrity post: pre-push to top engaged followers

## Q16. DB sharding?

**Posts:**
- By user_id (author): all user's posts co-located
- Or by post_id hash: even distribution
- Or time-based: partition by month (older → archive)

**User data:**
- By user_id hash

**Follow graph:**
- User → follows table: by user_id
- User → followers table: by user_id (both directions often needed)
- Dedicated graph DB (Neo4j) or sharded tables

**Timelines (Redis):**
- By user_id (Redis cluster sharding)
- `user:{id}:timeline` — slot by user_id

**Read pattern impact:**
- If sharded by user_id, "posts from user X" = one shard (fast)
- "Global feed, all users" = all shards (rare for per-user feed)

## Q17. (!) Как справиться с viral posts?

**Viral post:** millions of likes / comments → hot key problem.

**Symptoms:**
- Single post cache row — read amplification
- Comment / like write storm

**Mitigations:**

**1. Read replicas + CDN для post content:**
- Cache post in CDN/edge
- Many reads served without origin hit

**2. Counter sharding:**
- Like count: partition counter across N rows
- Write: random shard +1
- Read: sum всех shards

**3. Async aggregation:**
- Events → Kafka
- Periodic aggregation → final count
- Users see slightly stale count (eventually consistent)

**4. Rate-limit write-amplification:**
- Comments rate-limit per second
- Likes throttled (can use idempotency)

**5. Server-side aggregation:**
- "10 friends liked this" — show aggregate
- Don't send 100K like events to every viewer

## Q18. Feed freshness vs latency trade-off?

**Freshness:** how recent are posts in feed.
**Latency:** how fast feed loads.

**Trade-offs:**
- Aggressive pre-computation → fresh, but expensive writes
- Pure pull → minimal pre-compute, slow reads
- Cached feed → fast reads, stale до cache refresh

**Typical knobs:**

**Post TTL в timeline cache:**
- Long TTL: stale but fast
- Short TTL: fresh but рест cache miss

**Fanout lag:**
- Real-time: seconds
- Batch: minutes OK для low-priority users

**Freshness monitoring:**
- Time from post creation → visible в random follower's feed
- SLO: 95% < 5s

---

## See also

- [System Design](system-design-interview.md) — общие принципы
- [Design Chat System](design-chat-system-interview.md) — fan-out parallels
- [Caching](../architecture/caching-strategies-interview.md) — Redis, CDN
- [Cassandra](../databases/cassandra-interview.md) — post storage
- [Redis](../databases/redis-interview.md) — timeline sorted set
- [[messaging-interview|Messaging]] — Kafka fanout
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — fanout patterns
- [Distributed Systems](../architecture/distributed-systems-interview.md) — eventual consistency
- [Design URL Shortener](design-url-shortener-interview.md) — read-heavy similar
- [Load Balancing](../architecture/load-balancing-interview.md) — traffic distribution
