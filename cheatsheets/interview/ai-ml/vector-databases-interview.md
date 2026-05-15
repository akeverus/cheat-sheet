---
title: "Вопросы на собеседовании: Vector Databases"
description: "Vector databases для AI/ML: HNSW, IVF, индексы, метрики (cosine, dot, L2), pgvector, Pinecone, Weaviate, Qdrant, Milvus, Chroma, hybrid search, scaling"
tags:
  - interview
  - ai-ml
  - vector-databases-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Vector Databases"
  - "Vector databases interview"
  - "Vector DB interview"
prerequisites: []
next: []
updated: "2026-04-25"
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


> [!mcq]
>
> - [x] **A.** Специализированная БД для high-dimensional векторов с ANN-индексами (HNSW, IVF) и kNN-поиском
>
>     Vector database хранит embeddings (256–3072 dims) и быстро находит k ближайших векторов через approximate nearest neighbor алгоритмы. Дополнительно: metadata filtering, hybrid search, replication. Применения — semantic search, RAG, recommendations, anomaly detection.
>
>     ВАЖНО: основная операция — kNN search, а не точное равенство по ключу как в classic OLTP.
>
> - [ ] **B.** Обычная реляционная БД (Postgres / MySQL) с колонкой типа `VARBINARY` для хранения векторов
>
>     B-tree индексы и WHERE-фильтры не работают для multi-dimensional similarity search — придётся делать full table scan и сравнивать каждый вектор (`O(n)`).
>
>     ПОСЛЕДСТВИЕ: на 10M векторов один запрос займёт минуты вместо миллисекунд — production-сервис ляжет под нагрузкой RAG.
>
> - [ ] **C.** Time-series база (InfluxDB, TimescaleDB) для хранения временных метрик ML-моделей
>
>     Это совершенно другой класс БД — оптимизирован для append-only timestamped данных, а не для similarity search в high-dim пространстве.
>
>     ПОСЛЕДСТВИЕ: при попытке хранить embeddings в TSDB получите ни kNN-индексов, ни эффективного фильтра по metadata — продукт не взлетит.
>
> - [ ] **D.** In-memory cache (Redis) исключительно для key-value доступа к embeddings по ID
>
>     Redis умеет хранить вектора и с RediSearch даже искать по ним, но «cache по ID» — это не vector DB. Без ANN-индекса возвращаемся к brute force.
>
>     ПОСЛЕДСТВИЕ: разработчики думают «у нас есть vector DB», а по факту делают O(n) сравнение на каждый запрос — latency растёт линейно с корпусом.

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


> [!mcq]
>
> - [ ] **A.** Реляционная БД с B-tree индексом и WHERE-фильтром по компонентам вектора отлично решает задачу
>
>     B-tree работает только на одномерных упорядоченных ключах. У 1024-мерного вектора нет естественного порядка — невозможно построить индекс, который ускорит «найди ближайшего соседа в L2».
>
>     ПОСЛЕДСТВИЕ: запросы вырождаются в seq scan по всей таблице — на миллионе строк это сотни мс CPU-времени per query, RAG-пайплайн становится неюзабельным.
>
> - [x] **B.** Brute force kNN — это O(n), а специальные БД дают O(log n) через ANN-индексы (HNSW/IVF) и quantization
>
>     1M векторов × 1024 dims = 4 GB; full scan на CPU ≈ 500 ms. Vector DB через HNSW даёт миллисекунды, IVF — десятки мс. Plus: distributed scaling, streaming updates, hybrid search, quantization для cost reduction. Postgres B-tree не работает на multi-dim — нужен pgvector с HNSW/IVFFlat.
>
>     ВАЖНО: разница не в «удобстве», а в алгоритмической сложности — иначе latency растёт линейно с корпусом.
>
> - [ ] **C.** Single-threaded сравнение каждого вектора с query достаточно быстрое на современных CPU
>
>     На корпусе 100K–1M это уже сотни мс на запрос даже с SIMD. На 10M+ — секунды. Production RAG требует p95 < 50 ms, а параллельные пользователи быстро забивают пул потоков.
>
>     ПОСЛЕДСТВИЕ: при росте корпуса latency деградирует линейно, throughput падает — реальный продакшен (Pinecone, Qdrant) использует ANN именно по этой причине.
>
> - [ ] **D.** Достаточно сохранить вектора в S3 как JSON и читать их при поиске
>
>     Без индекса в RAM каждый запрос превращается в I/O-bound full scan — сотни мегабайт на запрос. S3 — это object storage, не базовая абстракция для поиска.
>
>     ПОСЛЕДСТВИЕ: latency измеряется секундами, cost на S3 GET-запросы взлетает, а throughput ограничен пропускной способностью сети.

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

Подробнее — в [Embeddings](embeddings-interview.md).


> [!mcq]
>
> - [ ] **A.** Embedding — это hash от текста для быстрого lookup по равенству
>
>     Hash детерминирован, но не сохраняет семантическую близость: «king» и «queen» имеют близкие embeddings, но совершенно разные hash-значения. Hash годится для exact-match, не для similarity.
>
>     ПОСЛЕДСТВИЕ: если использовать hash вместо embedding, semantic search вырождается в exact-match и весь смысл RAG/recommendations теряется.
>
> - [ ] **B.** Embedding — это one-hot vector размером со словарь, где dimensions = количеству уникальных слов
>
>     One-hot — это устаревшее представление (до 2013) с миллионами sparse-измерений без семантики: «cat» и «kitten» ортогональны (similarity = 0).
>
>     ПОСЛЕДСТВИЕ: получите огромные sparse-вектора без semantic similarity — нельзя найти «похожее по смыслу», только exact word match.
>
> - [x] **C.** Embedding — dense vector чисел (256–3072 dims), где близость в пространстве отражает semantic similarity
>
>     Каждое измерение — abstract feature, выученная моделью (transformer, CLIP). «Hello» → `[0.12, -0.45, ...]` размерности 384–3072. Trade-off: больше dims = точнее, но дороже storage и slower поиск. Стандарты: 384 (MiniLM, быстро), 1024 (Cohere), 1536 (OpenAI text-embedding-3-small), 3072 (text-embedding-3-large).
>
>     ВАЖНО: high-dim нужна именно для того, чтобы линейная разделимость концепций стала возможной — в low-dim близкие смыслы коллапсируют.
>
> - [ ] **D.** Embedding — это zip-сжатие raw текста с восстановлением через декодер
>
>     Embedding — lossy lossy projection в смысловое пространство, а не сжатие. Из 1536-dim вектора нельзя восстановить исходный текст байт-в-байт — это feature extraction, не compression.
>
>     ПОСЛЕДСТВИЕ: ожидание «расжать обратно» приведёт к разочарованию — embeddings нужны для similarity, а для исходного текста надо хранить его рядом в metadata.

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


> [!mcq]
>
> - [ ] **A.** Все три метрики дают идентичный ranking результатов независимо от свойств векторов
>
>     Cosine и dot совпадают только на нормализованных векторах (`||v|| = 1`). Если magnitude варьируется — dot отдаёт предпочтение «длинным» векторам, что меняет порядок top-k по сравнению с cosine. Euclidean вообще измеряет разницу позиций, а не angle.
>
>     ПОСЛЕДСТВИЕ: смешивая метрики на ненормализованных embeddings, получите разные top-k и непредсказуемое качество retrieval — типичный баг при миграции между embedding-моделями.
>
> - [ ] **B.** Cosine similarity всегда лучше dot product, потому что dot product не имеет геометрического смысла
>
>     Dot имеет смысл: это проекция одного вектора на другой и совпадает с cosine на нормализованных векторах. Более того, для нормализованных embeddings dot **быстрее** — не надо считать норму.
>
>     ПОСЛЕДСТВИЕ: запрет dot product закроет оптимизацию, которая даёт 1.5–2× ускорение на нормализованных моделях (OpenAI, Cohere) без потери качества.
>
> - [ ] **C.** Euclidean distance даёт лучший recall чем cosine для всех типов embeddings
>
>     Recall зависит от того, **какой метрикой обучалась модель**. Большинство современных embedding-моделей (OpenAI, Cohere, Sentence-Transformers, CLIP) обучаются с cosine loss — Euclidean даст хуже recall именно потому, что не соответствует training objective.
>
>     ПОСЛЕДСТВИЕ: использование «не той» метрики ломает семантику — модель оптимизирована под angle, а вы меряете distance, retrieval-quality деградирует на 10–30%.
>
> - [x] **D.** Cosine не зависит от magnitude (только angle), dot зависит от magnitude и angle, Euclidean — от позиции; на нормализованных векторах cosine ≡ dot, но dot быстрее
>
>     `cos(A,B) = (A·B)/(|A|·|B|)`, range [-1,1]. `A·B = Σ a_i·b_i`, range (-∞,+∞). `||A-B|| = sqrt(Σ(a_i-b_i)²)`, range [0,∞). Для `||v||=1` (OpenAI, Cohere, Voyage) `cos == dot`, но dot быстрее (без normalization). Sentence-transformers/CLIP — cosine. Manhattan (L1) `Σ|a_i-b_i|` используется реже.
>
>     ВАЖНО: выбор метрики должен соответствовать тому, как обучалась embedding-модель — это не вопрос вкуса, а вопрос корректности.

## Q5. Когда какую метрику выбрать?

**Для embedding моделей:**
- **OpenAI, Cohere, Voyage** — нормализованные → **cosine = dot product** (выбирай dot для скорости)
- **Sentence transformers** — обычно cosine
- **Image embeddings (CLIP)** — cosine

**Best practice:** проверь docs модели. Если рекомендована cosine — используй cosine.

**Для нормализованных векторов** (`||v|| = 1`) — cosine == dot product. Вычисление dot быстрее (без normalization).


> [!mcq]
>
> - [x] **A.** Выбирать ту метрику, под которую обучалась embedding-модель; для нормализованных моделей (OpenAI, Cohere) — dot product как более быстрый эквивалент cosine
>
>     OpenAI/Cohere/Voyage выдают нормализованные векторы → cosine ≡ dot, выбирай dot для скорости (нет деления на норму). Sentence-transformers — cosine. CLIP — cosine. Главное правило: открыть docs модели и взять рекомендованную метрику — это та, под которую считался training loss.
>
>     ВАЖНО: на нормализованных векторах dot выигрывает 1.5–2× по latency без потери recall — это бесплатная оптимизация, если знать что embeddings уже unit-length.
>
> - [ ] **B.** Всегда выбирать Euclidean — она универсальна и работает с любой моделью
>
>     Euclidean игнорирует тот факт, что современные embeddings обучаются с cosine/dot loss. На нормализованных векторах Euclidean монотонно связана с cosine (`||a-b||² = 2 - 2·cos`), но на ненормализованных — даёт совершенно другой ranking.
>
>     ПОСЛЕДСТВИЕ: подмена рекомендуемой метрики на Euclidean «по умолчанию» снижает retrieval-quality на 10–30% и ломает RAG-пайплайн без видимой причины.
>
> - [ ] **C.** Выбор метрики не важен — можно использовать любую, главное consistency между upsert и query
>
>     Consistency обязательна, но недостаточна. Если индекс построен на Euclidean, а модель обучалась с cosine — top-k будет систематически «не тем» даже при идеальном matching upsert/query.
>
>     ПОСЛЕДСТВИЕ: получите стабильно низкий recall и думать что виновата модель — а виновато несоответствие метрики и training objective.
>
> - [ ] **D.** Cosine дороже dot product даже на нормализованных векторах, поэтому Pinecone/Qdrant используют только Euclidean
>
>     Pinecone, Qdrant, Weaviate, pgvector поддерживают **все три** метрики и default обычно cosine. На нормализованных векторах сам HNSW считает dot — это деталь реализации, не выбор метрики на уровне API.
>
>     ПОСЛЕДСТВИЕ: ложное утверждение про «только Euclidean» приведёт к неправильному выбору vector DB по несуществующему ограничению.

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


> [!mcq]
>
> - [ ] **A.** ANN — это exact kNN, реализованный через GPU; recall всегда 1.0, отличается только hardware
>
>     ANN расшифровывается как Approximate Nearest Neighbor — буква «A» означает именно «приблизительный», а не «accelerated». GPU-ускоренные exact kNN существуют (например, FAISS Flat на GPU), но это **не ANN** — у них recall=1.0 и сложность всё ещё O(n).
>
>     ПОСЛЕДСТВИЕ: путаница в терминологии приведёт к неверным архитектурным решениям — будете ждать exact-результатов от HNSW и удивляться отсутствующим записям в top-k.
>
> - [x] **B.** ANN — приблизительный поиск top-k с измеримой потерей качества (recall), даёт sub-linear latency в обмен на возможные near-miss; production обычно настраивают на recall 0.9–0.99
>
>     Exact kNN — O(n) full scan, гарантирует истинные top-k. ANN (HNSW/IVF) — sub-linear, может пропустить пару кандидатов. Recall = доля истинных top-k среди возвращённых: 0.95 = нашли 95% правильных. Trade-off: высокий recall ⇒ выше latency. В production 0.9–0.99 — приемлемо для RAG/recommendations, для критичных задач (fraud, medical) — выше или exact.
>
>     ВАЖНО: recall — это metric, который надо мерить на eval-set, а не «доверять» библиотеке. Параметры efSearch/nprobe тюнятся под целевой recall.
>
> - [ ] **C.** ANN означает «Artificial Neural Network» — это нейросеть, которая предсказывает соседей по embedding
>
>     В контексте vector search ANN = Approximate Nearest Neighbor. Artificial Neural Network — другая концепция (используется для генерации самих embeddings, но не для поиска по индексу).
>
>     ПОСЛЕДСТВИЕ: будете искать «модель ANN» в HuggingFace вместо настройки HNSW/IVF индекса — потеряете время на ложном пути.
>
> - [ ] **D.** ANN гарантирует тот же top-k что exact kNN, просто быстрее за счёт SIMD-инструкций
>
>     Если бы ANN гарантировал тот же top-k — он был бы exact kNN. Само слово «approximate» означает что результат может отличаться. SIMD ускоряет brute force, но не превращает его в ANN.
>
>     ПОСЛЕДСТВИЕ: при тестировании сравнения с exact будут «таинственные» расхождения, которые на самом деле — нормальное поведение ANN с recall < 1.0.

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


