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
updated: "2026-05-19"
---
# Вопросы на собеседовании: `Embeddings`

**Embedding** — преобразование текста (или изображения, аудио) в вектор чисел, где **семантически похожие** объекты имеют **близкие** векторы. Основа vector search, RAG, рекомендаций, кластеризации. На интервью спрашивают: как выбрать модель, dimensions, нормализацию, multilingual, fine-tuning, оптимизацию стоимости.

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

**Embedding** — числовое представление данных (текст, изображение, аудио) в виде **вектора** в многомерном пространстве.

```
"кошка"    → [0.12, -0.45, 0.78, ..., 0.03]  (1536 чисел)
"котёнок"  → [0.14, -0.43, 0.76, ..., 0.05]  (близкий к "кошка")
"автомобиль" → [-0.33, 0.51, 0.02, ..., 0.91] (далёкий от "кошка")
```

**Свойство:** семантически похожие → близкие векторы.

**Применения:**
- Семантический поиск (semantic search)
- RAG retrieval
- Кластеризация
- Рекомендации
- Поиск аномалий (anomaly detection)
- Классификация (embeddings + linear layer)


## Q2. (!) Зачем embeddings?

**Без embeddings** (BM25, поиск по ключевым словам):
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

**Embeddings улавливают семантику**, а не только синтаксис. Это революция в поиске и рекомендациях.


## Q3. (!) Как embedding model работает?

**Архитектура:** transformer (обычно encoder-only).

**Обучение:**
1. Берут пары семантически похожих текстов (или triplets)
2. Обучают модель так, чтобы их embeddings были близки
3. Negative samples — далёкие embeddings

**Loss-функции:**
- **Contrastive loss** — пары (похожие/непохожие)
- **Triplet loss** — anchor + positive + negative
- **InfoNCE** — multi-class contrastive

**Выход:** вектор нужного размера (обычно last hidden state с pooling — mean / `[CLS]` token).


## Q4. Sparse vs dense embeddings?

**Dense embeddings** — небольшая размерность, все измерения значимы (`[0.1, -0.5, 0.3, ...]`).
**Sparse embeddings** — большая размерность (размер словаря), большинство измерений = 0 (`[0, 0, 0.7, 0, 0.3, ...]`).

**Примеры sparse:**
- TF-IDF
- веса BM25
- SPLADE (современный sparse, обучаемый)

**Dense vs sparse:**
- Dense — лучше передаёт семантику, плохо работает на точных терминах
- Sparse — отлично подходит для точных терминов (коды, имена), хуже с семантикой

**Hybrid** = объединение обоих.


## Q5. (!) OpenAI text-embedding-3?

**OpenAI text-embedding-3** (2024):

| Модель | Размерность | Стоимость |
|-------|------|------|
| `text-embedding-3-small` | 1536 (можно truncate) | $0.02 / 1M tokens |
| `text-embedding-3-large` | 3072 (можно truncate) | $0.13 / 1M tokens |

**Особенности:**
- **Matryoshka** — можно обрезать размерность без сильной потери качества (1536 → 512 нормально)
- **Низкая цена** по сравнению с LLM API
- **Поддержка multilingual** (хорошо для популярных языков)
- контекст 8K (максимум на входе)

```python
response = client.embeddings.create(
    model="text-embedding-3-small",
    input="Hello, world!",
    dimensions=512  # truncate (matryoshka)
)
```

В 2025 — **выбор по умолчанию** для большинства случаев (лучший баланс).


## Q6. (!) Cohere embed-v3?

**Cohere Embed v3** (2023+):

| Модель | Размерность | Языки |
|-------|------|-----------|
| `embed-english-v3.0` | 1024 | английский |
| `embed-multilingual-v3.0` | 1024 | 100+ языков |

**Особенности:**
- **Учитывает тип входа** — `search_document`, `search_query`, `classification`, `clustering`
- Отличный multilingual
- Хорош для сценариев, заточенных под retrieval

```python
response = co.embed(
    texts=["Hello"],
    model="embed-english-v3.0",
    input_type="search_document"  # vs "search_query"
)
```

Часто **лучше OpenAI** на retrieval-бенчмарках.


## Q7. (!) Voyage AI?

**Voyage AI** — стартап, специализирующийся на embeddings. Рекомендуется Anthropic.

**Модели:**
- `voyage-large-2-instruct` — лучшее качество
- `voyage-code-2` — для кода
- `voyage-multilingual-2`
- `voyage-finance-2` — под конкретный домен

