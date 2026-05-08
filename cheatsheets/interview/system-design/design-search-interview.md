---
title: "Вопросы на собеседовании: Design Search System"
description: "System design search (Google-like, site search): inverted index, Elasticsearch, ranking, autocomplete, typo tolerance, crawling, indexing pipeline, ML relevance"
tags:
  - interview
  - system-design
  - design-search-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Design Search System"
  - "Search System design"
  - "Search engine architecture"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Design Search System`

`Search System` (Google Search, site search, product search) — глубокий system design. Tradeoff indexing latency vs query latency, ranking, typo tolerance, scale. Обычно рассматривается site/product search (не web-scale crawler Google), но principles те же.

## Полезные ссылки

- [Elasticsearch: definitive guide](https://www.elastic.co/guide/en/elasticsearch/guide/current/index.html)
- [Lucene internals](https://lucene.apache.org/)
- [How Google search works](https://www.google.com/search/howsearchworks/)
- [System Design Primer](https://github.com/donnemartin/system-design-primer)
- [High Scalability — search](http://highscalability.com/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Requirements**
- [Q1. (!) Functional и non-functional requirements?](#q1--functional-и-non-functional-requirements)
- [Q2. (!) Capacity estimation?](#q2--capacity-estimation)

**Indexing**
- [Q3. (!) Inverted index — что это?](#q3--inverted-index--что-это)
- [Q4. (!) Tokenization, normalization, stemming?](#q4--tokenization-normalization-stemming)
- [Q5. Elasticsearch vs Lucene — разница?](#q5-elasticsearch-vs-lucene--разница)

**Architecture**
- [Q6. (!) High-level architecture?](#q6--high-level-architecture)
- [Q7. (!) Indexing pipeline?](#q7--indexing-pipeline)
- [Q8. Near real-time индексация?](#q8-near-real-time-индексация)

**Query**
- [Q9. (!) Query flow (scatter-gather)?](#q9--query-flow-scatter-gather)
- [Q10. (!) Relevance scoring: TF-IDF, BM25?](#q10--relevance-scoring-tf-idf-bm25)
- [Q11. Ranking beyond text (ML)?](#q11-ranking-beyond-text-ml)

**Scalability**
- [Q12. (!) Sharding стратегии?](#q12--sharding-стратегии)
- [Q13. Replication?](#q13-replication)

**Features**
- [Q14. (!) Autocomplete / typeahead?](#q14--autocomplete--typeahead)
- [Q15. (!) Typo tolerance / fuzzy match?](#q15--typo-tolerance--fuzzy-match)
- [Q16. Faceted search / filters?](#q16-faceted-search--filters)
- [Q17. Semantic search / vector embeddings?](#q17-semantic-search--vector-embeddings)

**Production**
- [Q18. (!) Analytics и learning-to-rank?](#q18--analytics-и-learning-to-rank)
- [Q19. Hot queries cache?](#q19-hot-queries-cache)
- [Q20. Index rebuild / rollover?](#q20-index-rebuild--rollover)

## Q1. (!) Functional и non-functional requirements?

**Functional (site/e-commerce search):**
- Full-text query over documents
- Autocomplete / suggestions
- Typo tolerance
- Filters (price, category, rating)
- Sort (relevance, price, date)
- Pagination
- Highlight matching terms

**Non-functional:**
- **Low latency** (< 200ms p99)
- **High QPS** (thousands of queries/sec)
- **Fresh data** (newly added items searchable in seconds-minutes)
- **High availability** (99.9%+)
- **Relevance quality** (precision + recall)

**Scope:**
- NOT web crawler (Google-scale — separate topic)
- Assume documents provided (products, articles)


> [!mcq]
> - [ ] Consistency и durability — главные NFR для поисковой системы | ❌ ПОСЛЕДСТВИЕ: поиск — read-heavy, eventual consistency достаточно; строгий consistency добавляет latency без пользы
> - [ ] Throughput не важен если latency низкий | ❌ ПОСЛЕДСТВИЕ: при 10k QPS без throughput capacity система перегружается даже с хорошим p50; NFR нужны оба
> - [ ] Accuracy важнее latency — лучше 2с точный ответ, чем 200ms менее точный | ❌ ПОСЛЕДСТВИЕ: пользователи покидают поиск после 200-300ms; точность без скорости = неиспользуемая система
> - [x] Low latency (< 200ms p99) + high availability (99.9%+) + freshness (seconds) + relevance quality | ✓ ПРИМЕНЯТЬ: при проектировании NFR для site search; все четыре в балансе 📋 ПРАВИЛО: Search NFR = Latency + Availability + Freshness + Relevance 🔗 См. Q6

## Q2. (!) Capacity estimation?

**Assumptions (e-commerce):**
- 100M products indexed
- 1000 queries/sec average, 10k peak
- Product record ~ 1KB
- 5 product updates/sec

**Index size:**
- Documents: 100M × 1KB = **100 GB raw**
- Inverted index: ~50% of raw (terms, postings)
- Plus: fields, stored vectors, analyzers
- Total: ~150-200 GB (single copy)
- Replication 2x: 400 GB
- 3 replicas: 600 GB

**Query throughput:**
- 10k QPS peak
- Per-query CPU: ~10ms on single shard
- 10 shards parallel: near constant; aggregate ~100 queries per shard-second
- Need ~10 nodes для 10k QPS

**Memory для performance:**
- Hot indices in RAM → faster
- ~100 GB RAM across cluster for hot data


> [!mcq]
> - [ ] Для 100M docs достаточно одного узла с 1TB SSD — индексирование быстрее | ❌ ПОСЛЕДСТВИЕ: single node = single point of failure; 10k QPS невозможен без параллелизма по шардам
> - [ ] Index size ≈ raw data size (100 GB docs = 100 GB index) | ❌ ПОСЛЕДСТВИЕ: inverted index = postings lists + term dict + stored fields ≈ 50-150% от raw; плюс replication 2-3x → планировать 400-600 GB
> - [x] 100M docs × 1KB = 100GB raw; inverted index ~150GB; replication 3x = 450GB; 10k QPS → ~10 shards на ~10 nodes | ✓ ПРИМЕНЯТЬ: capacity estimation для site search на интервью 📋 ПРАВИЛО: Index = raw × 1.5, replication × 3, nodes = peak_QPS / QPS_per_node 🔗 См. Q12
> - [ ] QPS capacity не влияет на число нод — только на RAM | ❌ ПОСЛЕДСТВИЕ: каждый запрос использует CPU для scoring и IO для чтения postings; без достаточного числа нод CPU bottleneck при пиковой нагрузке

## Q3. (!) Inverted index — что это?

**Forward index:** doc → words (normal DB).
```
doc_1: "the quick brown fox"
doc_2: "quick fox jumps"
```

**Inverted index:** word → docs.
```
"quick" → [doc_1, doc_2]
"brown" → [doc_1]
"fox" → [doc_1, doc_2]
```

**Why:** query "quick fox" → intersect postings of "quick" + "fox" → candidates.

**Richer structure:**
- Term → [(doc_id, frequency, positions)]:
  ```
  "quick" → [(1, 1, [1]), (2, 1, [0])]
  ```
- Positions enable phrase queries

**Storage:**
- Disk: compressed (variable-byte encoding, delta encoding)
- Memory: cached hot terms

**Lucene:** implementation в Apache Lucene = basis for Elasticsearch, Solr.


> [!mcq]
> - [ ] Inverted index = forward index с обратной сортировкой документов | ❌ ПОСЛЕДСТВИЕ: это другая структура: forward index = doc→terms, inverted index = term→docs list; сортировка тут ни при чём
> - [x] Inverted index: term → {doc_id, positions, frequencies}; позволяет O(n_matches) lookup вместо O(all_docs) scan | ✓ ПРИМЕНЯТЬ: полнотекстовый поиск по любым размерам корпуса 📋 ПРАВИЛО: Inverted = term→postings; lookup = O(matches), не O(corpus) 🔗 См. Q3
> - [ ] Inverted index хранит только doc_id без позиций — позиции ищутся отдельно | ❌ ПОСЛЕДСТВИЕ: Lucene хранит позиции в postings list; без позиций невозможны phrase queries ("hello world") и highlight
> - [ ] B-tree индекс в базах данных эквивалентен inverted index для текста | ❌ ПОСЛЕДСТВИЕ: B-tree ищет по точному ключу/range; inverted index ищет по term → multiple docs; семантически разные структуры

## Q4. (!) Tokenization, normalization, stemming?

**Pipeline при indexing:**

**1. Tokenization:**
- Split text into tokens: `"Hello, world!"` → `["Hello", "world"]`
- Language-dependent (CJK different)
- Character filters (HTML strip, mapping)

**2. Normalization:**
- Lowercase: `"Hello"` → `"hello"`
- Unicode normalization (NFC, NFD)
- ASCII fold: `café` → `cafe`
- Remove punctuation

**3. Stop words:**
- Remove common words: "the", "a", "is"
- Saves space; marginal for relevance

**4. Stemming:**
- Reduce to root: `"running", "runs", "ran"` → `"run"`
- Porter, Snowball stemmers

**5. Lemmatization:**
- Smart stemming (dictionary-based): `"better"` → `"good"`
- More accurate, slower

**6. Synonyms:**
- `"automobile" → "car"`
- At index time or query time

**Example:**
- Input: `"Running the fastest cars"`
- After pipeline: `["run", "fast", "car"]`
- Query "car races" matches thanks to stemming

**Same pipeline at query time:**
- Consistency mandatory
- Misconfigured analyzer = terms в index don't match query tokens


> [!mcq]
> - [ ] Разный pipeline на indexing vs query time — нормальная практика | ❌ ПОСЛЕДСТВИЕ: если analyzer при indexing стеммирует "running"→"run", а при query не стеммирует — термин "running" не совпадает с "run" в индексе → 0 результатов
> - [ ] Stemming и lemmatization идентичны по точности | ❌ ПОСЛЕДСТВИЕ: stemming — эвристический (Porter), может давать нерелевантные корни; lemmatization — dictionary-based, точнее но медленнее
> - [x] Pipeline: tokenize → lowercase → ASCII fold → stop words → stem; одинаковый на indexing И query time; несинхронизированный = zero recall | ✓ ПРИМЕНЯТЬ: text analysis в Elasticsearch; обязательно совпадение analyzer на indexing и search 📋 ПРАВИЛО: Same analyzer both ways = consistent term matching 🔗 См. Q10
> - [ ] Stop words всегда надо оставлять — они улучшают recall | ❌ ПОСЛЕДСТВИЕ: stop words ("the", "a") увеличивают postings lists в 10x без пользы для precision; их удаление уменьшает index size и ускоряет lookup

## Q5. Elasticsearch vs Lucene — разница?

**Lucene:** Java library (индекс + search на одном машины).

**Elasticsearch:** distributed wrapper вокруг Lucene.
- REST API
- Cluster management
- Sharding + replication
- JSON docs
- Aggregations

**Solr:** another Lucene-based product; similar capabilities.

**Comparison:**

| Aspect | Lucene | Elasticsearch |
|--------|--------|---------------|
| Type | Library | Distributed product |
| API | Java | REST/JSON |
| Scale | Single machine | Cluster |
| Features | Core search | + aggregations, monitoring, Kibana |
| Learning curve | Low-level | User-friendly |

**Choose Elasticsearch** for most enterprise (cluster out-of-box).

**Alternatives:**
- **OpenSearch** (AWS fork of ES, open license)
- **Typesense** — simpler, lower ops
- **Meilisearch** — lightweight typo-tolerant
- **Algolia** — SaaS (fast, but $$$)


> [!mcq]
> - [ ] Elasticsearch — это замена реляционным БД с полным ACID | ❌ ПОСЛЕДСТВИЕ: ES eventual consistent, нет транзакций; для ACID данных нужна реляционная БД, ES — для поиска
> - [ ] Lucene напрямую масштабируется на кластер без оберток | ❌ ПОСЛЕДСТВИЕ: Lucene — single-node library; для кластеризации, репликации и REST API нужен ES/Solr
> - [x] Elasticsearch = distributed wrapper над Lucene: REST API + sharding + replication + aggregations; Lucene = low-level search library | ✓ ПРИМЕНЯТЬ: ES для production distributed search; Lucene embedded для in-process search 📋 ПРАВИЛО: ES = Lucene × cluster; Lucene = ES engine inside 🔗 См. Q12
> - [ ] Solr и Elasticsearch одинаковы по всем параметрам, можно выбрать любой | ❌ ПОСЛЕДСТВИЕ: ES лучше для real-time и JSON; Solr исторически сильнее для enterprise faceted search; разные эко-системы и monitoring tooling

## Q6. (!) High-level architecture?

```
Data sources → [Indexing Pipeline] → [Index Service (Elasticsearch)] ← [Query Service]
                       ↑                                                     ↑
                 [Change Stream/Kafka]                               [API Gateway]
                       ↑                                                     ↑
               [Product DB, CMS]                                       [Clients]