> [!mcq]
>
> **Вопрос:** Что делает HNSW многоуровневую графовую структуру эффективной для ANN-поиска и какая ключевая особенность отличает её от других ANN-индексов?
>
> - [ ] HNSW строит сбалансированное B-дерево по векторам и обеспечивает гарантированный O(log N) точный поиск без потери recall
>   - Почему неверно: B-деревья работают на 1D ключах и неприменимы к high-dim векторам (curse of dimensionality)
>   - Корректная модель: HNSW — это **multi-layer graph** (не дерево), верхние уровни sparse для быстрой навигации, нижний — dense со всеми узлами
>   - Последствие на практике: попытка использовать B-tree-логику приведёт к линейному скану и деградации latency на миллионах векторов
>
> - [ ] HNSW требует периодического полного rebuild при каждом insert/delete, поэтому подходит только для статичных датасетов
>   - Почему неверно: одна из главных фишек HNSW — поддержка **incremental updates** (insert/delete без rebuild)
>   - Корректная модель: новый вектор вставляется greedy walk от верхнего уровня, соединяется с M ближайшими соседями; rebuild не нужен
>   - Последствие на практике: ошибочный выбор IVF для динамичных данных приведёт к необходимости retraining и downtime для реиндексации
>
> - [x] HNSW использует иерархию графов с long-range рёбрами наверху и dense связностью внизу: search идёт greedy walk сверху вниз, давая отличный recall/latency trade-off при O(log N) ожидаемой сложности
>   - Механизм: верхние слои — sparse small-world граф для крупных прыжков; ef_search контролирует ширину beam-search на уровне 0; M задаёт связность графа
>   - Trade-off: память ~1.5-3× от размера векторов и медленный build (efConstruction 200-500) в обмен на лучший recall@latency среди ANN
>   - Когда применять: latency-critical workloads (RAG, semantic search, recommender) с динамическими данными — дефолт в Pinecone, Weaviate, Qdrant, pgvector
>
> - [ ] HNSW гарантирует exact nearest neighbor поиск и используется когда нужна 100% точность совпадения
>   - Почему неверно: HNSW — это **A**pproximate NN алгоритм, recall обычно 0.95-0.99, не 1.0
>   - Корректная модель: для exact search нужен brute-force (O(N)) — приемлемо только на маленьких корпусах (<10k); HNSW сознательно жертвует точностью ради скорости
>   - Последствие на практике: если бизнес-требование — exact match (например, дедупликация по точному эмбеддингу), HNSW даст false negatives на edge-cases


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


> [!mcq]
>
> **Вопрос:** Какова основная идея IVF (Inverted File Index) и какой параметр контролирует баланс recall/latency на этапе поиска?
>
> - [ ] IVF строит multi-layer навигационный граф со skip-уровнями, аналогично HNSW, но с меньшим параметром M для экономии памяти
>   - Почему неверно: IVF — это **partition-based** алгоритм (k-means clustering), а не graph-based; путать с HNSW — типичная ошибка
>   - Корректная модель: pre-build делает k-means на N точек → nlist центроидов; каждый вектор приписан ближайшему центроиду — никакого графа нет
>   - Последствие на практике: непонимание разницы приведёт к неверной настройке (efSearch не существует для IVF, нужен nprobe)
>
> - [ ] IVF использует параметр `nlist` во время search для выбора числа сканируемых clusters: чем больше nlist — тем выше recall
>   - Почему неверно: `nlist` — это **build-time** параметр (число центроидов, обычно √N), а не search-time
>   - Корректная модель: на этапе поиска nlist уже зафиксирован; recall/latency регулируется через **nprobe** (1-10% от nlist) — сколько кластеров обыскивать
>   - Последствие на практике: попытка крутить nlist в проде потребует полного rebuild индекса вместо мгновенной настройки nprobe
>
> - [ ] IVF поддерживает incremental updates без retraining и оптимален для динамичных датасетов с постоянными insert/delete
>   - Почему неверно: при значительном data drift центроиды становятся неоптимальными — нужен **retraining** (новый k-means)
>   - Корректная модель: IVF подходит для относительно стабильных распределений; для динамики лучше HNSW с incremental insertion
>   - Последствие на практике: без retraining recall деградирует со временем — векторы попадают не в свои кластеры, поиск пропускает relevant соседей
>
> - [x] IVF разбивает векторное пространство на nlist кластеров через k-means; при поиске пробует только top nprobe ближайших центроидов — параметр **nprobe** на этапе query управляет recall/latency trade-off
>   - Механизм: query vector сравнивается с nlist центроидами (дешёво) → выбираются top-nprobe → exhaustive search внутри них; всё остальное игнорируется
>   - Trade-off: меньше памяти и быстрее build чем HNSW, но хуже recall@latency и нужен retrain при data drift; nprobe=1 даёт максимум скорости, nprobe=nlist эквивалентен brute-force
>   - Когда применять: memory-constrained сценарии с миллиардами векторов и относительно статичным распределением — часто комбинируется с PQ (IVF-PQ) в FAISS/Milvus


## Q9. (!) Сравнение HNSW vs IVF?

| Критерий | HNSW | IVF |
|----------|------|-----|
| Recall/latency | **Лучше** | Хуже |
| Memory | Больше | Меньше |
| Build speed | Медленнее | Быстрее |
| Updates | **Incremental** | Нужен retrain |
| Best для | Latency-critical | Larger datasets, memory-constrained |

**В 2025** — HNSW **дефолт** в большинстве vector DBs (Pinecone, Weaviate, Qdrant, pgvector). IVF используется реже, для memory-constrained scenarios.


> [!mcq]
>
> **Вопрос:** Команда выбирает ANN-индекс для production semantic search с 50M документов, частыми обновлениями каталога и SLA p95 < 50ms. Какой индекс предпочтительнее и почему?
>
> - [x] HNSW — даёт лучший recall/latency trade-off, поддерживает incremental insert/delete без rebuild, и стал дефолтом в Pinecone/Weaviate/Qdrant/pgvector именно для latency-critical workloads
>   - Механизм: graph-based search идёт greedy walk сверху вниз → точное соответствие требованию p95<50ms на 50M; новые документы каталога вставляются inline через efConstruction без блокировки трафика
>   - Trade-off: память 1.5-3× от raw vectors (для 50M × 768-dim float32 ≈ 150GB → можно шардить), build медленнее IVF — но это разовая стоимость
>   - Когда применять: именно этот сценарий — latency-critical + dynamic data; HNSW — стандарт индустрии в 2025 для RAG/semantic search/recommender
>
> - [ ] IVF — экономит память по сравнению с HNSW, поэтому всегда предпочтительнее для production с большими датасетами
>   - Почему неверно: «всегда предпочтительнее» — false; IVF хуже по recall@latency и **требует retraining** при data drift, что несовместимо с частыми обновлениями каталога
>   - Корректная модель: IVF выбирают когда memory — главный constraint, а данные стабильны; для динамики и latency-критичности — HNSW
>   - Последствие на практике: периодический retrain k-means на 50M потребует часов downtime либо deployment второго индекса для switchover — операционно дороже, чем экономия памяти
>
> - [ ] Brute-force (exact KNN) — единственный способ гарантировать корректность search, ANN-индексы давать неверные результаты
>   - Почему неверно: brute-force на 50M даёт O(N) per query → сотни ms-секунд, p95<50ms физически недостижим; ANN — индустриальный стандарт с recall 0.95-0.99
>   - Корректная модель: для semantic search 95-99% recall более чем достаточно (релевантность определяется не топ-1, а топ-k); SLA важнее идеальной точности
>   - Последствие на практике: попытка использовать brute-force провалит SLA, потребует огромного кластера CPU/GPU, и пользователи всё равно не заметят разницы в recall
>
> - [ ] IVF-PQ — комбинация даёт максимальный recall и лучше всего подходит для динамичных каталогов с частыми обновлениями
>   - Почему неверно: PQ — **lossy** compression, recall ниже чем у чистого HNSW; «максимальный recall» — антитезис PQ
>   - Корректная модель: IVF-PQ выбирают для **миллиардов** векторов (не 50M) когда память — критичный bottleneck и можно жертвовать recall ради 128× compression
>   - Последствие на практике: для 50M IVF-PQ даст худший recall без выигрыша по памяти (50M влезает в HNSW на 1-2 нодах), плюс retrain при data drift


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


> [!mcq] В чём суть Product Quantization (PQ) для сжатия векторов?
>
> - [ ] **A) PQ сжимает каждый вектор целиком одним k-means на 256 центроидов и хранит ID центроида**
>   - **Что на самом деле:** так работает обычная скалярная/векторная квантизация — один codebook на весь вектор. PQ принципиально делит вектор на subvectors и обучает **отдельный** codebook для каждого куска.
>   - **Откуда путаница:** PQ действительно использует k-means внутри, поэтому легко свести его к «один codebook». Но именно декартово произведение независимых codebook'ов даёт экспоненциальный выигрыш (256^32 возможных комбинаций при 32 чанках).
>   - **Если бы это было правдой:** 1024-dim вектор сжимался бы в 1 байт, и вся high-dim информация терялась бы катастрофически — recall упал бы до неюзабельного уровня.
>
> - [ ] **B) PQ — это lossless алгоритм компрессии типа gzip, применённый к float32-векторам**
>   - **Что на самом деле:** PQ — это **lossy** quantization. Каждый subvector заменяется ближайшим центроидом из codebook, и оригинальные значения восстановить нельзя.
>   - **Откуда путаница:** слово «compression» по умолчанию ассоциируется с gzip/zstd. Но general-purpose сжатие даёт максимум 2-3× на float'ах, а PQ даёт 32-128× именно за счёт потерь.
>   - **Если бы это было правдой:** не пришлось бы делать re-ranking на оригинальных векторах, и не было бы trade-off с recall — но тогда бы и сжатия 128× не существовало.
>
> - [ ] **C) PQ строит graph index типа HNSW, но с пониженной размерностью векторов через PCA**
>   - **Что на самом деле:** PQ — это **компрессия векторов**, а не indexing structure. HNSW и IVF — отдельные ANN-индексы, которые можно скомбинировать с PQ (IVF-PQ, HNSW-PQ). PCA — линейная projection, совсем другой подход.
>   - **Откуда путаница:** PQ часто используется **внутри** HNSW/IVF индексов, поэтому в документации FAISS они идут рядом. Но это ортогональные техники: index = как искать, PQ = как хранить.
>   - **Если бы это было правдой:** не было бы комбинаций «HNSW-PQ» и «IVF-PQ» как отдельных опций в FAISS — они существуют именно потому, что PQ это слой компрессии поверх индекса.
>
> - [x] **D) PQ делит вектор на M subvectors, обучает отдельный codebook (k-means) на каждом, хранит вектор как M байтов с ID центроидов**
>   - **Развёрнутое объяснение:** Product Quantization — это lossy compression для high-dim векторов. Вектор размерности D режется на M равных частей размерности D/M. Для каждой позиции subvector'а обучается **независимый** codebook через k-means (обычно k=256, чтобы ID помещался в 1 байт). Итог: вектор хранится как M байтов вместо D×4 байт. Поиск ускоряется через **asymmetric distance computation (ADC)**: query не квантизуется, расстояния от query до всех центроидов каждого codebook'а считаются один раз и кэшируются в lookup table, дальше distance до любого compressed вектора = сумма M lookup'ов.
>   - **Пример:** 1024-dim float32 vector = 4096 bytes. M=32, D/M=32 dims на subvector, k=256 центроидов. Результат: 32 bytes на вектор — **128× compression**. Для 1B векторов это разница между 4 TB и 32 GB — помещается в RAM одного хоста.
>   - **Когда применять:** датасеты от ~100M векторов, когда RAM становится bottleneck'ом; в комбинации с IVF (IVF-PQ) для миллиардов; когда recall@10 = 0.85-0.90 приемлем (часто компенсируется re-ranking на топ-K оригинальных векторов).
>   - **Подводные камни:** (1) recall падает с ростом M — нужно подбирать под задачу; (2) обучение codebook'ов требует representative sample (>100k векторов); (3) ADC даёт оценку дистанции, а не точную — для финального ranking нужен rerank на uncompressed векторах; (4) PQ плохо работает на векторах с сильной корреляцией между координатами — стоит делать random rotation (OPQ) перед квантизацией.
>   - **Связанные вопросы:** [[Q8]] HNSW, [[Q9]] HNSW vs IVF, [[Q11]] FAISS как реализация PQ.

## Q11. ScaNN, FAISS, Annoy?

| Library | Создатель | Особенности |
|---------|-----------|-------------|
| **FAISS** | Meta | Самая популярная C++ library. IVF, HNSW, PQ. Не managed DB. |
| **ScaNN** | Google | Очень быстрый. Используется в YouTube, Search. |
| **Annoy** | Spotify | Tree-based (не graph). Простой, но хуже HNSW. |
| **HNSWlib** | — | Lightweight HNSW C++ |

**FAISS** часто используется как **embedded** library внутри custom apps. Vector DBs (Milvus, Vespa) построены поверх FAISS-подобных движков.


