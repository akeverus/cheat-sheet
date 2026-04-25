---
title: "Вопросы на собеседовании: Embeddings"
description: "Embeddings: text/image/audio embedding models, dimensions, normalization, semantic similarity, choice of model (OpenAI, Cohere, sentence-transformers), fine-tuning, multilingual"
tags:
  - interview
  - ai-ml
  - embeddings-interview
aliases:
  - "Embeddings interview"
  - "Embedding models interview"
  - "Sentence embeddings interview"
  - "Vector embeddings interview"
difficulty: "intermediate"
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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. (!) Зачем embeddings?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. (!) Как embedding model работает?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Sparse vs dense embeddings?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. (!) OpenAI text-embedding-3?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. (!) Cohere embed-v3?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. (!) Voyage AI?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. (!) Sentence Transformers (open-source)?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. BGE, E5 модели?

**BGE (BAAI General Embedding)** — open-source SOTA models от Beijing Academy of AI.

**Models:**
- `BAAI/bge-large-en-v1.5` (1024d) — top open-source English
- `BAAI/bge-m3` — multilingual, multi-functional (dense + sparse + colbert)

**E5** — Microsoft embeddings.
- `intfloat/multilingual-e5-large` — multilingual
- `intfloat/e5-mistral-7b-instruct` — large (7B), best open-source quality

В **2025** open-source качество подтянулось к proprietary.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. (!) Как выбрать embedding model?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. (!) MTEB benchmark?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. (!) Какие dimensions выбрать?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. (!) Normalization — нужна ли?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. (!) Cosine similarity vs dot product?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Token limit для embedding?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q16. Truncation длинных текстов?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q17. (!) Multilingual embeddings?

**Multilingual model** обучена на много языках, embeds разные языки в **одно общее space**.

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q18. Cross-lingual search?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q19. (!) Image embeddings (CLIP)?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q20. Audio embeddings?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q21. Multimodal embeddings?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q22. (!) Cost optimization для embeddings?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q23. (!) Caching embeddings?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q24. Matryoshka embeddings (truncate dimensions)?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q25. Quantization embeddings (binary, int8)?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q26. (!) Когда fine-tuning embedding model?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q27. (!) Как fine-tune (sentence transformers)?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q28. (!) Применения embeddings помимо RAG?

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
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q29. Какие частые проблемы?

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


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление- [AI Agents](ai-agents-interview.md)
- [LLM Basics](llm-basics-interview.md)
- [LLM Integration Patterns](llm-integration-patterns-interview.md)
- [MLOps](mlops-interview.md)
- [Model Serving](model-serving-interview.md)
- [Prompt Engineering](prompt-engineering-interview.md)