```

**Components:**

- **Indexing Pipeline:** ingest + transform data → index service
- **Index Service:** Elasticsearch cluster (master + data nodes)
- **Query Service:** fronts search API; handles query parsing, result post-processing, cache
- **Cache:** Redis для popular queries
- **Analytics:** clicked results → training data для ranking

**Separation of concerns:**
- Index path: write-heavy, batch-friendly
- Query path: read-heavy, latency-sensitive


> [!mcq]
> - [ ] Query service и indexing service должны быть одним компонентом для консистентности | ❌ ПОСЛЕДСТВИЕ: индексирование write-heavy (batch-friendly), поиск read-heavy (latency-sensitive); совмещение = resource contention → деградация одного из путей
> - [ ] Cache для поисковых запросов не нужен — каждый запрос уникален | ❌ ПОСЛЕДСТВИЕ: топ-1000 popular queries = 80% трафика; Redis cache с TTL 60s снижает нагрузку на ES в 5-10x
> - [x] Indexing pipeline: Source → CDC/Kafka → Transform → ES bulk; Query: Client → API GW → Query Service → ES scatter-gather → Cache | ✓ ПРИМЕНЯТЬ: разделение write path и read path в distributed search 📋 ПРАВИЛО: Search architecture = Index path (async) + Query path (sync, latency-sensitive) 🔗 См. Q7
> - [ ] Elasticsearch сам читает из БД — отдельный indexing pipeline не нужен | ❌ ПОСЛЕДСТВИЕ: ES не интегрируется с БД напрямую; нужны CDC connector (Debezium) или application-level event emission

## Q7. (!) Indexing pipeline?

**Batch indexing:**
```
Source (DB) → ETL → Transform → Bulk index (ES)
```
- Pull всё periodically (nightly)
- Simple, high latency

**Real-time CDC:**
```
Source DB → WAL → Debezium → Kafka → Consumer → Index (ES)
```
- Changes flow в seconds
- Scales well

**Event-driven:**
```
Application → Kafka topic → Consumer → Index (ES)
```
- Each write emits event
- Decoupled

**Pipeline stages:**
1. Read from source (CDC or event)
2. Transform (enrich, denormalize, derive fields)
3. Analyze (tokenize)
4. Bulk write к ES (_bulk API)

**Handle failures:**
- Retry on failure
- Dead-letter queue
- Idempotency (document ID from source)

**Bulk performance:**
- ES bulk API: 500-5000 docs per request
- Measure throughput vs latency


> [!mcq]
> - [ ] Batch nightly достаточен для всех use cases — freshness не критична | ❌ ПОСЛЕДСТВИЕ: продуктовый каталог с price changes нужен seconds freshness; nightly batch → пользователи видят неактуальные цены/наличие
> - [x] CDC (Debezium → Kafka) даёт real-time freshness; идемпотентные document ID из source = безопасный retry; bulk API для throughput | ✓ ПРИМЕНЯТЬ: real-time product search updates 📋 ПРАВИЛО: Indexing = CDC+Kafka+idempotency+bulk; DLQ для failed docs 🔗 См. Q8
> - [ ] ES _update API лучше bulk API для throughput | ❌ ПОСЛЕДСТВИЕ: каждый _update = отдельный HTTP request; bulk API батчит 500-5000 docs → 10-50x больший throughput
> - [ ] Без идемпотентного ID при retry индекс окажется дублями | ❌ ПОСЛЕДСТВИЕ: при использовании source document ID как ES _id retry = overwrite (upsert); дублей не будет если ID детерминированный

## Q8. Near real-time индексация?

**Elasticsearch:**
- Documents indexed в in-memory buffer
- `refresh_interval` (default 1s) flushes buffer → segment → searchable
- Trade-off: too frequent = costly; rare = stale

**Tuning:**
- Real-time: `refresh_interval=1s` (default)
- Bulk loading: disable refresh (`-1`), enable after load
- Relaxed: `30s` — reduce indexing overhead

**Durability:**
- Translog (write-ahead log) persists immediately
- Can recover on crash

**Visibility:**
- Write → in-memory → refresh → searchable (delay = refresh interval)

**When matters:**
- Product catalog update: 5s delay OK
- Chat search: near real-time critical


> [!mcq]
> - [ ] refresh_interval=1s означает что документ сразу виден после записи | ❌ ПОСЛЕДСТВИЕ: документ сначала в in-memory buffer → через 1s refresh → в новом segment → searchable; есть задержка до 1s
> - [ ] Translog в ES используется для replication, не для durability | ❌ ПОСЛЕДСТВИЕ: translog = write-ahead log для crash recovery на single node; replication отдельно через shard copies
> - [x] refresh_interval (default 1s) = buffer flush → new Lucene segment → searchable; bulk load → disable refresh (-1) → re-enable after; translog = crash recovery | ✓ ПРИМЕНЯТЬ: tuning freshness vs indexing throughput 📋 ПРАВИЛО: refresh_interval = freshness latency; -1 для bulk load, 1s для NRT 🔗 См. Q7
> - [ ] Для NRT нужно уменьшить refresh_interval до 100ms — это стандартная практика | ❌ ПОСЛЕДСТВИЕ: слишком частый refresh = много мелких Lucene segments → slow search; оптимально 1-5s для большинства use cases

## Q9. (!) Query flow (scatter-gather)?

**Distributed search:**
```
Query
  ↓
