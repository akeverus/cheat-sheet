---
title: "Вопросы на собеседовании: Vector Databases"
description: "Vector databases для AI/ML: HNSW, IVF, индексы, метрики (cosine, dot, L2), pgvector, Pinecone, Weaviate, Qdrant, Milvus, Chroma, hybrid search, scaling"
tags:
  - interview
  - ai-ml
  - vector-databases-interview
aliases:
  - "Vector databases interview"
  - "Vector DB interview"
  - "Pinecone interview"
  - "Qdrant interview"
  - "pgvector interview"
  - "HNSW interview"
difficulty: "intermediate"
updated: "2026-04-19"
---
# Вопросы на собеседовании: `Vector Databases`

**Vector databases** — специализированные БД для хранения и быстрого поиска **embeddings** (high-dimensional векторов). Главная операция — **kNN search**: найти k ближайших векторов к query. Используют ANN-алгоритмы (HNSW, IVF). Стек: Pinecone, Weaviate, Qdrant, Milvus, Chroma, pgvector, Elasticsearch.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [Pinecone Documentation](https://docs.pinecone.io/)
- [Weaviate Documentation](https://weaviate.io/developers/weaviate)
- [Qdrant Documentation](https://qdrant.tech/documentation/)
- [Milvus Documentation](https://milvus.io/docs/)
- [pgvector PostgreSQL extension](https://github.com/pgvector/pgvector)
- [HNSW paper](https://arxiv.org/abs/1603.09320)
- [Vector DB benchmarks](https://benchmark.vectorview.ai/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое vector database?](#q1--что-такое-vector-database)
- [Q2. (!) Зачем нужна специальная БД для векторов?](#q2--зачем-нужна-специальная-бд-для-векторов)
- [Q3. (!) Что такое embedding и почему они high-dim?](#q3--что-такое-embedding-и-почему-они-high-dim)

**Distance metrics**
- [Q4. (!) Cosine similarity vs Dot product vs Euclidean?](#q4--cosine-similarity-vs-dot-product-vs-euclidean)
- [Q5. Когда какую метрику выбрать?](#q5-когда-какую-метрику-выбрать)

**ANN алгоритмы**
- [Q6. (!) Что такое ANN (Approximate Nearest Neighbor)?](#q6--что-такое-ann-approximate-nearest-neighbor)
- [Q7. (!) HNSW (Hierarchical Navigable Small World)?](#q7--hnsw-hierarchical-navigable-small-world)
- [Q8. IVF (Inverted File Index)?](#q8-ivf-inverted-file-index)
- [Q9. (!) Сравнение HNSW vs IVF?](#q9--сравнение-hnsw-vs-ivf)
- [Q10. Product Quantization (PQ)?](#q10-product-quantization-pq)
- [Q11. ScaNN, FAISS, Annoy?](#q11-scann-faiss-annoy)

**Главные продукты**
- [Q12. (!) Pinecone — managed vector DB?](#q12--pinecone--managed-vector-db)
- [Q13. (!) Weaviate?](#q13--weaviate)
- [Q14. (!) Qdrant?](#q14--qdrant)
- [Q15. Milvus?](#q15-milvus)
- [Q16. (!) pgvector — PostgreSQL extension?](#q16--pgvector--postgresql-extension)
- [Q17. Chroma — embedded option?](#q17-chroma--embedded-option)
- [Q18. Elasticsearch / OpenSearch как vector DB?](#q18-elasticsearch--opensearch-как-vector-db)

**Возможности**
- [Q19. (!) Hybrid search (vector + keyword)?](#q19--hybrid-search-vector--keyword)
- [Q20. (!) Metadata filtering?](#q20--metadata-filtering)
- [Q21. Multi-tenancy?](#q21-multi-tenancy)
- [Q22. Replication, sharding?](#q22-replication-sharding)

**Performance**
- [Q23. (!) Recall vs latency trade-off?](#q23--recall-vs-latency-trade-off)
- [Q24. Quantization для cost reduction?](#q24-quantization-для-cost-reduction)
- [Q25. (!) Сколько векторов обычно?](#q25--сколько-векторов-обычно)

**Production**
- [Q26. (!) Какой vector DB выбрать?](#q26--какой-vector-db-выбрать)
- [Q27. Backup, restore, migrations?](#q27-backup-restore-migrations)
- [Q28. (!) Какие подводные камни в production?](#q28--какие-подводные-камни-в-production)

## Q1. (!) Что такое vector database?

**Vector database** — БД, оптимизированная для:
1. **Хранения** high-dimensional векторов (обычно 256-3072 dims)
2. **Быстрого kNN search** — найти k ближайших векторов
3. **Metadata filtering** — фильтровать с условиями (tenant, date)
4. **Hybrid search** — vector + keyword + metadata

**Применения:**
- **Semantic search** — поиск по смыслу
- **RAG** — retrieval для LLM
- **Recommendations** — похожие items
- **Anomaly detection** — outliers in vector space
- **Image / audio search** — multimedia retrieval

## Q2. (!) Зачем нужна специальная БД для векторов?

**Без vector DB:** brute force kNN — `O(n)` сравнений каждого vector с query.

```
1M векторов × 1024 dims = 4 GB
Brute force search: ~500 ms на CPU (slow)
```

**Vector DB через ANN:** `O(log n)` или близко, **миллисекунды** на поиск.

**Postgres с обычным индексом** — не работает для векторов (b-tree не для multi-dim).

**Vector DB предоставляет:**
- HNSW / IVF индексы
- Quantization
- Distributed scaling
- Hybrid search
- Streaming updates

## Q3. (!) Что такое embedding и почему они high-dim?

**Embedding** — представление текста/изображения как вектор чисел.

```
"Hello" → [0.12, -0.45, 0.78, ..., 0.03]  (например, 1536 чисел)
```

**Зачем high-dim:**
- Каждое измерение = abstract feature ("любовь", "технологии", "грусть", ...)
- Больше измерений = больше features = точнее представление
- Но дороже storage и slower поиск

**Trade-off:**
- 384 dims (`all-MiniLM-L6-v2`) — быстро, экономно
- 1024 dims (Cohere) — баланс
- 1536 dims (`text-embedding-3-small`) — стандарт OpenAI
- 3072 dims (`text-embedding-3-large`) — лучшее качество

Подробнее — в [[embeddings-interview|Embeddings]].

## Q4. (!) Cosine similarity vs Dot product vs Euclidean?

**Cosine similarity:**
```
cos(A, B) = (A · B) / (|A| × |B|)
range: [-1, 1] (1 = идентичные)
```
Не зависит от magnitude (длины) векторов.

**Dot product:**
```
A · B = Σ a_i × b_i
range: (−∞, +∞)
```
Зависит и от angle, и от magnitude.

**Euclidean distance (L2):**
```
||A − B|| = sqrt(Σ (a_i − b_i)²)
range: [0, ∞) (0 = идентичные)
```

**Manhattan (L1):** `Σ |a_i − b_i|` — реже используется.

## Q5. Когда какую метрику выбрать?

**Для embedding моделей:**
- **OpenAI, Cohere, Voyage** — нормализованные → **cosine = dot product** (выбирай dot для скорости)
- **Sentence transformers** — обычно cosine
- **Image embeddings (CLIP)** — cosine

**Best practice:** проверь docs модели. Если рекомендована cosine — используй cosine.

**Для нормализованных векторов** (`||v|| = 1`) — cosine == dot product. Вычисление dot быстрее (без normalization).

## Q6. (!) Что такое ANN (Approximate Nearest Neighbor)?

**Exact kNN** — гарантированно найти **истинные** top-k ближайших. `O(n)` без индекса.

**ANN** — найти **приблизительно** top-k, в обмен на скорость. Может пропустить пару near-misses.

**Recall:** доля **истинных** top-k среди возвращённых.
- Recall = 0.95 — нашли 95% правильных результатов
- Recall = 1.0 — exact (то же что brute force)

**Trade-off:**
- Высокий recall = более точно, но медленнее
- Низкий recall = быстрее, но больше false negatives

В production ANN с recall **0.9-0.99** — приемлемо для большинства задач.

## Q7. (!) HNSW (Hierarchical Navigable Small World)?

**HNSW** — самый популярный ANN алгоритм с **2018+**. Граф на нескольких уровнях.

```mermaid
graph TD
    subgraph "Level 2 (sparse, long edges)"
        A2 --- B2
        B2 --- C2
    end
    subgraph "Level 1"
        A1 --- B1 --- C1
        B1 --- D1
    end
    subgraph "Level 0 (dense, all nodes)"
        A0 --- B0 --- C0
        B0 --- D0 --- E0
    end
```

**Search:**
1. Стартуем с верхнего уровня
2. Greedy walk к ближайшему соседу
3. Спускаемся на уровень ниже
4. Повторяем до уровня 0
5. Best k результатов

**Преимущества:**
- Excellent recall/latency trade-off
- Поддерживает **incremental updates** (insert/delete)
- Работает в memory (быстро)

**Недостатки:**
- Память: ~1.5-3x размер векторов
- Build slow

**Параметры:**
- `M` — связность графа (16-64). Больше = точнее, больше памяти.
- `efConstruction` — quality build (200-500). Больше = медленнее build.
- `efSearch` — quality search. Больше = медленнее search, выше recall.

## Q8. IVF (Inverted File Index)?

**IVF** — разбить векторы на **clusters** (k-means), искать только в ближайших clusters.

```
1. Pre-build: kmeans → N clusters (centroids)
2. Каждый vector присвоен ближайшему centroid
3. Search: найти top-k closest centroids → искать только в них
```

**Параметры:**
- `nlist` — число clusters (обычно √N)
- `nprobe` — сколько clusters обыскивать (1-10% от nlist)

Чем больше `nprobe` — выше recall, медленнее search.

**Преимущества:**
- Меньше памяти, чем HNSW
- Быстрый build

**Недостатки:**
- Хуже recall на тех же latencies, чем HNSW
- Нужен retraining при больших data drift

## Q9. (!) Сравнение HNSW vs IVF?

| Критерий | HNSW | IVF |
|----------|------|-----|
| Recall/latency | **Лучше** | Хуже |
| Memory | Больше | Меньше |
| Build speed | Медленнее | Быстрее |
| Updates | **Incremental** | Нужен retrain |
| Best для | Latency-critical | Larger datasets, memory-constrained |

**В 2025** — HNSW **дефолт** в большинстве vector DBs (Pinecone, Weaviate, Qdrant, pgvector). IVF используется реже, для memory-constrained scenarios.

## Q10. Product Quantization (PQ)?

**Product Quantization** — compression векторов для memory savings.

**Идея:** разделить vector на **subvectors**, quantize каждый отдельно.

```
1024-dim vector → 32 chunks по 32 dims → quantize каждый в 8 bits
Result: 32 bytes (vs 4096 bytes original)
128x compression
```

**Trade-off:** меньше recall (lossy compression).

**Combined: IVF-PQ** или **HNSW-PQ** — для **очень больших** datasets (миллиарды векторов).

Используется в **FAISS**, **Milvus**.

## Q11. ScaNN, FAISS, Annoy?

| Library | Создатель | Особенности |
|---------|-----------|-------------|
| **FAISS** | Meta | Самая популярная C++ library. IVF, HNSW, PQ. Не managed DB. |
| **ScaNN** | Google | Очень быстрый. Используется в YouTube, Search. |
| **Annoy** | Spotify | Tree-based (не graph). Простой, но хуже HNSW. |
| **HNSWlib** | — | Lightweight HNSW C++ |

**FAISS** часто используется как **embedded** library внутри custom apps. Vector DBs (Milvus, Vespa) построены поверх FAISS-подобных движков.

## Q12. (!) Pinecone — managed vector DB?

**Pinecone** — самый популярный managed vector DB.

**Особенности:**
- **Fully managed SaaS** (нет self-hosted option)
- **Serverless** (с 2023+) — pay-per-use
- HNSW под капотом
- Metadata filtering, hybrid search
- Sparse-dense hybrid (с 2024)

```python
import pinecone
pinecone.init(api_key="...")
index = pinecone.Index("my-index")

index.upsert([
    ("id1", [0.1, 0.2, ...], {"category": "tech"}),
    ("id2", [0.3, 0.4, ...], {"category": "sport"})
])

results = index.query(
    vector=[0.1, 0.2, ...],
    top_k=10,
    filter={"category": "tech"}
)
```

**Цена:** дорогая для больших volumes (~$100/M vectors/month).

**Когда выбирать:** не хочется ops, малая команда, готовы платить.

## Q13. (!) Weaviate?

**Weaviate** — open-source vector DB, самый "feature-rich".

**Особенности:**
- Open-source (Apache 2.0) + managed cloud
- **GraphQL API** + REST + Python/JS clients
- **Modules** для embeddings (text2vec-openai, text2vec-cohere)
- **Hybrid search** (BM25 + vector)
- **Multi-tenancy** built-in
- **Generative search** — RAG в одной БД

```python
client.collections.create(
    name="Documents",
    vectorizer_config=Configure.Vectorizer.text2vec_openai(),
)

client.collections.get("Documents").data.insert({
    "title": "Hello",
    "content": "World"
})
# Embedding генерируется автоматически

results = collection.query.near_text(
    query="greetings",
    limit=5
)
```

**Когда:** хочется feature-rich, готовы запустить self-hosted.

## Q14. (!) Qdrant?

**Qdrant** — open-source vector DB на **Rust**. Растущая популярность.

**Особенности:**
- **Rust** — fast, memory-efficient
- Open-source + managed cloud
- **Excellent metadata filtering** (с indexed fields)
- HNSW, optional quantization
- gRPC + REST API
- **Sharding и replication** built-in

```python
from qdrant_client import QdrantClient
client = QdrantClient("localhost", port=6333)

client.upsert(
    collection_name="docs",
    points=[
        PointStruct(id=1, vector=[0.1, 0.2, ...], payload={"category": "tech"})
    ]
)

results = client.search(
    collection_name="docs",
    query_vector=[0.1, 0.2, ...],
    limit=10,
    query_filter=Filter(must=[FieldCondition(key="category", match=MatchValue(value="tech"))])
)
```

В **2025** — top-3 выбор для production. Особенно популярен в open-source LLM stack.

## Q15. Milvus?

**Milvus** — open-source vector DB от Zilliz.

**Особенности:**
- Distributed architecture (Kubernetes-native)
- Очень scaling — миллиарды векторов
- Multiple ANN algorithms (HNSW, IVF, ANNOY)
- Strong consistency
- GPU acceleration

**Применение:** очень большие datasets, enterprise.

**Минусы:** более complex deployment чем Qdrant/Weaviate.

## Q16. (!) pgvector — PostgreSQL extension?

**pgvector** — extension для PostgreSQL, добавляет vector type.

```sql
CREATE EXTENSION vector;

CREATE TABLE documents (
    id SERIAL PRIMARY KEY,
    content TEXT,
    embedding vector(1536)
);

-- HNSW index (с pgvector 0.5+)
CREATE INDEX ON documents USING hnsw (embedding vector_cosine_ops);

-- Search
SELECT * FROM documents
ORDER BY embedding <=> '[0.1, 0.2, ...]'::vector
LIMIT 5;
```

**Операторы:**
- `<->` — Euclidean distance
- `<#>` — negative inner product
- `<=>` — cosine distance

**Преимущества:**
- **Не нужна отдельная БД** — всё в Postgres
- Joins с обычными tables
- Transactions, ACID
- Существующая infra (backups, replication)

**Минусы:**
- Хуже масштабируется чем dedicated vector DBs (но достаточно для < 100M vectors)
- HNSW медленнее чем в Pinecone/Qdrant (но достаточно)

В **2025** — pgvector стал **default** для startups (нет смысла в отдельной БД для < 10M vectors).

## Q17. Chroma — embedded option?

**Chroma** — open-source, embedded vector DB.

```python
import chromadb
client = chromadb.Client()

collection = client.create_collection("docs")
collection.add(
    documents=["text1", "text2"],
    metadatas=[{"source": "a"}, {"source": "b"}],
    ids=["id1", "id2"]
)

results = collection.query(
    query_texts=["query"],
    n_results=5
)
```

**Когда:** prototypes, local development, маленькие apps. Не для production scale.

## Q18. Elasticsearch / OpenSearch как vector DB?

С **Elasticsearch 8+** — нативная поддержка vector search (через HNSW).

```json
PUT /docs
{
  "mappings": {
    "properties": {
      "embedding": {
        "type": "dense_vector",
        "dims": 1536,
        "index": true,
        "similarity": "cosine"
      }
    }
  }
}
```

**Преимущества:**
- **Hybrid search** "из коробки" (BM25 + vector)
- Production-grade scaling
- Уже знакомый tool

**Минусы:**
- HNSW медленнее dedicated vector DBs
- Memory hungry

В **2025** — серьёзный конкурент для **hybrid use cases** (RAG где важны и keywords, и semantic).

## Q19. (!) Hybrid search (vector + keyword)?

**Vector search** хорош для семантики. **BM25 keyword** — для точных терминов, names, IDs.

**Hybrid combine:**
```python
# Pseudocode
vector_results = vector_db.search(query_emb, top_k=50)
bm25_results = keyword_search(query_text, top_k=50)

# Reciprocal Rank Fusion
combined = rrf([vector_results, bm25_results])
top_10 = combined[:10]
```

**Когда нужен hybrid:**
- Технические термины (`jwt`, `oauth2`)
- Acronyms, names
- Code search
- Compliance / legal

Поддерживают: **Weaviate, Qdrant, Elasticsearch, Pinecone (с 2024)**.

## Q20. (!) Metadata filtering?

```python
results = qdrant.search(
    collection_name="docs",
    query_vector=embedding,
    query_filter=Filter(
        must=[
            FieldCondition(key="tenant_id", match=MatchValue(value="acme")),
            FieldCondition(key="date", range=Range(gte=1234567890))
        ]
    )
)
```

**Подходы к filtering:**

1. **Pre-filter** — сначала filter, потом vector search в filtered set
2. **Post-filter** — vector search → filter results
3. **Filter during search** — индексы интегрированы (Qdrant, Pinecone)

**Pre-filter** опасен: если filter aggressive — мало candidates, плохой recall.
**Post-filter** опасен: vector search вернёт нерелевантные → нечего отфильтровать.

**In-search filter** (Qdrant) — best, но требует **indexed fields**.

## Q21. Multi-tenancy?

**Multi-tenancy** — изоляция данных tenants в одной installation.

**Подходы:**

1. **Tenant per collection/index** — `index_acme`, `index_beta`. Простое, но scaling issues с тысячами tenants.
2. **Filter by tenant_id** — все в одной collection с metadata `tenant_id`. Compute resources shared.
3. **Native multi-tenancy** (Weaviate, Pinecone Serverless) — built-in isolation.

**Critical:** убедиться, что **не leak'ает** между tenants. Тестируй с unit tests.

## Q22. Replication, sharding?

**Replication** — копии данных для HA и read scaling.
**Sharding** — split данных между nodes для scale.

| Vector DB | Replication | Sharding |
|-----------|-------------|----------|
| **Pinecone** | ✓ (managed) | ✓ |
| **Weaviate** | ✓ | ✓ |
| **Qdrant** | ✓ | ✓ |
| **Milvus** | ✓ | ✓ |
| **pgvector** | через PostgreSQL | вручную |

В большинстве production cases — replication для HA, sharding для **очень больших** datasets (> 100M vectors).

## Q23. (!) Recall vs latency trade-off?

```
HNSW efSearch:
  efSearch = 10  → recall 0.85, latency 2 ms
  efSearch = 50  → recall 0.95, latency 8 ms
  efSearch = 200 → recall 0.99, latency 30 ms
```

**Зачем понимать:**
- Не нужен 100% recall для RAG (95%+ хватает)
- Latency budget определяет UX

**Best practice:** A/B тестировать разные `efSearch` settings против real queries.

## Q24. Quantization для cost reduction?

**Quantization** — сжатие векторов:

| Type | Compression | Recall impact |
|------|-------------|---------------|
| **fp32 → fp16** | 2x | Минимальный |
| **fp32 → int8** | 4x | Маленький |
| **Binary** (1 bit per dim) | 32x | Заметный (но окей с rerank) |
| **PQ (Product Quantization)** | 4-32x | Зависит от params |

**Binary quantization** — bytes per vector почти бесплатно. Используется как **cheap first stage**, потом rerank top-k через full precision.

В **2025** — quantization standard для systems с миллионами+ векторов.

## Q25. (!) Сколько векторов обычно?

**Типичные scales:**

- **Demo / prototype:** 1K-100K vectors → любой DB
- **Small product:** 100K-10M → pgvector, Qdrant cloud
- **Mid-size:** 10M-100M → Pinecone, Weaviate, Qdrant
- **Large:** 100M-1B → Milvus, Pinecone enterprise
- **Hyper-scale:** 1B+ → custom (FAISS-based), Milvus

**Storage estimation:**
```
10M vectors × 1024 dims × 4 bytes (fp32) = 40 GB
Плюс индекс HNSW: ~1.5x → 60 GB total
```

С quantization — в 4-10 раз меньше.

## Q26. (!) Какой vector DB выбрать?

**Decision tree:**

```
< 1M vectors, уже Postgres?
  → pgvector

Open-source, готов self-host?
  → Qdrant (modern, fast) или Weaviate (feature-rich)

Managed, не хочется ops?
  → Pinecone (если budget позволяет)

Уже Elasticsearch?
  → Elastic vector search (hybrid search в одной системе)

Очень большой scale (1B+)?
  → Milvus или custom

Local development / prototype?
  → Chroma
```

## Q27. Backup, restore, migrations?

**Vector DB не имеет** standard backup/restore tools (как Postgres).

**Подходы:**
1. **Re-embed everything** — если есть source documents, дешевле просто пересоздать (2-3 часа на M vectors)
2. **Export embeddings** — dump всех vectors в файл, restore
3. **Snapshot** (Pinecone, Qdrant) — точка во времени
4. **Replication-based** — реплика в другом регионе

**Migrations между DBs:** обычно через source documents (re-embed). Прямого export/import между разными DBs нет.

## Q28. (!) Какие подводные камни в production?

1. **Wrong distance metric** — embeddings нормализованы, а используешь Euclidean
2. **Embedding model mismatch** — query и docs embedded разными моделями
3. **Stale embeddings** — модель обновилась, не пересоздали
4. **Cost runaway** — каждое search costs (Pinecone serverless)
5. **Index не используется** — `EXPLAIN` показывает full scan
6. **Filtering после поиска** — recall падает (нужен indexed metadata)
7. **Multi-tenancy leak** — accidentally вернуть чужие data
8. **Re-indexing painful** — обновление модели = заново всё embed
9. **Memory pressure** — HNSW в RAM, OOM при больших datasets
10. **Slow updates** — bulk inserts могут заблокировать поиски

**Always test** с realistic data volumes до production.

---

## See also

- [[embeddings-interview|Embeddings]] — что хранится в vector DB
- [[rag-interview|RAG]] — главное применение
- [[llm-basics-interview|LLM Basics]] — context для AI
- [[postgresql-interview|PostgreSQL]] — pgvector
- [[elasticsearch-interview|Elasticsearch]] — hybrid search
- [[redis-interview|Redis]] — Redis Stack vector search
- [[caching-strategies-interview|Caching]] — embeddings cache
- [[microservices-interview|Микросервисы]] — где vector DB живёт
- [[distributed-systems-interview|Распределённые системы]] — sharding, replication
- [[mlops-interview|MLOps]] — embedding model versioning
- [[scalability-patterns-interview|Scalability Patterns]] — для больших scales

- [[ai-agents-interview|AI Agents]]
- [[embeddings-interview|Embeddings]]
- [[llm-basics-interview|LLM Basics]]
- [[llm-integration-patterns-interview|LLM Integration Patterns]]
- [[mlops-interview|MLOps]]
- [[model-serving-interview|Model Serving]]