> [!mcq] Чем принципиально отличаются FAISS, ScaNN и Annoy как ANN-библиотеки?
>
> - [x] **A) FAISS — C++ библиотека Meta с IVF/HNSW/PQ (не БД); ScaNN — оптимизированная Google-библиотека (asymmetric hashing); Annoy — Spotify, tree-based forest, проще но менее точная**
>   - **Развёрнутое объяснение:** Все три — **embedded ANN-библиотеки** (не managed БД), но с разной архитектурой. **FAISS** — самая универсальная: GPU support, десятки index types (Flat, IVF, IVFPQ, HNSW, IVFSQ), Python/C++ API. **ScaNN** делает упор на **asymmetric hashing + anisotropic quantization** — обучает квантизацию так, чтобы минимизировать ошибку именно для inner-product метрики; на ANN-benchmarks выигрывает по recall@10 на большинстве датасетов. **Annoy** строит **forest of random projection trees** — каждое дерево разбивает пространство случайной гиперплоскостью; ищет по нескольким деревьям и объединяет кандидатов; проигрывает HNSW по recall/speed, но даёт mmap-friendly формат файла (отсюда любовь Spotify к нему — load instantly).
>   - **Пример:** Pinterest visual search использует FAISS (миллиарды image embeddings, GPU); YouTube recommendations используют ScaNN внутри TFRS; Spotify music recommendations исторически на Annoy (mmap позволяет шарить index между сервисами без копирования в RAM).
>   - **Когда применять:** FAISS — дефолт для research и production когда нужны разные index types; ScaNN — когда нужен максимальный recall на inner-product метрике и есть TensorFlow в стеке; Annoy — когда index должен лежать на диске и mmap'иться (мобильные приложения, edge inference).
>   - **Подводные камни:** (1) ни одна из трёх не даёт **CRUD на векторах** — это библиотеки индексации, для production нужна обёртка с persistence и обновлениями; (2) FAISS не thread-safe для записи — нужен внешний lock; (3) Annoy требует **полной перестройки** при добавлении векторов (нет incremental updates); (4) ScaNN сложнее в deployment из-за зависимости от TensorFlow.
>   - **Связанные вопросы:** [[Q8]] HNSW, [[Q10]] PQ, [[Q12]] Pinecone, [[Q15]] Milvus как обёртка над FAISS-like движками.
>
> - [ ] **B) Все три — managed cloud services с REST API; различаются только ценой и регионами**
>   - **Что на самом деле:** все три — **embedded libraries**, которые подключаются как dependency в код (C++/Python). У них нет REST API, нет managed hosting, нет multi-tenancy. Managed-сервисы (Pinecone, Weaviate Cloud, Qdrant Cloud) — это отдельный класс продуктов, часто построенный **поверх** FAISS-подобных движков.
>   - **Откуда путаница:** в обсуждениях vector DB часто перечисляют «Pinecone, Weaviate, FAISS, Milvus» в одном списке. Но это разные уровни абстракции: FAISS — library, Pinecone — SaaS.
>   - **Если бы это было правдой:** не было бы смысла в Milvus и Vespa, которые именно оборачивают FAISS-style индексы в distributed БД с persistence и API.
>
> - [ ] **C) FAISS и Annoy используют HNSW под капотом, а ScaNN — это форк FAISS с поддержкой gRPC**
>   - **Что на самом деле:** HNSW — лишь один из десятков index types в FAISS, и Annoy его вообще не использует (Annoy строит RP-tree forest, фундаментально другой алгоритм). ScaNN — самостоятельная разработка Google на основе anisotropic quantization, не форк FAISS, и gRPC к нему не относится.
>   - **Откуда путаница:** HNSW стал де-факто стандартом и кажется, что «все ANN-библиотеки = HNSW». На деле есть три семейства: graph-based (HNSW, NSG), tree-based (Annoy, FLANN), quantization-based (IVF-PQ, ScaNN).
>   - **Если бы это было правдой:** не существовало бы benchmark-категорий по разным алгоритмам на ann-benchmarks.com — там сравнивают именно разные семейства.
>
> - [ ] **D) FAISS работает только на CPU, ScaNN только на TPU, Annoy только на мобильных устройствах**
>   - **Что на самом деле:** FAISS имеет **первоклассную GPU поддержку** (faiss-gpu — один из её главных козырей, ускорение в 10-20× на больших датасетах). ScaNN — это CPU-библиотека (TPU там не используется напрямую), оптимизированная под AVX-512. Annoy работает где угодно, не привязан к мобильным.
>   - **Откуда путаница:** Annoy любят за маленький binary и mmap, что удобно на мобильных — отсюда миф «только для мобильных». Но Spotify-серверы на нём работают годами.
>   - **Если бы это было правдой:** не было бы faiss-gpu пакета в PyPI и whole-FAISS-on-GPU benchmarks, которые регулярно показывают на NVIDIA конференциях.

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


> [!mcq] Что характеризует Pinecone как managed vector DB и когда его выбирать?
>
> - [ ] **A) Pinecone — open-source проект с возможностью self-hosted deployment в Kubernetes**
>   - **Что на самом деле:** Pinecone — это **fully managed proprietary SaaS**, исходного кода нет в публичном доступе, self-hosted opции **не существует**. Это его главное архитектурное отличие от Weaviate/Qdrant/Milvus, которые все open-source с managed-опцией.
>   - **Откуда путаница:** многие vector DB (Weaviate, Qdrant, Milvus) — open-source, и Pinecone часто упоминают в том же ряду. Но он сознательно построен как closed-source SaaS, что было причиной критики со стороны open-source сообщества.
>   - **Если бы это было правдой:** не было бы лock-in концерна при выборе Pinecone и не было бы такого роста Qdrant/Weaviate как «open-source альтернатив Pinecone».
>
> - [x] **B) Pinecone — fully managed proprietary SaaS (нет self-hosted); HNSW под капотом; serverless tier с pay-per-use; HTTP/gRPC API; выбирают когда нужно minimal ops и есть бюджет**
>   - **Развёрнутое объяснение:** Pinecone — самый известный managed vector DB, появился в 2019. Архитектура **closed-source SaaS**: данные хранятся в AWS/GCP/Azure под капотом, пользователь работает через REST/gRPC API. Под капотом — HNSW (с проприетарными оптимизациями). С 2023 появился **serverless tier** (отдельный movement compute/storage) — платишь только за storage и queries, без выделенных «pod'ов». Поддерживает **metadata filtering** (фильтры по атрибутам с pre-filter оптимизацией), **hybrid search** (sparse+dense, для BM25-like sparse vectors), namespaces (multi-tenancy внутри одного index). SDK для Python/Node/Go/Java.
>   - **Пример:** стартап делает RAG-чатбота для документации. Команда из 3 человек, нет DevOps. Берут Pinecone serverless — `pip install pinecone-client`, `index.upsert()`, `index.query()`, готово. Без него пришлось бы поднимать Qdrant в Kubernetes, настраивать persistence, мониторинг, backup'ы — недели работы.
>   - **Когда применять:** (1) маленькая команда без DevOps; (2) PoC/MVP — быстрый старт без инфраструктуры; (3) уже на AWS/GCP и проще через SaaS; (4) когда стоимость inженера-месяца на ops > чем разница в цене с self-hosted; (5) compliance требует SOC2/HIPAA, а Pinecone уже сертифицирован.
>   - **Подводные камни:** (1) **дорого** на масштабе — ~$70-100 в месяц на миллион векторов с базовой нагрузкой, против ~$30 на тот же объём в Qdrant Cloud или ~$10 на self-hosted; (2) **vendor lock-in** — нет миграционного пути «выгрузить index и поднять локально», только re-embedding и re-upsert; (3) ограниченная кастомизация — нельзя выбрать HNSW параметры (M, efConstruction); (4) latency p99 хуже чем у dedicated self-hosted из-за multi-tenant SaaS природы; (5) с 2024 Pinecone начал deprecation старых pod-based индексов в пользу serverless — миграция требует усилий.
>   - **Связанные вопросы:** [[Q1]] что такое vector DB, [[Q8]] HNSW, [[Q13]] Weaviate как альтернатива, [[Q14]] Qdrant как open-source альтернатива.
>
> - [ ] **C) Pinecone использует GraphQL API и встроенные модули для генерации embeddings типа text2vec-openai**
>   - **Что на самом деле:** это описание **Weaviate**, не Pinecone. Pinecone использует REST/gRPC (без GraphQL) и **не делает embeddings сам** — пользователь должен сгенерировать векторы внешним моделью (OpenAI Embeddings API, Cohere, локальная модель) и передать их в `upsert()`.
>   - **Откуда путаница:** оба продукта позиционируются как «managed vector DB для RAG», и фичу автоматических embeddings часто приписывают всем. Pinecone с 2024 добавил **Pinecone Inference** API для генерации embeddings, но это отдельный сервис, а не «модуль внутри index».
>   - **Если бы это было правдой:** не нужны были бы туториалы «как использовать OpenAI Embeddings + Pinecone», которые показывают именно явный шаг embed → upsert.
>
> - [ ] **D) Pinecone — это PostgreSQL extension типа pgvector, добавляющий vector type в существующую БД**
>   - **Что на самом деле:** Pinecone — самостоятельный SaaS, не связан с PostgreSQL. **pgvector** — это extension для Postgres, другой подход к проблеме (использовать существующую RDBMS вместо отдельной БД).
>   - **Откуда путаница:** оба решают задачу «хранение векторов», но это противоположные архитектурные подходы: Pinecone = специализированный SaaS, pgvector = embedded в OLTP-базу.
>   - **Если бы это было правдой:** Pinecone был бы бесплатным (как pgvector), и не имело бы смысла его managed tier $70+/M vectors.

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


> [!mcq] Что отличает Weaviate от других vector DB и почему его выбирают?
>
> - [ ] **A) Weaviate — closed-source proprietary SaaS без self-hosted опции, как Pinecone**
>   - **Что на самом деле:** Weaviate — **open-source под Apache 2.0**, можно self-host в Docker/Kubernetes; managed cloud (Weaviate Cloud Services) — опциональный SaaS поверх того же кода. Это противоположность Pinecone, которая именно proprietary-only.
>   - **Откуда путаница:** Weaviate Cloud рекламируется как managed-сервис, что у новичков ассоциируется с «значит закрытый». На деле open-source repo на GitHub имеет 11k+ звёзд и активно принимает PR'ы.
>   - **Если бы это было правдой:** не было бы смысла в self-hosted production deployments Weaviate в банках/госах, которым лицензионно нельзя использовать закрытый SaaS.
>
> - [x] **B) Weaviate — open-source (Apache 2.0) vector DB с GraphQL API, встроенными vectorizer-модулями (text2vec-openai/cohere), hybrid search BM25+vector и multi-tenancy**
>   - **Развёрнутое объяснение:** Weaviate позиционируется как самая **feature-rich** open-source vector DB. Ключевая идея — vectorizer-модули генерируют embedding автоматически при insert: пишешь `{"content": "Hello"}`, модуль `text2vec-openai` вызывает OpenAI и сохраняет вектор. Поддержка нескольких API: **GraphQL** (основной, удобен для nested filters), REST, gRPC, native Python/JS-клиенты. **Hybrid search** комбинирует BM25 (keyword) и vector similarity с alpha-параметром взвешивания. **Multi-tenancy** — каждый tenant изолирован, не нужно делать per-tenant collection. Есть **generative search** — модуль вызывает LLM (OpenAI/Cohere/Anthropic) прямо из БД, объединяя retrieval и generation в один запрос (RAG-in-DB).
>   - **Пример:** SaaS-документация для 1000 клиентов — multi-tenancy выделяет каждому изолированное пространство в одной collection; vectorizer-модуль избавляет от отдельного embedding-сервиса; hybrid search вытаскивает и точные совпадения по терминам (BM25), и семантически близкие (vector).
>   - **Когда применять:** нужен feature-rich vector DB с минимумом glue-кода (vectorizer внутри); готовы запустить self-hosted и поддерживать кластер; multi-tenant SaaS-сценарии; RAG, где хочется собрать pipeline в одной системе.
>   - **Подводные камни:** (1) GraphQL API мощный, но имеет learning curve и хуже работает с типичными ORM/SQL-инструментами; (2) vectorizer-модули привязывают к конкретному провайдеру (text2vec-openai требует OpenAI key и оплату на каждый insert); (3) memory footprint выше Qdrant'а на тех же датасетах; (4) consistency model — eventually consistent при репликации, не подходит для strict transactional сценариев.
>   - **Связанные вопросы:** [[Q12]] Pinecone (closed-source альтернатива), [[Q14]] Qdrant (Rust-альтернатива), [[Q15]] Milvus (distributed-альтернатива), [[Q16]] pgvector (extension вместо отдельной БД).
>
> - [ ] **C) Weaviate использует только sparse-векторы (TF-IDF/BM25) и не поддерживает dense embedding'и от neural networks**
>   - **Что на самом деле:** Weaviate — **dense-vector first** БД, dense-векторы являются основной структурой хранения. BM25 (sparse) добавлен как часть hybrid search **в дополнение** к dense vector search, а не как замена.
>   - **Откуда путаница:** упоминание BM25 в фичах могло создать впечатление «это keyword-search движок типа Elasticsearch». На деле BM25 в Weaviate — second-class citizen для hybrid mode.
>   - **Если бы это было правдой:** не было бы смысла в text2vec-openai модулях, которые именно генерируют dense embeddings размерности 1536.
>
> - [ ] **D) Weaviate написан на Python и предназначен только для prototyping, в production не используется**
>   - **Что на самом деле:** Weaviate написан на **Go** (production-grade language для concurrent systems), активно используется в production у клиентов уровня Stack Overflow, Instabase, Cohere. Python — только клиент.
>   - **Откуда путаница:** в туториалах виден Python-клиент, и легко спутать клиент с реализацией БД. Большинство modern vector DB именно так: сервер на Go/Rust/C++, клиенты на Python/JS.
>   - **Если бы это было правдой:** не было бы Helm-чартов для production Kubernetes-deployment'ов и benchmarks на миллионах векторов, которые требуют именно компилируемого языка.

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