**Особенности:**
- Часто **state-of-the-art** на MTEB
- Модели под конкретные домены
- интеграция с Anthropic

В **2025** — топовый выбор для production RAG.


## Q8. (!) Sentence Transformers (open-source)?

**`sentence-transformers`** — Python-библиотека от UKP Lab.

```python
from sentence_transformers import SentenceTransformer
model = SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2')

embeddings = model.encode(["Hello", "World"])
# numpy array shape (2, 384)
```

**Популярные модели:**
- `all-MiniLM-L6-v2` (384d) — компактная, быстрая
- `all-mpnet-base-v2` (768d) — выше качество
- `multi-qa-mpnet-base-cos-v1` — для Q&A
- `paraphrase-multilingual-MiniLM-L12-v2` — multilingual

**Преимущества:**
- **Self-host** (приватность, отсутствие затрат на API)
- огромный выбор моделей на Hugging Face
- легко делать fine-tuning

**Недостатки:**
- по качеству хуже проприетарных (отстают от OpenAI/Cohere)
- для производительности нужны GPU


## Q9. BGE, E5 модели?

**BGE (BAAI General Embedding)** — open-source SOTA-модели от Beijing Academy of AI.

**Модели:**
- `BAAI/bge-large-en-v1.5` (1024d) — лучшая open-source модель для английского
- `BAAI/bge-m3` — multilingual, многофункциональная (dense + sparse + colbert)

**E5** — embeddings от Microsoft.
- `intfloat/multilingual-e5-large` — multilingual
- `intfloat/e5-mistral-7b-instruct` — крупная (7B), лучшее качество среди open-source

В **2025** качество open-source подтянулось к проприетарным решениям.


## Q10. (!) Как выбрать embedding model?

**Факторы выбора:**

1. **Качество:** проверить **MTEB leaderboard**
2. **Стоимость:** API с оплатой за токены vs self-host (GPU)
3. **Latency:** локально vs по сети
4. **Приватность:** API утекает данные → для PII нужен self-host
5. **Языки:** нужен ли multilingual?
6. **Домен:** общий vs специализированный (код, финансы, медицина)
7. **Размерность:** баланс между качеством и стоимостью хранения
8. **Длина контекста:** 512 токенов (BERT) vs 8K (OpenAI)

**Выбор по умолчанию на 2025:**
- **Production RAG, без требований к приватности:** Voyage / OpenAI text-embedding-3-large
- **Приватность:** BGE-M3, E5-large
- **Чувствительно к стоимости:** OpenAI text-embedding-3-small
- **Multilingual:** Cohere embed-multilingual-v3 / BGE-M3


## Q11. (!) MTEB benchmark?

**MTEB (Massive Text Embedding Benchmark)** — стандартный бенчмарк для embeddings. Leaderboard на Hugging Face.

**Задачи:**
- Retrieval (релевантность поиска)
- Классификация
- Кластеризация
- Reranking
- Pair classification
- STS (semantic textual similarity)

**56 датасетов, 8 типов задач**.

**Топ моделей на 2025:**
1. NV-Embed (NVIDIA)
2. Voyage 3
3. BGE-EN-ICL
4. E5-mistral-7b
5. text-embedding-3-large

**Подвох:** MTEB не идеален. Лучше тестировать на **своих данных**.


## Q12. (!) Какие dimensions выбрать?

**Trade-off:**
- **Малые (256-512):** быстро, дёшево по хранению, качество чуть ниже
- **Средние (768-1024):** стандарт для большинства open-source
- **Большие (1536-3072):** OpenAI, Cohere; больше места на хранение, качество чуть выше

**С Matryoshka embeddings (OpenAI v3, Nomic):**
```
Train на 3072d → truncate в любой меньший size без сильной потери
```

**Затраты памяти:**
```
1M vectors × 1536 dims × 4 bytes = 6 GB
1M vectors × 384 dims × 4 bytes  = 1.5 GB
```

**Best practice:** начинать с 512-1024d. Увеличивать только если нужно качество.


## Q13. (!) Normalization — нужна ли?

**Нормализация** — привести вектор к единичной длине (`||v|| = 1`).

```python
import numpy as np
normalized = vector / np.linalg.norm(vector)
```

**OpenAI, Cohere** — возвращают **уже нормализованные** векторы.
**Sentence Transformers** — обычно нет (нужно `normalize_embeddings=True`).

**Зачем:**
- **Cosine similarity = dot product** (вычисление быстрее)
- vector DB могут оптимизировать работу с нормализованными векторами

**Подвох:** если часть embeddings нормализована, а часть нет — поиск будет плохим. Согласованность критична.


