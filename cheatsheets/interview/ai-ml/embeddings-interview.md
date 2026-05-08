---
title: "Вопросы на собеседовании: Embeddings"
description: "Embeddings: text/image/audio embedding models, dimensions, normalization, semantic similarity, choice of model (OpenAI, Cohere, sentence-transformers), fine-tuning, multilingual"
tags:
  - interview
  - ai-ml
  - embeddings-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Embeddings"
  - "Embeddings interview"
  - "Embedding models interview"
prerequisites: []
next: []
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Embeddings`

**Embedding** — преобразование текста (или image, audio) в вектор чисел, где **семантически похожие** items имеют **близкие** векторы. Основа vector search, RAG, recommendations, clustering. На интервью спрашивают: как выбрать модель, dimensions, normalization, multilingual, fine-tuning, cost optimization.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [OpenAI Embeddings Guide](https://platform.openai.com/docs/guides/embeddings)
- [Cohere Embed v3 Documentation](https://docs.cohere.com/docs/embeddings)
- [Sentence Transformers Documentation](https://www.sbert.net/)
- [Hugging Face Embeddings Course](https://huggingface.co/learn/cookbook/embeddings)
- [MTEB Leaderboard (Massive Text Embedding Benchmark)](https://huggingface.co/spaces/mteb/leaderboard)
- [Voyage AI Documentation](https://docs.voyageai.com/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое embedding?](#q1--что-такое-embedding)
- [Q2. (!) Зачем embeddings?](#q2--зачем-embeddings)
- [Q3. (!) Как embedding model работает?](#q3--как-embedding-model-работает)
- [Q4. Sparse vs dense embeddings?](#q4-sparse-vs-dense-embeddings)

**Главные модели**
- [Q5. (!) OpenAI text-embedding-3?](#q5--openai-text-embedding-3)
- [Q6. (!) Cohere embed-v3?](#q6--cohere-embed-v3)
- [Q7. (!) Voyage AI?](#q7--voyage-ai)
- [Q8. (!) Sentence Transformers (open-source)?](#q8--sentence-transformers-open-source)
- [Q9. BGE, E5 модели?](#q9-bge-e5-модели)

**Сравнения и выбор**
- [Q10. (!) Как выбрать embedding model?](#q10--как-выбрать-embedding-model)
- [Q11. (!) MTEB benchmark?](#q11--mteb-benchmark)
- [Q12. (!) Какие dimensions выбрать?](#q12--какие-dimensions-выбрать)

**Технические детали**
- [Q13. (!) Normalization — нужна ли?](#q13--normalization--нужна-ли)
- [Q14. (!) Cosine similarity vs dot product?](#q14--cosine-similarity-vs-dot-product)
- [Q15. Token limit для embedding?](#q15-token-limit-для-embedding)
- [Q16. Truncation длинных текстов?](#q16-truncation-длинных-текстов)

**Multilingual**
- [Q17. (!) Multilingual embeddings?](#q17--multilingual-embeddings)
- [Q18. Cross-lingual search?](#q18-cross-lingual-search)

**Не-текстовые embeddings**
- [Q19. (!) Image embeddings (CLIP)?](#q19--image-embeddings-clip)
- [Q20. Audio embeddings?](#q20-audio-embeddings)
- [Q21. Multimodal embeddings?](#q21-multimodal-embeddings)

**Optimization**
- [Q22. (!) Cost optimization для embeddings?](#q22--cost-optimization-для-embeddings)
- [Q23. (!) Caching embeddings?](#q23--caching-embeddings)
- [Q24. Matryoshka embeddings (truncate dimensions)?](#q24-matryoshka-embeddings-truncate-dimensions)
- [Q25. Quantization embeddings (binary, int8)?](#q25-quantization-embeddings-binary-int8)

**Fine-tuning**
- [Q26. (!) Когда fine-tuning embedding model?](#q26--когда-fine-tuning-embedding-model)
- [Q27. (!) Как fine-tune (sentence transformers)?](#q27--как-fine-tune-sentence-transformers)

**Применения**
- [Q28. (!) Применения embeddings помимо RAG?](#q28--применения-embeddings-помимо-rag)
- [Q29. Какие частые проблемы?](#q29-какие-частые-проблемы)

## Q1. (!) Что такое embedding?

**Embedding** — числовое представление data (text, image, audio) как **вектор** в многомерном пространстве.

```
"кошка"    → [0.12, -0.45, 0.78, ..., 0.03]  (1536 чисел)
"котёнок"  → [0.14, -0.43, 0.76, ..., 0.05]  (близкий к "кошка")
"автомобиль" → [-0.33, 0.51, 0.02, ..., 0.91] (далёкий от "кошка")
```

**Свойство:** семантически похожие → близкие векторы.

**Применения:**
- Semantic search
- RAG retrieval
- Clustering
- Recommendations
- Anomaly detection
- Classification (embeddings + linear layer)


> [!mcq]
> - [ ] Сжатое бинарное представление текста для экономии памяти | ❌ ПОСЛЕДСТВИЕ: бинарное сжатие не сохраняет семантику — "кошка" и "котёнок" будут случайными битами без близости в пространстве
> - [ ] Числовое ID каждого слова в словаре (one-hot encoding) | ❌ ПОСЛЕДСТВИЕ: one-hot vectors ортогональны — "кошка" и "котёнок" cosine similarity = 0, семантическая близость не представима
> - [x] Числовой вектор в многомерном пространстве, где семантически похожие items имеют близкие векторы | ✓ ПРИМЕНЯТЬ: для semantic search, RAG, recommendations 📋 ПРАВИЛО: embedding = dense vector, близость = семантическая близость 🔗 См. Q2
> - [ ] Hash функция текста для быстрого сравнения строк | ❌ ПОСЛЕДСТВИЕ: hash функции не сохраняют семантику — изменение одного символа даёт полностью разный hash; "cat" и "cats" не имеют схожих хэшей

## Q2. (!) Зачем embeddings?

**Без embeddings** (BM25 keyword search):
```
Query: "горячий напиток"
Doc: "кофе бодрит"
Match: 0 (нет общих слов)
```

**С embeddings:**
```
emb("горячий напиток") близко к emb("кофе бодрит")
Match: 0.85
```

**Embeddings улавливают семантику**, не только syntax. Это революция в search, recommendations.


> [!mcq]
> - [ ] Keyword search BM25 достаточен для любой задачи поиска | ❌ ПОСЛЕДСТВИЕ: BM25 находит только точные совпадения слов — "горячий напиток" не найдёт "кофе бодрит"; синонимы и парафразы дают нулевые совпадения
> - [x] Embeddings позволяют сравнивать семантическую близость там где keyword search не справляется (разные слова, одинаковый смысл) | ✓ ПРИМЕНЯТЬ: для semantic search, RAG retrieval 📋 ПРАВИЛО: embeddings = понимание смысла, не поиск слов 🔗 См. Q4
> - [ ] Embeddings нужны только для ускорения поиска, не для качества | ❌ ПОСЛЕДСТВИЕ: embeddings улучшают recall (находят релевантные документы без точного совпадения слов) — это качество, не скорость; vector search может быть медленнее BM25
> - [ ] Embeddings заменяют SQL full-text search только для производительности | ❌ ПОСЛЕДСТВИЕ: FTS и embeddings решают разные задачи: FTS = exact/prefix match; embeddings = semantic similarity; замена без понимания → плохой recall на exact term queries

## Q3. (!) Как embedding model работает?

**Архитектура:** transformer (encoder-only обычно).

**Обучение:**
1. Брать пары semantically similar текстов (или triplets)
2. Обучать модель так, чтобы их embeddings были близки
3. Negative samples — далёкие embeddings

**Loss функции:**
- **Contrastive loss** — pairs (similar/dissimilar)
- **Triplet loss** — anchor + positive + negative
- **InfoNCE** — multi-class contrastive

**Output:** vector нужного размера (обычно last hidden state с pooling — mean / [CLS] token).


> [!mcq]
> - [ ] Decoder-only transformer (как GPT) с next-token prediction | ❌ ПОСЛЕДСТВИЕ: decoder-only не оптимален для embeddings — он предсказывает следующий токен, а не кодирует весь контекст; encoder-only (BERT) видит весь текст bidirectionally
> - [ ] Bag-of-words модель с tf-idf весами для каждого слова | ❌ ПОСЛЕДСТВИЕ: BoW/TF-IDF — sparse, не учитывает порядок слов и контекст; "not good" и "good" имеют схожие TF-IDF векторы, несмотря на противоположный смысл
> - [x] Encoder-only transformer (BERT-like), обученный contrastive loss на парах похожих текстов; output = pooled last hidden state | ✓ ПРИМЕНЯТЬ: при понимании архитектуры embedding моделей 📋 ПРАВИЛО: contrastive training = подтягивать похожее, отталкивать разное 🔗 См. Q5
> - [ ] CNN с max-pooling над символьными n-gramами | ❌ ПОСЛЕДСТВИЕ: char-level CNN не понимает семантику на уровне слов и предложений — "кошка" и "котёнок" нет n-gram overlap → низкое similarity несмотря на семантическую близость

## Q4. Sparse vs dense embeddings?

**Dense embeddings** — small dim, all dimensions имеют значение (`[0.1, -0.5, 0.3, ...]`).
**Sparse embeddings** — large dim (vocab size), most dimensions = 0 (`[0, 0, 0.7, 0, 0.3, ...]`).

**Sparse примеры:**
- TF-IDF
- BM25 weights
- SPLADE (modern sparse, learned)

**Dense vs sparse:**
- Dense — лучше семантика, плохо для exact terms
- Sparse — отлично для exact terms (codes, names), хуже для семантики

**Hybrid** = combine оба.


> [!mcq]
> - [ ] Dense embeddings лучше во всех случаях, sparse устарели | ❌ ПОСЛЕДСТВИЕ: для точного поиска кодов, артикулов, имён собственных sparse (BM25) превосходит dense — "iPhone 15 Pro" найдёт точное совпадение, dense может вернуть похожие смартфоны
> - [ ] Sparse embeddings занимают меньше памяти чем dense | ❌ ПОСЛЕДСТВИЕ: наоборот: sparse имеют vocab_size dims (~30K), dense — 256-3072; но sparse хранятся в compressed форме (только non-zero), поэтому на практике sparse компактнее
> - [x] Dense = малые dims, все значимы, хорош для семантики; Sparse = большие dims, большинство нулей, хорош для exact terms; Hybrid = best of both | ✓ ПРИМЕНЯТЬ: hybrid для production RAG 📋 ПРАВИЛО: semantic query → dense; exact term (коды, названия) → sparse; оба → hybrid 🔗 См. Q11
> - [ ] Sparse embeddings поддерживают только BM25, не нейросетевые модели | ❌ ПОСЛЕДСТВИЕ: SPLADE — нейросетевая sparse модель; современные learned sparse embeddings сочетают точность exact match с некоторой семантикой

## Q5. (!) OpenAI text-embedding-3?

**OpenAI text-embedding-3** (2024):

| Model | Dims | Cost |
|-------|------|------|
| `text-embedding-3-small` | 1536 (можно truncate) | $0.02 / 1M tokens |
| `text-embedding-3-large` | 3072 (можно truncate) | $0.13 / 1M tokens |

**Особенности:**
- **Matryoshka** — можно truncate dimensions без сильной потери качества (1536 → 512 OK)
- **Цена низкая** vs LLM API
- **Supports multilingual** (хорошо для популярных языков)
- 8K context (max input)

```python
response = client.embeddings.create(
    model="text-embedding-3-small",
    input="Hello, world!",
    dimensions=512  # truncate (matryoshka)
)
```

В 2025 — **default** для большинства случаев (best balance).


> [!mcq]
> - [ ] text-embedding-ada-002 — актуальная модель OpenAI для embeddings | ❌ ПОСЛЕДСТВИЕ: ada-002 устарела; text-embedding-3 лучше по качеству при более низкой цене и добавляет Matryoshka truncation — выбор ada-002 в 2025 suboptimal
> - [ ] text-embedding-3-large обязательно нужна для production RAG | ❌ ПОСЛЕДСТВИЕ: text-embedding-3-small дешевле в 6.5× при 5-10% разнице качества — для большинства задач small достаточна; overspending без бенчмарка на своих данных
> - [x] text-embedding-3-small (1536d, $0.02/1M) и large (3072d, $0.13/1M) с Matryoshka truncation; 8K context; default выбор для non-privacy задач | ✓ ПРИМЕНЯТЬ: production RAG, semantic search 📋 ПРАВИЛО: small сначала — если quality OK, не переплачивать за large 🔗 См. Q12
> - [ ] OpenAI embeddings возвращают non-normalized векторы, требуют ручной нормализации | ❌ ПОСЛЕДСТВИЕ: OpenAI embeddings возвращают уже нормализованные векторы — ненужная дополнительная нормализация не ломает, но вводит в заблуждение

## Q6. (!) Cohere embed-v3?

**Cohere Embed v3** (2023+):

| Model | Dims | Languages |
|-------|------|-----------|
| `embed-english-v3.0` | 1024 | English |
| `embed-multilingual-v3.0` | 1024 | 100+ languages |

**Особенности:**
- **Input type aware** — `search_document`, `search_query`, `classification`, `clustering`
- Excellent multilingual
- Хорош для **retrieval-specific** use cases

```python
response = co.embed(
    texts=["Hello"],
    model="embed-english-v3.0",
    input_type="search_document"  # vs "search_query"
)
```

Часто **лучше OpenAI** на retrieval benchmarks.


> [!mcq]
> - [ ] Cohere embed принимает одинаковый input_type для queries и documents | ❌ ПОСЛЕДСТВИЕ: Cohere обучен с asymmetric setup: query → `search_query`, document → `search_document`; использование одного типа для обоих снижает retrieval качество
> - [x] Cohere embed-v3 с input_type-aware API (`search_document`/`search_query`/`classification`/`clustering`); excellent multilingual; часто лучше OpenAI на retrieval | ✓ ПРИМЕНЯТЬ: multilingual RAG, retrieval-first задачи 📋 ПРАВИЛО: правильный input_type = ключ к лучшему retrieval 🔗 См. Q17
> - [ ] Cohere embed-v3 поддерживает только английский язык | ❌ ПОСЛЕДСТВИЕ: `embed-multilingual-v3.0` поддерживает 100+ языков — неверное предположение ведёт к выбору менее оптимальной модели для multilingual задач
> - [ ] input_type параметр в Cohere — только для logging, не влияет на embeddings | ❌ ПОСЛЕДСТВИЕ: input_type фундаментально меняет embedding — модель обучена разграничивать query embedding от document embedding; игнорирование даёт плохой retrieval

## Q7. (!) Voyage AI?

**Voyage AI** — стартап, специализируется на embeddings. Использует Anthropic для рекомендаций.

**Models:**
- `voyage-large-2-instruct` — best quality
- `voyage-code-2` — для code
- `voyage-multilingual-2`
- `voyage-finance-2` — domain-specific

**Особенности:**
- Часто **state-of-the-art** на MTEB
- Domain-specific models
- Anthropic интеграция

В **2025** — топ выбор для production RAG.


> [!mcq]
> - [ ] Voyage AI — product Anthropic, поставляется вместе с Claude API | ❌ ПОСЛЕДСТВИЕ: Voyage AI — независимый стартап с отдельным billing; путаница в поставщике ведёт к неверной настройке authentication и billing
> - [x] Независимый стартап, SOTA на MTEB, domain-specific модели (code, finance, multilingual), интегрирован с Anthropic ecosystem | ✓ ПРИМЕНЯТЬ: production RAG где важна top quality 📋 ПРАВИЛО: voyage-code-2 для code, voyage-large-2-instruct для общего RAG 🔗 См. Q10
> - [ ] Voyage AI поддерживает только text модальность без code или finance специализации | ❌ ПОСЛЕДСТВИЕ: voyage-code-2 специализирован для code retrieval — неверное предположение ведёт к использованию generic модели для code search с худшим качеством
> - [ ] Voyage AI требует 100K+ документов для работы, не подходит малым проектам | ❌ ПОСЛЕДСТВИЕ: Voyage API работает для любого размера; нет минимального порога документов

## Q8. (!) Sentence Transformers (open-source)?

**`sentence-transformers`** — Python library от UKP Lab.

```python
from sentence_transformers import SentenceTransformer
model = SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2')