Coordinator node receives
  ↓
Fan out to ALL shards (scatter)
  ↓
Each shard performs local search, returns top K
  ↓
Coordinator merges + re-ranks globally (gather)
  ↓
Coordinator fetches full docs for top K
  ↓
Return results
```

**Steps:**
1. Parse query
2. Send to shards (parallel)
3. Each shard returns (doc_id, score) for local top K
4. Coordinator merges — top K globally
5. Fetch docs (by ID)
6. Format response

**Latency:**
- p99 = slowest shard + merge + fetch
- Slow shard = entire query slow (scatter-gather amplifies tail)

**Optimizations:**
- **adaptive_replica_selection:** send к fastest replica
- **search_timeout:** abort slow shards
- **Pre-filtering:** narrow по индексу before scoring


> [!mcq]
> - [ ] Coordinator fetch full docs from all shards для merge | ❌ ПОСЛЕДСТВИЕ: fetch full docs от всех шардов = сетевой overhead O(shards × pageSize); scatter-gather возвращает только top-K scores per shard, full docs только для final top-K
> - [ ] Scatter-gather работает только если все шарды ответили | ❌ ПОСЛЕДСТВИЕ: search_timeout позволяет вернуть частичный результат при slow shard; partial results с degraded quality лучше timeout
> - [x] Coordinator скаттерит запрос ко всем шардам; каждый возвращает local top-K (IDs + scores); coordinator мержит → global top-K → fetches full docs | ✓ ПРИМЕНЯТЬ: distributed full-text search в ES 📋 ПРАВИЛО: scatter=local top-K, gather=global merge, fetch=full docs 🔗 См. Q12
> - [ ] Query идёт только к одному шарду — тому, где документ хранится | ❌ ПОСЛЕДСТВИЕ: нельзя знать заранее какой шард хранит релевантные документы; scatter-gather = обязательный паттерн для полноты результатов

## Q10. (!) Relevance scoring: TF-IDF, BM25?

**TF-IDF:**
- TF (term frequency): more occurrences = more relevant
- IDF (inverse doc frequency): rare terms more informative
- Score = TF × IDF

**BM25 (default в Lucene/ES):**
- Improved TF-IDF
- Term saturation (diminishing returns for TF)
- Document length normalization

**Formula BM25:**
```
score = IDF(term) × (TF × (k+1)) / (TF + k × (1 - b + b × |D|/avgdl))
```
- `k = 1.2` — saturation parameter
- `b = 0.75` — length normalization
- `|D|` = doc length; `avgdl` = avg doc length

**Why BM25 better:**
- TF-IDF: doc с 100 occurrences of "quick" scored 100x than 1 occurrence
- BM25: saturation — 10 occurrences ≈ 100 (diminishing)
- More realistic

**Default:** ES uses BM25.

**Practical search score:**
- BM25 base
- Field boosts: title weight × 3, body weight × 1
- Freshness decay
- Popularity boost


> [!mcq]
> - [ ] TF-IDF и BM25 идентичны по результатам — выбор не важен | ❌ ПОСЛЕДСТВИЕ: TF-IDF линейно растёт при повторениях; BM25 saturates после порога → BM25 лучше избегает keyword stuffing; ES использует BM25 по умолчанию
> - [ ] Длина документа не влияет на релевантность в BM25 | ❌ ПОСЛЕДСТВИЕ: BM25 нормализует по длине (параметр b=0.75); без нормализации длинные docs получали бы несправедливо высокий score
> - [x] BM25 = TF-IDF с saturation (k) + length normalization (b); default в ES; score = IDF × saturated_TF / length_adjusted | ✓ ПРИМЕНЯТЬ: text relevance scoring в ES/Lucene 📋 ПРАВИЛО: BM25 = diminishing TF returns + doc length penalty; k=1.2, b=0.75 defaults 🔗 См. Q11
> - [ ] IDF важнее TF при оценке релевантности в любом случае | ❌ ПОСЛЕДСТВИЕ: оба важны; для rare terms IDF доминирует; для short precise queries TF критичен; BM25 балансирует оба

## Q11. Ranking beyond text (ML)?

**Text relevance только часть:**

**Signals:**
- Query-doc match (BM25, embeddings similarity)
- Freshness (recent > older)
- Popularity (click-through, sales)
- User personalization (past searches, location)
- Quality (reviews, editorial score)

**Approaches:**

**1. Hand-tuned formula:**
- `score = BM25 + 0.5×freshness + 0.3×popularity`
- Simple, understandable

**2. Learning-to-Rank (LTR):**
- ML model scores (user features, query features, doc features)
- Training data: clicks, engagement
- Models: LambdaMART (gradient boosting), DNN

**3. Two-stage (common):**
- Stage 1: candidate retrieval (BM25 gets top 1000)
- Stage 2: re-rank с ML (deep model on top 100)

**Elasticsearch LTR plugin:**
- Feature extractors define signals
- External model (XGBoost) scores

**A/B testing:**
- New ranker vs baseline
- Metric: CTR, conversion, revenue


> [!mcq]
> - [ ] ML-based LTR можно применять к миллионам кандидатов напрямую | ❌ ПОСЛЕДСТВИЕ: deep ML-модель дорогая; применять к 1M docs = latency 10+ секунд; two-stage: BM25 top-1000 → ML re-rank top-100
> - [ ] BM25 учитывает персонализацию и freshness автоматически | ❌ ПОСЛЕДСТВИЕ: BM25 = pure text relevance; freshness и personalization = дополнительные сигналы поверх BM25 в hand-tuned formula или LTR
> - [x] Two-stage: BM25 retrieval top-1000 → ML re-rank top-100; signals: clicks, CTR, freshness, popularity, personalization | ✓ ПРИМЕНЯТЬ: production search ranking с ML в крупных системах 📋 ПРАВИЛО: Stage 1 = fast recall (BM25), Stage 2 = slow precision (ML) 🔗 См. Q18
> - [ ] A/B тестирование ранкера требует полного rollout перед измерением | ❌ ПОСЛЕДСТВИЕ: A/B test = parallel traffic split; метрики (CTR, conversion) измеряются одновременно; full rollout = нет baseline для сравнения

## Q12. (!) Sharding стратегии?

**Shard:** partition of index on one node.

**Number of shards:**
- More = parallelism, но overhead per shard
- Rule: shard size 20-50GB; plan capacity
- Example: 200GB data, 50GB/shard → 4 shards

**Sharding modes:**

**Per-index (default):**
- Documents hash to shards by doc_id
- Even distribution
- Query: scatter to all shards

**Routing (custom):**
- Specify shard key (`routing` parameter)
- Example: by user_id → all user's data on 1 shard
- Query: targeted (if routing known) = faster
- Uneven если hot user

**Time-based (rollover):**
- Daily/monthly indices: `logs-2024-01`, `logs-2024-02`
- Query: time filter → search relevant indices only
- Old: close or move to cold storage

**Cross-cluster search:**
- Multiple clusters searchable as one
- Geo-distributed


> [!mcq]
> - [ ] Больше шардов всегда лучше — увеличивают параллелизм | ❌ ПОСЛЕДСТВИЕ: слишком много мелких шардов = overhead (metadata, JVM heap per shard); правило: 20-50 GB per shard; 1000 шардов на кластере = проблема
> - [ ] Time-based sharding подходит для product catalog | ❌ ПОСЛЕДСТВИЕ: time-based = для append-only logs; product catalog без временного dimension → routing по hash или category
> - [x] Shard count = data_size / target_shard_size (20-50GB); routing key для targeted search; time-based для logs | ✓ ПРИМЕНЯТЬ: initial sharding design для ES index 📋 ПРАВИЛО: Shard size 20-50GB; # primaries fixed at creation; replicas изменяемы 🔗 См. Q13
> - [ ] Число шардов можно изменить после создания индекса | ❌ ПОСЛЕДСТВИЕ: primary shards фиксированы при создании; изменение = reindex в новый индекс с другим sharding

## Q13. Replication?

**Replica shard:** copy for HA + read scale.

**Configuration:**
- Primary shard + N replicas
- Replicas distributed across nodes (no two on same node)

**Purposes:**
- **Durability:** node failure → replica promoted
- **Read throughput:** queries served by primaries + replicas (round-robin)
- **Zero-downtime restarts:** rolling restart

**Typical:**
- 1 replica (2 copies total): some HA
- 2 replicas (3 copies): safer (quorum)

**Write flow:**
- Write to primary
- Primary replicates to replicas (sync)
- All in-sync before ACK (default)

**Tuning:**
- `index.number_of_replicas: 1-2` typically
- More replicas = more storage + write cost, но better read scale


> [!mcq]
> - [ ] Replicas увеличивают write throughput | ❌ ПОСЛЕДСТВИЕ: каждый write реплицируется синхронно на все replicas; больше replicas = slower writes; replicas помогают только read throughput
> - [x] Replicas = HA (node failure → replica promoted) + read scale (queries to primaries + replicas); write: primary → replicas sync | ✓ ПРИМЕНЯТЬ: production ES с HA требованиями; 1-2 replicas стандарт 📋 ПРАВИЛО: 0 replicas = data loss risk; 1 replica = 2x storage + HA; 2 replicas = quorum 🔗 См. Q12
> - [ ] Primary сhard и replica синхронизируются asynchronously — eventual consistency | ❌ ПОСЛЕДСТВИЕ: ES по умолчанию sync replication; write ACK только после replica confirm; для async нужен wait_for_active_shards=1
> - [ ] Replicas на том же node что primary для производительности | ❌ ПОСЛЕДСТВИЕ: ES не размещает replica и primary одного shard на одном node; это защита от node failure

## Q14. (!) Autocomplete / typeahead?

**Goal:** suggest completions as user types.

**Challenges:**
- Ultra-low latency (< 50ms)
- Relevance (popular first)
- Typo-tolerant

**Implementations:**

**1. Prefix-based inverted index:**
- Elasticsearch `completion` suggester
- FST (Finite State Transducer) data structure
- In-memory, very fast
- Supports fuzzy matching

**2. Dedicated cache (Redis sorted sets):**
- Each prefix → sorted set of terms with popularity scores
- `ZRANGE prefix:"app" 0 5 WITHSCORES`
- Precompute on schedule

**3. Trie:**
- Classic data structure
- In-memory per-node
- Distributed trie сложно

**Ranking:**
- By popularity (search frequency)
- Personalization (user's history)
- Context (current page, location)

**Real-time update:**
- On query logs → update scores
- Periodic rebuild


> [!mcq]
> - [ ] Autocomplete работает через full-text BM25 поиск по всему индексу | ❌ ПОСЛЕДСТВИЕ: full-text search на каждый keystroke при 100M docs = latency 200ms+; autocomplete требует специальных структур (FST, Redis sorted sets) для < 50ms
> - [ ] Redis sorted sets не поддерживают prefix queries | ❌ ПОСЛЕДСТВИЕ: ZRANGEBYLEX команда ES lookup по prefix; sorted sets с lexicographic order = эффективный prefix suggester
> - [ ] Trie легко масштабировать горизонтально | ❌ ПОСЛЕДСТВИЕ: distributed trie сложен (split/merge при добавлении); в practice используют ES completion suggester (FST) или Redis per-prefix sorted sets
> - [x] ES completion suggester (FST, in-memory, < 50ms) или Redis sorted sets по prefix; ранжировать по popularity; обновлять из query logs | ✓ ПРИМЕНЯТЬ: typeahead с < 50ms latency требованием 📋 ПРАВИЛО: Autocomplete = dedicated structure (FST/Redis), не full-text search 🔗 См. Q15

## Q15. (!) Typo tolerance / fuzzy match?

**Goal:** "appel" finds "apple".

**Approaches:**

**1. Edit distance (Levenshtein):**
- Distance = chars to change (insert/delete/replace)
- Match terms with distance ≤ N
- Elasticsearch `fuzzy` query: `fuzziness: 2`

**2. Damerau-Levenshtein:**
- Adds transposition (swap)
- Better for common typo patterns

**3. N-gram indexing:**
- Index substrings (3-grams: "app", "ppl", "ple" for "apple")
- Query partial "appl" matches
- Higher storage cost

**4. Phonetic encoding:**
- Soundex, Metaphone
- "Smith" and "Smyth" → same code
- Useful для names

**5. ML spell correction:**
- Train on query logs (user retypes after typo)
- Suggest corrections: "Did you mean ..."

**Elasticsearch:**
- `fuzzy` query for edit-distance
- `ngram` analyzer для partial match
- `phonetic` analyzer

**Trade-off:**
- More tolerance = more recall, less precision (irrelevant matches)
- Tune по use case


> [!mcq]
> - [ ] fuzziness: AUTO всегда лучше фиксированного значения | ❌ ПОСЛЕДСТВИЕ: AUTO применяет fuzziness 1 для коротких слов (< 4 chars); для 2-char "is" fuzzy = "in", "it" etc. — много false positives; иногда fixed fuzziness точнее
> - [x] Levenshtein distance (edit distance) через ES fuzzy query; N-gram для partial; phonetic для names; больше tolerance = больше recall, меньше precision | ✓ ПРИМЕНЯТЬ: "appel" → "apple"; fuzziness: 1-2 для слов > 4 chars 📋 ПРАВИЛО: fuzzy = edit distance; ngram = substring; phonetic = sounds-like 🔗 См. Q14
> - [ ] N-gram подход точнее edit distance для опечаток | ❌ ПОСЛЕДСТВИЕ: n-gram ищет общие substrings; edit distance ищет минимальные правки; для typos (замена буквы) edit distance точнее; n-gram лучше для partial match
> - [ ] Phonetic encoding работает для всех языков | ❌ ПОСЛЕДСТВИЕ: Soundex и Metaphone разработаны для английского; для русского/китайского нужны другие алгоритмы или phonetic-aware tokenizers

## Q16. Faceted search / filters?

**Facets:** categorical breakdowns (brand, price range, rating).

**Use case:** e-commerce "Nike shoes under $100, rating 4+".

**Implementation (Elasticsearch aggregations):**
```json
{
  "query": { "match": { "name": "shoes" } },
  "aggs": {
    "brands": { "terms": { "field": "brand" } },
    "price_ranges": {
      "range": {
        "field": "price",
        "ranges": [
          { "to": 50 }, { "from": 50, "to": 100 }, { "from": 100 }
        ]
      }
    }
  }
}
```

**Response:**
- Top matches + counts per facet

**UI:**
- Facets as sidebar filters
- Click filter → narrow search

**Performance:**
- Aggregations cached (filter cache)
- Cardinality (# unique values) affects speed


> [!mcq]
> - [ ] Faceted aggregations всегда быстры независимо от cardinality | ❌ ПОСЛЕДСТВИЕ: terms aggregation на high-cardinality field (user_id = millions) = OOM или timeout; используй cardinality < 100K для realtime facets
> - [x] Facets = ES aggregations на query result; cached bitsets для filters; cardinality limits performance; sidebar UI shows counts | ✓ ПРИМЕНЯТЬ: product catalog с фильтрами по категории, цене, рейтингу 📋 ПРАВИЛО: Facets = aggs on search results; filter context cached, query context не cached 🔗 См. Q9
> - [ ] Facets и filters — синонимы, одна операция | ❌ ПОСЛЕДСТВИЕ: facets = counts breakdown per value (aggregation); filters = narrow результаты; facets обычно применяются к уже отфильтрованным результатам
> - [ ] post_filter не влияет на facets aggregations | ❌ ПОСЛЕДСТВИЕ: post_filter применяется ПОСЛЕ aggregations; позволяет видеть полные facet counts при активном filter — это намеренная разница

## Q17. Semantic search / vector embeddings?

**Problem:** lexical search (BM25) misses synonyms, semantic similarity.
- Query "running shoes" won't match "jogging footwear"

**Semantic search:**
- Embed query и docs в vector space (dense vectors)
- Similar meaning = close vectors
- Nearest-neighbor search

**Embedding models:**
- BERT, sentence-transformers
- OpenAI text-embedding-3 (API)
- Domain-specific fine-tuned

**Vector DBs:**
- Elasticsearch (dense_vector + kNN 8+)
- Pinecone, Weaviate, Milvus
- pgvector (Postgres extension)

**Hybrid search:**
- BM25 + vector similarity
- Combine scores: `score = α × bm25 + (1-α) × vector_similarity`
- Best of both (lexical precision + semantic recall)

**RAG (LLM context):**
- Embed knowledge base
- Query → retrieve similar chunks → pass to LLM
- See [RAG](../ai-ml/rag-interview.md)

**Challenges:**
- Embedding drift (model updates → re-index)
- High-dim vector storage expensive
- ANN index trade-off (approximate для speed)


> [!mcq]
> - [ ] Semantic search заменяет BM25 полностью — lexical search устарел | ❌ ПОСЛЕДСТВИЕ: BM25 лучше для exact keyword match (product ID, names); semantic лучше для synonyms/paraphrase; hybrid дает лучший recall
> - [ ] Vector embeddings можно использовать без переиндексации при смене модели | ❌ ПОСЛЕДСТВИЕ: при смене embedding model размерность и пространство меняются; все документы нужно переиндексировать с новой моделью
> - [x] Hybrid: score = α × BM25 + (1-α) × cosine_similarity(query_vec, doc_vec); ANN index (HNSW) для kNN; ES dense_vector + kNN | ✓ ПРИМЕНЯТЬ: semantic search при семантических запросах + lexical для exact 📋 ПРАВИЛО: Hybrid = BM25 recall + vector precision; ANN = approximate для скорости 🔗 См. Q11
> - [ ] kNN exact search быстрее ANN для больших коллекций | ❌ ПОСЛЕДСТВИЕ: exact kNN = O(N × d); ANN (HNSW) = O(log N) с приемлемой точностью; для 100M vectors exact = секунды vs ANN = milliseconds

## Q18. (!) Analytics и learning-to-rank?

**Query logs:**
- Every search + click → event log
- Build training data for ranking

**Metrics:**
- **Click-through rate (CTR):** clicks / impressions
- **NDCG (Normalized Discounted Cumulative Gain):** relevance with position
- **Mean Reciprocal Rank (MRR):** position of first relevant
- **Conversion rate:** search → purchase

**LTR feedback loop:**
- Collect: query, shown results, click, dwell time, conversion
- Label training: clicked = relevant, not = less
- Train: XGBoost / neural
- Deploy via A/B test
- Measure, iterate

**Counterfactual evaluation:**
- "If we'd ranked differently, CTR would be X" — estimate without full A/B
- Inverse propensity scoring

**Personalization:**
- User features (history, location) — input к ranker
- Privacy considerations


> [!mcq]
> - [ ] Click = прямой сигнал релевантности без искажений | ❌ ПОСЛЕДСТВИЕ: position bias (users click top results regardless of relevance); нужна counterfactual correction или interleaving experiments
> - [ ] NDCG и CTR измеряют одно и то же | ❌ ПОСЛЕДСТВИЕ: CTR = clicks/impressions (пользовательское поведение); NDCG = relevance × position (quality metric); CTR biased by position, NDCG требует relevance labels
> - [x] Query logs → clicks/dwell time/conversion → LTR training data; feedback loop: collect → label → train (XGBoost/neural) → A/B test → iterate | ✓ ПРИМЕНЯТЬ: continuous improvement поискового ранкера 📋 ПРАВИЛО: LTR = behavioral data → model → A/B test → metric improvement 🔗 См. Q11
> - [ ] LTR требует ручной разметки relevance для каждого запроса | ❌ ПОСЛЕДСТВИЕ: ручная разметка дорога; implicit signals (clicks, dwell) используются как weak labels с position bias correction

## Q19. Hot queries cache?

**20% queries = 80% volume** typically.

**Cache layers:**

**CDN / HTTP cache:**
- Public queries (no user-specific) → CDN cacheable
- `Cache-Control: public, max-age=60`

**Redis query cache:**
- Key: hash(query + filters + sort)
- Value: result IDs + metadata
- TTL 60s-5min

**Elasticsearch native:**
- **Request cache:** cached by shard for structured queries
- **Query cache:** cached filter results (bit sets)
- Default enabled

**Invalidation:**
- TTL for most
- Event-driven для specific (product out of stock → invalidate key)

**Measure:**
- Hit ratio: track
- Stale risk: acceptable lag vs freshness requirement


> [!mcq]
> - [ ] Cache всех поисковых запросов без разбора — universal cache | ❌ ПОСЛЕДСТВИЕ: персонализированные/уникальные запросы = cache miss всегда; только popular queries кешировать стоит (top 20% = 80% trафика)
> - [ ] ES request cache работает для все типов запросов | ❌ ПОСЛЕДСТВИЕ: ES request cache только для size=0 (aggs без hits); regular search queries не кешируются request cache; только filter context кеширует bitsets
> - [x] Redis: hash(query+filters) → result IDs, TTL 60s-5min; CDN для public queries; ES filter cache для bitsets; 20% queries = 80% volume | ✓ ПРИМЕНЯТЬ: popular search results caching в e-commerce 📋 ПРАВИЛО: Cache query hash → results; TTL = freshness tolerance; персонализированные не кешировать 🔗 См. Q6
> - [ ] Инвалидация cache при обновлении продукта не нужна если TTL короткий | ❌ ПОСЛЕДСТВИЕ: продукт "out of stock" в cache дает плохой UX; event-driven invalidation при критических изменениях даже с TTL < 60s

## Q20. Index rebuild / rollover?

**When needed:**
- Schema change (new analyzer, new fields)
- Bulk backfill after data migration
- Language analyzer update

**Zero-downtime rebuild:**
1. Build new index (alongside old)
2. Populate: `reindex` API (or from source)
3. Switch alias: `old` → `new`
4. Queries use new index seamlessly
5. Delete old

**Alias pattern:**
```
alias "products" → index "products-v1"
# rebuild:
alias "products" → index "products-v2"
```

**Rollover (time-based):**
- Indices per day/month
- Alias points to current
- Auto-rollover when size/age reached:
```
POST products/_rollover
```

**Cost:**
- Double storage temporarily
- Takes hours for large dataset
- Plan maintenance window

---

## See also


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [System Design](system-design-interview.md) — общие принципы ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Elasticsearch](../databases/elasticsearch-interview.md) — deep dive
- [Design URL Shortener](design-url-shortener-interview.md) — read-heavy patterns
- [Design Feed System](design-feed-system-interview.md) — ranking parallels
- [Caching](../architecture/caching-strategies-interview.md) — query cache
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — sharding
- [Distributed Systems](../architecture/distributed-systems-interview.md) — scatter-gather
- [[llm-interview|LLM]] — semantic search for RAG
- [Embeddings](../ai-ml/embeddings-interview.md) — vector search
- [MLOps](../ai-ml/mlops-interview.md) — LTR model deployment