## Q14. (!) Cosine similarity vs dot product?

```
cos(A, B) = (A · B) / (|A| × |B|)
```

Если **оба нормализованы** (`|A| = |B| = 1`):
```
cos(A, B) = A · B
```

**Dot product быстрее** (нет нормализации). Поэтому в production с нормализованными векторами используй метрику `dot product` в vector DB (выигрыш ~10-30% по latency).

```python
# В Pinecone, Weaviate, Qdrant
metric = "dot"  # вместо "cosine" если уже normalized
```


## Q15. Token limit для embedding?

**У каждой embedding model есть максимальная длина входа:**

| Модель | Макс. токенов |
|-------|-----------|
| OpenAI text-embedding-3 | 8191 |
| Cohere embed-v3 | 512 (!) |
| BGE-large | 512 |
| Voyage-large-2 | 16000 |
| Sentence Transformers (на базе BERT) | 512 (обычно) |

**Подвох с Cohere/BGE:** **512 tokens** = ~400 английских слов. Длинные документы нужно разбивать на чанки.


## Q16. Truncation длинных текстов?

Если текст > max_tokens:

1. **Truncate** — обрезать до максимума
2. **Chunk** — разбить на кусочки и embed каждый
3. **Сначала суммаризировать** (LLM) → embed summary
4. **Embedding pooling** — mean / max / первый чанк

**Best practice (RAG):**
- использовать **правильный chunking** на стадии препроцессинга (см. [RAG](rag-interview.md))
- каждый чанк влезает в лимит модели
- embed каждый чанк отдельно


## Q17. (!) Multilingual embeddings?

**Multilingual-модель** обучена на множестве языков и помещает разные языки в **одно общее пространство**.

```
emb("hello") ≈ emb("привет") ≈ emb("你好")
```

**Модели:**
- **Cohere embed-multilingual-v3** — 100+ языков
- **BGE-M3** — multilingual + многофункциональная
- **OpenAI text-embedding-3** — хорошо для популярных языков, хуже для низкоресурсных
- **paraphrase-multilingual-MiniLM** (sentence-transformers)

**Применения:**
- международные продукты
- cross-lingual search
- документы на корейском ↔ запрос на английском


## Q18. Cross-lingual search?

С multilingual embeddings можно искать запросом **на одном языке** документы **на другом**.

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

**Качество:** multilingual-модели обычно **немного хуже** монолингвальных на конкретном языке, но **намного лучше** в cross-lingual-задачах.


## Q19. (!) Image embeddings (CLIP)?

**CLIP (Contrastive Language-Image Pre-training)** — модель от OpenAI, помещающая **изображения и текст в одно общее пространство**.

```python
import clip
model, preprocess = clip.load("ViT-B/32")

image_features = model.encode_image(image)  # 512d
text_features = model.encode_text(["a photo of a cat"])

similarity = (image_features @ text_features.T)
```

**Применения:**
- **поиск изображений по тексту** ("найди фото кошки")
- **классификация изображений** через текстовые метки (zero-shot)
- **multi-modal RAG** (изображения + текст)

**Альтернативы:** SigLIP (качество выше), BLIP-2, OpenCLIP (open-source).


## Q20. Audio embeddings?

**Модели:**
- **Whisper embeddings** — для речи
- **CLAP** — text + audio (Contrastive Language-Audio Pretraining)
- **Wav2Vec 2.0** — для произвольного аудио
- **VGGish** — для музыкальных тегов

**Применения:**
- схожесть музыки / рекомендации
- embeddings для распознавания речи
- классификация аудио

Менее зрелое направление, чем текстовые embeddings.


## Q21. Multimodal embeddings?

**Multimodal embeddings** — единое пространство для **разных модальностей** (текст + изображение + видео).

**Модели на 2025:**
- **CLIP** (текст + изображение)
- **ImageBind** (Meta) — 6 модальностей (текст, изображение, аудио, видео, IMU, depth)
- **LanguageBind** — расширение ImageBind
- **Cohere Embed v3 multimodal** — текст + изображение (2024)

**Применения:**
- cross-modal search ("найди видео, похожее на этот текст")
- multimodal RAG

В **2025** — растущая область, но менее зрелая, чем text-only embeddings.


## Q22. (!) Cost optimization для embeddings?