embeddings = model.encode(["Hello", "World"])
# numpy array shape (2, 384)
```

**Популярные models:**
- `all-MiniLM-L6-v2` (384d) — small, fast
- `all-mpnet-base-v2` (768d) — better quality
- `multi-qa-mpnet-base-cos-v1` — для Q&A
- `paraphrase-multilingual-MiniLM-L12-v2` — multilingual

**Преимущества:**
- **Self-host** (privacy, no API costs)
- Огромный выбор models на Hugging Face
- Fine-tuning easy

**Недостатки:**
- Хуже proprietary в quality (отстают от OpenAI/Cohere)
- Нужны GPU для perf


> [!mcq]
> - [ ] Sentence Transformers доступны только через платный API | ❌ ПОСЛЕДСТВИЕ: sentence-transformers полностью open-source, self-hosted — нет API costs; неверное предположение ведёт к ненужным расходам при privacy-sensitive данных
> - [x] Open-source Python library для local embedding: all-MiniLM-L6-v2 (fast/small), all-mpnet-base-v2 (better quality); без API costs, privacy-safe; чуть хуже качеством vs proprietary | ✓ ПРИМЕНЯТЬ: privacy-sensitive данные или высокий объём 📋 ПРАВИЛО: self-host = данные не покидают инфраструктуру 🔗 См. Q9
> - [ ] Sentence Transformers требуют минимум 8GB GPU RAM для inference | ❌ ПОСЛЕДСТВИЕ: all-MiniLM-L6-v2 (22M параметров) работает на CPU с разумной скоростью; GPU ускоряет, но не обязателен для небольших объёмов
> - [ ] Sentence Transformers выдают ненормализованные векторы, всегда нужна ручная нормализация | ❌ ПОСЛЕДСТВИЕ: поведение зависит от модели; лучше использовать `normalize_embeddings=True` явно, но некоторые модели нормализуют по умолчанию

## Q9. BGE, E5 модели?

**BGE (BAAI General Embedding)** — open-source SOTA models от Beijing Academy of AI.

**Models:**
- `BAAI/bge-large-en-v1.5` (1024d) — top open-source English
- `BAAI/bge-m3` — multilingual, multi-functional (dense + sparse + colbert)

**E5** — Microsoft embeddings.
- `intfloat/multilingual-e5-large` — multilingual
- `intfloat/e5-mistral-7b-instruct` — large (7B), best open-source quality

В **2025** open-source качество подтянулось к proprietary.


> [!mcq]
> - [ ] BGE и E5 — коммерческие платные модели Microsoft и Alibaba | ❌ ПОСЛЕДСТВИЕ: BGE (BAAI) и E5 (Microsoft) полностью open-source на Hugging Face; неверное предположение ведёт к ненужному использованию платных API
> - [x] BGE (BAAI) — open-source SOTA, BGE-M3 = multilingual + dense+sparse+colbert; E5 — Microsoft, e5-mistral-7b = лучшее open-source качество | ✓ ПРИМЕНЯТЬ: privacy требования + high quality 📋 ПРАВИЛО: open-source quality 2025 сравнимо с proprietary 🔗 См. Q10
> - [ ] BGE-M3 поддерживает только английский, несмотря на "M" в названии | ❌ ПОСЛЕДСТВИЕ: "M3" означает Multi-Functionality, Multi-Linguality, Multi-Granularity — это multilingual модель для 100+ языков
> - [ ] E5-mistral-7b — слишком большая для production inference из-за 7B параметров | ❌ ПОСЛЕДСТВИЕ: 7B модель требует ~14GB GPU RAM при fp16 — для high-throughput embedding это много, но для offline indexing приемлемо; зависит от SLA требований

## Q10. (!) Как выбрать embedding model?

**Decision factors:**

1. **Quality:** проверить **MTEB leaderboard**
2. **Cost:** API per-token vs self-host (GPU)
3. **Latency:** локально vs network
4. **Privacy:** API leaks data → нужен self-host для PII
5. **Languages:** multilingual?
6. **Domain:** general vs domain-specific (code, finance, medical)
7. **Dimensions:** balance quality vs storage cost
8. **Context length:** 512 (BERT) vs 8K (OpenAI) tokens

**Default 2025:**
- **Production RAG, не privacy-sensitive:** Voyage / OpenAI text-embedding-3-large
- **Privacy:** BGE-M3, E5-large
- **Cost-sensitive:** OpenAI text-embedding-3-small
- **Multilingual:** Cohere embed-multilingual-v3 / BGE-M3


> [!mcq]
> - [ ] Всегда выбирать модель с наибольшим MTEB score | ❌ ПОСЛЕДСТВИЕ: MTEB score на чужих данных не гарантирует качество на твоих; NV-Embed #1 на MTEB может проигрывать text-embedding-3-small на твоём specific domain
> - [x] Выбор по: MTEB (baseline), privacy (API vs self-host), языки, domain specifics, context length, dimensions/storage tradeoff | ✓ ПРИМЕНЯТЬ: чеклист при выборе embedding модели 📋 ПРАВИЛО: тестируй на СВОИХ данных — MTEB = отправная точка, не финальный ответ 🔗 См. Q11
> - [ ] Самая дорогая API модель всегда превосходит open-source | ❌ ПОСЛЕДСТВИЕ: BGE-M3 и e5-mistral-7b competitive с OpenAI/Cohere в 2025; на domain-specific задачах fine-tuned open-source может превосходить generic API модели
> - [ ] Dimensions не важны при выборе — все модели одинаково хороши | ❌ ПОСЛЕДСТВИЕ: 1M documents × 3072 dims × 4 bytes = 12GB vs × 384 dims = 1.5GB; выбор модели с избыточными dims приводит к 8× перерасходу памяти

## Q11. (!) MTEB benchmark?

**MTEB (Massive Text Embedding Benchmark)** — стандартный бенчмарк для embeddings. Hugging Face leaderboard.

**Tasks:**
- Retrieval (search relevance)
- Classification
- Clustering
- Reranking
- Pair classification
- STS (semantic textual similarity)

**56 datasets, 8 task types**.

**Top models 2025:**
1. NV-Embed (NVIDIA)
2. Voyage 3
3. BGE-EN-ICL
4. E5-mistral-7b
5. text-embedding-3-large

**Подвох:** MTEB не идеален. Лучше тестировать на **своих data**.


> [!mcq]
> - [ ] MTEB — benchmark только для retrieval задач, не для classification | ❌ ПОСЛЕДСТВИЕ: MTEB охватывает 8 task types включая classification, clustering, STS — неполное понимание ведёт к выбору модели без оценки нужного task type
> - [ ] Модель #1 на MTEB оптимальна для любого production use case | ❌ ПОСЛЕДСТВИЕ: NV-Embed #1 на общем MTEB может быть overfit на benchmark datasets; реальный performance на domain-specific данных может отличаться; тестируй на своих данных
> - [x] Стандартный benchmark: 56 datasets, 8 task types (retrieval, classification, clustering...); отправная точка выбора модели; обязательно проверить на своих данных | ✓ ПРИМЕНЯТЬ: при сравнении embedding моделей 📋 ПРАВИЛО: MTEB leaderboard = start, not finish; собственный eval обязателен 🔗 См. Q10
> - [ ] Достаточно MTEB score, собственный бенчмарк не нужен | ❌ ПОСЛЕДСТВИЕ: модель обученная на новостях (#1 MTEB) может быть плохой для медицинских документов; domain shift реален — без собственного eval риск выбрать неоптимальную модель

## Q12. (!) Какие dimensions выбрать?

**Trade-off:**
- **Малые (256-512):** быстро, дёшево storage, чуть хуже quality
- **Средние (768-1024):** standard для большинства open-source
- **Большие (1536-3072):** OpenAI, Cohere; больше storage, чуть лучше quality

**С Matryoshka embeddings (OpenAI v3, Nomic):**
```
Train на 3072d → truncate в любой меньший size без сильной потери
```

**Memory cost:**
```
1M vectors × 1536 dims × 4 bytes = 6 GB
1M vectors × 384 dims × 4 bytes  = 1.5 GB
```

**Best practice:** start с 512-1024d. Increase only если нужна квалити.


> [!mcq]
> - [ ] Всегда использовать максимальные dimensions (3072d) для лучшего качества | ❌ ПОСЛЕДСТВИЕ: 1M docs × 3072d × 4 bytes = 12GB vs 1.5GB для 384d; 8× перерасход памяти при 5-10% gains в quality — несоразмерный трadeoff
> - [x] Малые (256-512): быстро+дёшево; средние (768-1024): баланс; большие (1536-3072): лучшее quality; Matryoshka позволяет truncate без значительных потерь | ✓ ПРИМЕНЯТЬ: начни с 512-1024d, увеличь если quality недостаточно 📋 ПРАВИЛО: dimensions tradeoff: quality vs storage/speed 🔗 См. Q24
> - [ ] Dimensions не влияют на скорость поиска в vector DB | ❌ ПОСЛЕДСТВИЕ: vector similarity search — O(d × n); увеличение dims в 4× замедляет поиск в 4×; HNSW индекс также занимает больше памяти при больших dims
> - [ ] Меньше dimensions всегда хуже — нельзя truncate без полной потери качества | ❌ ПОСЛЕДСТВИЕ: Matryoshka embeddings (OpenAI v3) специально обучены чтобы truncation давала минимальные потери; 1536→512 = ~5-10% quality loss, not catastrophic

## Q13. (!) Normalization — нужна ли?

**Normalization** — превратить vector в unit-length (`||v|| = 1`).

```python
import numpy as np
normalized = vector / np.linalg.norm(vector)
```

**OpenAI, Cohere** — возвращают **уже normalized** vectors.
**Sentence Transformers** — обычно нет (нужно `normalize_embeddings=True`).

**Зачем:**
- **Cosine similarity = dot product** (вычисление быстрее)
- Vector DBs могут оптимизировать с normalized

**Подвох:** если half embeddings normalized, half нет — поиск будет плохим. Согласованность критична.


> [!mcq]
> - [ ] Нормализация не нужна — vector DB автоматически нормализует при поиске | ❌ ПОСЛЕДСТВИЕ: некоторые vector DBs нормализуют только если явно указан metric="cosine"; при metric="dot" ненормализованные векторы дают неверный similarity ranking
> - [ ] Нормализовать нужно дважды: при indexing и при query | ❌ ПОСЛЕДСТВИЕ: double normalization бессмысленна — единичный вектор уже нормализован; лишняя операция не улучшает, но при ошибке может изменить magnitude
> - [x] Нормализация (||v||=1) позволяет использовать dot product вместо cosine (быстрее 10-30%); OpenAI/Cohere нормализуют автоматически; Sentence Transformers — нужно явно; главное — согласованность всех векторов | ✓ ПРИМЕНЯТЬ: normalize_embeddings=True для ST 📋 ПРАВИЛО: если хоть часть ненормализована — similarity scores некорректны 🔗 См. Q14
> - [ ] Нормализация ухудшает качество semantic search, удаляя magnitude информацию | ❌ ПОСЛЕДСТВИЕ: для cosine/dot similarity magnitude не несёт semantic информации — только направление вектора важно; нормализация не ухудшает semantic search

## Q14. (!) Cosine similarity vs dot product?

```
cos(A, B) = (A · B) / (|A| × |B|)
```

Если **оба нормализованы** (`|A| = |B| = 1`):
```
cos(A, B) = A · B
```

**Dot product быстрее** (нет normalization). Поэтому в production с normalized vectors — используй `dot product` метрику в vector DB (выигрыш ~10-30% latency).

```python
# В Pinecone, Weaviate, Qdrant
metric = "dot"  # вместо "cosine" если уже normalized
```


> [!mcq]
> - [ ] Cosine similarity и dot product дают разные результаты даже для normalized vectors | ❌ ПОСЛЕДСТВИЕ: для unit-norm vectors cos(A,B) = A·B математически; они идентичны — но dot product быстрее из-за отсутствия division
> - [ ] Euclidean distance лучше cosine для embedding similarity | ❌ ПОСЛЕДСТВИЕ: для normalized embeddings cosine и dot product оптимальны; Euclidean distance не захватывает semantic similarity так же эффективно и медленнее в HNSW
> - [x] Для normalized vectors dot product = cosine similarity, но быстрее на 10-30% (нет деления на нормы); используй metric="dot" в vector DB если embeddings нормализованы | ✓ ПРИМЕНЯТЬ: всегда с normalized OpenAI/Cohere embeddings 📋 ПРАВИЛО: normalized vectors → dot product = бесплатная оптимизация 🔗 См. Q13
> - [ ] Dot product требует дополнительной настройки vector DB, cosine работает везде | ❌ ПОСЛЕДСТВИЕ: все major vector DBs (Pinecone, Weaviate, Qdrant) поддерживают dot product; настройка — один параметр metric="dot"

## Q15. Token limit для embedding?

**Каждая embedding model имеет max input length:**

| Model | Max tokens |
|-------|-----------|
| OpenAI text-embedding-3 | 8191 |
| Cohere embed-v3 | 512 (!) |
| BGE-large | 512 |
| Voyage-large-2 | 16000 |
| Sentence Transformers (BERT-based) | 512 (обычно) |

**Подвох с Cohere/BGE:** **512 tokens** = ~400 английских слов. Длинные документы нужно chunk'ить.


> [!mcq]
> - [ ] Все embedding модели поддерживают неограниченную длину текста | ❌ ПОСЛЕДСТВИЕ: Cohere embed-v3 / BGE: 512 tokens ≈ 400 слов; превышение лимита → truncation или ошибка; длинные документы без chunking дают embedding только первых 512 токенов
> - [x] Каждая модель имеет лимит: Cohere/BGE = 512; OpenAI = 8191; Voyage = 16K; превышение → truncation; длинные документы → chunking | ✓ ПРИМЕНЯТЬ: chunk документы под лимит модели 📋 ПРАВИЛО: Cohere/BGE 512 токенов ≈ half страницы текста 🔗 См. Q16
> - [ ] Лимит токенов не важен если использовать sliding window averaging | ❌ ПОСЛЕДСТВИЕ: averaging embeddings из sliding window — неточный метод; proper chunking с последующим independent embedding + retrieval даёт лучший recall
> - [ ] OpenAI и Cohere имеют одинаковый лимит 512 токенов | ❌ ПОСЛЕДСТВИЕ: OpenAI: 8191 токенов, Cohere: 512 — разница в 16×; использование chunking для OpenAI когда не нужно — лишние API calls и overhead

## Q16. Truncation длинных текстов?

Если текст > max_tokens:

1. **Truncate** — обрезать до max
2. **Chunk** — разбить на кусочки, embed каждый
3. **Summarize first** (LLM) → embed summary
4. **Embedding pooling** — mean / max / first chunk

**Best practice (RAG):**
- Использовать **proper chunking** на стадии preprocessing (см. [RAG](rag-interview.md))
- Каждый chunk влезает в model limit
- Embed каждый chunk отдельно


> [!mcq]
> - [ ] Truncate первые 512 токенов и игнорировать остальное | ❌ ПОСЛЕДСТВИЕ: ключевая информация в документе может быть в конце; raw truncation теряет до 90% контента длинного документа — recall в RAG деградирует
> - [ ] Embed целый длинный документ без chunking используя модели с 8K контекстом | ❌ ПОСЛЕДСТВИЕ: один embedding для 8K документа усредняет всё — конкретные детали "растворяются" в общем embedding; retrieval precision падает, нет granular matching
> - [x] Proper chunking (400-1000 tokens с overlap) для RAG; embed каждый chunk отдельно; альтернативы: LLM summarization или embedding pooling | ✓ ПРИМЕНЯТЬ: preprocessing pipeline перед indexing 📋 ПРАВИЛО: chunk size < model limit; overlap предотвращает разрезание контекста 🔗 См. Q15
> - [ ] Summarize каждый документ LLM перед embedding — лучшее качество | ❌ ПОСЛЕДСТВИЕ: LLM summarization дорого ($0.01+ per document); теряет specific details нужные для precise retrieval; chunking + independent embedding дешевле и точнее

## Q17. (!) Multilingual embeddings?

**Multilingual model** обучена на много языков, embeds разные языки в **одно общее space**.

```
emb("hello") ≈ emb("привет") ≈ emb("你好")
```

**Models:**
- **Cohere embed-multilingual-v3** — 100+ languages
- **BGE-M3** — multilingual + multi-functional
- **OpenAI text-embedding-3** — хорошо для популярных, хуже для низко-ресурсных
- **paraphrase-multilingual-MiniLM** (sentence-transformers)

**Применения:**
- International products
- Cross-lingual search
- Korean docs ↔ English query


> [!mcq]
> - [ ] Для multilingual достаточно translate всё в английский перед embedding | ❌ ПОСЛЕДСТВИЕ: machine translation вносит ошибки и теряет нюансы; перевод через MT API = дополнительная стоимость и latency; multilingual embedding быстрее и дешевле
> - [x] Multilingual модели embeds разные языки в одно shared space: emb("hello") ≈ emb("привет"); Cohere/BGE-M3 для 100+ языков | ✓ ПРИМЕНЯТЬ: cross-lingual search, international products 📋 ПРАВИЛО: multilingual model = один index для всех языков 🔗 См. Q18
> - [ ] OpenAI text-embedding-3 оптимален для всех языков включая низко-ресурсные | ❌ ПОСЛЕДСТВИЕ: OpenAI хорош для популярных языков (EN, FR, DE, RU), но слабее для низко-ресурсных (тайский, суахили); Cohere multilingual/BGE-M3 лучше для широкого multilingual
> - [ ] Нужно создать отдельный index для каждого языка при multilingual search | ❌ ПОСЛЕДСТВИЕ: отдельные индексы не позволяют cross-lingual search (EN query → RU documents); общее embedding space — ключевое преимущество multilingual моделей

## Q18. Cross-lingual search?

С multilingual embeddings можно искать **на одном языке** в **другом**.

```
Indexed: documents на русском
Query: на английском
Result: relevant Russian docs
```

```python
# Index Russian docs
docs_emb = model.encode(russian_docs)
vector_db.upsert(docs_emb, ...)