> [!mcq] Что отличает Qdrant и почему он стал популярен в 2025?
>
> - [ ] **A) Qdrant — Python-библиотека, встроенная в numpy для быстрого поиска ближайших соседей**
>   - **Что на самом деле:** Qdrant — **standalone vector database** на Rust с собственным сетевым сервером (REST + gRPC), а не in-process библиотека. Python — лишь клиент (`qdrant-client`), который ходит по сети. numpy с ним не связан архитектурно.
>   - **Откуда путаница:** в туториалах для разработчика всё выглядит как `from qdrant_client import QdrantClient`, что напоминает обычную библиотеку. Но `QdrantClient("localhost", port=6333)` явно указывает на сетевое подключение к серверу.
>   - **Если бы это было правдой:** не было бы Docker-образа `qdrant/qdrant`, на котором запускают production-кластеры, и не было бы managed Qdrant Cloud.
>
> - [ ] **B) Qdrant написан на Java и работает только внутри JVM-приложений, не имеет standalone сервера**
>   - **Что на самом деле:** Qdrant написан на **Rust** — это его ключевая отличительная черта (memory-safety без GC, низкая latency, малый footprint). Standalone-сервер запускается одним бинарём, не требует JVM.
>   - **Откуда путаница:** Lucene/Elasticsearch — Java, и vector search ассоциируется с этим стеком. Но Qdrant сознательно вышел за пределы JVM-мира.
>   - **Если бы это было правдой:** не было бы преимущества по memory-эффективности и cold-start latency, которые Qdrant именно подчёркивает в маркетинге.
>
> - [x] **C) Qdrant — open-source vector DB на Rust с HNSW + опциональной quantization, excellent metadata filtering на indexed fields, gRPC+REST API, built-in sharding и replication**
>   - **Развёрнутое объяснение:** Qdrant написан на **Rust**, что даёт ему преимущества по latency и memory footprint (нет GC pauses, predictable performance). Использует **HNSW** как основной индекс с поддержкой **scalar quantization (int8) и binary quantization** для compress'а в 4-32× с минимальной потерей recall. Главная фишка — **payload (metadata) filtering**: поля, помеченные как indexed, попадают в отдельные индексы (B-tree для чисел, hash для keyword), и фильтр применяется **во время** HNSW-обхода, а не post-filter после ANN — это критично для cardinality-неравномерных фильтров. **gRPC** даёт высокую throughput для bulk-операций, **REST** удобен для exploration. Sharding (по hash от point ID) и replication (Raft consensus) встроены — кластер растягивается на несколько нод без внешних оркестраторов.
>   - **Пример:** e-commerce поиск похожих товаров с фильтром по категории/цене: индекс на `category` + `price_range` позволяет HNSW обходить только релевантные регионы графа; на 50M товаров p99 latency остаётся <50ms даже при выборках в 0.1% от датасета.
>   - **Когда применять:** open-source LLM-stack (LangChain/LlamaIndex имеют first-class интеграцию); сценарии с интенсивным metadata filtering; команды, которым важна низкая latency и низкое потребление памяти; production deployments без vendor lock-in.
>   - **Подводные камни:** (1) Rust-стек — меньше готовых решений и экспертизы, чем у Java/Go; (2) replication через Raft требует чёткой конфигурации quorum; (3) при квантизации recall падает на 2-5% — нужно профилировать на своих данных; (4) hot-reload конфигурации шардов ограничен — некоторые изменения требуют рестарта; (5) memory-mapping payload'ов работает хуже на NFS/cloud-disk'ах, чем на локальном NVMe.
>   - **Связанные вопросы:** [[Q8]] HNSW, [[Q10]] PQ/quantization, [[Q13]] Weaviate (Go-альтернатива), [[Q15]] Milvus (distributed-альтернатива).
>
> - [ ] **D) Qdrant поддерживает только exact KNN (brute-force) и не имеет ANN-индексов**
>   - **Что на самом деле:** Qdrant основан на **HNSW** — одном из самых эффективных ANN-алгоритмов. Brute-force поиск доступен как fallback (`exact=true`) для маленьких датасетов или тестов, но **не является основным режимом**.
>   - **Откуда путаница:** возможно, путают с FAISS Flat-index или с тем, что Qdrant можно заставить выполнить точный поиск флагом.
>   - **Если бы это было правдой:** не было бы смысла говорить о sub-millisecond latency на 10M+ векторах — brute-force такого не даёт даже на Rust.

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


> [!mcq] Чем Milvus принципиально отличается от Qdrant/Weaviate и когда его выбирать?
>
> - [ ] **A) Milvus — embedded ANN-библиотека уровня FAISS, без сетевого сервера и без persistence**
>   - **Что на самом деле:** Milvus — **distributed vector database** с компонентной архитектурой (proxy, query node, data node, index node, root coordinator), persistence через object storage (S3/MinIO), сетевые API (gRPC/REST). Это уровень выше FAISS — FAISS используется как один из движков **внутри** Milvus.
>   - **Откуда путаница:** FAISS-like алгоритмы (IVF, HNSW) используются внутри Milvus, что может создать впечатление эквивалентности. Но Milvus добавляет distributed orchestration, persistence, schema, мульти-replica.
>   - **Если бы это было правдой:** не было бы Helm-чартов для Kubernetes-deployment'а Milvus с десятком компонентов и не было бы Zilliz Cloud как managed-сервиса поверх.
>
> - [ ] **B) Milvus поддерживает только один ANN-алгоритм (HNSW) и не масштабируется горизонтально**
>   - **Что на самом деле:** Milvus поддерживает **множество ANN-индексов**: HNSW, IVF_FLAT, IVF_PQ, IVF_SQ8, ANNOY, DiskANN, GPU-IVF. Горизонтальное масштабирование — одна из его архитектурных целей: миллиарды векторов на распределённом кластере.
>   - **Откуда путаница:** HNSW — самый известный ANN-алгоритм, и его упоминают первым в туториалах. Но Milvus сознательно делает выбор алгоритма параметром, а не привязкой.
>   - **Если бы это было правдой:** не было бы доказанных production-deployments на десятках миллиардов векторов в enterprise, для которых выбор индекса под задачу критичен.
>
> - [ ] **C) Milvus — простая single-node БД с deployment в один docker-compose, проще чем Qdrant**
>   - **Что на самом деле:** Milvus имеет **distributed architecture** с разделёнными ролями нод (proxy/query/data/index/coordinator), что делает deployment сложнее, чем у monolith-БД (Qdrant/Weaviate запускаются одним бинарём). Milvus Lite существует как embedded-вариант для прототипов, но production-deployment требует Kubernetes.
>   - **Откуда путаница:** есть docker-compose для quick-start, что создаёт впечатление простоты. Но production требует K8s, etcd, MinIO/S3, Pulsar/Kafka.
>   - **Если бы это было правдой:** не было бы основной критики Milvus в индустрии — «overkill для маленьких проектов».
>
> - [x] **D) Milvus — open-source vector DB от Zilliz с distributed Kubernetes-native архитектурой, поддержкой множества ANN-алгоритмов (HNSW, IVF, ANNOY, DiskANN, GPU-IVF), strong consistency и GPU-acceleration для миллиардов векторов**
>   - **Развёрнутое объяснение:** Milvus спроектирован как **cloud-native distributed vector database** для масштаба «миллиарды векторов». Архитектура: **compute и storage разделены** (compute scales horizontally, storage в S3/MinIO), компоненты (Proxy, Query Node, Data Node, Index Node, Coordinator) деплоятся в Kubernetes независимо. Поддерживается **широкий спектр индексов**: HNSW (баланс), IVF_FLAT/IVF_PQ/IVF_SQ8 (для big data), DiskANN (для on-disk датасетов больше RAM), GPU_IVF_FLAT/GPU_IVF_PQ (для batch-сценариев с GPU). **Strong consistency** через Pulsar/Kafka log — гарантирует ordered writes и snapshot isolation для reads. **GPU acceleration** — индексация и поиск могут выполняться на NVIDIA GPU, что даёт 10-100× ускорение на больших датасетах.
>   - **Пример:** enterprise photo search на 10B изображений: distributed sharding раскидывает данные по 50 query-нодам, DiskANN-индекс позволяет хранить большую часть на NVMe; GPU-нода ускоряет nightly batch reindex; strong consistency гарантирует, что новые загрузки видны во всех репликах после commit.
>   - **Когда применять:** датасеты от 100M+ векторов с прогнозируемым ростом до миллиардов; enterprise-сценарии, где SLA на latency и consistency важнее простоты deployment; команды с Kubernetes-экспертизой; need для GPU-acceleration или DiskANN для больших данных, не помещающихся в RAM.
>   - **Подводные камни:** (1) **complex deployment** — десятки компонентов в K8s, что-то всегда «не запускается» (etcd, Pulsar, MinIO); (2) overkill для проектов <10M векторов — Qdrant/Weaviate проще и дешевле в ops; (3) Pulsar как message log — отдельная система, требующая отдельной экспертизы; (4) совместимость API между мажорными версиями (1.x → 2.x) ломалась — миграция большой боли; (5) memory overhead на coordinator-нодах высокий на маленьких deployments.
>   - **Связанные вопросы:** [[Q8]] HNSW, [[Q9]] HNSW vs IVF, [[Q10]] PQ для сжатия, [[Q11]] FAISS как движок под капотом, [[Q13]] Weaviate (monolith-альтернатива), [[Q14]] Qdrant (monolith-альтернатива).

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


> [!mcq] Что такое pgvector и когда его выбирать вместо dedicated vector DB?
>
> - [ ] **A) pgvector — отдельный standalone-сервер для векторов, независимый от PostgreSQL**
>   - **Что на самом деле:** pgvector — это **PostgreSQL extension** (`CREATE EXTENSION vector;`), который работает внутри обычного Postgres-инстанса. Отдельного процесса/сервера нет — vector type становится нативным типом колонки наряду с `int`, `text`, `jsonb`.
>   - **Откуда путаница:** другие vector DB (Pinecone, Qdrant, Milvus) — отдельные сервисы, и можно по аналогии решить, что pgvector тоже отдельный. Но вся его ценность как раз в том, что он живёт **внутри Postgres**.
>   - **Если бы это было правдой:** не было бы главного преимущества — JOIN'ов с обычными таблицами в одной транзакции и переиспользования существующей Postgres-инфраструктуры (backups, replication, pgBouncer).
>
> - [ ] **B) pgvector рассчитан только на flat (brute-force) поиск и не поддерживает ANN-индексы**
>   - **Что на самом деле:** pgvector 0.5+ поддерживает **HNSW** и **IVFFlat** ANN-индексы (`CREATE INDEX ... USING hnsw (embedding vector_cosine_ops)`), что даёт sub-linear поиск без full scan. Также есть операторы для cosine (`<=>`), Euclidean (`<->`) и inner product (`<#>`).
>   - **Откуда путаница:** ранние версии pgvector (до 0.5) действительно делали только sequential scan, что породило репутацию «медленный». С 0.5 это уже не так.
>   - **Если бы это было правдой:** pgvector не использовался бы в production на десятках миллионов векторов, как сейчас в стартапах в 2025.
>
> - [x] **C) pgvector — PostgreSQL extension, добавляющий тип `vector`, операторы расстояний (`<=>`, `<->`, `<#>`) и HNSW/IVFFlat ANN-индексы; оптимален когда уже есть Postgres и объём < 10–100M векторов**
>   - **Развёрнутое объяснение:** pgvector превращает обычный Postgres в гибридный store: одна и та же таблица содержит `embedding vector(1536)` и реляционные поля (`tenant_id`, `created_at`, `status`). Поиск идёт через `ORDER BY embedding <=> $1 LIMIT k`, индекс HNSW делает его sub-linear. Главные плюсы: **ACID и транзакции** (insert документа и его embedding атомарно), **JOIN'ы** с обычными таблицами и фильтрами (`WHERE tenant_id = ? AND status = 'active'`), **готовая инфра** (pg_dump, streaming replication, pgBouncer). В 2025 — default для startups до ~10M векторов, потому что не надо тащить отдельный сервис.
>   - **Пример:** RAG-чат для SaaS: документы и их embeddings в одной таблице `documents(id, tenant_id, content, embedding vector(1536))`, HNSW-индекс с `vector_cosine_ops`, поиск `WHERE tenant_id = $1 ORDER BY embedding <=> $2 LIMIT 10` — pre-filter по `tenant_id` + ANN, всё в одной транзакции с проверкой прав.
>   - **Когда применять:** уже есть Postgres и не хочется заводить отдельный сервис; объём 100K–100M векторов; нужны JOIN'ы и фильтры по реляционным полям; важны ACID и существующие backup/HA-процессы; команда не имеет опыта эксплуатации dedicated vector DB.
>   - **Подводные камни:** (1) HNSW-индекс **строится в RAM** — большой объём = долгая индексация и пик памяти; (2) HNSW в pgvector медленнее, чем в Qdrant/Pinecone на одинаковом железе (~2–3×); (3) **bloat** при частых UPDATE векторов — нужен periodic VACUUM/REINDEX; (4) выбор `vector_cosine_ops` vs `vector_l2_ops` критичен — несоответствие embedding-модели даст плохой recall; (5) на > 100M векторов начинаются проблемы с памятью и долгие reindex'ы — пора смотреть на Qdrant/Milvus.
>   - **Связанные вопросы:** [[Q8]] HNSW-индекс, [[Q14]] Qdrant как альтернатива, [[Q19]] hybrid search, [[Q20]] metadata filtering.
>
> - [ ] **D) pgvector использует другой PostgreSQL-форк (например, Yugabyte) и несовместим со стандартным PostgreSQL**
>   - **Что на самом деле:** pgvector — extension для **обычного upstream PostgreSQL** (9.6+), устанавливается через `CREATE EXTENSION vector;` на любом стандартном Postgres-инстансе (включая RDS, Cloud SQL, Supabase, Neon).
>   - **Откуда путаница:** есть отдельные vector-форки Postgres (Lantern, Timescale), но pgvector к ним не относится — он работает на обычном Postgres.
>   - **Если бы это было правдой:** managed-сервисы (RDS, Supabase) не поддерживали бы pgvector «из коробки», как сейчас.

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


> [!mcq] Что такое Chroma и для каких сценариев она подходит?
>
> - [ ] **A) Chroma — managed cloud-сервис для production-scale векторного поиска с гарантированным SLA**
>   - **Что на самом деле:** Chroma — **open-source embedded vector DB**, работает в том же процессе, что и приложение (Python-библиотека), без отдельного сервера и без managed cloud-предложения от вендора. Это аналог SQLite, а не Pinecone.
>   - **Откуда путаница:** в 2024–2025 появился Chroma Cloud (early access), и можно решить, что Chroma — это managed-сервис. Но основное использование — embedded и self-host.
>   - **Если бы это было правдой:** документация Chroma не начиналась бы с `pip install chromadb` + `chromadb.Client()` как primary entry point.
>
> - [x] **B) Chroma — open-source embedded vector DB на Python, работает в процессе приложения (как SQLite), оптимальна для прототипов, локальной разработки и маленьких приложений**
>   - **Развёрнутое объяснение:** Chroma спроектирована как **dev-friendly**: `pip install chromadb`, `client = chromadb.Client()`, `collection.add(documents=[...], embeddings=[...])` — и сразу работает. Под капотом — SQLite + DuckDB для хранения, HNSW-индекс в памяти. Нет отдельного сервера (хотя есть server-mode), нет cluster, нет sharding. Это её и сильная, и слабая сторона: zero ops, но и zero production-scale.
>   - **Пример:** RAG-прототип для демо инвесторам: индексируем 50K документов из локальной папки, всё в одном Python-процессе, демо запускается одной командой; после валидации идеи мигрируем на Qdrant/pgvector.
>   - **Когда применять:** прототипы и MVP, локальная разработка перед выбором production-DB, Jupyter-ноутбуки и research, маленькие приложения с < 1M векторов и одним процессом, тестовые fixtures для unit-tests RAG-pipeline.
>   - **Подводные камни:** (1) **не для production scale** — нет sharding, replication, HA; (2) **embedded-режим** означает, что данные живут в одном процессе — рестарт без persistence теряет всё (нужен `PersistentClient`); (3) API менялось между версиями (0.3 → 0.4 → 0.5) — миграции болезненные; (4) метаданные-фильтры работают, но без advanced индексов; (5) производительность падает на > 5M векторах — пора мигрировать.
>   - **Связанные вопросы:** [[Q14]] Qdrant как production-альтернатива, [[Q16]] pgvector для startup-scale, [[Q26]] выбор vector DB.
>
> - [ ] **C) Chroma — distributed vector DB на Rust с поддержкой миллиардов векторов и Kubernetes-deployment**
>   - **Что на самом деле:** Chroma написана на **Python с C++/Rust компонентами для производительности**, но это не distributed-система. Для миллиардов векторов используется Milvus или Pinecone, а Chroma остаётся в нише embedded/local-dev.
>   - **Откуда путаница:** часть кода переписывается на Rust для скорости, что породило мнение о «production-grade Rust DB». Но архитектура остаётся embedded.
>   - **Если бы это было правдой:** Chroma попадала бы в benchmark'и distributed vector DBs наравне с Milvus, а её сравнивают с FAISS/SQLite-vector.
>
> - [ ] **D) Chroma работает только через REST API и не поддерживает Python-клиент**
>   - **Что на самом деле:** primary use case Chroma — именно **Python-клиент в том же процессе** (embedded). Server-mode с REST/gRPC появился позже и используется реже. Это противоположность утверждению.
>   - **Откуда путаница:** некоторые vector DB (Pinecone, Qdrant Cloud) первично API-driven, и можно по аналогии предположить то же про Chroma. Но Chroma родилась как Python-библиотека.
>   - **Если бы это было правдой:** `pip install chromadb` не был бы первой командой в getting-started-гайде Chroma.

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