1. **Выбрать правильную модель** — нужна ли large? Часто достаточно small.
2. **Использовать малую размерность** (Matryoshka) — 1536 → 512 даёт экономию ~3x
3. **Кешировать embeddings** — для того же текста → закешированный embedding
4. **Batch-запросы** — embedding API часто дешевле в batch
5. **Self-host** для больших объёмов (>10M embeddings/месяц)
6. **Асинхронная обработка** — embed ночью, а не в realtime
7. **Избегать повторного embedding** — если документ не изменился, не embed заново

**Пример стоимости:**
```
1M docs × avg 500 tokens × $0.02/1M tokens = $10
с text-embedding-3-small
```

Очень дёшево по сравнению с LLM API.



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
- повторяющиеся запросы (популярные поиски)
- A/B-тестирование разных моделей на одних и тех же запросах
- dev / staging окружения

**Не кешировать**, если:
- embeddings обновляются часто
- стоимость хранения > стоимости API (редко, обычно кеш дешёвый)


## Q24. Matryoshka embeddings (truncate dimensions)?

**Matryoshka representation learning (MRL)** — embeddings обучены так, что их **префиксы** тоже остаются валидными.

```
3072-dim embedding → truncate первые 512 dims → still useful (5-10% quality loss)
```

**Применение:**
- **многоуровневый поиск (tiered search)** — сначала поиск по 256d (быстро), затем rerank top-k по 1536d
- **экономия хранения** — в 3-5 раз

**Модели:** OpenAI text-embedding-3, Nomic embed.

```python
emb_full = openai.embeddings.create(model="text-embedding-3-large", input=text).data[0].embedding
# 3072 dims

emb_truncated = emb_full[:512]
# Можно использовать! Quality чуть ниже
```


## Q25. Quantization embeddings (binary, int8)?

Превращаем `float32` (4 байта на измерение) в:
- **fp16** — 2 байта на измерение (экономия 2x)
- **int8** — 1 байт на измерение (экономия 4x)
- **Binary** — 1 бит на измерение (экономия 32x)

**Binary quantization:**
```python
binary_vec = (vec > 0).astype(np.uint8)  # 1 bit per dim
# Compare через Hamming distance — XOR + popcount, очень быстро
```

**Trade-off:**
- Binary: ~5-10% потери recall, но в 32x быстрее и дешевле
- int8: ~1-2% потери, экономия 4x

В **2025** binary quantization стала популярной для **гигантских масштабов** (100M+ векторов).


## Q26. (!) Когда fine-tuning embedding model?

**Когда нужен:**
- **специфичная для домена** лексика (медицина, юриспруденция, код)
- **узкие задачи** (например, сопоставление тикетов поддержки)
- общие модели дают плохой recall на твоих данных

**Когда НЕ нужен:**
- типовой контент (новости, блоги, общие документы)
- маленький датасет (< 1000 примеров)
- нет ML-экспертизы

**Trade-off:** fine-tuning ~$500-5000 (обучение) + поддержка, против $0 при использовании готовой модели.


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

**Loss-функции:**
- **MultipleNegativesRankingLoss** — самая частая, использует in-batch negatives
- **TripletLoss** — anchor + positive + negative
- **CosineSimilarityLoss** — пары с similarity scores

**Подготовка данных:** основная работа — собрать **golden pairs** (query, relevant_doc).


## Q28. (!) Применения embeddings помимо RAG?

1. **Семантический поиск** — поиск по смыслу, а не по ключевым словам
2. **Рекомендации** — "похожие объекты" (Netflix, Amazon)
3. **Кластеризация** — группировка документов без разметки
4. **Поиск аномалий** — выбросы (outliers) в векторном пространстве
5. **Классификация** — embedding + linear classifier
6. **Дедупликация** — поиск почти дублирующихся документов
7. **Персонализация** — профиль пользователя как embedding (среднее по понравившимся объектам)
8. **Сопоставление A/B-вариантов**
9. **Обнаружение плагиата**
10. **Построение графа знаний** — entity linking


## Q29. Какие частые проблемы?

1. **Embedding drift** — модель обновили, embeddings стали другими → нужно re-embed всё
2. **Несовпадение моделей query/doc** — embed-нуты разными моделями
3. **Неверная метрика расстояния** — Euclidean для нормализованных векторов
4. **Плохой chunking** — embed целых документов вместо чанков
5. **Нет нормализации там, где она нужна**
6. **Короткие тексты** (1-2 слова) — плохие embeddings
7. **Неконтролируемые затраты** — embed всего подряд, без кеша
8. **Деградация качества** — для домена без fine-tuning
9. **Multilingual в монолингвальной модели** — плохое качество на других языках
10. **Восприятие embeddings как сжатия** — из embedding нельзя восстановить текст


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