# Search with English query
query_emb = model.encode("How to login?")
results = vector_db.search(query_emb)
# Returns Russian docs about login
```

**Quality:** multilingual models обычно **немного хуже** чем monolingual specifically для одного языка, но **намного лучше** для cross-lingual.


> [!mcq]
> - [ ] Cross-lingual search требует machine translation на стороне сервера | ❌ ПОСЛЕДСТВИЕ: translation adds latency (100-500ms) и cost; multilingual embeddings обеспечивают cross-lingual search без translation — один encode вызов
> - [x] Multilingual embedding space позволяет query на языке А находить documents на языке Б без translation; monolingual model лучше для одного языка, multilingual — лучше для cross-lingual | ✓ ПРИМЕНЯТЬ: международные продукты с документацией на нескольких языках 📋 ПРАВИЛО: один multilingual index > N monolingual 🔗 См. Q17
> - [ ] Cross-lingual поиск невозможен без параллельного corpora для каждой пары языков | ❌ ПОСЛЕДСТВИЕ: modern multilingual models обучены с contrastive loss на параллельных текстах; пользователю не нужно предоставлять parallel corpora
> - [ ] Качество cross-lingual search идентично monolingual для всех языков | ❌ ПОСЛЕДСТВИЕ: multilingual models компромисс — немного хуже для каждого отдельного языка чем специализированная monolingual модель; для критичного EN-only — monolingual предпочтительнее

## Q19. (!) Image embeddings (CLIP)?

**CLIP (Contrastive Language-Image Pre-training)** — модель от OpenAI, embeds **изображения и текст в одно общее space**.

```python
import clip
model, preprocess = clip.load("ViT-B/32")

