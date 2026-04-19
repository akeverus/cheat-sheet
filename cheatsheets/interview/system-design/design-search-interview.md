---
title: "Вопросы на собеседовании: Design Search System"
description: "System design search (Google-like, site search): inverted index, Elasticsearch, ranking, autocomplete, typo tolerance, crawling, indexing pipeline, ML relevance"
tags:
  - interview
  - system-design
  - design-search-interview
aliases:
  - "Search System design"
  - "Search engine architecture"
  - "Autocomplete design"
  - "Search System собеседование"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `Design Search System`

`Search System` (Google Search, site search, product search) — глубокий system design. Tradeoff indexing latency vs query latency, ranking, typo tolerance, scale. Обычно рассматривается site/product search (не web-scale crawler Google), но principles те же.

Дата последнего обновления: 2026-04-19

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

- [System Design](system-design-interview.md) — общие принципы
- [Elasticsearch](../databases/elasticsearch-interview.md) — deep dive
- [Design URL Shortener](design-url-shortener-interview.md) — read-heavy patterns
- [Design Feed System](design-feed-system-interview.md) — ranking parallels
- [Caching](../architecture/caching-strategies-interview.md) — query cache
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — sharding
- [Distributed Systems](../architecture/distributed-systems-interview.md) — scatter-gather
- [[llm-interview|LLM]] — semantic search for RAG
- [Embeddings](../ai-ml/embeddings-interview.md) — vector search
- [MLOps](../ai-ml/mlops-interview.md) — LTR model deployment