> [!mcq] Когда стоит использовать Elasticsearch/OpenSearch как vector DB вместо dedicated решения?
>
> - [ ] **A) Elasticsearch не поддерживает vector search — для семантики обязательно нужна отдельная vector DB**
>   - **Что на самом деле:** Elasticsearch 8+ и OpenSearch имеют **нативную поддержку dense_vector** с HNSW-индексом (`"type": "dense_vector", "index": true, "similarity": "cosine"`). Vector search работает «из коробки» через `knn` query.
>   - **Откуда путаница:** до Elasticsearch 7.x vector search действительно был ограниченным (script_score, медленно), что породило репутацию «не для векторов». С 8.x это полностью изменилось.
>   - **Если бы это было правдой:** не было бы официальной документации `dense_vector field type` и продуктовых интеграций с LangChain/LlamaIndex для Elasticsearch.
>
> - [ ] **B) Elasticsearch — embedded-библиотека уровня Chroma и не подходит для production**
>   - **Что на самом деле:** Elasticsearch — **distributed production-grade** поисковый движок с шардированием, репликацией и cluster-mode, рассчитанный на миллиарды документов. Это противоположность embedded-решениям.
>   - **Откуда путаница:** иногда сравнивают «vector-фичи» Elasticsearch с dedicated vector DB по latency, что верно, но не делает Elasticsearch embedded.
>   - **Если бы это было правдой:** не было бы Elastic Cloud и тысяч production-deployments Elasticsearch как primary search-системы.
>
> - [ ] **C) Elasticsearch использует только BM25 и не умеет combine'ить keyword + vector в одном запросе**
>   - **Что на самом деле:** Elasticsearch 8+ поддерживает **hybrid search в одном запросе**: combination BM25 + kNN через `rank` blocks или RRF (Reciprocal Rank Fusion). Это его ключевое преимущество перед чистыми vector DB.
>   - **Откуда путаница:** исторически Elasticsearch был только BM25, и hybrid появился относительно недавно (8.x). У кого-то остался стереотип «keyword-only».
>   - **Если бы это было правдой:** Elasticsearch не позиционировался бы как ведущий выбор для RAG-сценариев, где важна точная лексическая часть.
>
> - [x] **D) Elasticsearch/OpenSearch с 8.x поддерживают `dense_vector` с HNSW и hybrid search (BM25 + kNN через RRF), оптимальны когда уже есть ES в стеке и нужен hybrid поиск в одной системе**
>   - **Развёрнутое объяснение:** Elasticsearch добавил `dense_vector` field type с **HNSW-индексом** (приближённый kNN) и операторы `knn` для search. Главная фича — **hybrid search**: в одном запросе можно делать `match` (BM25 keyword) + `knn` (vector semantic) и комбинировать через **RRF (Reciprocal Rank Fusion)** или weighted scoring. Это даёт лучший recall, чем чистый vector search, для запросов с конкретными терминами (имена, ID, технические аббревиатуры). Под капотом — Lucene HNSW, который медленнее, чем Qdrant/Pinecone (~2–3×), но достаточно быстр для большинства сценариев. Memory-hungry: HNSW-индекс держится в heap.
>   - **Пример:** документация enterprise SaaS с RAG-чатом: пользователь ищет «JWT authentication 401 error» — keyword-часть ловит точный код `401` и термин `JWT`, vector-часть подбирает семантически близкие статьи; RRF комбинирует результаты, top-10 идёт в LLM как context.
>   - **Когда применять:** уже есть Elasticsearch/OpenSearch в инфраструктуре (логи, поиск, аналитика) и не хочется заводить отдельный сервис; нужен hybrid search (BM25 + vector) — это сильная сторона ES; объём до сотен миллионов векторов; команда уже владеет эксплуатацией ES (cluster, shards, ILM).
>   - **Подводные камни:** (1) **HNSW медленнее**, чем в dedicated vector DB на одинаковом железе — для p99 < 10 ms на больших данных Pinecone/Qdrant лучше; (2) **память** — HNSW в heap, нужно правильно настраивать `-Xmx`, иначе OOM; (3) reindex для смены модели embedding — болезненный (нужен full reindex кластера); (4) ANN-параметры (`m`, `ef_construction`, `num_candidates`) надо тюнить — defaults не оптимальны; (5) лицензия Elasticsearch (SSPL/Elastic License) — у OpenSearch Apache 2.0, выбор по lawfully использованию.
>   - **Связанные вопросы:** [[Q8]] HNSW-индекс, [[Q19]] hybrid search, [[Q14]] Qdrant как dedicated-альтернатива, [[Q23]] recall/latency trade-off.

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


> [!mcq] Что такое hybrid search (vector + keyword) и зачем он нужен поверх чистого semantic-поиска?
>
> - [ ] **A) Hybrid search — то же самое, что BM25 keyword search, просто с другим названием**
>   - **Что на самом деле:** Hybrid search — это **комбинация** BM25 (keyword/lexical) и vector (semantic) поиска с fusion-алгоритмом (обычно RRF). Это два разных метода, бегущих параллельно, результаты которых объединяются — не синоним BM25.
>   - **Откуда путаница:** BM25 — самый известный keyword-алгоритм, и термин может звучать «достаточно». Но смысл hybrid именно в добавлении semantic-сигнала к лексическому.
>   - **Если бы это было правдой:** не было бы измеримого улучшения recall'а в RAG-сценариях при переходе с pure BM25 на hybrid.
>
> - [x] **B) Hybrid search — параллельный запуск vector ANN (для семантики) и BM25 keyword search (для точных терминов/имён/ID), с fusion результатов через Reciprocal Rank Fusion (RRF) или weighted score**
>   - **Развёрнутое объяснение:** Чистый vector search хорош для **семантически близких** запросов («что такое stateful service?»), но проваливается на **редких точных терминах** — имена (`AccountServiceImpl`), аббревиатуры (`JWT`, `OAuth2`), коды ошибок (`401`), идентификаторы (`order-12345`). BM25 наоборот ловит точные термины, но не понимает синонимы. **Hybrid combine** делает оба поиска параллельно (top-50 от каждого), затем фьюзит: **RRF** (Reciprocal Rank Fusion: `score = Σ 1/(k + rank_i)`, типично `k=60`) — не требует калибровки скоров между методами; **weighted** — `α·vector + (1−α)·BM25` — требует normalize и tuning. Поддержка: Weaviate, Qdrant (с 1.10+), Elasticsearch/OpenSearch, Pinecone (с 2024). Для RAG-сценариев hybrid даёт +5–15% recall@10 vs pure vector.
>   - **Пример:** документация API: пользователь ищет «`401 JWT expired error`» — BM25 ловит точный код `401` и аббревиатуру `JWT`, vector подбирает семантически похожие статьи про token expiry и refresh; RRF объединяет, top-10 идёт в LLM. Без BM25 чистый vector мог бы пропустить статью про `401`, потому что embedding-модель не сильна в коротких кодах.
>   - **Когда применять:** RAG-системы с технической документацией; code search; legal/compliance (точные формулировки); продукты, где есть имена/SKU/ID в запросах; любой scenario, где «и точно, и по смыслу».
>   - **Подводные камни:** (1) **fusion-параметры** надо tuning'овать на real queries — defaults RRF `k=60` не всегда оптимальны; (2) **latency растёт** (два поиска параллельно + fusion); (3) BM25-индекс надо строить и поддерживать отдельно — двойная инфра; (4) **дубликаты в top-k** — один документ может прийти от обоих, fusion должен корректно их объединить; (5) для не-английского текста BM25 требует proper analyzer/tokenizer (русская морфология).
>   - **Связанные вопросы:** [[Q18]] Elasticsearch hybrid, [[Q13]] Weaviate hybrid API, [[Q14]] Qdrant fusion, [[Q23]] recall/latency.
>
> - [ ] **C) Hybrid search означает индексирование векторов с двумя разными distance metric (cosine + Euclidean) одновременно**
>   - **Что на самом деле:** «Hybrid» в контексте vector DB — это **комбинация vector + keyword (BM25)**, а не комбинация двух distance metrics. Distance metric выбирается один раз под задачу (обычно cosine для нормализованных embeddings).
>   - **Откуда путаница:** слово «hybrid» широкое и в других контекстах может означать что-то ещё. Но в vector DB community это устоявшийся термин для vector+keyword.
>   - **Если бы это было правдой:** конференционные доклады про hybrid search обсуждали бы метрики, а они обсуждают RRF и fusion vector/BM25.
>
> - [ ] **D) Hybrid search работает только в pgvector и не поддерживается dedicated vector DB**
>   - **Что на самом деле:** Hybrid search поддерживается практически во всех современных vector DB: **Weaviate** (native hybrid API), **Qdrant** (с 1.10+), **Elasticsearch/OpenSearch** (RRF в одном запросе), **Pinecone** (с 2024). В pgvector — наоборот, hybrid требует ручной комбинации с tsvector/pg_trgm.
>   - **Откуда путаница:** в Postgres есть и vector (pgvector), и full-text (tsvector), и можно подумать, что это «hybrid-friendly». Но native hybrid API нет — это скорее dedicated vector DB feature.
>   - **Если бы это было правдой:** Weaviate не позиционировал бы hybrid search как одну из своих ключевых фич с 2022 года.

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


> [!mcq] Какой подход к metadata filtering при vector search наиболее надёжен и почему?
>
> - [ ] **A) Pre-filter всегда лучший — сначала отфильтровать всех, потом искать топ-k среди оставшихся**
>   - **Что на самом деле:** Pre-filter (filter → vector search в filtered set) **опасен при aggressive фильтрах**: если после фильтра осталось мало кандидатов, ANN-индекс не сможет дать хороший recall, и качество поиска проваливается. Особенно плохо для HNSW, который рассчитан на работу с большим набором.
>   - **Откуда путаница:** интуитивно «сначала отфильтровать, потом искать» кажется правильным (как в SQL: `WHERE` перед `ORDER BY`). Но vector ANN — не линейный scan, ему нужен достаточный pool кандидатов.
>   - **Если бы это было правдой:** Qdrant и Pinecone не вкладывались бы в **filterable HNSW** (indexed filters) — оптимизированный путь, чтобы избежать этой проблемы.
>
> - [ ] **B) Post-filter — единственно правильный подход: сделать vector search и потом отфильтровать результаты**
>   - **Что на самом деле:** Post-filter (vector search top-k → filter) **опасен**, потому что vector ANN ничего не знает о фильтре и может вернуть top-k, где **ни один** не проходит filter. Тогда после filter остаётся пустота, recall катастрофически падает. Особенно при селективных фильтрах (`tenant_id = X`, где X — редкий tenant).
>   - **Откуда путаница:** post-filter прост в реализации (всё работает на любой DB), и для широких фильтров (`status = 'active'`, где 90% докумов проходят) проблема не видна. Но на узких фильтрах ломается.
>   - **Если бы это было правдой:** не было бы термина «post-filter recall problem» в литературе по vector DB.
>
> - [x] **C) In-search filter (filter during ANN traversal) — оптимальный подход: фильтр проверяется во время обхода HNSW-графа, что сохраняет recall и даёт правильный top-k; требует indexed metadata fields**
>   - **Развёрнутое объяснение:** Все три подхода имеют недостатки: pre-filter ломает recall при aggressive фильтрах, post-filter ломается при селективных фильтрах. **In-search filter** — это компромисс: ANN-обход идёт нормально, но на каждом узле проверяется filter; узлы, не прошедшие filter, не попадают в результаты, но **используются для навигации** по графу. Это требует, чтобы метаданные были **индексированы** в той же структуре (filterable HNSW в Qdrant, payload-индексы), иначе проверка filter на каждой вершине слишком дорогая. Qdrant внедрил это first-class через `payload_indexing`, Pinecone — через namespaces и metadata filters, Weaviate — через inverted index. Это позволяет получить корректный top-k результатов с правильным recall даже при `tenant_id = $rare` или `date BETWEEN ...`.
>   - **Пример:** multi-tenant SaaS с миллионом документов на 10K тенантов: запрос `vector_search WHERE tenant_id='acme'` (≈100 документов из миллиона); pre-filter дал бы 100 кандидатов, среди них ANN не разогнаться, recall падает; post-filter взял бы top-100 из миллиона, среди них ≈0 от 'acme'; in-search filter обходит HNSW целиком, но в результаты добавляет только тех, кто проходит filter — top-10 корректный.
>   - **Когда применять:** всегда, когда vector DB это поддерживает (Qdrant, Pinecone, Weaviate); особенно критично для multi-tenant, time-range filters, ACL/permission-фильтров; для pgvector — pre-filter с обычным B-tree индексом по реляционному полю работает достаточно хорошо до ~10M записей.
>   - **Подводные камни:** (1) **обязательно индексировать filter-fields** — без payload index Qdrant fallback'нется в slow path; (2) **cardinality фильтра** — на низкой селективности (< 1% проходит) даже in-search может быть медленным; (3) **планировщик** vector DB иногда выбирает неоптимальный путь — нужен EXPLAIN/profile для проверки; (4) не все DB одинаково умеют — pgvector не имеет filterable HNSW, post-filter по `WHERE` после `ORDER BY ... <=> ...` работает похуже; (5) сложные фильтры (`OR`, ranges, geo) могут падать в slow scan.
>   - **Связанные вопросы:** [[Q8]] HNSW-индекс, [[Q14]] Qdrant payload indexing, [[Q21]] multi-tenancy filter, [[Q28]] production pitfalls.
>
> - [ ] **D) Metadata filtering не нужен в vector DB — все фильтры надо делать в отдельной реляционной БД после получения IDs**
>   - **Что на самом деле:** Делать filter в отдельной БД после получения top-k от vector search — это **самый дешёвый по реализации post-filter**, и он имеет те же проблемы: vector DB ничего не знает о фильтре и может вернуть top-k, который весь отфильтруется. На селективных фильтрах ломается.
>   - **Откуда путаница:** в архитектуре «vector DB + SQL для метаданных» (которую иногда практикуют для разделения concerns) может казаться, что фильтры — задача SQL. Но это упускает оптимизацию in-search.
>   - **Если бы это было правдой:** Qdrant/Pinecone/Weaviate не имели бы native metadata filtering API — а они есть и являются ключевой production-фичей.