image_features = model.encode_image(image)  # 512d
text_features = model.encode_text(["a photo of a cat"])

similarity = (image_features @ text_features.T)
```

**Применения:**
- **Search images by text** ("найди фото кошки")
- **Classify images** через text labels (zero-shot)
- **Multi-modal RAG** (изображения + текст)

**Альтернативы:** SigLIP (лучше качество), BLIP-2, OpenCLIP (open-source).


> [!mcq]
> - [ ] CLIP — только для классификации изображений, не для поиска | ❌ ПОСЛЕДСТВИЕ: CLIP специально обучен для text-image alignment → image search by text — основное применение; "только для классификации" — неполное понимание модели
> - [x] CLIP embeds images и text в общее space: image_features ≈ text_features для похожих; позволяет text→image и image→image search, zero-shot classification | ✓ ПРИМЕНЯТЬ: multimodal RAG, visual search 📋 ПРАВИЛО: CLIP = shared embedding space для text и image 🔗 См. Q21
> - [ ] CLIP требует fine-tuning для работы с конкретными типами изображений | ❌ ПОСЛЕДСТВИЕ: CLIP работает zero-shot из коробки для широкого круга задач; fine-tuning нужен только для highly specialized domains (медицинские снимки, спутниковые данные)
> - [ ] Для image search нужны только pixel-level hash fingerprints | ❌ ПОСЛЕДСТВИЕ: hash fingerprints — exact duplicate detection, не semantic search; "найди похожие фотографии кошек" требует CLIP-подобных embeddings, не хэши

## Q20. Audio embeddings?

**Models:**
- **Whisper embeddings** — для speech
- **CLAP** — text + audio (Contrastive Language-Audio Pretraining)
- **Wav2Vec 2.0** — для general audio
- **VGGish** — для music tags

**Применения:**
- Music similarity / recommendations
- Speech recognition embeddings
- Audio classification

Менее зрелое, чем text embeddings.


> [!mcq]
> - [ ] Для audio достаточно конвертировать в текст через Whisper и использовать text embeddings | ❌ ПОСЛЕДСТВИЕ: speech-to-text теряет prosody, tone, rhythm; для music similarity и audio classification нужны native audio embeddings которые сохраняют acoustic features
> - [x] Whisper/Wav2Vec для speech, CLAP для text-audio alignment, VGGish для music; менее зрелое чем text embeddings | ✓ ПРИМЕНЯТЬ: music recommendations, audio classification 📋 ПРАВИЛО: audio embeddings = acoustic features, не text 🔗 См. Q21
> - [ ] CLAP — это CLIP для audio, полностью взаимозаменяемы | ❌ ПОСЛЕДСТВИЕ: CLIP (images + text) и CLAP (audio + text) — разные модальности; использование CLIP для audio не работает — нужны audio-specific модели
> - [ ] Audio embeddings работают только для речи, не для музыки | ❌ ПОСЛЕДСТВИЕ: VGGish специально для music; CLAP обрабатывает любой звук; audio embeddings широко применяются в музыкальных рекомендациях (Spotify-like)

## Q21. Multimodal embeddings?

**Multimodal embeddings** — единое space для **разных модальностей** (text + image + video).

**Models 2025:**
- **CLIP** (text + image)
- **ImageBind** (Meta) — 6 modalities (text, image, audio, video, IMU, depth)
- **LanguageBind** — экзtension к ImageBind
- **Cohere Embed v3 multimodal** — text + image (2024)

**Применения:**
- Cross-modal search ("найди video похожее на этот текст")
- Multimodal RAG

В **2025** — растущая область, но менее зрелая чем text-only embeddings.


> [!mcq]
> - [ ] Multimodal embeddings — это просто concatenation text и image векторов | ❌ ПОСЛЕДСТВИЕ: concatenation создаёт смешанный вектор с разными scale — не создаёт shared semantic space; text и image остаются в разных пространствах, cross-modal search не работает
> - [x] Единое semantic space для нескольких модальностей; CLIP (text+image), ImageBind (6 modalities), Cohere multimodal; cross-modal search без перевода между модальностями | ✓ ПРИМЕНЯТЬ: multimodal RAG, cross-modal search 📋 ПРАВИЛО: multimodal = одно пространство, разные входы 🔗 См. Q19
> - [ ] ImageBind поддерживает только text и image, как CLIP | ❌ ПОСЛЕДСТВИЕ: ImageBind (Meta) поддерживает 6 модальностей: text, image, audio, video, IMU (inertial), depth; это ключевое отличие от CLIP
> - [ ] Multimodal embeddings в 2025 имеют такое же качество как text-only | ❌ ПОСЛЕДСТВИЕ: text-only embeddings значительно более зрелые — больше обучающих данных, более точные benchmarks; multimodal — растущая область с более низким baseline quality

## Q22. (!) Cost optimization для embeddings?

1. **Choose right model** — нужна ли large? Часто small достаточно.
2. **Use small dimensions** (Matryoshka) — 1536 → 512 даёт ~3x economy
3. **Cache embeddings** — для same text → cached embedding
4. **Batch requests** — embedding API часто дешевле в batch
5. **Self-host** для high volume (>10M embeddings/month)
6. **Async processing** — embed ночью, не realtime
7. **Avoid re-embedding** — если document не изменился, не embed заново

**Cost example:**
```
1M docs × avg 500 tokens × $0.02/1M tokens = $10
с text-embedding-3-small
```

Очень дёшево по сравнению с LLM API.


> [!mcq]
> - [ ] Embeddings стоят столько же сколько LLM вызовы — основная статья расходов | ❌ ПОСЛЕДСТВИЕ: text-embedding-3-small = $0.02/1M tokens vs GPT-4o = $10/1M; embeddings в 500× дешевле — неверная оценка стоимости ведёт к wrong priority оптимизаций
> - [x] Оптимизации: small model, truncate dims (Matryoshka), cache repeated texts, batch requests, self-host при >10M/month, не re-embed неизменённые документы | ✓ ПРИМЕНЯТЬ: при scaling embedding pipeline 📋 ПРАВИЛО: кэшировать все повторные запросы — embeddings детерминированы 🔗 См. Q23
> - [ ] Self-hosting всегда дешевле API независимо от объёма | ❌ ПОСЛЕДСТВИЕ: GPU стоимость ($0.5-3/час) + инженерное время; при малых объёмах API дешевле; self-host выгоден при >10M embeddings/month примерно
> - [ ] Batch API дороже single requests из-за overhead | ❌ ПОСЛЕДСТВИЕ: batch обычно дешевле или одинаково по цене, но значительно эффективнее по throughput — меньше HTTP overhead, лучше GPU utilization при self-hosting

## Q23. (!) Caching embeddings?

```python
import hashlib

