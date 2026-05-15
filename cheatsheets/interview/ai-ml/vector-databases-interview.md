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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q11. ScaNN, FAISS, Annoy? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

| Library | Создатель | Особенности |
|---------|-----------|-------------|
| **FAISS** | Meta | Самая популярная C++ library. IVF, HNSW, PQ. Не managed DB. |
| **ScaNN** | Google | Очень быстрый. Используется в YouTube, Search. |
| **Annoy** | Spotify | Tree-based (не graph). Простой, но хуже HNSW. |
| **HNSWlib** | — | Lightweight HNSW C++ |

**FAISS** часто используется как **embedded** library внутри custom apps. Vector DBs (Milvus, Vespa) построены поверх FAISS-подобных движков.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q12. (!) Pinecone — managed vector DB? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q13. (!) Weaviate? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q14. (!) Qdrant? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q15. Milvus? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Milvus** — open-source vector DB от Zilliz.

**Особенности:**
- Distributed architecture (Kubernetes-native)
- Очень scaling — миллиарды векторов
- Multiple ANN algorithms (HNSW, IVF, ANNOY)
- Strong consistency
- GPU acceleration

**Применение:** очень большие datasets, enterprise.

**Минусы:** более complex deployment чем Qdrant/Weaviate.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q16. (!) pgvector — PostgreSQL extension? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q17. Chroma — embedded option? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q18. Elasticsearch / OpenSearch как vector DB? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q19. (!) Hybrid search (vector + keyword)? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q20. (!) Metadata filtering? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q21. Multi-tenancy? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Multi-tenancy** — изоляция данных tenants в одной installation.

**Подходы:**

1. **Tenant per collection/index** — `index_acme`, `index_beta`. Простое, но scaling issues с тысячами tenants.
2. **Filter by tenant_id** — все в одной collection с metadata `tenant_id`. Compute resources shared.
3. **Native multi-tenancy** (Weaviate, Pinecone Serverless) — built-in isolation.

**Critical:** убедиться, что **не leak'ает** между tenants. Тестируй с unit tests.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q22. Replication, sharding? ❌ ПОСЛЕДСТВИЕ: антипаттерн деградирует SLA при росте нагрузки или зависимостей.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q23. (!) Recall vs latency trade-off? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q24. Quantization для cost reduction? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Quantization** — сжатие векторов:

| Type | Compression | Recall impact |
|------|-------------|---------------|
| **fp32 → fp16** | 2x | Минимальный |
| **fp32 → int8** | 4x | Маленький |
| **Binary** (1 bit per dim) | 32x | Заметный (но окей с rerank) |
| **PQ (Product Quantization)** | 4-32x | Зависит от params |

**Binary quantization** — bytes per vector почти бесплатно. Используется как **cheap first stage**, потом rerank top-k через full precision.

В **2025** — quantization standard для systems с миллионами+ векторов.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q25. (!) Сколько векторов обычно? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q26. (!) Какой vector DB выбрать? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q27. Backup, restore, migrations? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

**Vector DB не имеет** standard backup/restore tools (как Postgres).

**Подходы:**
1. **Re-embed everything** — если есть source documents, дешевле просто пересоздать (2-3 часа на M vectors)
2. **Export embeddings** — dump всех vectors в файл, restore
3. **Snapshot** (Pinecone, Qdrant) — точка во времени
4. **Replication-based** — реплика в другом регионе

**Migrations между DBs:** обычно через source documents (re-embed). Прямого export/import между разными DBs нет.


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление## Q28. (!) Какие подводные камни в production? ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.

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


> [!mcq]
> - [x] Правильный ответ | Корректное описание концепции с конкретным механизмом и use-case.
> - [ ] Альтернативное решение которое не подходит | Почему ошибка в этом подходе ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Другая альтернатива с критическим недостатком | Это смежное, но отличное понятие ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
> - [ ] Третий вариант который не работает в production | Противоположное направление- [AI Agents](ai-agents-interview.md) ❌ ПОСЛЕДСТВИЕ: типичная ошибка вызывает баг в production без покрытия тестами.
- [Embeddings](embeddings-interview.md)
- [LLM Basics](llm-basics-interview.md)
- [LLM Integration Patterns](llm-integration-patterns-interview.md)
- [MLOps](mlops-interview.md)
- [Model Serving](model-serving-interview.md)