## Q21. Multi-tenancy?

**Multi-tenancy** — изоляция данных tenants в одной installation.

**Подходы:**

1. **Tenant per collection/index** — `index_acme`, `index_beta`. Простое, но scaling issues с тысячами tenants.
2. **Filter by tenant_id** — все в одной collection с metadata `tenant_id`. Compute resources shared.
3. **Native multi-tenancy** (Weaviate, Pinecone Serverless) — built-in isolation.

**Critical:** убедиться, что **не leak'ает** между tenants. Тестируй с unit tests.


> [!mcq] Как корректно реализовать multi-tenancy в vector DB и какие есть подходы?
>
> - [ ] **A) Multi-tenancy не нужна — все vector DB изолируют тенантов автоматически на уровне engine**
>   - **Что на самом деле:** Vector DB **не имеют автоматической tenant-изоляции** — это явная задача архитектора. Один indexes/collection по умолчанию доступен всем, кто имеет credentials, и leak'и между tenants — реальная угроза, которую надо тестировать.
>   - **Откуда путаница:** managed DB (Pinecone) имеют namespaces, которые выглядят как «автоматическая» изоляция. Но и там надо явно указывать namespace в каждом запросе.
>   - **Если бы это было правдой:** не было бы security-инцидентов с leak'ом данных между tenants в RAG-сервисах (а они задокументированы в 2024–2025).
>
> - [ ] **B) Единственный правильный подход — один collection/index на tenant (`index_acme`, `index_beta`)**
>   - **Что на самом деле:** Подход «collection per tenant» работает на малом числе тенантов (< 100), но **scaling issues** на тысячах: каждая collection — отдельный HNSW-граф со своим memory overhead; metadata-операции (list, status) становятся медленными; индексация холодных тенантов жрёт ресурсы. Это один из подходов, но не единственный правильный.
>   - **Откуда путаница:** интуитивно «полная изоляция» через отдельные collection кажется самой надёжной. И для < 100 тенантов это действительно работает. Но для SaaS с 10K+ тенантов — антипаттерн.
>   - **Если бы это было правдой:** Weaviate и Pinecone Serverless не вводили бы **native multi-tenancy** с тысячами логических тенантов в одной collection.
>
> - [x] **D) Есть три подхода: (1) collection per tenant для < 100 тенантов, (2) shared collection + `tenant_id` filter для большинства SaaS-сценариев, (3) native multi-tenancy (Weaviate, Pinecone Serverless) для тысяч тенантов; критично тестировать isolation unit-тестами**
>   - **Развёрнутое объяснение:** Multi-tenancy в vector DB — это **архитектурное решение**, выбор зависит от числа тенантов и требований изоляции. **Подход 1 (collection per tenant):** `index_acme`, `index_beta` — полная физическая изоляция, легко удалить тенанта (drop collection), но не масштабируется > 100–1000 collections. **Подход 2 (shared collection + filter):** все векторы в одной collection с metadata `tenant_id`, каждый запрос **обязан** включать `WHERE tenant_id = ?`; compute и memory shared, scaling до миллионов тенантов, но **leak-риск** если забыть filter в запросе. **Подход 3 (native multi-tenancy):** Weaviate `multiTenancyConfig`, Pinecone Serverless namespaces — built-in isolation с tenant-aware optimizations (per-tenant HNSW shards внутри одной collection), best-of-both-worlds. Любой подход требует **integration-тестов**, проверяющих, что запрос tenant A никогда не возвращает данные tenant B.
>   - **Пример:** B2B SaaS-чат: 50K тенантов, средне 1K документов на тенанта. Подход 1 (50K collections) — не пройдёт. Подход 2 (shared collection + `tenant_id` filter) — работает, но требует строгого code review, чтобы каждый search/upsert содержал tenant_id; в Qdrant — indexed payload `tenant_id` + in-search filter. Подход 3 (Weaviate native multi-tenancy) — каждый тенант = логический shard, активные шарды в RAM, неактивные на диске, изоляция гарантирована engine.
>   - **Когда применять:** оценить число тенантов и SLA на изоляцию; для regulated industries (медицина, финансы) — native multi-tenancy или collection per tenant; для обычного SaaS — shared collection + tenant_id filter; всегда писать тест «tenant A search не возвращает данные tenant B».
>   - **Подводные камни:** (1) **leak через забытый filter** — главный риск shared-подхода, лучше wrap'ить search в helper, который инжектит tenant_id из security context; (2) **noisy-neighbour** — тяжёлый tenant ест ресурсы у других в shared-collection; (3) удаление тенанта (`DELETE WHERE tenant_id`) в HNSW — дорогая операция, иногда требует reindex; (4) **per-tenant rate-limiting** надо делать вручную; (5) backup/restore per-tenant — нетривиально для shared collection.
>   - **Связанные вопросы:** [[Q20]] in-search filter, [[Q22]] replication/sharding, [[Q13]] Weaviate native multi-tenancy, [[Q28]] production pitfalls.
>
> - [ ] **C) Multi-tenancy эквивалентна тому, чтобы каждому пользователю давать свой URL vector DB-сервиса**
>   - **Что на самом деле:** Multi-tenancy — это **архитектурный паттерн внутри одной installation**, не про отдельный URL/сервис на пользователя. Отдельный сервис на тенанта — это single-tenant deployment, что дорого и не масштабируется.
>   - **Откуда путаница:** В B2B иногда дают enterprise-клиентам отдельные deployments (premium tier), но это противоположность multi-tenancy.
>   - **Если бы это было правдой:** SaaS-индустрия не существовала бы экономически — multi-tenancy ровно про shared infrastructure при logical isolation.

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


> [!mcq] В чём разница между replication и sharding в vector DB и когда что нужно?
>
> - [ ] **A) Replication и sharding — синонимы, оба означают одно и то же распределение данных**
>   - **Что на самом деле:** Это **разные паттерны**: replication — **копии** одних и тех же данных на нескольких нодах (для HA и read scaling), sharding — **разделение** данных по нодам (для horizontal scale больших датасетов). Используются обычно вместе, но решают разные задачи.
>   - **Откуда путаница:** оба относятся к «распределённой архитектуре», и в маркетинге их часто упоминают вместе. Но архитектурный смысл различен.
>   - **Если бы это было правдой:** не существовало бы отдельных параметров `replication_factor` и `shard_count` в конфигурации Weaviate/Qdrant/Milvus.
>
> - [ ] **B) Replication нужен только для production, а sharding бесполезен в vector DB**
>   - **Что на самом деле:** Sharding **критичен** для vector DB при > 100M векторов: один node не выдержит HNSW-индекс в RAM, нужно распределение по shards. Это одна из главных причин выбора Milvus/Pinecone для больших датасетов.
>   - **Откуда путаница:** для маленьких датасетов (< 10M) sharding действительно не нужен, и можно решить, что он «бесполезен в принципе». Но для scale это must-have.
>   - **Если бы это было правдой:** Milvus не позиционировался бы как «распределённая vector DB для миллиардов векторов» с фокусом на sharding.
>
> - [ ] **C) Sharding в vector DB всегда выполняется автоматически и не требует настройки**
>   - **Что на самом деле:** Sharding **требует явной конфигурации** — выбор shard count, sharding key (random/hash/range), strategy для resharding. Только managed-сервисы (Pinecone) делают это «прозрачно», но и там есть параметры (pods, replicas).
>   - **Откуда путаница:** в managed cloud действительно sharding выглядит автоматическим, и можно перенести это впечатление на self-host. Но в Qdrant/Weaviate/Milvus self-hosted всё настраивается явно.
>   - **Если бы это было правдой:** не было бы документации «sharding configuration» в self-hosted vector DB и инцидентов из-за плохо выбранного shard key.
>
> - [x] **D) Replication — копии данных для HA и read scaling; sharding — разделение данных между нодами для horizontal scale больших датасетов; для большинства production cases нужна replication, sharding — только при > 100M векторов**
>   - **Развёрнутое объяснение:** **Replication** делает N копий одних и тех же векторов на N нодах: чтения масштабируются (load balancer распределяет запросы между репликами), при падении ноды другие принимают трафик. Параметр — `replication_factor`. Сильная (synchronous) репликация даёт consistency но снижает throughput; eventual (async) — наоборот. **Sharding** разделяет векторы между N нодами по shard key (обычно random или hash от ID): каждый node хранит свою часть HNSW-индекса, поиск идёт на **все shards параллельно** (scatter-gather), результаты merge'ятся в координаторе. Параметр — `shard_count`. Sharding нужен когда индекс не помещается в RAM одной ноды (HNSW требует ~1.5× от размера raw vectors). Поддержка: **Pinecone** — оба (managed), **Weaviate/Qdrant/Milvus** — оба (self-hosted с явной конфигурацией), **pgvector** — replication через PostgreSQL streaming replication, sharding вручную через Citus или application-level.
>   - **Пример:** RAG-сервис на 50M документов с 1K QPS: replication_factor=3 (HA + 3× read throughput), shard_count=1 (50M × 1536 × 4B = 300 GB raw + ~450 GB с HNSW — помещается в 512 GB instance), всего 3 ноды. Если бы 500M — потребовался бы shard_count=4 и 12 нод (3 replicas × 4 shards).
>   - **Когда применять:** **replication** — почти всегда в production (минимум RF=2 для HA), плюс масштабирование чтений; **sharding** — когда индекс не влезает в одну ноду (> 100M векторов на embedding-1024 или > 50M на embedding-3072) или когда нужен parallel-search для снижения latency на огромных датасетах.
>   - **Подводные камни:** (1) **replica lag** — async-replication может вернуть устаревшие результаты; (2) **shard skew** — плохой shard key даёт неравномерную загрузку нод; (3) **scatter-gather latency** — на K shards latency = max(latency_i), плюс merge overhead; (4) **resharding** при росте — дорогая операция, требует копирования и reindex; (5) cross-shard ANN-поиск не даёт точно top-k в общем случае — top-k на каждом shard потом merged, что может терять recall.
>   - **Связанные вопросы:** [[Q15]] Milvus distributed, [[Q14]] Qdrant cluster, [[Q12]] Pinecone managed, [[Q25]] scales.

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


> [!mcq] Что определяет trade-off между recall и latency в ANN-поиске (HNSW) и как его настраивать?
>
> - [ ] **A) Trade-off отсутствует — современные HNSW дают 100% recall с константной latency на любых данных**
>   - **Что на самом деле:** HNSW — это **approximate** nearest neighbor, и trade-off recall/latency встроен в его дизайн. Параметр `efSearch` (или `ef`) напрямую регулирует: больше `ef` → больше посещённых узлов графа → лучше recall, выше latency. 100% recall достигается только при `ef → N` (что равно brute-force).
>   - **Откуда путаница:** маркетинг vector DB обещает «sub-millisecond latency at high recall», что верно для конкретных параметров и данных. Но trade-off никуда не исчезает.
>   - **Если бы это было правдой:** не было бы benchmark'ов ANN-benchmarks.com, где recall@10 vs QPS показывает чёткий Pareto-frontier.
>
> - [x] **B) Recall/latency регулируется параметром `efSearch` в HNSW: больше `ef` → больше посещённых узлов графа → выше recall, выше latency; для RAG обычно 95%+ recall достаточно, что соответствует efSearch=50–100**
>   - **Развёрнутое объяснение:** HNSW при поиске выполняет **best-first traversal** графа с поддержкой priority queue размера `efSearch`: на каждой итерации берётся ближайший непосещённый узел, обновляются top-k кандидатов. Чем больше `ef`, тем больший «фронт» исследуется, тем выше шанс найти истинных ближайших — но и больше distance computations. Типичные точки на Pareto-кривой: `ef=10` → recall≈0.85, latency~2 ms; `ef=50` → recall≈0.95, latency~8 ms; `ef=200` → recall≈0.99, latency~30 ms. Дополнительно влияют **build-time параметры**: `M` (число связей на узел, обычно 16–32) и `efConstruction` (качество индекса, обычно 200). Для RAG-сценариев 95% recall обычно избыточно — LLM сам устойчив к шуму в context, поэтому `ef=50` даёт хороший компромисс. Для precision-critical (legal, medical) лучше `ef=200+` и rerank.
>   - **Пример:** RAG-чат с p95 latency budget 50 ms (LLM-call займёт ещё 1–2 секунды): начать с `ef=50`, измерить recall@10 на golden set (помеченных вручную правильных answers); если recall < 90% — увеличить до 100; если latency > 20 ms — попробовать quantization вместо роста `ef`. A/B тест на real queries: следить за LLM answer quality и user satisfaction, а не только за raw recall — иногда 90% recall даёт такой же UX, как 99%.
>   - **Когда применять:** **всегда** при выборе vector DB определить latency budget и acceptable recall; настраивать `ef` per workload (search vs upsert), а не один глобальный; для batch-задач (offline reindex) — высокий `ef`, для interactive — низкий; для GPU-индексов параметры другие (см. cuVS).
>   - **Подводные камни:** (1) **`efSearch` < `k`** не работает — должно быть `ef >= k` минимум; (2) recall измеряется относительно **ground truth** (brute-force) — без него все benchmark'и обман; (3) `efConstruction` влияет на качество индекса (выше — лучше recall при том же `ef`, но дольше build); (4) latency не линейна — `ef×2` редко даёт latency×2, скорее ×1.5; (5) recall падает на out-of-distribution queries (запросы не похожи на distribution индекса) — нужен monitoring recall в production.
>   - **Связанные вопросы:** [[Q8]] HNSW параметры, [[Q9]] HNSW vs IVF (другой trade-off), [[Q10]] PQ quantization (другой rycaż), [[Q24]] quantization для cost.
>
> - [ ] **C) Trade-off регулируется только распределением данных — параметры алгоритма не имеют значения**
>   - **Что на самом деле:** Параметры алгоритма (`efSearch`, `M`, `efConstruction`) **критически** влияют на trade-off. Распределение данных тоже важно (skewed embeddings дают плохой ANN-результат), но это не отменяет роль параметров.
>   - **Откуда путаница:** иногда обвиняют «плохие embeddings» в проблемах с recall, но даже на идеальных embeddings без tuning `ef` будут проблемы.
>   - **Если бы это было правдой:** документация Qdrant/Pinecone не имела бы детальных гайдов по tuning ANN-параметров.
>
> - [ ] **D) Recall/latency регулируется только размером embedding (768 vs 1536 vs 3072 dims)**
>   - **Что на самом деле:** Размер embedding влияет на **качество семантического представления** (большие embeddings обычно лучше), но это **отдельная ось** от ANN-trade-off. Внутри одного embedding-размера `ef` регулирует recall/latency.
>   - **Откуда путаница:** интуитивно «больше dims = лучше», и можно решить, что это главный регулятор. Но dims задают потолок recall, а `ef` — реальный достигаемый recall в search.
>   - **Если бы это было правдой:** все vector DB поставлялись бы с фиксированным `ef` без возможности tuning, что не так.

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