def get_embedding(text):
    cache_key = hashlib.md5(text.encode()).hexdigest()
    if cached := redis.get(f"emb:{cache_key}"):
        return cached
    embedding = openai.embeddings.create(input=text, model="...")
    redis.set(f"emb:{cache_key}", embedding, ex=86400)
    return embedding
```

**Когда использовать кеш:**
- Repeated queries (popular searches)
- A/B testing разные models на same queries
- Dev / staging environments

**Не кешировать** если:
- Embeddings обновляются часто
- Storage cost > API cost (редко, обычно cache дешёвый)


> [!mcq]
> - [ ] Кэшировать embeddings нет смысла — API достаточно быстро | ❌ ПОСЛЕДСТВИЕ: latency API embedding = 50-200ms; cache hit = <1ms; для high-throughput search повторные запросы без cache деградируют latency; стоимость тоже не нулевая при масштабе
> - [x] MD5 hash text → Redis cache; экономит cost и latency для repeated queries; TTL зависит от update frequency текста | ✓ ПРИМЕНЯТЬ: для popular queries и неизменяемых документов 📋 ПРАВИЛО: embeddings детерминированы для одного текста → всегда можно кэшировать 🔗 См. Q22
> - [ ] Кэш нужно инвалидировать при каждом deployment приложения | ❌ ПОСЛЕДСТВИЕ: embeddings зависят только от входного текста и модели — deployment приложения не меняет embeddings; инвалидировать нужно только при смене embedding model
> - [ ] Cache invalidation при обновлении embedding model не нужна | ❌ ПОСЛЕДСТВИЕ: смена модели (ada-002 → text-embedding-3-small) меняет vector space — старые cached vectors несовместимы с новыми; обязателен cache flush при смене модели

## Q24. Matryoshka embeddings (truncate dimensions)?

**Matryoshka representation learning (MRL)** — embeddings обучены так, что их **префиксы** тоже валидны.

```
3072-dim embedding → truncate первые 512 dims → still useful (5-10% quality loss)
```

**Применение:**
- **Tiered search** — сначала search по 256d (быстро), rerank top-k по 1536d
- **Storage savings** — 3-5x экономия

**Models:** OpenAI text-embedding-3, Nomic embed.

```python
emb_full = openai.embeddings.create(model="text-embedding-3-large", input=text).data[0].embedding
# 3072 dims

emb_truncated = emb_full[:512]
# Можно использовать! Quality чуть ниже
```


> [!mcq]
> - [ ] Truncation применима к любой embedding модели | ❌ ПОСЛЕДСТВИЕ: truncation только для Matryoshka-trained моделей (OpenAI v3, Nomic); для обычных моделей truncate первых N dims даёт случайный шум, не useful embedding
> - [x] MRL обучает embeddings так что любой prefix-subset валиден; 3072→512 = ~5-10% quality loss; tiered search: сначала 256d для recall, затем full для precision | ✓ ПРИМЕНЯТЬ: storage экономия без полного reindex 📋 ПРАВИЛО: Matryoshka = truncate freely; обычные embeddings — нет 🔗 См. Q12
> - [ ] Matryoshka embeddings поддерживают только уменьшение до половины исходного размера | ❌ ПОСЛЕДСТВИЕ: можно truncate до любого размера — 3072→1536, 3072→512, 3072→256; более агрессивное truncation даёт больше потерь качества, но допустимо
> - [ ] Tiered search с Matryoshka не даёт преимущества над прямым поиском по full dims | ❌ ПОСЛЕДСТВИЕ: tiered: 256d search (быстро) → rerank top-100 по 1536d; итог: precision full-dim при скорости малого dim — значительный выигрыш при M+ vectors

## Q25. Quantization embeddings (binary, int8)?

Превращаем `float32` (4 bytes/dim) в:
- **fp16** — 2 bytes/dim (2x economy)
- **int8** — 1 byte/dim (4x economy)
- **Binary** — 1 bit/dim (32x economy)

**Binary quantization:**
```python
binary_vec = (vec > 0).astype(np.uint8)  # 1 bit per dim
# Compare через Hamming distance — XOR + popcount, очень быстро
```

**Trade-off:**
- Binary: ~5-10% recall loss, но 32x faster и cheaper
- int8: ~1-2% loss, 4x economy

В **2025** binary quantization стал популярным для **гигантских scales** (100M+ vectors).


> [!mcq]
> - [ ] Binary quantization не подходит для semantic search из-за слишком большой потери качества | ❌ ПОСЛЕДСТВИЕ: 5-10% recall loss при 32x compression — для 100M+ vectors это acceptable трadeoff; без quantization 100M × 1536d × 4 bytes = 600GB
> - [x] float32→fp16 (2x), int8 (4x), binary (32x) compression; binary = Hamming distance (XOR+popcount), очень быстро; binary 5-10% recall loss при 32x economy | ✓ ПРИМЕНЯТЬ: при 10M+ vectors 📋 ПРАВИЛО: quantization = экономия без полного reindex; binary для гигантских scale 🔗 См. Q12
> - [ ] int8 quantization дает те же результаты что fp32 — нет потери качества | ❌ ПОСЛЕДСТВИЕ: int8 = 1-2% recall loss; приемлемо для большинства задач, но не zero loss; для критичных high-precision задач fp32 или fp16 предпочтительнее
> - [ ] Quantization применяется только при inference, не при хранении vectors | ❌ ПОСЛЕДСТВИЕ: quantization часто применяется именно при хранении — quantized vectors хранятся в vector DB; это главная цель: memory savings, fast Hamming distance

## Q26. (!) Когда fine-tuning embedding model?

**Когда нужен:**
- **Domain-specific** vocabulary (medical, legal, code)
- **Specific tasks** (например, customer support tickets matching)
- General models дают плохой recall на твоих data

**Когда НЕ нужен:**
- Generic content (news, blogs, общие документы)
- Маленький dataset (< 1000 examples)
- Нет ML expertise

**Trade-off:** fine-tuning ~$500-5000 (training) + maintenance, vs $0 для использования off-the-shelf.


> [!mcq]
> - [ ] Fine-tuning всегда улучшает embedding модель и обязателен для production | ❌ ПОСЛЕДСТВИЕ: fine-tuning стоит $500-5000 + инженерное время; для generic content off-the-shelf модели отлично работают без доп. расходов
> - [ ] Fine-tuning нужен при < 100 domain documents | ❌ ПОСЛЕДСТВИЕ: с < 100 примерами нет достаточно данных для meaningful fine-tuning — нужно минимум 1000+ golden pairs; маленький dataset приведёт к overfitting
> - [x] Fine-tuning оправдан при domain-specific vocabulary (medical, legal, code) и плохом recall generic модели; не нужен для generic content или при < 1000 training examples | ✓ ПРИМЕНЯТЬ: сначала оценить generic model recall на своих данных 📋 ПРАВИЛО: fine-tune если MTEB модель показывает < 0.7 recall на твоих данных 🔗 См. Q27
> - [ ] Fine-tuning устраняет необходимость в proper chunking и normalization | ❌ ПОСЛЕДСТВИЕ: fine-tuning улучшает domain understanding; chunking и normalization — инфраструктурные требования, не связаны с domain; fine-tuned модель всё равно требует proper preprocessing

## Q27. (!) Как fine-tune (sentence transformers)?

```python
from sentence_transformers import SentenceTransformer, InputExample, losses
from torch.utils.data import DataLoader