> [!mcq] Что такое quantization векторов и какие есть варианты для production?
>
> - [ ] **A) Quantization — это zip-сжатие векторов, lossless по природе**
>   - **Что на самом деле:** Quantization — **lossy** compression (с потерей точности): значения мапятся в меньшее число бит (fp32 → fp16/int8/binary). Lossless-сжатие на float-векторах работает плохо (entropy уже высокая) и не даёт нужного gain.
>   - **Откуда путаница:** «сжатие» интуитивно ассоциируется с zip. Но в ML quantization — это про reduction of precision, всегда с trade-off на quality.
>   - **Если бы это было правдой:** quantization не оказывал бы влияния на recall — а влияет, и эта потеря — главная характеристика выбора метода.
>
> - [ ] **B) Quantization применима только к embeddings, не к индексу — индекс остаётся в полной точности**
>   - **Что на самом деле:** Quantization применяется и к **raw vectors**, и к **индексу** — современные методы (PQ, OPQ) сжимают данные внутри HNSW/IVF-структур. Можно сжимать только indexed vectors при сохранении originals для rerank stage.
>   - **Откуда путаница:** в некоторых конфигурациях действительно хранят quantized для search + originals для rerank. Но «индекс остаётся в полной точности» — неверно.
>   - **Если бы это было правдой:** не было бы методов вроде IVF_PQ (PQ внутри IVF-индекса), которые именно сжимают индексные структуры.
>
> - [x] **C) Quantization — сжатие векторов с потерей точности: fp32→fp16 (2×, минимальный impact на recall), fp32→int8 (4×, малый impact), binary 1-bit/dim (32×, заметный impact, используется как cheap first stage + rerank), Product Quantization (4–32×, параметрическая); в 2025 standard для миллионов+ векторов**
>   - **Развёрнутое объяснение:** Quantization снижает **storage и memory** (и часто **latency**), ценой некоторого падения recall. Варианты от мягких к агрессивным: **fp16** (half-precision) — экономия 2×, recall практически не страдает, обычно безопасный default. **int8** (scalar quantization) — экономия 4×, нужна калибровка scale/zero-point per dimension, recall падает на 1–3%. **Binary quantization** — каждая координата заменяется на 0/1 (`sign(x)`), экономия 32×, Hamming distance вместо cosine; recall падает заметно, поэтому используется как **cheap first stage** (быстро отфильтровать top-N=1000), затем **rerank** топ-N через full precision. **Product Quantization (PQ)** — разделяет вектор на подвектора, каждый кодируется через k-means (256 центроидов = 8 бит); экономия 4–32×, recall зависит от параметров (m=число подвекторов, nbits). Поддержка: Qdrant (binary/scalar/PQ), Pinecone (scalar/PQ), Milvus (полный набор включая GPU). Главная мотивация — **cost**: для 1B векторов × 1536 × 4B = 6 TB; с int8 → 1.5 TB; с PQ → 200 GB; разница в стоимости hardware кратная.
>   - **Пример:** RAG для enterprise documents, 100M векторов: fp32 raw = 600 GB, не помещается в одну ноду. Применяем int8 quantization → 150 GB, помещается в 256 GB instance с overhead на HNSW. Дополнительно binary quantization для first-stage search (на retrieval top-200), затем rerank через int8 raw — даёт latency p99 < 20 ms при recall@10 > 92%.
>   - **Когда применять:** датасет > 10M векторов (на меньшем не окупается сложность); cost-sensitive (memory/disk — главная статья); готовы потерять 1–5% recall ради 4–10× экономии; есть golden set для измерения recall before/after. Для < 10M — оставайтесь на fp32, не усложняйте.
>   - **Подводные камни:** (1) **calibration**: int8/PQ требуют sample данных для калибровки — на out-of-distribution данных recall падает сильнее; (2) **двойное хранение**: для rerank-pipeline нужно хранить и quantized, и original — экономия меньше теоретической; (3) **тестирование** на realистичных queries — synthetic benchmarks могут не показать regression; (4) **embedding-обновление** требует re-quantization всего индекса; (5) binary quantization работает только на нормализованных embeddings и теряет много recall без rerank stage.
>   - **Связанные вопросы:** [[Q10]] Product Quantization детально, [[Q23]] recall/latency trade-off, [[Q14]] Qdrant quantization config, [[Q25]] storage estimation.
>
> - [ ] **D) Quantization применяется только в FAISS и не поддерживается production vector DB**
>   - **Что на самом деле:** Quantization поддерживается **всеми major production vector DB**: Qdrant (binary/scalar/PQ), Pinecone (PQ), Milvus (полный набор), Weaviate (PQ, binary). FAISS — это библиотека, на которой многие из них построены, но quantization не ограничена ей.
>   - **Откуда путаница:** FAISS — pioneer в этой области, и literature много ссылается на FAISS examples. Но коммерческие DB давно интегрировали аналогичные методы.
>   - **Если бы это было правдой:** Qdrant Cloud не предлагал бы quantization-конфигурации в UI и Pinecone не имел бы p1/s1 pod types с PQ.

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


> [!mcq] Какие реалистичные масштабы датасетов в production и как оценить storage?
>
> - [x] **A) Типичные scales: demo 1K–100K (любая DB), small product 100K–10M (pgvector, Qdrant), mid 10M–100M (Pinecone, Weaviate, Qdrant), large 100M–1B (Milvus, Pinecone enterprise), hyper-scale 1B+ (custom/Milvus); storage = N × dims × bytes_per_dim, плюс ~1.5× HNSW overhead**
>   - **Развёрнутое объяснение:** Понимание scale критично для выбора DB и архитектуры. **Demo/prototype** (1K–100K) — любая DB, даже Chroma в одном процессе; **small product** (100K–10M) — pgvector если уже есть Postgres, иначе Qdrant Cloud начального тира; **mid-size** (10M–100M) — Pinecone/Weaviate/Qdrant с serious config; **large** (100M–1B) — Milvus distributed или Pinecone enterprise; **hyper-scale** (1B+) — FAISS-based custom build или Milvus с sharding. **Storage formula:** `N × dims × bytes_per_dim`. Для 10M × 1024 × 4 (fp32) = 40 GB raw. Плюс HNSW overhead ≈ 1.5× (graph edges) → ~60 GB total. С int8 quantization → 15 GB. С PQ → 5 GB. **Memory** обычно ≥ storage (HNSW любит RAM). Plus metadata, indexes на payload, replicas — реальный footprint ×2–3 от raw.
>   - **Пример:** RAG-сервис на 50M документов из enterprise wiki, embedding-3-large (3072 dims, fp32): raw = 50M × 3072 × 4 = ~600 GB. С HNSW → ~900 GB. Не помещается в обычные instances. Решения: (1) меньше dims — embedding-3-small (1536 dims) → 300 GB raw, 450 GB с индексом, помещается в 512 GB instance; (2) quantization int8 → 75 GB raw; (3) sharding на 4 ноды по 250 GB.
>   - **Когда применять:** при дизайне system — посчитать storage estimation в первый же день, чтобы выбрать DB и instance size; при планировании cost (особенно managed — Pinecone берёт деньги за pod-hours и storage); при оценке latency budget (большие индексы — медленнее).
>   - **Подводные камни:** (1) **dim choice** — `embedding-3-large` (3072) часто overkill, `text-embedding-3-small` (1536) или `bge-small` (384) дают тот же recall за меньшие деньги; (2) **HNSW overhead** на маленьких векторах непропорционально большой — для 384 dims HNSW edges почти равны самим векторам; (3) **payload** (metadata) добавляет storage — JSON-документы могут весить больше vectors; (4) **replicas** — RF=3 утраивает storage; (5) **growth** — планировать на 2× от текущего.
>   - **Связанные вопросы:** [[Q22]] sharding, [[Q24]] quantization, [[Q26]] выбор DB, [[Q16]] pgvector до 10M.
>
> - [ ] **B) Все production-системы работают с миллиардами векторов — меньшие масштабы не существуют**
>   - **Что на самом деле:** Подавляющее большинство production RAG-систем — это **10K–100M векторов**. Миллиарды — редкое исключение (Spotify, Pinterest, large enterprises). Для startup-ов 100K–10M — типичный диапазон.
>   - **Откуда путаница:** маркетинг vector DB рекламирует «billions of vectors», что верно как технический предел, но не как медианный use case.
>   - **Если бы это было правдой:** не было бы стартап-сегмента с pgvector + Postgres как достаточным решением.
>
> - [ ] **C) Storage можно не учитывать — vector DB сжимают данные автоматически до 1% от raw**
>   - **Что на самом деле:** Без явной quantization vector DB **не сжимают** данные — fp32 хранится как fp32. Quantization есть как опция, но требует включения и tuning'а. Plus HNSW overhead обычно увеличивает storage, не уменьшает.
>   - **Откуда путаница:** некоторые managed-сервисы (Pinecone serverless) применяют compression «под капотом», что создаёт иллюзию «автоматического сжатия». Но это конкретные tiers, не правило.
>   - **Если бы это было правдой:** Pinecone не имел бы «storage cost» как отдельную статью billing'а — а имеет.
>
> - [ ] **D) Размер embedding не влияет на storage — все embeddings весят одинаково**
>   - **Что на самом деле:** Storage **линейно** зависит от dims. embedding с 3072 dims весит ровно 2× от 1536-dim и 8× от 384-dim. Это одна из главных переменных при оценке cost.
>   - **Откуда путаница:** «embedding это число» может звучать абстрактно. Но физически — это N float-чисел, и N разный для разных моделей (384/768/1024/1536/3072).
>   - **Если бы это было правдой:** не было бы рекомендации Matryoshka embeddings («хочешь меньше storage — отрежь dims»), а она есть в OpenAI text-embedding-3.

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