# Pairs: (query, positive_doc) или triplets (query, positive, negative)
train_examples = [
    InputExample(texts=["query1", "relevant doc1"]),
    InputExample(texts=["query2", "relevant doc2"]),
    ...
]

model = SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2')
train_loss = losses.MultipleNegativesRankingLoss(model)

train_dataloader = DataLoader(train_examples, batch_size=16, shuffle=True)
model.fit(train_objectives=[(train_dataloader, train_loss)], epochs=3)
model.save("my-fine-tuned-model")
```

**Loss functions:**
- **MultipleNegativesRankingLoss** — самый частый, использует in-batch negatives
- **TripletLoss** — anchor + positive + negative
- **CosineSimilarityLoss** — pairs с similarity scores

**Data preparation:** main работа — собрать **golden pairs** (query, relevant_doc).


> [!mcq]
> - [ ] Для fine-tuning нужны только negative примеры (irrelevant pairs) | ❌ ПОСЛЕДСТВИЕ: без positive pairs модель не знает что считать "похожим"; MultipleNegativesRankingLoss использует остальные samples в batch как negatives — нужны только positive pairs
> - [x] InputExample(texts=[query, relevant_doc]) + MultipleNegativesRankingLoss (in-batch negatives); главная работа — собрать 1000+ golden pairs | ✓ ПРИМЕНЯТЬ: для domain-specific retrieval 📋 ПРАВИЛО: MNRL = минимальная аннотация (только positive pairs), negatives берёт из batch 🔗 См. Q26
> - [ ] CrossEntropyLoss — стандартный loss для fine-tuning embedding моделей | ❌ ПОСЛЕДСТВИЕ: CrossEntropyLoss для классификации; для contrastive embedding fine-tuning используют MultipleNegativesRankingLoss, TripletLoss, CosineSimilarityLoss — contrastive losses
> - [ ] Одного epoch достаточно для effective fine-tuning | ❌ ПОСЛЕДСТВИЕ: 1 epoch при маленьком dataset даёт underfitting; обычно 3-10 epochs с validation; следить за val loss — остановить при начале overfitting

## Q28. (!) Применения embeddings помимо RAG?

1. **Semantic search** — поиск по смыслу, не keywords
2. **Recommendations** — "похожие items" (Netflix, Amazon)
3. **Clustering** — group documents без supervision
4. **Anomaly detection** — outliers in vector space
5. **Classification** — embedding + linear classifier
6. **Deduplication** — find near-duplicate documents
7. **Personalization** — user profile как embedding (avg of liked items)
8. **A/B variant matching**
9. **Plagiarism detection**
10. **Knowledge graph construction** — entity linking


> [!mcq]
> - [ ] Embeddings применяются только для search и RAG задач | ❌ ПОСЛЕДСТВИЕ: узкое понимание — recommendations (Netflix, Amazon), anomaly detection, clustering, classification через embeddings — widespread применения за пределами RAG
> - [x] RAG — одно из многих применений: recommendations, semantic search, clustering, anomaly detection, deduplication, personalization (user = avg liked items embeddings) | ✓ ПРИМЕНЯТЬ: анализ применимости embeddings к задаче 📋 ПРАВИЛО: если задача = "найти похожее" → embeddings решение 🔗 См. Q2
> - [ ] Embeddings для classification хуже чем fine-tuned BERT с classification head | ❌ ПОСЛЕДСТВИЕ: embedding + linear layer — это и есть fine-tuned encoder с classification head; разница только в терминологии; оба подхода дают похожее качество
> - [ ] Deduplication требует exact match, embeddings для near-duplicate не подходят | ❌ ПОСЛЕДСТВИЕ: near-duplicate detection — классическое применение embeddings; "одинаковое по смыслу" при разных формулировках — задача для cosine similarity, не exact match

## Q29. Какие частые проблемы?

1. **Embedding drift** — модель updated, embeddings стали другие → re-embed всё
2. **Query/doc model mismatch** — embedded разными models
3. **Wrong distance metric** — Euclidean для normalized vectors
4. **Bad chunking** — embed целые документы вместо chunks
5. **No normalization когда нужна**
6. **Short texts** (1-2 words) — плохие embeddings
7. **Cost runaway** — embed everything, never cache
8. **Quality degradation** — для domain без fine-tuning
9. **Multilingual в monolingual model** — плохая quality на других языках
10. **Treating embeddings as compression** — нельзя reconstruct text из embedding


> [!mcq]
> - [ ] Главная проблема — неправильный выбор embedding dimensions | ❌ ПОСЛЕДСТВИЕ: dimensions — важный трadeoff, но не #1 проблема; training-serving mismatch (query и document embedded разными моделями) и embedding drift гораздо критичнее для production
> - [x] Типичные проблемы: drift при смене модели (re-embed всё), query/doc model mismatch, bad chunking, no normalization, cost runaway без cache | ✓ ПРИМЕНЯТЬ: production embedding checklist 📋 ПРАВИЛО: query и document ДОЛЖНЫ быть embedded одной и той же моделью 🔗 См. Q10
> - [ ] Embedding drift — маловероятная проблема, модели редко меняются | ❌ ПОСЛЕДСТВИЕ: OpenAI менял модели (ada-002 → text-embedding-3); без re-index старые vectors несовместимы с новой моделью — silent retrieval degradation
> - [ ] Normalization при наличии векторов в vector DB не важна | ❌ ПОСЛЕДСТВИЕ: добавление ненормализованных vectors в index с нормализованными → inconsistent similarity scores; некоторые векторы получают несправедливо высокие/низкие scores

---

## See also

- [Vector Databases](vector-databases-interview.md) — где хранят embeddings
- [RAG](rag-interview.md) — основное применение
- [LLM Basics](llm-basics-interview.md) — LLM генерируют embeddings
- [Prompt Engineering](prompt-engineering-interview.md) — для query embedding
- [MLOps](mlops-interview.md) — model versioning
- [Caching](../architecture/caching-strategies-interview.md) — embedding cache
- [Микросервисы](../architecture/microservices-interview.md) — embedding services
- [PostgreSQL](../databases/postgresql-interview.md) — pgvector
- [Elasticsearch](../databases/elasticsearch-interview.md) — vector search в ES
- [Performance Testing](../performance/performance-testing-interview.md) — embedding latency