> [!mcq] Как выбрать vector DB под конкретный сценарий и какие основные decision-факторы?
>
> - [ ] **A) Всегда выбирать Pinecone — это безопасный default для любого проекта**
>   - **Что на самом деле:** Pinecone отличный managed-сервис, но **не оптимален везде**: для startup с уже имеющимся Postgres pgvector проще и дешевле; для self-host enterprise — Qdrant/Weaviate/Milvus; для prototype — Chroma. Выбор зависит от scale, ops-capacity, бюджета и existing stack.
>   - **Откуда путаница:** Pinecone имеет сильный маркетинг и часто упоминается в туториалах как «standard». Но это не делает его универсальным выбором.
>   - **Если бы это было правдой:** не было бы рынка open-source vector DB и стартапов на pgvector — все бы выбирали Pinecone.
>
> - [ ] **B) Выбор vector DB не имеет значения — все они эквивалентны по производительности и фичам**
>   - **Что на самом деле:** vector DB сильно различаются: Pinecone (managed, простой), Qdrant (modern, fast Rust), Weaviate (feature-rich, hybrid), Milvus (distributed K8s), pgvector (Postgres extension), Chroma (embedded). Выбор влияет на cost, latency, ops, feature-set.
>   - **Откуда путаница:** для маленьких prototype действительно «любая работает», что переносится на production. Но в production различия становятся критичными.
>   - **Если бы это было правдой:** все vector DB сходились бы по фичам и benchmarks, чего нет.
>
> - [ ] **C) Решение принимается только на основе benchmarks — никакие другие факторы не важны**
>   - **Что на самом деле:** Benchmarks важны, но **не единственный фактор**: важны ops-стоимость (managed vs self-host), existing stack (есть ли Postgres/ES), feature-fit (hybrid search, multi-tenancy), цена, lock-in. Часто «более медленная» DB выигрывает по совокупности факторов.
>   - **Откуда путаница:** ann-benchmarks.com и vendor whitepapers фокусируются на latency/recall, что создаёт впечатление «benchmark = выбор». Но team velocity и cost тоже критичны.
>   - **Если бы это было правдой:** ВСЕ выбирали бы FAISS как фундамент (она быстрее всего в benchmarks), но это не происходит из-за ops-сложности.
>
> - [x] **D) Decision tree по факторам: < 1M векторов и есть Postgres → pgvector; open-source self-host → Qdrant (fast) или Weaviate (feature-rich); managed без ops → Pinecone; уже есть Elasticsearch → ES vector + hybrid; > 1B vectors → Milvus или custom; prototype/local → Chroma**
>   - **Развёрнутое объяснение:** Выбор vector DB — это **multi-factor decision**, не «лучший benchmark wins». Ключевые оси: (1) **Scale** — pgvector до 10M, dedicated до 100M, Milvus/Pinecone до миллиардов; (2) **Ops capacity** — managed (Pinecone, Qdrant Cloud, Weaviate Cloud) vs self-host (Qdrant/Weaviate/Milvus в K8s) vs embedded (Chroma) vs piggyback (pgvector на существующем Postgres); (3) **Existing stack** — если уже Postgres → pgvector, если уже Elasticsearch → ES vector (бесплатно при существующем кластере); (4) **Features** — hybrid search (Weaviate, ES native), multi-tenancy (Weaviate, Pinecone Serverless), GPU acceleration (Milvus), GraphQL API (Weaviate); (5) **Budget** — pgvector дёшево (на железе Postgres'а), Pinecone дорого на pod-hours, self-host Qdrant — баланс; (6) **Lock-in** — Pinecone тесно завязан на свой API, открытые DB переносимы. Decision tree выше — стартовый rule-of-thumb, требует уточнения per project.
>   - **Пример:** Стартап делает RAG-чат для B2B-SaaS, ~5M документов, есть Postgres, маленькая команда без K8s-экспертизы, бюджет $1K/мес. Анализ: scale в зоне pgvector, ops-capacity низкая, existing stack — Postgres, features — нужен tenant-filter (поддержан pgvector + B-tree index). Выбор: **pgvector**. Полгода спустя датасет вырос до 30M, latency p99 = 200 ms (вместо 50 ms), команда выросла — миграция на Qdrant Cloud.
>   - **Когда применять:** при выборе DB — пройти все 6 осей честно; не повторять выбор «как у больших» (Pinterest использует Milvus — это не значит, что вам он подходит); document trade-offs в ADR для будущей миграции.
>   - **Подводные камни:** (1) **переоценка scale** — закладывать на 1B векторов, когда реально будет 5M = overkill и trato убивает team velocity; (2) **недооценка ops** — self-host Milvus в K8s требует full-time DevOps; (3) **lock-in** через API/payload format — учитывать exit cost; (4) **roadmap** — какие фичи нужны через год (multi-tenancy, hybrid)? (5) **benchmarks обманывают** — на ваших данных и query distribution результаты другие.
>   - **Связанные вопросы:** [[Q12]] Pinecone, [[Q13]] Weaviate, [[Q14]] Qdrant, [[Q15]] Milvus, [[Q16]] pgvector, [[Q17]] Chroma, [[Q25]] scales.

## Q27. Backup, restore, migrations?

**Vector DB не имеет** standard backup/restore tools (как Postgres).

**Подходы:**
1. **Re-embed everything** — если есть source documents, дешевле просто пересоздать (2-3 часа на M vectors)
2. **Export embeddings** — dump всех vectors в файл, restore
3. **Snapshot** (Pinecone, Qdrant) — точка во времени
4. **Replication-based** — реплика в другом регионе

**Migrations между DBs:** обычно через source documents (re-embed). Прямого export/import между разными DBs нет.


> [!mcq] Как делать backup, restore и migrations в vector DB и в чём отличие от обычных БД?
>
> - [ ] **A) Vector DB имеют стандартные backup/restore tools уровня pg_dump/pg_restore, миграции между разными DB — стандартный процесс**
>   - **Что на самом деле:** Vector DB **не имеют унифицированных** backup/restore tools уровня PostgreSQL. У каждой свой формат снапшотов (Qdrant `snapshot`, Pinecone `collection backup`, Milvus `BulkInsert`), несовместимый с другими. Миграции между разными DB — обычно через re-embed source documents, прямой export/import не работает.
>   - **Откуда путаница:** опыт работы с реляционными БД создаёт ожидание pg_dump-эквивалента. Но vector DB — молодая категория, стандарты ещё не сложились.
>   - **Если бы это было правдой:** не было бы commercial-tools (langchain migrations, custom scripts) для миграции между vector DB.
>
> - [ ] **B) Backup делается через копирование файлов на disk — это всегда работает**
>   - **Что на самом деле:** Простое копирование файлов работает только при **остановленной DB** (cold backup), что неприемлемо в production. Hot backup требует consistent snapshot, который DB должна поддерживать (snapshot API).
>   - **Откуда путаница:** для embedded DB (Chroma, SQLite-based) копирование файла действительно работает после flush. Но это не правило.
>   - **Если бы это было правдой:** не было бы инцидентов «backup восстановился с corrupted index» при попытке копировать живые файлы.
>
> - [x] **C) Подходы: (1) re-embed everything из source documents (часто дешевле, чем восстанавливать снапшоты), (2) export embeddings в файл и restore, (3) snapshot API (Pinecone, Qdrant, Milvus), (4) replication-based DR; миграции между DB обычно через source documents, прямой export/import редко работает из-за разных форматов**
>   - **Развёрнутое объяснение:** В отличие от RDBMS, embeddings — это **детерминированная функция** от source documents (при фиксированной модели). Это даёт уникальный подход: **re-embedding** часто дешевле, чем backup/restore. Например, восстановить 10M документов через re-embedding занимает 2–3 часа (OpenAI API) и стоит ~$50, тогда как держать backup и инфраструктуру для restore — дороже. **Snapshot-подходы**: Pinecone имеет `pinecone-client.create_collection_backup`, Qdrant — `POST /collections/{name}/snapshots`, Milvus — `BulkInsert` с S3-snapshot. Эти снапшоты несовместимы между DB. **Export/import** между разными DB — обычно через intermediate format (JSONL с `{id, vector, metadata}`), но требует вручную написать конвертер. **Replication-based DR**: Pinecone multi-region replicas, Qdrant cluster replication, Weaviate replication — позволяют failover без классического restore. **Production strategy**: храните source documents в S3/Postgres как ground truth, vector DB — derived state, который можно пересоздать.
>   - **Пример:** RAG-сервис, 20M документов в vector DB. Стратегия backup: (1) source documents в S3 (immutable, lifecycle policy 90 days); (2) Qdrant nightly snapshot на S3 (для быстрого restore без re-embed); (3) replica в другой availability zone. При disaster: сначала пробуем restore из snapshot (1 час), если не работает — re-embed из S3 (3 часа, $50). RTO 4 часа, RPO 24 часа — устраивает SLA.
>   - **Когда применять:** для маленьких прод-систем (< 10M) — re-embed подход, не тратить на сложную backup-инфру; для больших — snapshot + replica; всегда хранить source documents отдельно как «ground truth»; писать playbook restore с конкретными командами; тестировать restore раз в квартал.
>   - **Подводные камни:** (1) **embedding model versioning** — если model обновилась, re-embed даст другие vectors, что ломает existing queries; (2) **stale source documents** — если есть deletes, source-bucket должен синхронизироваться; (3) **PII в embeddings** — даже vectors могут быть PII (embedding inversion attacks), backup нужно шифровать; (4) **partial restore** — для multi-tenant восстановить одного tenant без аффекта на других сложно; (5) **incremental backup** vs full — incremental в vector DB обычно не поддерживается, каждый snapshot — full.
>   - **Связанные вопросы:** [[Q21]] multi-tenancy backup, [[Q22]] replication для DR, [[Q28]] production pitfalls, [[Q26]] выбор DB.
>
> - [ ] **D) Backup в vector DB вообще не нужен — embeddings можно всегда пересоздать через API**
>   - **Что на самом деле:** Re-embedding **возможен**, но имеет ограничения: занимает время (часы), стоит денег (API calls), требует stable embedding model (если model меняется — vectors будут другими), требует доступа к source documents. Так что backup нужен — просто стратегия другая, чем для RDBMS.
>   - **Откуда путаница:** философия «embeddings — derived state» иногда трактуется как «backup не нужен». Но source documents — это сами backup, и их restore требует процесса.
>   - **Если бы это было правдой:** Pinecone и Qdrant не реализовывали бы snapshot API — а реализовали, потому что иногда быстрее восстановить.

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


> [!mcq] Какой подводный камень в production vector DB чаще всего вызывает silent regression качества поиска и как его избежать?
>
> - [ ] **A) Самый частый подводный камень — недостаточная сетевая bandwidth между приложением и vector DB**
>   - **Что на самом деле:** Network bandwidth — **редкая** проблема (typical vector ~6 KB для 1536 dims fp32, latency search ~10 ms). Реальные «silent regression» приходят от **embedding model mismatch**, **wrong distance metric** и **stale embeddings**, которые ломают качество без явных ошибок.
>   - **Откуда путаница:** для cross-region setups bandwidth действительно может быть проблемой, но это видимая (timeouts), а не silent regression.
>   - **Если бы это было правдой:** main troubleshooting гайды vector DB фокусировались бы на network, а они — на embedding consistency и distance metric.
>
> - [ ] **B) Главный pitfall — недостаточный CPU; всё остальное вторично**
>   - **Что на самом деле:** CPU редко bottleneck в vector DB (HNSW — memory-bound, не CPU-bound). Главные риски — **memory pressure** (OOM на больших HNSW в RAM), **wrong distance metric**, **embedding model drift** при обновлении модели, **multi-tenancy leaks**.
>   - **Откуда путаница:** в общих БД CPU часто bottleneck. Но vector DB — другой профиль: memory + I/O.
>   - **Если бы это было правдой:** sizing guides vector DB начинались бы с CPU, а они начинаются с RAM и количества векторов.
>
> - [ ] **C) Все pitfalls легко детектируются monitoring'ом и не требуют отдельного внимания**
>   - **Что на самом деле:** Большинство pitfalls — **silent**: wrong distance metric не вызывает alert (всё «работает»), просто результаты плохие; embedding model drift не виден через CPU/memory metrics — только через recall на golden set; multi-tenancy leak не покажется в логах, если код «корректно» возвращает (чужие) данные.
>   - **Откуда путаника:** мониторинг (latency, error rate, memory) хорош для infrastructure-проблем. Но quality regressions — другой класс, требуют **recall-monitoring** и **shadow queries**.
>   - **Если бы это было правдой:** не было бы инцидентов с deployed RAG-системами, которые «работают» (нет 5xx), но возвращают мусор.
>
> - [x] **D) Главные silent pitfalls: (1) wrong distance metric (cosine vs Euclidean) — embeddings нормализованы, а поиск Euclidean даёт плохой результат без error; (2) embedding model mismatch — query embedded одной моделью, docs другой; (3) stale embeddings после обновления модели; (4) filtering после поиска убивает recall; (5) multi-tenancy leak; (6) memory pressure OOM при росте индекса; защита — golden set с regression-тестами, recall monitoring, версионирование embeddings**
>   - **Развёрнутое объяснение:** Production-pitfalls vector DB делятся на **silent quality regressions** и **operational failures**. Silent regressions опаснее, т.к. система работает (нет 5xx), но качество поиска проваливается. Топ-10: (1) **wrong distance metric** — `cosine` vs `euclidean` vs `inner_product` дают разные результаты, embedding-модели обычно требуют конкретную (OpenAI рекомендует cosine для normalized embeddings); (2) **embedding model mismatch** — индексировали `text-embedding-ada-002`, query embedded `text-embedding-3-small` → катастрофа; (3) **stale embeddings** — модель обновилась, индекс не пересоздали; (4) **cost runaway** — Pinecone Serverless берёт деньги за каждый search, наивное использование = большие счета; (5) **index не используется** — `EXPLAIN` показывает full scan (например, pgvector без HNSW индекса); (6) **post-filter** — фильтр после поиска убивает recall; (7) **multi-tenancy leak**; (8) **re-indexing painful** — full reindex на обновлении модели; (9) **memory pressure** — HNSW в RAM, OOM при росте датасета; (10) **slow updates** блокируют поиски. **Защита**: golden set из 100–500 (query, expected_result) пар с автоматическим recall-тестом в CI; recall monitoring на real queries в production (sample 1% и проверять); embedding model version в metadata каждого вектора; integration-тесты на tenant isolation; capacity planning на 2× от текущего размера.
>   - **Пример:** RAG-чат: после обновления embedding-модели с ada-002 на text-embedding-3-small качество упало, но никаких alerts — latency и error rate в норме. Discovered через golden set CI test: recall@10 упал с 0.92 до 0.55. Root cause: docs реиндексировали, но query-embedder ещё использовал старую модель (rolling deploy). Fix: добавлен embedding model version в metadata + проверка version match при search.
>   - **Когда применять:** в day-one design vector DB сервиса заложить мониторинг качества (golden set), versioning embeddings, и runbook для основных pitfalls; перед production launch — load test с realistic queries; периодически (раз в спринт) запускать recall regression test.
>   - **Подводные камни:** (1) **golden set degradation** — со временем golden queries не отражают real distribution; (2) **monitoring overhead** — recall measurement дорогой, делать sample, не on every query; (3) **alert fatigue** — слишком строгие пороги recall дают false positives; (4) **embedding model deprecation** — провайдеры обновляют/удаляют модели, нужна стратегия миграции; (5) **shadow traffic** для тестирования — ещё одна инфра-задача.
>   - **Связанные вопросы:** [[Q20]] filtering pitfalls, [[Q21]] multi-tenancy isolation, [[Q23]] recall/latency, [[Q27]] re-embedding cost, [[Q22]] capacity planning.

---

## See also

- [Embeddings](embeddings-interview.md) — что хранится в vector DB
- [RAG](rag-interview.md) — главное применение
- [LLM Basics](llm-basics-interview.md) — context для AI
- [PostgreSQL](../databases/postgresql-interview.md) — pgvector
- [Elasticsearch](../databases/elasticsearch-interview.md) — hybrid search
- [Redis](../databases/redis-interview.md) — Redis Stack vector search
- [Caching](../architecture/caching-strategies-interview.md) — embeddings cache
- [Микросервисы](../architecture/microservices-interview.md) — где vector DB живёт
- [Распределённые системы](../architecture/distributed-systems-interview.md) — sharding, replication
- [MLOps](mlops-interview.md) — embedding model versioning
- [Scalability Patterns](../architecture/scalability-patterns-interview.md) — для больших scales
- [AI Agents](ai-agents-interview.md) — vector DB как memory для агентов
- [LLM Integration Patterns](llm-integration-patterns-interview.md) — паттерны интеграции
- [Model Serving](model-serving-interview.md) — serving embedding-моделей
