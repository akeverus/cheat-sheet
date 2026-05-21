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
mcq_format_version: 2
updated: "2026-05-19"
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


> [!mcq] Что такое embedding в контексте ML/NLP?
>
> - [ ] A. Сжатое бинарное представление текста для экономии памяти при хранении.
>
>     **Что на самом деле.** Embedding — dense float-вектор (обычно 384–3072 чисел float32), а не битовая компрессия. Цель — не уменьшить размер, а закодировать семантику: близкие по смыслу тексты получают близкие векторы в L2/cosine метрике.
>
>     **Откуда путаница.** Слово «вектор чисел» ассоциируется с бинарным форматом, плюс есть отдельная техника binary quantization (1 bit/dim), которая действительно сжимает embeddings — но это пост-обработка, а не суть embedding.
>
>     **Если бы это было правдой.** Тогда `emb("кошка")` и `emb("котёнок")` были бы случайными битовыми строками без cosine similarity ≈ 0.9 — и весь RAG/semantic search не работал бы.
>
>     **Как было бы правильно.** Сказать «dense вектор фиксированной размерности, где геометрическая близость отражает семантическую близость», и отдельно упомянуть quantization как способ компрессии готовых embeddings.
>
> - [ ] B. Числовое ID каждого слова в словаре (one-hot encoding) длины vocabulary.
>
>     **Что на самом деле.** Embedding — это противоположность one-hot: dense low-dim (256–3072) вместо sparse high-dim (30K+ нулей с одной единицей). One-hot был предшественником, который заменили именно потому что он не несёт семантики.
>
>     **Откуда путаница.** В старых NLP-курсах (до 2013) one-hot называли «word representation», и термин «embedding» иногда путают со словарным индексом. Word2Vec (2013) и BERT (2018) окончательно сместили смысл на dense vectors.
>
>     **Если бы это было правдой.** `cos(one_hot("кошка"), one_hot("котёнок")) = 0` — два разных слова всегда ортогональны. Semantic search вернул бы только точные совпадения слова, что хуже даже BM25.
>
>     **Как было бы правильно.** One-hot — это lookup-индекс; embedding — это **обученное** dense-представление, где близость отражает семантику.
>
> - [x] C. Числовой вектор фиксированной размерности в многомерном пространстве, где семантически похожие элементы имеют геометрически близкие векторы.
>
>     **Развёрнутое объяснение.** Embedding — output обученной нейросети (обычно encoder-only transformer типа BERT/sentence-transformers), которая отображает текст/картинку/звук в `R^d` (d = 384–3072). Близость измеряется cosine similarity или dot product. Свойство «семантически похожее → близкое» получается обучением на контрастивных парах: similar pairs притягиваются, dissimilar отталкиваются. Это база semantic search, RAG retrieval, recommendations, clustering, anomaly detection.
>
>     **Пример.** OpenAI `text-embedding-3-small` (1536d): `emb("горячий напиток")` и `emb("чашка кофе бодрит утром")` дают cosine ≈ 0.78, тогда как `emb("горячий напиток")` и `emb("ремонт автомобиля")` ≈ 0.12. Pinecone/Qdrant хранят такие векторы и за миллисекунды находят top-K ближайших среди миллионов.
>
>     **Когда применять.** Везде, где «найти похожее по смыслу, а не по словам»: RAG-индексы (Notion AI, Perplexity), product recommendations (Amazon, Spotify «discover weekly»), customer support deflection (matching ticket → KB article), deduplication, clustering пользовательских запросов.
>
>     **Подводные камни.** Короткие тексты (1–2 слова) дают шумные embeddings — модель обучена на предложениях. Embedding-space модели несовместимы между собой: нельзя смешать OpenAI и Cohere векторы в одном индексе. При смене модели (ada-002 → text-embedding-3) нужен полный re-embed корпуса.
>
>     **Связанные вопросы.** [[embeddings-interview#Q2]] зачем embeddings vs BM25; [[embeddings-interview#Q3]] как обучается модель; [[embeddings-interview#Q13]] normalization и cosine.
>
> - [ ] D. Hash-функция текста (MD5/SHA) для быстрого поиска точных совпадений строк.
>
>     **Что на самом деле.** Hash-функции спроектированы так, чтобы малейшее изменение входа полностью меняло выход (avalanche effect) — это противоположность embedding, где небольшая семантическая разница даёт небольшое смещение вектора.
>
>     **Откуда путаница.** И hash, и embedding превращают строку в фиксированный массив байт — на этом сходство кончается. Locality-sensitive hashing (LSH) частично сближает эти миры, но это специальная техника поверх embeddings, а не сам embedding.
>
>     **Если бы это было правдой.** `hash("cat") = 9e1...` и `hash("cats") = 3f8...` — никакой близости. Semantic поиск по фразе «домашнее животное» не нашёл бы ни «кошка», ни «собака», только точное вхождение слова.
>
>     **Как было бы правильно.** Hash — для exact match и дедупликации байт; embedding — для approximate semantic match. Они решают разные задачи и не взаимозаменяемы.

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


> [!mcq] Зачем нужны embeddings, если есть проверенный BM25/full-text search?
>
> - [ ] A. BM25 keyword search достаточен для любой задачи поиска — embeddings не дают принципиально нового.
>
>     **Что на самом деле.** BM25 работает только на лексическом уровне: матчит слова и их частотные веса. Синонимы, парафразы, переводы и другие формулировки одного и того же смысла дают нулевой score. Embeddings закрывают именно этот gap — семантический recall.
>
>     **Откуда путаница.** BM25 действительно достаточен для специфичных доменов (поиск по артикулам, GUID, точным фразам в логах) и долго был дефолтом в Elasticsearch/Solr. Инженеры, выросшие на этих стеках, склонны переоценивать его универсальность.
>
>     **Если бы это было правдой.** Тогда Google после 2019 (BERT в ranking) и Microsoft Bing (2023, GPT-rewrite) не потратили бы миллиарды на neural retrieval — лексический подход был бы достаточен.
>
>     **Как было бы правильно.** BM25 — отличный baseline для exact term matching; embeddings добавляют слой семантики поверх. Production-системы используют hybrid (BM25 + dense) через RRF/weighted fusion.
>
> - [x] B. Embeddings позволяют сравнивать смысл текстов независимо от слов — синонимы, парафразы и разные языки попадают в одно пространство.
>
>     **Развёрнутое объяснение.** Embedding-модель обучена так, что `emb("горячий напиток") ≈ emb("чашка кофе") ≈ emb("hot drink")` по cosine similarity. BM25 на тех же запросах вернёт 0 совпадений, потому что нет общих токенов. Это principal advantage: semantic recall. На практике production-системы используют hybrid (BM25 + embeddings + reranker), потому что embeddings лучше для парафраза, а BM25 — для точных терминов (артикулы, имена собственные, code).
>
>     **Пример.** Stack Overflow для своего AI-поиска (2024) переехал с pure BM25 на hybrid: dense embedding снимает 40% запросов, где формулировка пользователя сильно отличается от заголовков вопросов («error 500» → «server keeps crashing under load»). Recall@10 вырос с 0.62 до 0.84.
>
>     **Когда применять.** RAG-системы (ChatGPT custom GPTs, Notion AI), product search в e-commerce (Wolt, Lavka), customer support deflection, recommendations, FAQ-bots. Везде, где пользователь формулирует запрос «по-своему», а корпус — формальным языком.
>
>     **Подводные камни.** Pure dense поиск проседает на exact terms (артикулы, версии, hash-ID) — `iPhone 15 Pro Max 256GB` может вернуть «похожие смартфоны» вместо точной модели. Решение — hybrid поиск. Также embeddings зависимы от языка обучения: на низко-ресурсных языках качество резко падает.
>
>     **Связанные вопросы.** [[embeddings-interview#Q4]] sparse vs dense; [[embeddings-interview#Q11]] MTEB benchmark; [[embeddings-interview#Q22]] стоимость и cost-optimization.
>
> - [ ] C. Embeddings нужны только для ускорения поиска по сравнению с BM25, качество ровно то же.
>
>     **Что на самом деле.** Embeddings, наоборот, обычно **медленнее** BM25: dense ANN-поиск с HNSW делает 10–50ms на миллионе векторов, тогда как BM25 в Lucene — 1–5ms. Преимущество embeddings — качество (recall на парафразах), а не скорость.
>
>     **Откуда путаница.** «Новая технология = быстрее старой» — частый стереотип. Плюс vector DB маркетируется как «миллион запросов в секунду», что создаёт иллюзию выигрыша по latency.
>
>     **Если бы это было правдой.** Никто бы не строил hybrid поиск — все мигрировали бы с BM25 на dense ради скорости. Реальность: hybrid существует именно потому, что dense даёт качество, а BM25 — скорость и точность на exact terms.
>
>     **Как было бы правильно.** Embeddings улучшают recall на семантически близких документах ценой большего CPU/memory; ускорение — не их цель, а иногда побочный эффект ANN-индексации.
>
> - [ ] D. Embeddings заменяют SQL full-text search исключительно ради лучшей производительности на больших таблицах.
>
>     **Что на самом деле.** SQL FTS (PostgreSQL `tsvector`, MySQL `FULLTEXT`) и embeddings решают разные задачи: FTS — это специализированный BM25/TF-IDF для exact/prefix term matching внутри RDBMS; embeddings — для семантической близости. Их не заменяют, а сочетают.
>
>     **Откуда путаница.** pgvector в PostgreSQL дал ощущение «теперь FTS не нужен — vector внутри той же БД». Но pgvector — это про cosine similarity, а не про токенизацию и весовую модель BM25.
>
>     **Если бы это было правдой.** Запрос вида `SELECT * WHERE document @@ to_tsquery('error & 500')` нельзя было бы выразить через embeddings — нужен exact term match. Без FTS пришлось бы делать full table scan + LIKE, что катастрофично по latency.
>
>     **Как было бы правильно.** FTS + pgvector дополняют друг друга: FTS для precise term queries, embeddings для semantic recall. Production — hybrid через `tsvector @@ query OR embedding <=> query`.

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


> [!mcq] Как устроена и обучается типичная embedding-модель?
>
> - [ ] A. Decoder-only transformer (GPT-style), обученный задачей next-token prediction на большом корпусе.
>
>     **Что на самом деле.** Decoder-only оптимизирован под генерацию, а не под representation. Он видит контекст только слева (causal mask) и предсказывает следующий токен. Embedding-модели обычно encoder-only (BERT-style) — bidirectional self-attention, видит весь текст сразу, что лучше для смыслового сжатия в один вектор.
>
>     **Откуда путаница.** В 2024+ появились decoder-based embedders (E5-Mistral-7B, NV-Embed): берут decoder-LLM, добавляют contrastive fine-tuning. Это работает, но не «next-token prediction», а специальный pooling + contrastive loss поверх.
>
>     **Если бы это было правдой.** Тогда GPT-4 «из коробки» был бы топом MTEB. На практике GPT-4 без специальной адаптации даёт посредственные embeddings — нужна отдельная adaptation phase.
>
>     **Как было бы правильно.** Encoder-only (BERT, MPNet) с contrastive loss — классика; decoder-based embedders — новая ветвь, но требует contrastive fine-tuning, а не голого LM-обучения.
>
> - [ ] B. Bag-of-words модель с TF-IDF весами — простое статистическое усреднение по словам.
>
>     **Что на самом деле.** TF-IDF — это классический pre-neural подход. Современные embedding-модели — обученные нейросети, которые учитывают порядок слов, контекст, отрицания. «not good» и «good» в TF-IDF почти идентичны, в BERT-embeddings — противоположны.
>
>     **Откуда путаница.** TF-IDF исторически назывался «word embedding» в старых учебниках (до 2013). С появлением Word2Vec/GloVe термин закрепился за dense neural representations.
>
>     **Если бы это было правдой.** На MTEB benchmark TF-IDF давал бы топ-результаты, а на практике он отстаёт от BERT-моделей на 30–50 пунктов NDCG.
>
>     **Как было бы правильно.** TF-IDF — sparse statistical baseline, embedding-модели — dense neural representations с контекстом и порядком слов.
>
> - [x] C. Encoder-only transformer (BERT-подобный), обучается contrastive loss на парах похожих/непохожих текстов; финальный вектор получают pooling-ом последнего hidden state.
>
>     **Развёрнутое объяснение.** Архитектура — Transformer encoder со self-attention (bidirectional). На вход — токены, на выход — последовательность hidden states. Pooling (mean over tokens или CLS-token) сжимает их в один вектор `R^d`. Обучение в два этапа: (1) MLM-pretraining как у BERT, (2) contrastive fine-tuning — модель видит pairs `(anchor, positive)` и должна сделать их близкими, а случайные negatives из batch — далёкими. Loss-функции: InfoNCE, MultipleNegativesRankingLoss, TripletLoss. Triplet `(anchor, positive, negative)` — anchor притягивается к positive, отталкивается от negative с margin.
>
>     **Пример.** `sentence-transformers/all-mpnet-base-v2`: MPNet-base (110M params), pretrain → contrastive fine-tune на ~1B пар из Reddit, StackExchange, S2ORC. Pooling = mean. Output — 768d вектор. Cohere embed-v3 и OpenAI text-embedding-3 — proprietary архитектуры того же класса с большим масштабом и asymmetric query/document обучением.
>
>     **Когда применять.** Понимание архитектуры нужно при выборе модели (decoder-based E5-Mistral мощнее, но требует GPU), при fine-tuning (нужны golden pairs для contrastive loss), при отладке (короткие тексты дают шумный pooled vector, потому что mean-pool усредняет почти ничего).
>
>     **Подводные камни.** Pooling важен: mean vs [CLS] vs max дают разные embeddings — нельзя смешивать в одном индексе. Asymmetric модели (Cohere, E5) требуют разный input_type для query и document — игнорирование роняет recall на 10–20%. Long-context модели (8K у OpenAI) реально дают качественный embedding только до ~512 токенов — дальше mean-pool «размывает» смысл.
>
>     **Связанные вопросы.** [[embeddings-interview#Q5]] OpenAI text-embedding-3; [[embeddings-interview#Q8]] sentence-transformers; [[embeddings-interview#Q27]] как fine-tune.
>
> - [ ] D. CNN с max-pooling над character-level n-grams без attention-механизма.
>
>     **Что на самом деле.** Char-CNN embedders (FastText, 2016) — pre-Transformer era. Они хороши для morphologically rich языков (русский) и OOV-слов, но не понимают семантику предложений. Современные embedding-модели — Transformer-based.
>
>     **Откуда путаница.** FastText от Facebook был популярен 2016–2019 и часто упоминался как «word embedding». Но это word-level, не sentence-level, и не SOTA в 2025.
>
>     **Если бы это было правдой.** «кошка» и «котёнок» имеют общие n-grams (`кот`, `ошк`), но «кошка» и «кот» (разные семантические оттенки) тоже имели бы высокое similarity без понимания контекста.
>
>     **Как было бы правильно.** Char-CNN — это историческая ветвь word embeddings; современные sentence embeddings — Transformer encoders с attention и contrastive обучением.

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


> [!mcq] В чём разница между sparse и dense embeddings и когда какой использовать?
>
> - [ ] A. Dense embeddings строго лучше sparse во всех сценариях, sparse — устаревшая технология.
>
>     **Что на самом деле.** Sparse (BM25, SPLADE) живее всех живых: на запросах с exact terms (артикулы `SKU-12345`, имена `Sergey Voronin`, версии `Java 21`) sparse даёт near-perfect precision, dense — нет. Hybrid (dense + sparse + reranker) — production-стандарт в Pinecone, Qdrant, Weaviate.
>
>     **Откуда путаница.** Маркетинг vector DB в 2022–2023 продвигал «dense побеждает BM25», что верно для парафразов и не верно для exact match. К 2024 индустрия признала hybrid.
>
>     **Если бы это было правдой.** Stack Overflow, GitHub Search, Amazon product search, юридический e-discovery — все они полностью отказались бы от BM25. На практике они все используют hybrid.
>
>     **Как было бы правильно.** Dense для семантики, sparse для exact match; hybrid комбинирует их через RRF (reciprocal rank fusion) или weighted scores.
>
> - [ ] B. Sparse embeddings занимают меньше памяти на диске, чем dense, из-за меньшей размерности.
>
>     **Что на самом деле.** Размерность sparse-вектора — это размер словаря (~30K–100K), а dense — 256–3072. На бумаге sparse гигантский. Но sparse хранятся в compressed indexes (Lucene posting lists, COO/CSR), где хранятся только non-zero значения — типично 10–100 ненулей на документ. На практике это сопоставимо с dense, иногда меньше.
>
>     **Откуда путаница.** «Меньше = меньше памяти» — наивная интуиция. Реальность зависит от sparsity rate и формата хранения.
>
>     **Если бы это было правдой.** Lucene inverted index был бы тривиально маленьким, но он реально занимает 30–60% от raw text size из-за term dictionaries и positional info.
>
>     **Как было бы правильно.** Sparse сравним с dense по storage, но требует другого storage engine (inverted index vs vector index с HNSW/IVF).
>
> - [x] C. Dense — малая размерность, все значимы, силён в семантической близости; sparse — большая размерность с почти всеми нулями, силён в exact match; production обычно строится как hybrid.
>
>     **Развёрнутое объяснение.** Dense embedding (`[0.12, -0.45, ..., 0.03]`, 768d) — каждое из 768 измерений несёт информацию, обучен contrastive loss-ом ловить семантику. Sparse embedding (`[0, 0, 0.7, 0, ..., 0.3, 0]`, 30K dims) — большинство нулей, ненулевые позиции — term importance scores. Классический sparse — BM25; modern learned sparse — SPLADE (BERT, который генерирует term weights). Hybrid: для запроса считаем оба score, объединяем через RRF (rank-based) или weighted (`alpha*dense + (1-alpha)*sparse`).
>
>     **Пример.** Weaviate hybrid search: для запроса «Capital One data breach 2019» dense embedding находит документы про «cybersecurity incidents at banks», sparse BM25 находит точные упоминания «Capital One» и «2019». RRF объединяет: документ с обоими сигналами получает топ rank. Recall@10 hybrid обычно на 15–25% выше pure dense.
>
>     **Когда применять.** Production RAG, e-commerce search (где есть и парафраз «удобный кофе», и точный артикул `iPhone 15 Pro Max 256GB`), legal/medical search (термины + смысл), code search (имена функций + семантика). Pure dense — для чистого парафразного поиска (FAQ-боты). Pure sparse — для точного поиска кодов/ID.
>
>     **Подводные камни.** Hybrid требует tuning alpha (вес dense vs sparse) на eval-датасете — нет универсального значения. SPLADE — learned sparse, требует GPU при inference (медленнее BM25 в 10×). RRF parameter `k=60` — не нужно менять без серьёзных причин; weighted-fusion более гибок, но капризнее.
>
>     **Связанные вопросы.** [[embeddings-interview#Q2]] зачем embeddings vs BM25; [[embeddings-interview#Q11]] MTEB benchmark; [[embeddings-interview#Q22]] cost optimization.
>
> - [ ] D. Sparse embeddings — это только BM25 и TF-IDF, нейросетевых sparse-моделей не существует.
>
>     **Что на самом деле.** SPLADE (Stanford/Naver, 2021) — нейросетевая sparse-модель: BERT генерирует term weights с regularization для разреженности. Есть также uniCOIL, DeepImpact. Они дают точность BM25 на exact terms + добавляют семантическую gen­ерализацию (расширяют запрос синонимами в sparse space).
>
>     **Откуда путаница.** Sparse исторически ассоциируется с pre-neural NLP (TF-IDF, BM25). Learned sparse — относительно молодое направление (2020+), менее на слуху.
>
>     **Если бы это было правдой.** На MTEB benchmark SPLADE не было бы в топах retrieval — а оно там стабильно занимает места 5–15.
>
>     **Как было бы правильно.** Sparse делится на statistical (BM25, TF-IDF) и learned (SPLADE, uniCOIL). Learned sparse — middle ground между BM25 и dense.

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


> [!mcq] Какое утверждение про линейку OpenAI text-embedding-3 верно в 2025 году?
>
> - [ ] A. `text-embedding-ada-002` остаётся актуальной production-моделью OpenAI и официально рекомендуется по умолчанию.
>
>     **Что на самом деле.** Ada-002 (2022) официально legacy с релиза text-embedding-3 в январе 2024. Новая линейка дешевле, качественнее, поддерживает Matryoshka truncation. OpenAI рекомендует мигрировать.
>
>     **Откуда путаница.** Ada-002 была дефолтом 1.5 года и попала во множество туториалов, курсов и blog-постов, которые до сих пор первыми выдаются в поиске. Инженеры копируют код из старых статей.
>
>     **Если бы это было правдой.** Pricing page OpenAI продолжала бы рекомендовать ada-002, а MTEB оценивал бы её как top. Реально — она ниже text-embedding-3-small в MTEB и в 2.5× дороже за токен.
>
>     **Как было бы правильно.** Для нового проекта — `text-embedding-3-small` как baseline; `text-embedding-3-large` если small не хватает. Ada-002 — только если ради совместимости со старым индексом.
>
> - [ ] B. Для production RAG обязательно нужен `text-embedding-3-large` — small не годится из-за низкого качества.
>
>     **Что на самом деле.** `text-embedding-3-small` (1536d, $0.02/1M tokens) уступает large всего на 3–7% по MTEB, но в 6.5× дешевле. Для большинства RAG-задач разница в quality незаметна на пользовательском уровне, особенно при наличии reranker.
>
>     **Откуда путаница.** «Large = лучше» — наивная интуиция. Менеджеры/архитекторы без бенчмаркинга часто выбирают самую дорогую модель «для надёжности».
>
>     **Если бы это было правдой.** Стартапы с миллионами документов разорились бы на embedding-биллах. Реально — большинство выбирают small + reranker и держат cost под контролем.
>
>     **Как было бы правильно.** Начинать с small, мерить retrieval-метрики на своих данных; апгрейдиться до large, только если small демонстрирует измеримый recall gap.
>
> - [x] C. Линейка text-embedding-3 включает small (1536d, $0.02/1M) и large (3072d, $0.13/1M) с поддержкой Matryoshka truncation и контекстом 8191 токенов.
>
>     **Развёрнутое объяснение.** OpenAI text-embedding-3 (январь 2024) — два варианта: small (max 1536d) и large (max 3072d). Оба возвращают L2-normalized векторы (||v||=1), что позволяет использовать dot product вместо cosine. Matryoshka representation learning (MRL) позволяет `dimensions=512` параметром в API — модель truncate'нет вектор, потеряв ~5–10% качества при 3–6× экономии storage. Контекст 8191 токен, цена small — $0.02 за миллион токенов, large — $0.13. Embeddings детерминированы для одного входа.
>
>     **Пример.** Notion AI и Perplexity для своих RAG-pipeline'ов используют text-embedding-3-small с `dimensions=512` — это даёт `1M docs × 512d × 4 bytes = 2GB` индекс вместо 12GB на full 3072d. На retrieval-метриках разница ~3%, что компенсируется reranker'ом (Cohere Rerank или voyage-rerank-2).
>
>     **Когда применять.** Default-выбор для non-privacy RAG, semantic search, recommendations, deduplication. Особенно когда нужен `8K context` — другие модели (Cohere, BGE) ограничены 512 токенов. Если нужен self-host для PII — берите BGE-M3 или E5.
>
>     **Подводные камни.** API rate limits: 5M tokens/min на tier 4 — для индексирования 100M документов нужны несколько часов. Truncation через `dimensions` параметр в API работает только для text-embedding-3, на ada-002 не сработает. Embeddings нормализованы автоматически — дополнительная `np.linalg.norm` лишняя, но не вредная.
>
>     **Связанные вопросы.** [[embeddings-interview#Q10]] как выбрать модель; [[embeddings-interview#Q12]] какие dimensions; [[embeddings-interview#Q24]] Matryoshka.
>
> - [ ] D. OpenAI embeddings возвращают ненормализованные векторы и требуют ручной нормализации перед сохранением в vector DB.
>
>     **Что на самом деле.** OpenAI embeddings API возвращает L2-normalized векторы (||v||=1) для всех моделей линейки text-embedding-3 и ada-002. Это документировано, и можно проверить: `np.linalg.norm(emb) ≈ 1.0`.
>
>     **Откуда путаница.** Sentence-transformers по умолчанию НЕ нормализует (нужен `normalize_embeddings=True`). Инженеры переносят эту привычку на OpenAI и пишут лишний код.
>
>     **Если бы это было правдой.** Cosine и dot product в Pinecone/Qdrant давали бы разные результаты для OpenAI embeddings. На практике они идентичны — потому что векторы уже unit-length.
>
>     **Как было бы правильно.** Не нормализовать OpenAI embeddings повторно (идемпотентно, но избыточно); использовать `metric="dotproduct"` в vector DB для скорости.

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


> [!mcq] Какая ключевая особенность отличает Cohere embed-v3 от других embedding API?
>
> - [ ] A. Cohere embed-v3 принимает одинаковый input_type для query и document — это упрощает интеграцию.
>
>     **Что на самом деле.** Главная фича Cohere embed-v3 — именно asymmetric API: разные эндпоинты для query и document. `input_type="search_query"` для пользовательского запроса, `"search_document"` для индексируемых документов, `"classification"`/`"clustering"` для других задач. Модель обучена разделять эти роли.
>
>     **Откуда путаница.** OpenAI и Sentence Transformers по умолчанию используют один режим для query и document — это symmetric setup. Инженеры, привыкшие к нему, упускают параметр Cohere.
>
>     **Если бы это было правдой.** Тогда `input_type` был бы no-op параметром. Эксперименты показывают: правильный input_type повышает NDCG@10 на 5–15% против использования одного режима для обоих.
>
>     **Как было бы правильно.** При indexing передавать `search_document`, при поиске — `search_query`. Это инвариант для всей линейки Cohere embed v3+.
>
> - [x] B. Cohere embed-v3 — asymmetric модель с input_type API (`search_query`/`search_document`/`classification`/`clustering`), сильна в multilingual и retrieval-задачах.
>
>     **Развёрнутое объяснение.** Cohere embed-v3 (октябрь 2023) — 1024d векторы, 512-токенный контекст. Ключевая особенность — task-aware encoding через `input_type`. Модель тренировалась с asymmetric contrastive loss: query и document проходят через немного разные «головы», что улучшает retrieval. Два варианта: `embed-english-v3.0` для англоязычных корпусов и `embed-multilingual-v3.0` для 100+ языков (включая русский, китайский, арабский). По MTEB retrieval часто обходит OpenAI text-embedding-3-large при меньшей размерности.
>
>     **Пример.** Notion при выборе embedding-провайдера для AI search в 2024 сравнивал OpenAI text-embedding-3-large и Cohere embed-multilingual-v3. На корпусе из EN+JA+DE документов Cohere дал на 8% выше recall@10 при правильном использовании input_type. Финальный пайплайн: `cohere.embed(texts=docs, input_type="search_document")` при индексировании, `input_type="search_query"` при поиске.
>
>     **Когда применять.** Multilingual RAG-системы (международные продукты с EN+RU+ZH+DE документами), retrieval-first задачи, корпуса с асимметричным query/document gap (короткие запросы пользователей vs длинные документы). Также Cohere предлагает Rerank API в той же линейке — удобная end-to-end интеграция.
>
>     **Подводные камни.** 512-токенный лимит — половина страницы текста; длинные документы требуют chunking перед embedding. Asymmetric setup означает: при миграции с symmetric модели нужно полностью переиндексировать корпус с правильным `input_type`. `embed-v3.0` несовместим с `embed-v2.0` — разные embedding spaces.
>
>     **Связанные вопросы.** [[embeddings-interview#Q10]] выбор модели; [[embeddings-interview#Q15]] token limits; [[embeddings-interview#Q17]] multilingual.
>
> - [ ] C. Cohere embed-v3 поддерживает только английский язык — для других нужны отдельные специализированные модели.
>
>     **Что на самом деле.** Линейка включает `embed-multilingual-v3.0` с поддержкой 100+ языков в едином embedding space. Английская модель — отдельный вариант для случаев, когда корпус строго EN и нужно максимальное качество.
>
>     **Откуда путаница.** Многие API имеют отдельные модели на каждый язык (например, Whisper для разных языков). Embedding-модели часто либо EN-only, либо multilingual-only — Cohere предлагает оба варианта.
>
>     **Если бы это было правдой.** Многоязычные продукты (Wolt, Booking.com) не могли бы использовать Cohere для cross-lingual search. На практике Cohere multilingual — один из лидеров для таких задач.
>
>     **Как было бы правильно.** Выбирать `embed-english-v3.0` для EN-only корпуса (чуть выше качество), `embed-multilingual-v3.0` — для multilingual или cross-lingual.
>
> - [ ] D. Параметр input_type в Cohere влияет только на логирование/биллинг и не меняет получаемый вектор.
>
>     **Что на самом деле.** Параметр фундаментально меняет внутренний обработку: модель применяет разную головку (или prefix-токен) для разных типов. Векторы для `input_type="search_query"` и `input_type="search_document"` для одного и того же текста — разные, не совпадают.
>
>     **Откуда путаница.** Некоторые API имеют декоративные параметры (типа `user` в OpenAI — только для abuse-detection). Инженеры экстраполируют это на Cohere.
>
>     **Если бы это было правдой.** A/B-тест с одинаковым и правильным input_type давал бы идентичный recall. Реально — разница 5–15% NDCG@10.
>
>     **Как было бы правильно.** Считать `input_type` обязательным параметром, не опциональным; задокументировать в pipeline какой тип где используется.

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


> [!mcq] Что верно характеризует Voyage AI как embedding-провайдера?
>
> - [ ] A. Voyage AI — это product Anthropic, поставляется внутри Claude API и оплачивается через Anthropic billing.
>
>     **Что на самом деле.** Voyage AI — независимый стартап (Стэнфорд, основан 2023), специализирующийся на embedding-моделях. Anthropic рекомендует Voyage как партнёрский embedding-провайдер для построения RAG поверх Claude, но это отдельная компания, отдельные API-ключи, отдельный billing на `voyageai.com`.
>
>     **Откуда путаница.** Anthropic активно продвигает Voyage в документации Claude как «recommended embedding provider», это создаёт впечатление product bundle. Но Anthropic Inc. не владеет Voyage AI.
>
>     **Если бы это было правдой.** Tokens для embedding списывались бы с Anthropic-аккаунта, и embedding-эндпоинт жил бы на `api.anthropic.com`. Реально — отдельный URL `api.voyageai.com` и отдельный API-key.
>
>     **Как было бы правильно.** «Voyage AI — независимый стартап, партнёр Anthropic; для использования нужны отдельный аккаунт и API-key, оплата отдельная».
>
> - [x] B. Voyage AI — независимый стартап с SOTA-моделями на MTEB и domain-specific вариантами (code, finance, multilingual); официально рекомендуется Anthropic как RAG-провайдер.
>
>     **Развёрнутое объяснение.** Voyage AI выпускает линейку embedding-моделей: `voyage-3` (general purpose, 1024d), `voyage-3-large` (high quality, 2048d), `voyage-code-3` (для кода), `voyage-finance-2` (финансовые документы), `voyage-multilingual-2` (мультиязычный). Топ-3 на MTEB retrieval benchmark в 2025. Контекст 16K–32K токенов — больше чем у Cohere (512) и OpenAI (8K). Также есть Voyage Rerank (`rerank-2`) для двухстадийного retrieval. Anthropic рекомендует Voyage в Claude docs как embedding-партнёра, но это отдельный продукт.
>
>     **Пример.** Harvey AI (legal tech startup) построил свою RAG-систему для юридических документов на Voyage AI: `voyage-finance-2` для финансовых отчётов клиентов и `voyage-large-2-instruct` для договоров. Получили на 12% выше precision@5 против text-embedding-3-large на их eval-датасете.
>
>     **Когда применять.** Production RAG, где важна максимальная quality и нужна domain-специализация: code search (GitHub Copilot–level retrieval), финансовые отчёты, legal docs. Также если нужен длинный контекст (16K+) для embedding целых страниц без агрессивного chunking. Если строите на Claude — Voyage официально интегрирован в Anthropic cookbook'и.
>
>     **Подводные камни.** Дороже OpenAI (`voyage-3-large` ≈ $0.12/1M tokens vs text-embedding-3-small $0.02). Меньшая экосистема: библиотеки и интеграции отстают от OpenAI. Rate limits ниже на free tier. Domain-specific модели несовместимы между собой — нельзя смешать `voyage-code-3` и `voyage-finance-2` в одном индексе.
>
>     **Связанные вопросы.** [[embeddings-interview#Q10]] выбор модели; [[embeddings-interview#Q11]] MTEB benchmark; [[embeddings-interview#Q15]] token limits.
>
> - [ ] C. Voyage AI поддерживает только text-модальность и не имеет domain-специализированных моделей (code, finance).
>
>     **Что на самом деле.** Voyage — пионер domain-specific embedding-моделей: `voyage-code-3` (для retrieval по исходному коду), `voyage-finance-2` (для финансовых отчётов с пониманием специфической терминологии), `voyage-law-2` (юридические тексты), `voyage-multilingual-2`. Это одно из их главных конкурентных преимуществ.
>
>     **Откуда путаница.** OpenAI и Cohere предлагают одну универсальную модель — инженеры экстраполируют это на всю индустрию.
>
>     **Если бы это было правдой.** Voyage не имел бы конкурентного преимущества над OpenAI — но в реальности их domain-модели дают +10–20% precision на специализированных датасетах.
>
>     **Как было бы правильно.** При выборе embedding для специфичного домена (code, finance, legal) — сравнить общую модель с domain-specific вариантом от Voyage на своих данных.
>
> - [ ] D. Voyage AI требует минимум 100K документов для активации аккаунта и не подходит малым проектам.
>
>     **Что на самом деле.** Voyage API работает по pay-per-token модели без минимальных порогов: 200M free tokens на старте, дальше usage-based. Можно использовать на одной странице или на миллиарде документов.
>
>     **Откуда путаница.** Enterprise-продукты часто имеют minimum commitment; стартапы — нет. Voyage — стартап с PLG-моделью.
>
>     **Если бы это было правдой.** Маленькие команды/проекты не могли бы использовать Voyage — но в реальности это популярный выбор именно у стартапов.
>
>     **Как было бы правильно.** Free tier даёт 200M tokens (хватит на ~400K средних документов); pay-as-you-go без минимумов.

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


> [!mcq] Что верно про sentence-transformers как embedding-решение?
>
> - [ ] A. Sentence Transformers доступны только через платный API провайдера UKP/Hugging Face.
>
>     **Что на самом деле.** Sentence-transformers — open-source Python библиотека (Apache 2.0) от UKP Lab (TU Darmstadt) и community. Модели лежат на Hugging Face Hub, скачиваются бесплатно, запускаются локально. Никаких API costs за inference.
>
>     **Откуда путаница.** Hugging Face Inference Endpoints предлагает hosted версию за деньги — это опция, а не обязательное использование. Library работает полностью self-hosted.
>
>     **Если бы это было правдой.** Privacy-sensitive проекты (медицина, банки) не могли бы использовать sentence-transformers для PII-данных. Реально — это любимый стек именно для on-premise embedding.
>
>     **Как было бы правильно.** `pip install sentence-transformers` + `SentenceTransformer('all-MiniLM-L6-v2')` — всё бесплатно и локально.
>
> - [x] B. Sentence-transformers — open-source Python библиотека для локального embedding (популярные модели: all-MiniLM-L6-v2 384d, all-mpnet-base-v2 768d); без API costs и privacy-safe, но обычно уступает в качестве проприетарным моделям.
>
>     **Развёрнутое объяснение.** Библиотека построена поверх Hugging Face transformers и упрощает работу с embedding-моделями: загрузка, batched encoding, pooling, fine-tuning. Самые популярные модели: `all-MiniLM-L6-v2` (22M params, 384d, CPU-friendly, ~5K sentences/sec на CPU), `all-mpnet-base-v2` (110M params, 768d, лучше quality), `multi-qa-mpnet-base-cos-v1` (для Q&A retrieval). Поддерживает GPU через PyTorch. Fine-tuning встроен через `model.fit()` с готовыми loss-функциями (MultipleNegativesRankingLoss, TripletLoss).
>
>     **Пример.** Bloomberg для внутреннего поиска по аналитическим отчётам использует `all-mpnet-base-v2`, дообученный на 50K golden pairs (запрос аналитика → релевантный отчёт). Self-hosted на 2× A100, обрабатывает 5K embeddings/sec. Данные не покидают периметр — критично для финансового compliance.
>
>     **Когда применять.** Privacy-sensitive домены (медицина, юриспруденция, банки, госсектор), high-volume сценарии (>50M embeddings/month, где API становится дороже self-host), proof-of-concept без бюджета на API, edge inference (mobile, embedded). Также как foundation для fine-tuning под domain.
>
>     **Подводные камни.** Качество на 5–15% ниже OpenAI/Cohere/Voyage на MTEB (хотя BGE и E5 на HF подтянулись близко к proprietary). По умолчанию НЕ нормализует — нужно явно `model.encode(texts, normalize_embeddings=True)`, иначе cosine vs dot product не идентичны. CPU inference для крупных моделей (mpnet) медленный — для batch >1000 нужен GPU. Pooling-стратегия (mean vs CLS) различается между моделями — несовместимы в одном индексе.
>
>     **Связанные вопросы.** [[embeddings-interview#Q9]] BGE/E5 модели; [[embeddings-interview#Q13]] normalization; [[embeddings-interview#Q27]] fine-tuning.
>
> - [ ] C. Sentence Transformers требуют минимум 8GB GPU RAM для inference — на CPU не работают.
>
>     **Что на самом деле.** `all-MiniLM-L6-v2` (22M params) спокойно работает на CPU: ~1K–5K predictions/sec на современном x86. Даже `all-mpnet-base-v2` (110M params) запустится на CPU, хоть и медленнее. GPU нужен для high-throughput (>10K/sec) или fine-tuning, а не для inference в принципе.
>
>     **Откуда путаница.** ML-сообщество часто транслирует «нейросети = GPU». Для маленьких embedding-моделей это неверно — они оптимизированы под edge inference.
>
>     **Если бы это было правдой.** Embedded-системы, mobile apps, Raspberry Pi не могли бы использовать sentence-transformers. На практике это популярный выбор для on-device.
>
>     **Как было бы правильно.** Для inference на single text — CPU достаточно (≤50ms). GPU нужен от 1K texts/batch или для fine-tuning.
>
> - [ ] D. Sentence Transformers всегда возвращают ненормализованные векторы, ручная нормализация обязательна.
>
>     **Что на самом деле.** Зависит от модели и параметра `normalize_embeddings`. По умолчанию `normalize_embeddings=False`, но многие модели имеют suffix `-cos` (`multi-qa-mpnet-base-cos-v1`) — они обучены для cosine, и нормализация рекомендуется. Лучшая практика — явно ставить `normalize_embeddings=True` при encode.
>
>     **Откуда путаница.** Документация неоднородна между моделями; пользователи иногда упускают параметр и получают inconsistent результаты.
>
>     **Если бы это было правдой.** Тогда не было бы параметра `normalize_embeddings` — но он есть и явно нужен в API.
>
>     **Как было бы правильно.** Всегда явно `model.encode(texts, normalize_embeddings=True)` для совместимости с cosine/dot product метриками в vector DB.

## Q9. BGE, E5 модели?

**BGE (BAAI General Embedding)** — open-source SOTA models от Beijing Academy of AI.

**Models:**
- `BAAI/bge-large-en-v1.5` (1024d) — top open-source English
- `BAAI/bge-m3` — multilingual, multi-functional (dense + sparse + colbert)

**E5** — Microsoft embeddings.
- `intfloat/multilingual-e5-large` — multilingual
- `intfloat/e5-mistral-7b-instruct` — large (7B), best open-source quality

В **2025** open-source качество подтянулось к proprietary.


> [!mcq] Что верно про BGE и E5 как open-source embedding-модели?
>
> - [ ] A. BGE и E5 — это коммерческие проприетарные модели Microsoft и Alibaba, доступные только через платный API.
>
>     **Что на самом деле.** BGE (BAAI General Embedding) — open-source модели от Beijing Academy of Artificial Intelligence (BAAI), не Alibaba. E5 — open-source модели от Microsoft Research. Обе линейки на Hugging Face под MIT лицензией, веса скачиваются бесплатно.
>
>     **Откуда путаница.** «Microsoft = коммерческое» — стереотип. Microsoft Research активно публикует open-source модели (Phi, Florence, E5). BAAI часто путают с Alibaba/Baidu.
>
>     **Если бы это было правдой.** На Hugging Face Hub не было бы `BAAI/bge-m3` и `intfloat/multilingual-e5-large` с ~миллионами скачиваний. Реально — они в топ-10 downloads среди embedding-моделей.
>
>     **Как было бы правильно.** Обе линейки полностью open-source; коммерческое использование разрешено лицензией.
>
> - [x] B. BGE (BAAI) — open-source SOTA-семейство, BGE-M3 объединяет dense + sparse + ColBERT в одной модели; E5 (Microsoft) с топ-моделью e5-mistral-7b-instruct.
>
>     **Развёрнутое объяснение.** BGE (Beijing Academy of AI, 2023+) — линейка от small (`bge-small-en-v1.5`, 384d) до large (`bge-large-en-v1.5`, 1024d). Флагман `BAAI/bge-m3` (2024) уникален: одна модель генерирует dense embedding, sparse weights (для BM25-like поиска) и multi-vector ColBERT representation одновременно — это позволяет один индекс для трёх типов retrieval. E5 (Microsoft, 2022+) — `multilingual-e5-large` (560M params, multilingual), `e5-mistral-7b-instruct` (7B params, лучшее open-source качество). Обе линейки требуют префиксов на input: BGE — `query:` для запросов в некоторых вариантах, E5 — `query:` / `passage:`.
>
>     **Пример.** Cohere внутренний benchmark (2024) показал: BGE-M3 на multilingual retrieval обходит OpenAI text-embedding-3-large на 4% NDCG@10 при self-hosted inference. Bloomberg использует `e5-mistral-7b` для финансового retrieval — 7B параметров требуют A100 80GB, но дают best-in-class quality для closed корпуса без API leakage.
>
>     **Когда применять.** Privacy + high quality (медицина, банки, госсектор), где нельзя API; multilingual задачи (BGE-M3 топ для 100+ языков); когда нужен hybrid retrieval в одной модели (BGE-M3 dense+sparse+ColBERT). E5-mistral — когда есть GPU-бюджет и нужно лучшее open-source качество для domain-агностичных задач.
>
>     **Подводные камни.** BGE и E5 требуют specific input prefixes (`Represent this sentence for searching relevant passages:` у некоторых E5-моделей) — без них качество падает на 10–20%. BGE-M3 ColBERT-выход даёт многовекторное представление (один токен = один вектор), что увеличивает storage в 50× — нужен особый vector DB (Vespa, Qdrant с multi-vector). e5-mistral-7b в fp16 = ~14GB GPU RAM — нужен A10/A100, на consumer GPU не запустится.
>
>     **Связанные вопросы.** [[embeddings-interview#Q8]] sentence-transformers; [[embeddings-interview#Q10]] выбор модели; [[embeddings-interview#Q17]] multilingual.
>
> - [ ] C. BGE-M3 поддерживает только английский, несмотря на «M» в названии — для других языков нужны отдельные модели.
>
>     **Что на самом деле.** «M3» расшифровывается как Multi-Linguality, Multi-Functionality, Multi-Granularity. Поддерживает 100+ языков (включая русский, китайский, арабский, хинди) в едином embedding space. Это явное multilingual-преимущество над BGE-large-en.
>
>     **Откуда путаница.** «M» в названии часто означает разное в моделях («Mini», «Multi», «Modal»). Без чтения model card легко неверно интерпретировать.
>
>     **Если бы это было правдой.** BGE-M3 не появлялся бы в multilingual leaderboards. Реально — он там в топ-3 для cross-lingual retrieval.
>
>     **Как было бы правильно.** BGE-M3 = multilingual flagship от BAAI; для EN-only задач — `bge-large-en-v1.5` (чуть выше качество на EN).
>
> - [ ] D. e5-mistral-7b-instruct непригоден для production inference из-за слишком большого размера в 7B параметров.
>
>     **Что на самом деле.** 7B модель требует ~14GB GPU RAM в fp16 или ~28GB в fp32. На A10 (24GB) или A100 (40/80GB) работает прекрасно. Для high-throughput embedding (1K+/sec) нужен квантизация (int8) или batch optimization. Производительность приемлема для offline indexing и средне-нагруженного online retrieval.
>
>     **Откуда путаница.** «Большие модели = слишком тяжёлые» — наивная экстраполяция с LLM (70B+) на embedding-модели. 7B для embedding — большая, но не запредельная.
>
>     **Если бы это было правдой.** Никто не выпускал бы 7B+ embedding-модели — но они активно появляются (NV-Embed 8B, SFR-Embedding-Mistral, gte-Qwen2-7B).
>
>     **Как было бы правильно.** 7B embedding-модели подходят для production при наличии GPU; для CPU-only — берите MiniLM/mpnet.

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


> [!mcq] Как корректно выбрать embedding-модель для конкретного production-кейса?
>
> - [ ] A. Всегда брать модель с наибольшим текущим MTEB score — это гарантирует лучшее качество в production.
>
>     **Что на самом деле.** MTEB — синтетический бенчмарк на 56 публичных датасетах. Модели #1 часто overfit на benchmark distribution и могут уступать дешёвым/быстрым моделям на специфическом домене (медицина, finance, internal jargon).
>
>     **Откуда путаница.** Leaderboards в ML — частый shortcut для выбора моделей. Простота «модель X #1 = берём X» соблазнительна, особенно для менеджеров.
>
>     **Если бы это было правдой.** Никто бы не использовал text-embedding-3-small (не топ MTEB) — но это default-выбор в большинстве RAG-туториалов 2024–2025.
>
>     **Как было бы правильно.** MTEB — отправная точка для shortlisting; финальный выбор — после eval на собственных golden pairs (минимум 200 query-doc пар).
>
> - [x] B. Выбор делается по чеклисту: качество (MTEB baseline + собственный eval), privacy (API vs self-host), языки, domain specifics, context length, dimensions/storage tradeoff и латентность.
>
>     **Развёрнутое объяснение.** Чеклист 2025: (1) **Privacy** — есть PII/коммерческая тайна? Self-host (BGE-M3, E5, sentence-transformers). (2) **Языки** — multilingual? Cohere embed-multilingual, BGE-M3. (3) **Domain** — code/finance/legal? Voyage domain-specific. (4) **Context** — длинные документы? OpenAI 8K, Voyage 16–32K vs Cohere 512. (5) **Dimensions** — индекс 100M+ векторов? Matryoshka-модели (text-embedding-3 с `dimensions=512`). (6) **Cost** — API per-token vs GPU self-host (break-even ~10M tokens/month). (7) **Latency** — sub-50ms? Self-host CPU-модели или batch async API. (8) **Eval** — обязательно на 200+ собственных query-doc парах.
>
>     **Пример.** Российский маркетплейс выбирает embedding для product search. Чеклист: privacy не критична (публичные карточки) → API OK; язык — RU (Cohere multilingual или OpenAI); domain — generic e-commerce; context — короткие тайтлы (нет нужды в 8K); dimensions — 50M товаров × 1536 = 300GB → нужно 512d (Matryoshka). Выбор: `text-embedding-3-small` с `dimensions=512`. Eval на 500 ручных запросов: recall@10 = 0.84, что приемлемо.
>
>     **Когда применять.** При старте любого RAG/search-проекта: первый шаг — заполнить чеклист, второй — shortlist 2–3 моделей, третий — собрать golden eval set и сравнить.
>
>     **Подводные камни.** Часто упускают: rate limits API (5M tokens/min у OpenAI tier 4 — для индексирования 1B документов нужны дни); GPU доступность для self-host (A100 — long lead time); cost при росте (API расходы линейные по объёму). Также: миграция моделей требует полного re-embed корпуса — закладывайте downtime/parallel index.
>
>     **Связанные вопросы.** [[embeddings-interview#Q11]] MTEB; [[embeddings-interview#Q12]] dimensions; [[embeddings-interview#Q22]] cost optimization.
>
> - [ ] C. Самая дорогая API-модель всегда лучше любой open-source — цена коррелирует с качеством.
>
>     **Что на самом деле.** BGE-M3 и e5-mistral-7b в 2025 competitive с OpenAI text-embedding-3-large и Cohere embed-v3 по MTEB. На domain-specific задачах fine-tuned open-source часто превосходит generic API модели. Цена API отражает infrastructure cost + margin, не качество.
>
>     **Откуда путаница.** В b2b/enterprise культура «премиум = лучше» переносится на ML. Также API-провайдеры активно маркетят quality, что создаёт иллюзию доминирования.
>
>     **Если бы это было правдой.** Hugging Face MTEB leaderboard был бы захвачен только проприетарными моделями. Реально — open-source модели регулярно в топ-5.
>
>     **Как было бы правильно.** Сравнивать по собственному eval, не по бренду/цене; open-source часто выигрывает в TCO на больших объёмах.
>
> - [ ] D. Dimensions не важны для production — все модели имеют одинаковые storage и latency характеристики.
>
>     **Что на самом деле.** Dimensions критичны: 1M документов × 3072d × 4 bytes (fp32) = 12GB, vs × 384d × 4 = 1.5GB — разница 8×. Vector search complexity O(d × n) — больше dims = медленнее ANN. HNSW index при больших dims занимает в разы больше RAM.
>
>     **Откуда путаница.** В небольших PoC (10K документов) разница незаметна — экстраполяция на 1M документов даёт неверные оценки.
>
>     **Если бы это было правдой.** Pinecone/Qdrant не имели бы оптимизаций для разных dimensions — но они активно рекомендуют truncation и quantization для cost.
>
>     **Как было бы правильно.** Dimensions — один из главных tradeoffs; Matryoshka-модели (text-embedding-3) дают гибкость post-hoc truncation.

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


> [!mcq] Что такое MTEB и как правильно его использовать?
>
> - [ ] A. MTEB — это benchmark только для retrieval-задач; classification и clustering он не оценивает.
>
>     **Что на самом деле.** MTEB покрывает 8 task types: Retrieval, Classification, Clustering, Reranking, Pair Classification, STS (Semantic Textual Similarity), Summarization, Bitext Mining. Это многозадачный benchmark, дающий целостную картину embedding-модели.
>
>     **Откуда путаница.** В RAG-контексте embedding ассоциируется только с retrieval. Многие гайды цитируют только MTEB retrieval subset, создавая впечатление, что MTEB == retrieval.
>
>     **Если бы это было правдой.** Невозможно было бы сравнить модели для clustering или classification — но MTEB leaderboard явно показывает score по каждой задаче отдельно.
>
>     **Как было бы правильно.** При выборе смотреть на subscore нужной задачи (Retrieval column для RAG, Classification для feature extraction).
>
> - [ ] B. Модель #1 на общем MTEB ranking — оптимальный выбор для любого production-сценария.
>
>     **Что на самом деле.** Топовые модели MTEB часто overfit на benchmark datasets. Они хороши «в среднем», но на специфическом домене (медицина с её терминологией, finance с тикерами, code с синтаксисом) специализированные модели часто выигрывают.
>
>     **Откуда путаница.** «Leaderboard #1 = best» — кажущаяся очевидность. Без понимания eval methodology легко переоценить ranking.
>
>     **Если бы это было правдой.** Voyage AI не выпускал бы domain-specific модели (`voyage-code-3`, `voyage-finance-2`) — но они дают +10–20% precision на своих доменах.
>
>     **Как было бы правильно.** MTEB #1 для shortlisting; для финального выбора — eval на собственных golden pairs.
>
> - [x] C. MTEB — стандартный benchmark с 56 датасетами и 8 task types; служит отправной точкой при выборе модели, но обязательно нужен собственный eval на своих данных.
>
>     **Развёрнутое объяснение.** MTEB (Massive Text Embedding Benchmark) — open-source бенчмарк от Hugging Face и BAAI (2022+). Покрывает 56 датасетов (BEIR, STSBenchmark, MS MARCO, и др.) на 8 типах задач. Leaderboard публичный на `huggingface.co/spaces/mteb/leaderboard`. Используется для сравнения моделей — но не идеален: датасеты публичные, top-модели могут быть overfit. Существуют domain-specific subsets: MTEB-Russian, MTEB-French, MTEB-medical, MTEB-code. Best practice: использовать MTEB для shortlist 2–3 моделей → собрать 200+ golden pairs на собственных данных → запустить eval (recall@k, NDCG@k) → выбрать.
>
>     **Пример.** Stack Overflow при миграции embedding для AI-поиска (2024) использовал MTEB-Retrieval subset для shortlist (OpenAI text-embedding-3-large, Cohere embed-v3, BGE-large-en-v1.5). Затем собрали 500 ручных пар (вопрос → правильный ответ) → BGE-large дал NDCG@10 = 0.78, OpenAI = 0.76. Выбрали BGE как self-hosted для cost-control.
>
>     **Когда применять.** При выборе любой embedding-модели; при оценке новых моделей (вышел NV-Embed v2 — стоит ли мигрировать?); при сравнении нескольких подходов (Cohere multilingual vs BGE-M3 для русско-английского корпуса).
>
>     **Подводные камни.** Public датасеты — модели могут быть train-leakage'нуты (видели данные во время обучения). Domain mismatch — общий MTEB не покрывает узкие домены. STS-задача оценивает similarity между предложениями — это не то же, что retrieval-quality. Также MTEB обновляется (MTEB v2 в 2024 добавил новые задачи) — числа из старых статей могут устареть.
>
>     **Связанные вопросы.** [[embeddings-interview#Q10]] выбор модели; [[embeddings-interview#Q26]] fine-tuning; [[embeddings-interview#Q29]] частые проблемы.
>
> - [ ] D. Достаточно посмотреть MTEB score — собственный benchmark на проектных данных избыточен.
>
>     **Что на самом деле.** Domain shift — реальное явление: модель, отлично работающая на news и Wikipedia (MTEB-датасеты), может плохо работать на медицинских записях, финансовых отчётах, юридических документах. Без собственного eval риск выбрать неоптимальную модель — высок.
>
>     **Откуда путаница.** Экономия времени: «MTEB уже сделал работу за нас». Управленческое давление на быстрый выбор стека.
>
>     **Если бы это было правдой.** Все компании использовали бы одну топ-MTEB модель — но на практике крупные игроки (Stripe, Stack Overflow, Notion) делают собственные evals и выбирают разные модели для разных задач.
>
>     **Как было бы правильно.** Минимум 200 golden pairs на своих данных, eval ≥ 2 моделей через recall@k и NDCG@k; затраты на это окупаются за месяцы production-эксплуатации.

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


> [!mcq] Как корректно выбрать dimensions при проектировании embedding-индекса?
>
> - [ ] A. Всегда использовать максимальные dimensions модели (3072d у OpenAI large) — это гарантирует лучшее качество и нет смысла экономить.
>
>     **Что на самом деле.** При переходе с 384d на 3072d storage растёт в 8×, latency vector search — в ~8×, RAM для HNSW индекса — в 5–10×. Прирост качества обычно 3–10% NDCG@10. Соотношение «8× ресурсов за 5% качества» — плохой ROI для большинства production.
>
>     **Откуда путаница.** «Больше = лучше» — наивная интуиция. ML-сообщество часто эту иллюзию подкрепляет, не подчёркивая cost/quality tradeoff.
>
>     **Если бы это было правдой.** Никто не использовал бы Matryoshka truncation, и OpenAI не предлагал бы `dimensions=512` параметр в API. На практике все production-системы используют ≤1024d.
>
>     **Как было бы правильно.** Начать с 512–1024d; растить только при подтверждённой метрической необходимости.
>
> - [x] B. Малые (256–512) — быстро и дёшево с приемлемым качеством; средние (768–1024) — обычно баланс; большие (1536–3072) — лучшее качество ценой ресурсов; Matryoshka-модели позволяют truncate без катастрофической потери.
>
>     **Развёрнутое объяснение.** Tradeoff: quality vs storage vs latency vs RAM. Storage cost: `1M docs × 3072d × 4 bytes (fp32) = 12GB` vs `× 384d × 4 = 1.5GB`. Vector search complexity: O(d × n). HNSW index size грубо `~1.2 × n × d × 4 bytes`. Matryoshka representation learning (MRL) — техника обучения, при которой prefix-subset embedding всё ещё валиден: можно обучить на 3072d и потом использовать первые 512d с потерей ~5–10% качества (vs 60–80% потери на не-Matryoshka моделях). OpenAI text-embedding-3 и Nomic Embed поддерживают MRL.
>
>     **Пример.** Spotify для discovery search (1B треков, embedding каждого трека) использовал 256d вариант Matryoshka-обученной модели: индекс — 1TB вместо 12TB на 3072d. Сэкономили ~$30K/месяц на RAM-инстансах vector DB при 7% recall@100 деградации, что компенсировали reranker'ом.
>
>     **Когда применять.** Большие корпуса (>1M документов) — экономия storage критична; high-throughput retrieval (>1K QPS) — latency важна; cost-sensitive проекты — RAM/SSD дешевле. Малые корпуса (<100K) — можно брать full dimensions без боли.
>
>     **Подводные камни.** Не все модели Matryoshka — у sentence-transformers truncation первых N dims даёт случайный шум (надо использовать Matryoshka-loss обучение или специальную модель). Quantization (fp16, int8) — параллельный путь экономии, но мешает реранкерам. Bench всегда на собственных данных: 512d может быть достаточно для FAQ-бота, но недостаточно для legal search.
>
>     **Связанные вопросы.** [[embeddings-interview#Q5]] OpenAI text-embedding-3; [[embeddings-interview#Q24]] Matryoshka; [[embeddings-interview#Q25]] quantization.
>
> - [ ] C. Dimensions не влияют на скорость поиска в vector DB — алгоритмы ANN абстрагируют это.
>
>     **Что на самом деле.** ANN-алгоритмы (HNSW, IVF, ScaNN) имеют сложность O(d × log n) или O(d × √n) в худшем случае. Линейная зависимость от d сохраняется. 3072d поиск в 8× медленнее 384d на том же корпусе и индексе.
>
>     **Откуда путаница.** ANN-алгоритмы скрывают O(n) при naive поиске — кажется, что они полностью абстрагируют complexity. Но фактор `d` остаётся.
>
>     **Если бы это было правдой.** Vector DB не предупреждали бы о dimensions в pricing/performance гайдах — но Pinecone/Qdrant явно указывают latency vs dimensions.
>
>     **Как было бы правильно.** Считать dimensions частью performance budget: больше dims = больше CPU/RAM на запрос.
>
> - [ ] D. Меньшие dimensions всегда дают катастрофическую потерю качества — truncate'ить нельзя.
>
>     **Что на самом деле.** Зависит от модели. Для Matryoshka-моделей (OpenAI v3, Nomic Embed) truncate 3072→512 даёт ~5–10% потери — приемлемо. Для не-Matryoshka (старые sentence-transformers) — да, прямой truncate ломает embedding.
>
>     **Откуда путаница.** Эксперименты на не-Matryoshka моделях — да, truncate даёт катастрофу. Это правильно для них, но не универсально.
>
>     **Если бы это было правдой.** OpenAI не предлагал бы `dimensions` параметр; Nomic не маркетил бы Matryoshka как фичу. На практике truncation на MRL-моделях работает отлично.
>
>     **Как было бы правильно.** Truncate можно у MRL-моделей; у остальных — переобучить с MRL loss или использовать PCA для уменьшения размерности.

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


> [!mcq] Нужна ли L2-нормализация embedding-векторов и как с ней правильно работать?
>
> - [ ] A. Нормализация не нужна — vector DB сама нормализует векторы при поиске независимо от настроек.
>
>     **Что на самом деле.** Vector DB нормализует автоматически только при `metric="cosine"` (где формула включает деление на нормы). При `metric="dotproduct"` или `"euclidean"` нормализация не происходит — ненормализованные векторы дают неверный ranking. Поведение зависит от выбранной метрики.
>
>     **Откуда путаница.** Многие туториалы говорят «cosine = нормализация», что верно для cosine, но не для других метрик. Инженеры экстраполируют это на все случаи.
>
>     **Если бы это было правдой.** Pinecone/Qdrant не имели бы документации про важность нормализации — но она есть и явно предупреждает о cosine vs dotproduct различиях.
>
>     **Как было бы правильно.** Заранее нормализовать векторы и использовать `dotproduct` метрику для скорости; либо использовать `cosine` метрику и не думать про нормализацию.
>
> - [ ] B. Нормализовать нужно дважды: при indexing и отдельно при поиске запроса — для надёжности.
>
>     **Что на самом деле.** Нормализация идемпотентна: `normalize(normalize(v)) == normalize(v)`. Двойная нормализация лишняя — единичный вектор уже unit-length. Лишняя операция тратит CPU, но не вредит результату.
>
>     **Откуда путаница.** Defensive programming: «нормализую везде на всякий случай». В большинстве случаев безвредно, но избыточно.
>
>     **Если бы это было правдой.** Pipelines стали бы заметно медленнее. На практике нормализация делается один раз — при создании embedding.
>
>     **Как было бы правильно.** Нормализовать один раз — при создании вектора (либо API возвращает unit-length, либо явно `v / np.linalg.norm(v)`).
>
> - [x] C. Нормализация делает ||v||=1, что позволяет использовать dot product как замену cosine (быстрее на 10–30%); OpenAI/Cohere нормализуют автоматически, sentence-transformers — только явно; критично, чтобы все векторы в индексе были одинаково обработаны.
>
>     **Развёрнутое объяснение.** L2-нормализация: `v_norm = v / ||v||`. После неё `cos(A, B) = A · B`, потому что `|A| = |B| = 1`. Это позволяет vector DB использовать `dotproduct` метрику без деления — 10–30% выигрыш latency. OpenAI text-embedding-3 и ada-002, Cohere embed-v3, Voyage возвращают нормализованные векторы. Sentence-transformers — зависит от модели: с suffix `-cos` обычно обучены под cosine; явный параметр `model.encode(texts, normalize_embeddings=True)` гарантирует unit-length. Главное правило: все векторы в индексе должны быть одинаковой длины — иначе similarity scores становятся некомпарабельными.
>
>     **Пример.** Команда мигрировала с ada-002 на all-mpnet-base-v2 для cost-cutting. Забыли поставить `normalize_embeddings=True` при индексировании 10M документов. При поиске cosine давал «странные» результаты — топ-выдача содержала очень длинные документы (высокие magnitudes доминировали в similarity). Решение: re-embed весь корпус с явной нормализацией.
>
>     **Когда применять.** Всегда в production: либо использовать API, который нормализует автоматически (OpenAI, Cohere), либо явно ставить `normalize_embeddings=True` при использовании sentence-transformers. Особенно критично при смене embedding-модели или метрики vector DB.
>
>     **Подводные камни.** Inconsistency опасна: половина индекса нормализована, половина — нет → silent retrieval degradation, recall падает на 10–30%, отлавливается только по метрикам. Также: некоторые модели (например, всякие dual-encoder с тренировкой на dot product) не предполагают нормализацию — нужно читать model card.
>
>     **Связанные вопросы.** [[embeddings-interview#Q14]] cosine vs dot product; [[embeddings-interview#Q5]] OpenAI embeddings; [[embeddings-interview#Q29]] частые проблемы.
>
> - [ ] D. Нормализация ухудшает качество semantic search, удаляя информацию о magnitude.
>
>     **Что на самом деле.** Для cosine/dot product similarity magnitude вектора не несёт семантической информации — только направление. Эмbedding-модели обучены так, что семантика кодируется в направлении вектора в R^d. Нормализация не теряет информацию для этих метрик.
>
>     **Откуда путаница.** В Euclidean space magnitude влияет на расстояние — но это другая метрика, редко применимая к semantic embeddings.
>
>     **Если бы это было правдой.** OpenAI/Cohere не возвращали бы нормализованные векторы — но они возвращают.
>
>     **Как было бы правильно.** Для cosine/dot product семантика = направление; magnitude нормализуется без потери качества.

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


> [!mcq] В чём разница между cosine similarity и dot product при поиске по embedding-индексу?
>
> - [ ] A. Cosine similarity и dot product дают принципиально разные результаты даже для нормализованных векторов — это разные метрики.
>
>     **Что на самом деле.** Для unit-norm векторов (||A||=||B||=1) выполняется математическое тождество: `cos(A, B) = (A · B) / (||A|| × ||B||) = A · B`. Результаты идентичны, ranking одинаковый. Разница — только в вычислительной стоимости.
>
>     **Откуда путаница.** В общем случае (без нормализации) cosine и dot product дают разные ranking — это известный факт. Не учитывая precondition «нормализованные векторы», легко перенести это на все случаи.
>
>     **Если бы это было правдой.** OpenAI/Cohere документация не говорила бы «можно использовать любую из метрик» — но они это явно указывают для своих normalized embeddings.
>
>     **Как было бы правильно.** Для unit-length векторов cosine = dot product математически; различие в скорости, не в ranking.
>
> - [ ] B. Euclidean distance — лучшая метрика для embedding similarity, превосходит cosine.
>
>     **Что на самом деле.** Для семантического embedding magnitude вектора не несёт смысла — только направление. Euclidean distance смешивает оба, что снижает качество retrieval. Стандартная практика — cosine или dot product. Также Euclidean в HNSW медленнее (sqrt operation).
>
>     **Откуда путаница.** Euclidean — интуитивно понятная метрика «расстояние», знакомая со школы. ML-новички склонны к ней по умолчанию.
>
>     **Если бы это было правдой.** Все vector DBs рекомендовали бы Euclidean как default — но они рекомендуют cosine для embedding-моделей.
>
>     **Как было бы правильно.** Cosine/dot product для семантики; Euclidean — только если модель явно обучена под него (редкость).
>
> - [x] C. Для нормализованных векторов dot product математически идентичен cosine similarity, но быстрее на 10–30%; в production стоит явно использовать `metric="dotproduct"` в vector DB, когда известно, что векторы unit-length.
>
>     **Развёрнутое объяснение.** Cosine similarity: `cos(A, B) = (A · B) / (||A|| × ||B||)`. Для unit-vectors знаменатель = 1, формула упрощается до dot product. Vector DB при `metric="cosine"` делает деление на нормы на каждом запросе — это лишние операции. При `metric="dotproduct"` пропускает деление — экономит 10–30% latency. Pinecone, Qdrant, Weaviate, Milvus — все поддерживают оба варианта одним параметром. Выгода масштабируется: 1B запросов/день × 10ms = ~115 CPU-часов экономии в день.
>
>     **Пример.** Команда RAG-системы на Qdrant с 50M документов и OpenAI embeddings. Изначально использовали `metric="cosine"`, p95 latency = 32ms. Сменили на `dotproduct` (векторы уже unit-length), p95 упало до 24ms — 25% выигрыш бесплатно. Изменение в коде: один параметр в конфигурации коллекции.
>
>     **Когда применять.** Везде, где известно, что векторы нормализованы (OpenAI, Cohere, Voyage — автоматически; sentence-transformers с `normalize_embeddings=True`). High-throughput RAG, рекомендательные системы, real-time semantic search.
>
>     **Подводные камни.** Если хоть один вектор в индексе ненормализован, dot product даст неверный ranking — длинные векторы будут «выигрывать» из-за большего dot product. Контроль качества: проверять `np.linalg.norm(vec) ≈ 1.0 ± 1e-5` для случайной выборки. При смене embedding-модели — повторить проверку.
>
>     **Связанные вопросы.** [[embeddings-interview#Q13]] normalization; [[embeddings-interview#Q5]] OpenAI embeddings; [[embeddings-interview#Q22]] cost optimization.
>
> - [ ] D. Dot product требует особой настройки vector DB, cosine работает «из коробки» везде.
>
>     **Что на самом деле.** Все major vector DBs (Pinecone, Qdrant, Weaviate, Milvus, pgvector) поддерживают dot product как built-in метрику. Настройка — один параметр `metric="dotproduct"` или `vector_size + distance="Dot"` в конфиге коллекции. Никаких custom-операторов или плагинов.
>
>     **Откуда путаница.** Cosine традиционно — default в туториалах, что создаёт иллюзию «cosine = простой путь, dot product = exotic».
>
>     **Если бы это было правдой.** Документация Pinecone/Qdrant имела бы отдельные разделы про «как включить dot product» — но это тривиальный параметр.
>
>     **Как было бы правильно.** Cosine и dot product — equally first-class metrics в любой современной vector DB; различие в performance, не в complexity настройки.

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


> [!mcq] Какие token-лимиты у популярных embedding-моделей и почему это важно?
>
> - [ ] A. Все embedding-модели поддерживают неограниченную длину текста — лимиты есть только у LLM (GPT, Claude).
>
>     **Что на самом деле.** У каждой embedding-модели жёсткий лимит max_input_tokens, определяемый архитектурой transformer (positional embeddings обучены до конкретной длины). Cohere embed-v3 — 512 токенов, BGE-large — 512, OpenAI text-embedding-3 — 8191, Voyage — 16K–32K. Превышение → silent truncation или ошибка API.
>
>     **Откуда путаница.** «Embedding — это вектор, размер не важен» — наивная интуиция. Внутри сидит transformer с конкретным max context length.
>
>     **Если бы это было правдой.** Не нужен был бы chunking в RAG-pipeline — embedding-модель просто кодировала бы весь документ. На практике chunking — обязательный шаг preprocessing.
>
>     **Как было бы правильно.** Каждая модель имеет лимит; для длинных документов нужен chunking перед embedding.
>
> - [x] B. Каждая embedding-модель имеет лимит: Cohere/BGE = 512, sentence-transformers BERT-based = 512, OpenAI text-embedding-3 = 8191, Voyage до 16–32K; превышение → truncation, длинные документы требуют chunking.
>
>     **Развёрнутое объяснение.** Транзит токены → векторы происходит через transformer с фиксированным max position embedding. Для BERT-based моделей (Cohere embed-v3, BGE, multi-qa-mpnet, large multilingual E5) лимит унаследован от BERT — 512 токенов (~400 английских слов или ~300 русских). OpenAI text-embedding-3 обучен на длинном контексте — 8191 токенов. Voyage-large-2 — 16K, Voyage-3 — 32K. При превышении: OpenAI silent truncate'нет вход и вернёт embedding только первых 8191 токенов; Cohere вернёт ошибку 422. В обоих случаях информация дальше лимита теряется.
>
>     **Пример.** Команда RAG для PDF-документов (юридические контракты, ~10K токенов каждый) изначально использовала Cohere embed-v3 без chunking — каждый документ давал embedding только первых 512 токенов (около первого параграфа), recall@10 = 0.31. Перешли на proper chunking (500 токенов, 50 overlap) — recall@10 вырос до 0.78. Альтернативно: миграция на Voyage с 32K context позволила бы один embedding на документ, но потерять granular retrieval.
>
>     **Когда применять.** Знание лимита нужно при: (1) выборе модели — длинные документы требуют 8K+ моделей или chunking-стратегии; (2) preprocessing — chunk size < model_limit с safety margin (например, 400 токенов для 512-лимита модели); (3) cost-расчётах — API часто берёт деньги за полный токен-проход, не за единицу запроса.
>
>     **Подводные камни.** Token != word: 1 русское слово ≈ 1.5–2 токена (BPE-токенизатор), 1 английское ≈ 0.75–1.3 токена. Лимиты обычно в токенах модели, считайте `tiktoken`/`tokenizers.AutoTokenizer`. Silent truncation в OpenAI — без warning, отлаживается только мониторингом метрик recall. Также: long-context модели (Voyage 32K) формально дают один embedding, но качество retrieval на больших chunks хуже, чем у granular chunks.
>
>     **Связанные вопросы.** [[embeddings-interview#Q5]] OpenAI text-embedding-3; [[embeddings-interview#Q6]] Cohere; [[embeddings-interview#Q16]] truncation/chunking.
>
> - [ ] C. Token-лимит не важен, если использовать sliding window averaging — он покрывает любую длину текста.
>
>     **Что на самом деле.** Sliding window averaging (создать embeddings для overlapping окон, усреднить) — устаревшая техника. Результирующий «средний» вектор размывает специфические детали — конкретные термины, имена, даты «растворяются» в общем смысле документа. Production-стандарт — independent chunking + retrieval по chunks.
>
>     **Откуда путаница.** Усреднение интуитивно кажется хорошим обобщением. На практике для retrieval нужны granular матчи, а не «общее впечатление».
>
>     **Если бы это было правдой.** Все RAG-системы использовали бы averaging — но индустрия перешла на chunk-level retrieval.
>
>     **Как было бы правильно.** Proper chunking (200–1000 токенов с overlap), independent embedding каждого chunk, retrieval по chunks с возможностью parent-doc lookup для контекста.
>
> - [ ] D. OpenAI и Cohere имеют одинаковый лимит 512 токенов — они построены на одной архитектуре.
>
>     **Что на самом деле.** OpenAI text-embedding-3 имеет лимит 8191 токенов — в 16× больше Cohere embed-v3 (512). Архитектуры разные: OpenAI обучен на длинном контексте, Cohere — на коротком (как классический BERT).
>
>     **Откуда путаница.** Обе модели — proprietary transformer embedders, кажутся «похожими».
>
>     **Если бы это было правдой.** Стратегия chunking была бы одинакова для обоих — но при работе с OpenAI можно chunk'ить большими блоками, экономя API calls.
>
>     **Как было бы правильно.** OpenAI 8191, Cohere/BGE 512, Voyage 16–32K — выбор модели влияет на chunking-стратегию.

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


> [!mcq] Как правильно обработать длинный документ, превышающий token-лимит embedding-модели?
>
> - [ ] A. Просто truncate'нуть первые 512 токенов и игнорировать остаток документа.
>
>     **Что на самом деле.** Truncation теряет 80–95% содержимого длинного документа. Ключевая информация (выводы, цифры, имена) часто в середине или конце — теряется навсегда. Recall в RAG деградирует катастрофически.
>
>     **Откуда путаница.** Это «дефолтное» поведение многих API при превышении лимита (OpenAI silent truncate). Инженеры могут не заметить, что они полагаются на truncation вместо chunking.
>
>     **Если бы это было правдой.** RAG-системы на длинных PDF давали бы случайные результаты. Конкретный кейс: legal RAG, контракт 50 страниц, retrieval — только embedding первой страницы (типично оглавление) → нерелевантные матчи.
>
>     **Как было бы правильно.** Chunking перед embedding; truncation допустимо только для коротких документов на грани лимита.
>
> - [ ] B. Embed целый длинный документ одним вызовом, используя модели с 8K–32K контекстом — это даёт лучшее качество, чем chunking.
>
>     **Что на самом деле.** Один embedding на 8K-документ — это mean-pooled представление всего текста. Конкретные детали (термины, числа, named entities) «растворяются» в усреднённом векторе. Retrieval precision падает: запрос «какие правила про refund в section 5.3» матчит много документов одинаково плохо.
>
>     **Откуда путаница.** Маркетинг long-context моделей («embed entire documents!») создаёт иллюзию, что это лучше granular chunking.
>
>     **Если бы это было правдой.** Не было бы целой индустрии chunking-стратегий (LangChain TextSplitters, LlamaIndex node parsers, Unstructured.io). Реально — chunking стандарт в production RAG.
>
>     **Как было бы правильно.** Long-context модели полезны, чтобы расширить chunk size (1000–2000 токенов), но не для замены chunking. Granular retrieval > coarse retrieval.
>
> - [x] C. Proper chunking (200–1000 токенов с 10–20% overlap) — стандартная стратегия; embedded каждый chunk отдельно, retrieval возвращает релевантные chunks; альтернативы — LLM summarization (дорого) или embedding pooling.
>
>     **Развёрнутое объяснение.** Chunking стратегии: (1) **Fixed-size** — 500 токенов с 50 overlap, простой и быстрый, может разрезать предложения; (2) **Sentence-aware** — разбивать по предложениям, набирать до лимита, не ломает синтаксис; (3) **Recursive** (LangChain) — рекурсивно по separators (`\n\n` → `\n` → `. ` → пробел), сохраняет структуру; (4) **Semantic chunking** — кластеризация по similarity embeddings, продвинутый и медленный. Overlap (10–20%) предотвращает потерю контекста на границах chunks. Каждый chunk independently embedded, vector DB хранит pairs `(chunk_text, embedding, doc_id, chunk_idx)`. При retrieval возвращаются top-K chunks; для генерации можно подгружать parent-doc (parent-child retriever).
>
>     **Пример.** Notion AI для RAG поверх рабочих пространств использует hybrid chunking: страницы Notion разбиваются по блокам (heading, paragraph, list), каждый блок embedded как chunk если ≤500 токенов или дальше chunked рекурсивно. Метаданные блока (heading hierarchy, page title) сохраняются в payload chunk'a — это даёт контекст при reranking.
>
>     **Когда применять.** Любой production RAG; documents > 500 токенов (страница текста); смешанные корпуса (короткие FAQ + длинные политики). Размер chunk выбирается по: model_limit, query_specificity (точные запросы → меньшие chunks), retrieval_metric (precision vs recall).
>
>     **Подводные камни.** Очень мелкие chunks (50–100 токенов) теряют контекст — модель не понимает «этот метод возвращает», не видя предыдущего предложения. Очень крупные (>1500) — pool усредняет детали. Граница между chunks ломает context (overlap решает частично). При migration важно перечанковать заново — старые chunks не совместимы с новыми parameters.
>
>     **Связанные вопросы.** [[embeddings-interview#Q15]] token limits; [[embeddings-interview#Q22]] cost optimization; [[embeddings-interview#Q29]] частые проблемы.
>
> - [ ] D. LLM summarize каждый документ → embed summary — это лучший подход качества и стоимости.
>
>     **Что на самом деле.** LLM summarization дорого ($0.01+ per document с GPT-4o), теряет specific details (конкретные числа, имена, цитаты), и summary одного документа даёт один embedding — нет granular retrieval. Chunking дешевле (только embedding API) и точнее (granular).
>
>     **Откуда путаница.** «Summarize → embed» интуитивно кажется элегантнее «дробить → embed». На практике details важны для retrieval.
>
>     **Если бы это было правдой.** Production RAG-системы массово использовали бы summarization — но это скорее исключение для special cases (audio transcripts с очисткой шума, длинные emails).
>
>     **Как было бы правильно.** Summarization применять для специфичных случаев (зашумлённые данные, multi-modal contexts), не как замену chunking.

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


> [!mcq] Как правильно реализовать multilingual embeddings для международного продукта?
>
> - [ ] A. Translate всё в английский через MT API → embed английский → достаточно для multilingual.
>
>     **Что на самом деле.** Translation pipeline добавляет latency (100–500ms за вызов MT), стоимость ($20/1M chars на DeepL/Google), и вносит шум (потеря культурного контекста, ошибки на низко-ресурсных языках). Modern multilingual embeddings делают это лучше: один проход через модель кодирует семантику напрямую, без промежуточного translation.
>
>     **Откуда путаница.** До 2020 года не было хороших multilingual моделей, translation был обходным путём. Эта practice до сих пор встречается в legacy системах.
>
>     **Если бы это было правдой.** Cohere embed-multilingual и BGE-M3 не были бы маркетинговыми флагманами — но они активно продвигаются именно для cross-lingual.
>
>     **Как было бы правильно.** Использовать multilingual embedding модель — один вызов вместо MT + embed; быстрее, дешевле, качественнее.
>
> - [x] B. Multilingual embedding-модели проецируют разные языки в одно shared semantic space: `emb("hello") ≈ emb("привет") ≈ emb("你好")`; ключевые модели — Cohere embed-multilingual-v3 и BGE-M3 для 100+ языков.
>
>     **Развёрнутое объяснение.** Multilingual модели обучены на параллельных corpus'ах (translation pairs или multilingual datasets) с contrastive loss, который притягивает translations друг к другу в embedding space. После обучения язык становится практически невидимым для retrieval: запрос «refund policy» на английском матчит документ «политика возврата» на русском с высоким cosine. Главные модели 2025: `cohere embed-multilingual-v3.0` (100+ языков, 1024d, asymmetric API), `BAAI/bge-m3` (100+ языков, dense+sparse+ColBERT), `intfloat/multilingual-e5-large` (100+ языков, open-source), `paraphrase-multilingual-MiniLM-L12-v2` (легковесная, 50+ языков). OpenAI text-embedding-3 поддерживает популярные языки (EN, FR, DE, ES, RU, ZH), но слабее на низко-ресурсных.
>
>     **Пример.** Booking.com использует multilingual embeddings для поиска отзывов отелей: запрос на немецком «sauberes Hotel» (чистый отель) находит отзывы на английском «clean hotel» и французском «hôtel propre» в одном индексе. Cohere embed-multilingual-v3 дал на 18% выше recall чем translate-then-embed pipeline.
>
>     **Когда применять.** Cross-border SaaS (Notion, Slack), e-commerce с международной аудиторией, агрегаторы (Booking, TripAdvisor), enterprise с глобальными командами (документация на разных языках). Также — внутренний поиск в компаниях с EN+local language stack.
>
>     **Подводные камни.** Tradeoff с monolingual: multilingual модель чуть слабее на каждом отдельном языке, чем специализированная monolingual (например, для чисто английского RAG `text-embedding-3-large` лучше чем `bge-m3`). Низко-ресурсные языки (тайский, суахили, киргизский) поддерживаются плохо у всех моделей. Code-mixed тексты (русский + английский в одном предложении) — частая проблема, BGE-M3 справляется лучше всех.
>
>     **Связанные вопросы.** [[embeddings-interview#Q6]] Cohere; [[embeddings-interview#Q9]] BGE/E5; [[embeddings-interview#Q18]] cross-lingual search.
>
> - [ ] C. OpenAI text-embedding-3 — оптимальный выбор для любых языков, включая низко-ресурсные.
>
>     **Что на самом деле.** OpenAI хорош для top-20 языков (EN, FR, DE, ES, RU, ZH, JA, AR, и др.), но на низко-ресурсных (суахили, киргизский, тайский, бенгальский) заметно отстаёт от Cohere multilingual и BGE-M3, специализированных на широком multilingual coverage.
>
>     **Откуда путаница.** OpenAI — most-known бренд, кажется универсальным решением. Без бенчмарка на low-resource языках это предположение принимают без проверки.
>
>     **Если бы это было правдой.** Cohere и BGE не имели бы конкурентного преимущества в multilingual space — но они активно используются для проектов с экзотическими языками.
>
>     **Как было бы правильно.** Для популярных языков OpenAI приемлем; для широкого multilingual — Cohere multilingual или BGE-M3 + eval на конкретных языках.
>
> - [ ] D. Нужно создавать отдельный embedding-индекс для каждого языка — это даёт лучшее качество.
>
>     **Что на самом деле.** Отдельные индексы блокируют cross-lingual search (EN query не найдёт RU documents). Главное преимущество multilingual моделей — единое embedding space, позволяющее искать запросом на одном языке по документам на другом.
>
>     **Откуда путаница.** Performance optimization mindset: «меньший индекс = быстрее». Но это убивает основную use case.
>
>     **Если бы это было правдой.** Cross-lingual поиск был бы невозможен без MT — но именно ради него и существуют multilingual embeddings.
>
>     **Как было бы правильно.** Один multilingual index для всех языков; cross-lingual search «бесплатно»; экономия на инфраструктуре и DevOps.

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


> [!mcq] Что верно про cross-lingual search с multilingual embeddings?
>
> - [ ] A. Cross-lingual search требует machine translation запроса/документов на стороне сервера — без MT не работает.
>
>     **Что на самом деле.** Multilingual embeddings обеспечивают cross-lingual search без translation: query на одном языке encoded в общее space, документы на другом языке тоже в это space — cosine similarity напрямую сравнивает их. Никаких MT-вызовов посередине.
>
>     **Откуда путаница.** До появления хороших multilingual embeddings (≤2020) translation был обходным путём. Это инерция мышления.
>
>     **Если бы это было правдой.** Cross-lingual search стоил бы $20/1M chars (MT API) на каждый запрос. Реально — один вызов embedding API, всё.
>
>     **Как было бы правильно.** Multilingual embedding model позволяет cross-lingual поиск без translation.
>
> - [x] B. Multilingual embedding space позволяет query на языке A находить документы на языке B без translation; monolingual модель чуть лучше для одного языка, multilingual — выигрывает на cross-lingual.
>
>     **Развёрнутое объяснение.** Cross-lingual search работает потому, что multilingual embeddings обучены через parallel-pair contrastive loss: `("hello", "привет")` притягиваются в embedding space. После обучения запрос на любом языке попадает в общую часть пространства, и vector DB находит ближайшие документы независимо от их языка. Tradeoff: на чисто monolingual задаче (например, EN query → EN docs) специализированная monolingual модель `text-embedding-3-large` обходит multilingual на 3–8% NDCG. Для cross-lingual задачи — наоборот: multilingual выигрывает кардинально, потому что monolingual не понимает второй язык.
>
>     **Пример.** Российский Booking-like сервис с отзывами на 12 языках использует BGE-M3 с одним общим индексом. Турист из Германии ищет «sauberes Strandhotel in Sotschi» — система находит русские отзывы «чистый отель у моря в Сочи» и английские «clean beach hotel in Sochi» в топ-10. Без multilingual embedding пришлось бы либо вести отдельные индексы по языкам (нет cross-lingual), либо переводить всё через DeepL (latency + cost).
>
>     **Когда применять.** Международные продукты с мультиязычным content, корпоративный search через офисы в разных странах, документации tech-стека (EN docs + локальные guides), e-commerce с отзывами на разных языках, support tickets от глобальных клиентов.
>
>     **Подводные камни.** Quality неравномерно по языкам: high-resource (EN, FR, DE) работают отлично, low-resource (киргизский, лаосский) — посредственно. Code-mixed («русский+English code») часто проблема — некоторые модели лучше справляются (BGE-M3, multilingual-e5), другие хуже (paraphrase-multilingual-MiniLM). Также: при индексировании всегда указывайте `metadata.language` для возможной фильтрации (для случаев, когда нужно ограничить только определёнными языками).
>
>     **Связанные вопросы.** [[embeddings-interview#Q17]] multilingual embeddings; [[embeddings-interview#Q6]] Cohere asymmetric API; [[embeddings-interview#Q10]] выбор модели.
>
> - [ ] C. Cross-lingual search невозможен, если у вас нет параллельного corpus для каждой пары языков — обучать должны вы сами.
>
>     **Что на самом деле.** Pretrained multilingual модели уже обучены на огромных параллельных corpus'ах (Wikipedia, OPUS, CommonCrawl). Конечному пользователю не нужно предоставлять никакие данные — модель работает «из коробки». Fine-tuning на parallel pairs — опционально, для domain-specific случаев.
>
>     **Откуда путаница.** Cross-lingual NLP исторически требовал parallel data — это релевантно для обучения, не для использования pretrained моделей.
>
>     **Если бы это было правдой.** Никто не мог бы использовать multilingual embeddings без огромных translation pairs. На практике это work-out-of-the-box.
>
>     **Как было бы правильно.** Cross-lingual search готов к использованию с любой pretrained multilingual моделью; parallel data — только если нужен fine-tuning под domain.
>
> - [ ] D. Качество cross-lingual поиска идентично monolingual поиску для всех языков — никакого tradeoff нет.
>
>     **Что на самом деле.** Multilingual модели делают компромисс: один embedding space на все языки → чуть меньше capacity на каждый отдельный язык. Для EN-only задач `text-embedding-3-large` обходит BGE-M3 на 3–8%. Для cross-lingual — наоборот, multilingual выигрывает кардинально.
>
>     **Откуда путаница.** Маркетинг multilingual моделей подчёркивает их универсальность, не упоминая компромисс по сравнению с monolingual.
>
>     **Если бы это было правдой.** Не было бы смысла иметь monolingual модели — но они активно используются для EN-only задач.
>
>     **Как было бы правильно.** Tradeoff существует; выбор зависит от use case (monolingual для одного языка, multilingual для нескольких).

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


> [!mcq] Что верно про CLIP-модель и image embeddings?
>
> - [ ] A. CLIP применяется только для классификации изображений (например, ImageNet) и не годится для поиска.
>
>     **Что на самом деле.** CLIP был обучен именно для text-image alignment на 400M парах (изображение, описание) с contrastive loss. Главное применение — image search by text query («найди фото кошки на пляже»). Zero-shot classification — побочный продукт: можно классифицировать изображения через text labels без обучения на конкретном датасете.
>
>     **Откуда путаница.** OpenAI представляла CLIP с примерами zero-shot ImageNet classification, что закрепило ассоциацию «CLIP = classification».
>
>     **Если бы это было правдой.** Pinterest, Google Images, Shutterstock не использовали бы CLIP-подобные модели для visual search — но они активно их применяют.
>
>     **Как было бы правильно.** CLIP — universal модель для text↔image semantic matching: search, classification, deduplication, content moderation.
>
> - [x] B. CLIP embeds изображения и текст в общее space так, что `image_features ≈ text_features` для семантически связанных пар; даёт text→image поиск, image→image, zero-shot classification.
>
>     **Развёрнутое объяснение.** CLIP (Contrastive Language-Image Pre-training, OpenAI 2021) — две модели в одной: vision encoder (ViT-B/32, ViT-L/14) для изображений и text encoder (BERT-like) для текста. Обе генерируют 512–768d вектор в одном space. Обучены contrastive loss на 400M (image, caption) пар: для batch'а размером N модель делает N×N similarity matrix и притягивает диагональ (правильные пары), отталкивает остальное. После обучения: `cos(emb_image("photo of cat"), emb_text("a cat"))` высокий; cross-modal retrieval работает напрямую. Альтернативы: OpenCLIP (open-source training), SigLIP (Google, лучше CLIP на 2024 benchmarks), BLIP-2 (с генеративной частью).
>
>     **Пример.** Pinterest для visual search использует CLIP-like модель: пользователь грузит фото своей гостиной, система embeds его и находит pins с похожим интерьером — `cos(uploaded_image, pin_images)` ранжирует pins. То же для text search: «modern Scandinavian bedroom» → embeddings text → находит matching pin images. Один shared index для всего.
>
>     **Когда применять.** Multimodal RAG (документы с картинками + текст), visual search в e-commerce («найди диван похожий на этот»), content moderation («найди контент, похожий на запрещённое»), accessibility (text descriptions для images через nearest neighbors), zero-shot classification под новые категории без переобучения.
>
>     **Подводные камни.** CLIP слабее на: domain-specific images (медицинские снимки, спутниковые фото — нужен fine-tuning); тонком тексте внутри картинок (OCR-like задачи); очень детальной семантике («чёрная кошка с белой левой лапой» — пропадают детали). Также CLIP embedding chunky — `ViT-L/14` даёт 768d, что в 2× больше типичного text embedding; storage cost при миллионах images заметен. SigLIP в 2024 обычно outperforms.
>
>     **Связанные вопросы.** [[embeddings-interview#Q20]] audio embeddings; [[embeddings-interview#Q21]] multimodal embeddings; [[embeddings-interview#Q22]] cost optimization.
>
> - [ ] C. CLIP требует fine-tuning под каждый тип изображений — иначе результаты плохие.
>
>     **Что на самом деле.** CLIP отлично работает zero-shot на широком круге задач благодаря масштабу pretrain (400M images): photos, illustrations, screenshots, art. Fine-tuning нужен только для highly specialized доменов (медицинская визуализация, satellite imagery, microscopy) — обычные коммерческие use cases работают out-of-the-box.
>
>     **Откуда путаница.** ML-community привыкло к fine-tuning как обязательной фазе — но CLIP был дизайнирован именно для zero-shot применения.
>
>     **Если бы это было правдой.** Pinterest, Shutterstock тратили бы миллионы на fine-tuning перед использованием — реально они используют out-of-the-box CLIP/SigLIP с минимальной адаптацией.
>
>     **Как было бы правильно.** Сначала пробовать zero-shot; fine-tuning — только если eval показывает недостаточное качество на specific domain.
>
> - [ ] D. Для image search достаточно pixel-level hash fingerprints (perceptual hash, dHash) — embeddings избыточны.
>
>     **Что на самом деле.** Perceptual hashes (pHash, dHash) детектируют **exact или near-duplicate** изображения (то же фото с разной jpeg-компрессией). Они не понимают семантику: «фото чёрной кошки» и «фото белого котёнка» имеют совершенно разные хэши, хотя семантически близки.
>
>     **Откуда путаница.** Hash-based image matching — старая известная техника (TinEye, Google reverse image). Кажется, что её достаточно для всего image search.
>
>     **Если бы это было правдой.** Pinterest для «найди похожие интерьеры» использовал бы pHash и возвращал только точные копии — но реально он использует CLIP-like для semantic similarity.
>
>     **Как было бы правильно.** pHash для дедупликации (тот же фото с разными crop/compression); CLIP для semantic similarity («похожее по смыслу»).

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


> [!mcq] Что верно про audio embeddings и их применение?
>
> - [ ] A. Для audio достаточно конвертировать в текст через Whisper и использовать text embeddings — отдельные audio embeddings не нужны.
>
>     **Что на самом деле.** Speech-to-text работает только для речи и теряет акустические features: prosody (интонация), tone (тембр), rhythm (ритм), genre, instrumentation. Для music similarity (Spotify-like), sound effects detection, voice authentication нужны native audio embeddings, которые работают на спектрограммах/waveforms, а не на текстовой транскрипции.
>
>     **Откуда путаница.** Speech-to-text — самое известное применение audio ML; легко экстраполировать на «всё audio = текст».
>
>     **Если бы это было правдой.** Spotify Discover Weekly не работал бы — он рекомендует похожую музыку, а у инструментальной музыки нет «текста». Реально Spotify использует audio embeddings параллельно с lyrics embeddings.
>
>     **Как было бы правильно.** Speech-to-text + text embeddings подходит для transcript search; для music/audio similarity нужны audio-native embeddings.
>
> - [x] B. Audio embeddings включают: Whisper и Wav2Vec для speech, CLAP для text-audio alignment, VGGish для music; направление менее зрелое, чем text embeddings, но активно развивается.
>
>     **Развёрнутое объяснение.** Audio embeddings обрабатывают звук как 1D-сигнал или 2D-спектрограмму. Основные модели: (1) **Whisper** (OpenAI) — encoder-decoder для speech recognition; encoder выдаёт audio embeddings для downstream задач. (2) **Wav2Vec 2.0** (Meta) — self-supervised learning на raw waveform, base 95M / large 317M params. (3) **CLAP** (Microsoft, 2022) — Contrastive Language-Audio Pretraining: аналог CLIP но для пары (audio, text), позволяет text→audio search «найди аудио с лаем собаки». (4) **VGGish** (Google) — CNN на mel-спектрограммах, хорош для music tagging. (5) **MusicGen embeddings** (Meta) для генеративных music tasks. Embedding dimensions варьируются 128–1024d.
>
>     **Пример.** Spotify использует комбинацию embeddings для рекомендаций: audio embeddings (acoustic similarity), lyrics embeddings (text), user behavior embeddings (collaborative filtering). Музыкальная служба находит трек «похожий по vibe» через audio embeddings (та же tonality, BPM, instrumentation), даже если у него нет lyrics или они на другом языке.
>
>     **Когда применять.** Music recommendations (Spotify, Apple Music, Deezer), sound effects search (Adobe Audition, BBC Sound Effects), voice authentication (банковская биометрия), audio content moderation (детекция запрещённого контента), audiobook recommendations (по голосу диктора).
>
>     **Подводные камни.** Audio embeddings ресурсоёмкие: вычисление мел-спектрограммы + CNN/transformer проход — секунды на минуту audio. Sampling rate важен (CLAP обучен на 48kHz, downsample до 16kHz роняет качество). Длинные audio (>1 минуты) требуют сегментации, аналогично chunking текста. Также audio embedding моделей меньше и они моложе text — экосистема (libraries, evaluations, fine-tuning toolkits) менее зрелая.
>
>     **Связанные вопросы.** [[embeddings-interview#Q19]] CLIP image embeddings; [[embeddings-interview#Q21]] multimodal embeddings; [[embeddings-interview#Q3]] обучение embedding-моделей.
>
> - [ ] C. CLAP — это просто «CLIP для audio», обе модели взаимозаменяемы и могут обрабатывать любую модальность.
>
>     **Что на самом деле.** CLAP (Contrastive Language-Audio Pretraining) и CLIP (Contrastive Language-Image Pretraining) — параллельные архитектуры для разных модальностей. CLIP принимает (image, text), CLAP принимает (audio, text). Архитектурно похожи (dual-encoder + contrastive loss), но обучены на разных данных. Использовать CLIP для audio не получится — он принимает только image input.
>
>     **Откуда путаница.** Названия CLIP/CLAP похожи, оба применяют contrastive pretraining; для не-специалиста легко считать их синонимами.
>
>     **Если бы это было правдой.** Можно было бы загрузить .mp3 в CLIP API — но он принимает только image inputs.
>
>     **Как было бы правильно.** CLIP для image+text, CLAP для audio+text, ImageBind для нескольких модальностей сразу.
>
> - [ ] D. Audio embeddings работают только для речи, для музыки нет специальных embedding-моделей.
>
>     **Что на самом деле.** VGGish (Google) специально обучен на AudioSet (2M+ audio clips, multiple genres) — отлично для music tagging. MusicGen, Jukebox — для generative music tasks. CLAP — universal модель, работает и для music, и для sound effects.
>
>     **Откуда путаница.** Whisper — самая известная audio модель, и она для speech. Это создаёт впечатление, что «audio embeddings = speech».
>
>     **Если бы это было правдой.** Spotify, Pandora, Deezer не имели бы music recommendation систем на embeddings — но они активно их используют.
>
>     **Как было бы правильно.** Audio embeddings покрывают speech (Whisper, Wav2Vec), music (VGGish, MusicGen), general audio (CLAP, YAMNet).

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


> [!mcq] Что такое multimodal embeddings и какие модели на 2025 актуальны?
>
> - [ ] A. Multimodal embedding — это просто конкатенация text vector и image vector в один большой вектор.
>
>     **Что на самом деле.** Concatenation создаёт hybrid vector с разными distributions/scales для каждой модальности — это не единое semantic space. `cos([text_vec, image_vec], [text_vec2, image_vec2])` смешивает text similarity и image similarity, не позволяя cross-modal queries («найди image по text»).
>
>     **Откуда путаница.** Простое concatenation — наивный first approach, который не работает, но кажется логичным. Multimodal embedding требует совместного обучения, чтобы модальности попали в shared space.
>
>     **Если бы это было правдой.** CLIP/ImageBind не имели бы конкурентного преимущества — но они построены именно на shared space через contrastive обучение, не concatenation.
>
>     **Как было бы правильно.** Multimodal обучают так, чтобы embedding text «cat» был близок к embedding image кота в **одном** vector space — это даёт cross-modal search.
>
> - [x] B. Multimodal embeddings проецируют несколько модальностей в единое semantic space; ключевые модели — CLIP (text+image), ImageBind (6 модальностей), Cohere multimodal embed; позволяют cross-modal search без translation между модальностями.
>
>     **Развёрнутое объяснение.** Multimodal embedding-модели обучают так, чтобы text/image/audio/video попадали в общее пространство R^d, где близость означает семантическое сходство независимо от модальности. Главные модели 2025: (1) **CLIP / OpenCLIP / SigLIP** — text + image, 512–768d. (2) **ImageBind** (Meta, 2023) — 6 модальностей: text, image, audio, video, IMU (inertial sensors), depth. Обучен через image как «pivot»: каждая non-image модальность тренируется быть близкой к image embedding для пары. (3) **Cohere Embed v3 multimodal** (2024) — text + image API. (4) **LanguageBind** — расширение ImageBind с лучшим language coverage. Применения: image search by text, audio search by text, cross-modal RAG (документы с картинками + видео + текст).
>
>     **Пример.** Meta для AR/VR в Quest использует ImageBind: пользователь говорит «найди игру где можно сразиться с драконом» → text encoder в embedding → ANN search по library, где каждая игра имеет thumbnail (image embedding) + audio preview (audio embedding) + description (text embedding) — все в одном space. Cohere multimodal в 2024 используется в e-commerce: «найди диван похожий на этот + светлый цвет» (image + text query) → multi-input search.
>
>     **Когда применять.** Multimodal RAG (PDF с диаграммами, slides decks с image + text), cross-modal search (Pinterest visual search с text refinement), accessibility (alt-text generation через image embedding → nearest text), content moderation (детекция запрещённого по image+text+audio одновременно).
>
>     **Подводные камни.** Quality на cross-modal значительно ниже unimodal: CLIP на чисто text→text retrieval уступает text-embedding-3 в 2 раза; CLIP-like модели имеют bias датасета (LAION-5B содержит много веб-spam). ImageBind с 6 модальностями — research-grade, в production обычно используют 2–3 модальности. Storage cost при индексировании multi-modal данных растёт линейно по числу модальностей.
>
>     **Связанные вопросы.** [[embeddings-interview#Q19]] CLIP image embeddings; [[embeddings-interview#Q20]] audio embeddings; [[embeddings-interview#Q28]] применения embeddings.
>
> - [ ] C. ImageBind поддерживает только text и image, как и CLIP — других модальностей в multimodal embeddings пока нет.
>
>     **Что на самом деле.** ImageBind (Meta, 2023) специально дизайнировался как многомодальный — 6 модальностей: text, image, audio, video, IMU (inertial sensors), depth. Это его ключевое отличие от CLIP, который только text+image.
>
>     **Откуда путаница.** CLIP — самая известная multimodal модель, на ней многие останавливаются. ImageBind менее на слуху.
>
>     **Если бы это было правдой.** Не было бы demo Meta где можно искать «трёхмерный звук на видео» через ImageBind — а это публичные демки.
>
>     **Как было бы правильно.** ImageBind = 6 модальностей в одном space; CLIP = 2 модальности; LanguageBind, AudioCLIP, VideoCLIP — расширения для специфичных пар.
>
> - [ ] D. Multimodal embeddings в 2025 имеют такое же качество как text-only — миграция всегда оправдана.
>
>     **Что на самом деле.** Text-only embeddings значительно более зрелые: больший масштаб обучения, лучшие benchmarks, развитая экосистема. Multimodal — растущая область, на чисто text-similarity задачах уступает specialized text models. Использовать multimodal только когда нужна именно multi-modal capability.
>
>     **Откуда путаница.** Хайп вокруг multimodal AI создаёт впечатление готовности к production. Реальность: для текстовых задач text-only сильнее.
>
>     **Если бы это было правдой.** Все мигрировали бы на CLIP для общего semantic search — но реально для text-only используют OpenAI/Cohere/Voyage.
>
>     **Как было бы правильно.** Multimodal — для специфичных multi-modal use cases; для текстовых задач text-only embeddings качественнее.

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


> [!mcq] Как оптимизировать стоимость embedding pipeline в production?
>
> - [ ] A. Embeddings стоят столько же, сколько LLM-вызовы, и являются основной статьёй расходов RAG-системы.
>
>     **Что на самом деле.** text-embedding-3-small = $0.02 за 1M токенов, GPT-4o = $2.50 input + $10 output за 1M. Embeddings в 100–500× дешевле LLM. Главная статья расходов RAG — генерация ответа, не embedding.
>
>     **Откуда путаница.** «Все API стоят дорого» — обобщённая интуиция без знания цен. Менеджеры/архитекторы экстраполируют LLM-pricing на embedding.
>
>     **Если бы это было правдой.** Стартапы экономили бы только на embeddings, игнорируя LLM-расходы. Реально приоритет оптимизации — LLM (prompt caching, smaller models, reranker'ы для меньшего context).
>
>     **Как было бы правильно.** При cost-budgeting: LLM = 90–99% расходов; embeddings = 1–10%. Оптимизация в этом порядке.
>
> - [x] B. Стек оптимизаций: выбор small model по умолчанию, Matryoshka truncation, кеширование повторных запросов, batch requests, self-hosting при больших объёмах, инкрементальная индексация без re-embed неизменённых документов.
>
>     **Развёрнутое объяснение.** Полный набор техник: (1) **Small models** — text-embedding-3-small вместо large при ~5% разнице качества; экономия 6.5×. (2) **Matryoshka truncation** — `dimensions=512` параметр сокращает storage в 3× при 5–10% потере качества. (3) **Caching** — `MD5(text) → embedding` в Redis с TTL; embedding детерминирован для одного входа модели, можно кешировать навсегда (до смены модели). (4) **Batching** — Pinecone/Cohere принимают 96 текстов за один request; latency и cost эффективнее. (5) **Self-host break-even** — при ~10M tokens/месяц GPU дешевле API (примерно). (6) **Incremental indexing** — не re-embed документ, если его content hash не изменился. (7) **Async pipeline** — embed ночью, не realtime.
>
>     **Пример.** Notion для индексирования 100M документов клиентов использовал хитрый pipeline: text-embedding-3-small с `dimensions=512` ($0.02/1M tokens × ~50 tokens/doc avg = $100 на корпус), Redis cache хитов 30% на повторных text fragments (экономия $30), incremental re-indexing раз в день только для изменённых документов. Итого ~$70 на полную индексацию, vs $5000+ при наивном подходе с large model и full dimensions.
>
>     **Когда применять.** При scaling RAG-системы от прототипа к production (10K → 10M+ документов); при mounting cost ($1000+/месяц на embeddings); при cost-sensitive проектах (стартапы, бесплатные tier'ы); при batched data ingestion (ночные ETL).
>
>     **Подводные камни.** Cache invalidation при смене embedding model — обязательно полный flush. Self-host TCO включает GPU rent + engineer salary + monitoring — реальный break-even выше чем pure API cost suggest. Batch latency — embed раз в час vs realtime — может конфликтовать с user expectations свежего поиска.
>
>     **Связанные вопросы.** [[embeddings-interview#Q23]] caching стратегии; [[embeddings-interview#Q24]] Matryoshka; [[embeddings-interview#Q29]] частые проблемы.
>
> - [ ] C. Self-hosting всегда дешевле API независимо от объёма — overhead инфраструктуры пренебрежимо мал.
>
>     **Что на самом деле.** Self-host TCO: GPU rent ($0.5–3/час × 24 × 30 = $360–2160/мес), engineer time для setup/maintenance, observability, fallback на API при сбоях. При < 10M tokens/месяц API дешевле и проще. Break-even обычно где-то в районе 10M–50M tokens/месяц.
>
>     **Откуда путаница.** Naive расчёт «GPU = $X, API = $Y, GPU < Y значит дешевле» игнорирует engineer time и operational overhead.
>
>     **Если бы это было правдой.** Стартапы массово self-hosted'или бы с первого дня — но они стартуют с API и мигрируют только при масштабе.
>
>     **Как было бы правильно.** API при малых объёмах + privacy не критична; self-host при >10M/мес или privacy-требованиях.
>
> - [ ] D. Batch API дороже single requests из-за extra processing overhead на стороне провайдера.
>
>     **Что на самом деле.** Batch обычно одинаково по цене с single (per-token billing) или дешевле (OpenAI batch API 50% скидка с 24h SLA). Главное преимущество — throughput: меньше HTTP overhead, лучше GPU utilization на стороне провайдера. Самый дешёвый способ массового embedding.
>
>     **Откуда путаница.** «Batch» иногда ассоциируется с premium-services (Google BigQuery has batch loads), что неверно для embedding API.
>
>     **Если бы это было правдой.** Все индексировали бы по одному документу для экономии — но это в 10–100× медленнее и часто бьёт rate limits.
>
>     **Как было бы правильно.** Batch — preferred way: cheaper or equal cost + 10–100× throughput.

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


> [!mcq] Как правильно кешировать embeddings и когда инвалидировать кеш?
>
> - [ ] A. Кэшировать embeddings нет смысла — API embedding достаточно быстрое, и cache добавляет сложности.
>
>     **Что на самом деле.** API embedding latency = 50–200ms; cache hit в Redis = <1ms. При high-throughput retrieval (повторные popular queries, autocomplete, suggested searches) cache даёт 100–200× ускорение. Также экономит cost — повторные queries не идут в API.
>
>     **Откуда путаница.** «Cache — premature optimization» — общий принцип, но не для embedding-pipeline, где повторные запросы статистически частые.
>
>     **Если бы это было правдой.** Никто не использовал бы embedding cache — но он стандартная часть LangChain, LlamaIndex, Pinecone SDK.
>
>     **Как было бы правильно.** Embedding cache — high-ROI оптимизация: minimal код (Redis), большая экономия latency+cost.
>
> - [x] B. Стандартная схема: hash(text) как cache key → Redis с embedding value; экономит cost и latency для повторных queries; TTL зависит от частоты обновления текстов и embedding-model lifecycle.
>
>     **Развёрнутое объяснение.** Embeddings детерминированы: один и тот же текст + одна модель = идентичный вектор. Это идеальное свойство для кеширования. Pattern: `key = "emb:v3:" + hashlib.sha256(text.encode()).hexdigest()` (version в ключе для безопасной миграции), `value = serialized embedding (msgpack/pickle)`, `TTL = 30 days` для динамичного контента или unbounded для immutable документов. Cache invalidation важна **только** при: (1) смене embedding model — новые векторы несовместимы со старым space; (2) изменении текста — но это решается hash-based ключом автоматически. Cache hit rate в production обычно 30–70% (повторные queries, popular search terms).
>
>     **Пример.** Stripe для AI-assistant в documentation портале использует Redis cache для embeddings: TTL = 7 дней (docs обновляются), prefix `emb:cohere-v3:` (защита от model collision при миграции). Cache hit rate ~55% (top-200 запросов покрывают большую часть трафика). Экономия: $400/мес на API + p95 latency упал с 180ms до 35ms.
>
>     **Когда применять.** Любой production embedding pipeline с повторными queries: autocomplete/suggestions, popular searches, A/B testing моделей на тех же запросах, dev/staging environments, RAG-системы с stable knowledge base.
>
>     **Подводные камни.** Cache key должен включать model version (`emb:v3:` vs `emb:v3-small:`), иначе случайные коллизии при миграции. Не кешировать PII текст в shared Redis без шифрования. Memory budget: при 1M уникальных текстов × 1536 dims × 4 bytes ≈ 6GB — Redis должен это вынести. Cache-aside pattern: при miss embed и положить, не блокировать запрос на синхронизацию.
>
>     **Связанные вопросы.** [[embeddings-interview#Q22]] cost optimization; [[embeddings-interview#Q29]] частые проблемы; [[embeddings-interview#Q5]] OpenAI embeddings.
>
> - [ ] C. Cache нужно инвалидировать при каждом deployment приложения — это best practice для consistency.
>
>     **Что на самом деле.** Embeddings зависят **только** от входного текста и embedding model, не от версии приложения. Deployment, который не меняет model, не должен инвалидировать cache. Иначе теряется весь смысл кеша.
>
>     **Откуда путаница.** Defensive programming + общая «invalidate on deploy» best practice для backend-кешей переносится на embedding cache, где это не нужно.
>
>     **Если бы это было правдой.** Cache hit rate был бы около 0 для команд с CI/CD — деплои каждые 30 минут. Реально cache переживает деплои.
>
>     **Как было бы правильно.** Инвалидировать только при смене embedding model (через version в cache key).
>
> - [ ] D. При смене embedding-модели (например ada-002 → text-embedding-3-small) cache invalidation не нужна — векторы совместимы.
>
>     **Что на самом деле.** Разные модели имеют разные embedding spaces (даже одинаковая размерность 1536d не означает совместимость). Использовать ada-002 cached vector в индексе на text-embedding-3 → silent retrieval degradation. Обязателен либо полный re-embed корпуса, либо включение model name в cache key (тогда старые ключи естественно protokol, новые embed заново).
>
>     **Откуда путаница.** «Тот же провайдер, тот же endpoint» — но семантика embedding spaces разная.
>
>     **Если бы это было правдой.** Можно было бы мигрировать модели без re-embed корпуса. Реально это major incident, требующий dual-write/dual-read периода.
>
>     **Как было бы правильно.** Cache key должен включать model identifier; при смене модели старые ключи теряют валидность естественно; индекс vector DB полностью переиндексируется.

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


> [!mcq] Что такое Matryoshka embeddings и зачем они нужны?
>
> - [ ] A. Truncation первых N dimensions применима к любой embedding-модели для уменьшения размера индекса.
>
>     **Что на самом деле.** Для обычных embedding-моделей (sentence-transformers без MRL, ada-002, multilingual-e5) первые N dimensions не несут «больше информации» — все измерения примерно равноважны. Truncate первых N → случайный subset → деградация качества 30–80%.
>
>     **Откуда путаница.** Аналогия с PCA, где первые компоненты — главные. Это не работает для transformer embeddings без специальной тренировки.
>
>     **Если бы это было правдой.** Можно было бы тривиально уменьшить любой embedding в 8× — но эксперименты на не-MRL моделях показывают катастрофическое падение recall.
>
>     **Как было бы правильно.** Truncation работает только для MRL-trained моделей (OpenAI text-embedding-3, Nomic Embed); для других нужны PCA или fine-tuning.
>
> - [x] B. Matryoshka Representation Learning обучает embeddings так, что любой prefix-subset (первые N dims) остаётся валидным; 3072→512 даёт ~5–10% потери качества; используется для tiered search и storage экономии.
>
>     **Развёрнутое объяснение.** MRL (Matryoshka Representation Learning, paper 2022) — техника обучения с loss-функцией, которая одновременно оптимизирует embedding на нескольких размерностях (например, full 3072 + 1536 + 768 + 384 + 192). Модель учится распределять информацию так, что первые 192 dims несут «грубую» семантику, последующие добавляют детали. На inference можно truncate до любого `dimensions` и получить рабочий embedding. Главные модели с MRL в 2025: OpenAI text-embedding-3 (поддерживает `dimensions` параметр), Nomic Embed v1.5, JinaAI v2. Use cases: (1) **Storage экономия** — индексировать в 512d вместо 3072d. (2) **Tiered search** — сначала ANN-поиск по 256d (быстро) → rerank top-100 по full 3072d (precision); даёт скорость малого dim при качестве full.
>
>     **Пример.** Reddit для search API использовал Matryoshka подход: первичный ANN-индекс на 256d (4× быстрее full), затем rerank top-50 на полных 1536d через secondary index. P95 latency уменьшился с 80ms до 22ms, recall@10 деградация 2% — компенсируется reranker. Storage: primary index = 256GB вместо 1.5TB.
>
>     **Когда применять.** Большие корпуса (>10M документов) с storage constraints; high-throughput retrieval (>1K QPS) с tight latency SLO; cost-sensitive проекты, где RAM/SSD дорогие; A/B тесты разных dimensions без перепрогона embedding API.
>
>     **Подводные камни.** Не все embedding-модели поддерживают MRL — нужно проверить model card. Truncation > 6× (например 3072→256) даёт заметную деградацию (15–25%) — экстремальная экономия не бесплатна. Tiered search требует двух индексов (primary truncated + secondary full) — оперативная сложность. При смене модели MRL property не переносится автоматически — старые truncated векторы несовместимы с новой моделью.
>
>     **Связанные вопросы.** [[embeddings-interview#Q5]] OpenAI text-embedding-3; [[embeddings-interview#Q12]] dimensions; [[embeddings-interview#Q25]] quantization.
>
> - [ ] C. Matryoshka embeddings можно truncate только до половины исходного размера — дальше идёт катастрофическое падение качества.
>
>     **Что на самом деле.** MRL-модели обычно обучены на нескольких размерностях: full + 1/2 + 1/4 + 1/8 + 1/16. То есть 3072→1536, 3072→768, 3072→384, 3072→192 — все валидные точки. Quality деградация плавная, не катастрофическая. OpenAI документация явно поддерживает `dimensions=256` для text-embedding-3-large (3072→256).
>
>     **Откуда путаница.** Без чтения paper по MRL можно ассумировать «половина» как предел.
>
>     **Если бы это было правдой.** OpenAI не предлагал бы `dimensions=256` параметр для 3072d модели — но это поддерживается официально.
>
>     **Как было бы правильно.** Truncate можно до любой размерности, обученной во время MRL (обычно power-of-2 от full); чем меньше, тем больше потери, но catastrophe только при экстремальных коэффициентах.
>
> - [ ] D. Tiered search через Matryoshka не даёт преимущества над прямым поиском по full dimensions — лишняя сложность.
>
>     **Что на самом деле.** Tiered: сначала 256d поиск (4–12× быстрее) для recall стадии, затем rerank top-50 на full 3072d для precision стадии. Итоговая precision близка к full-dim search, но latency = малый dim search + reranking. На корпусах M+ это значительный выигрыш по cost/latency.
>
>     **Откуда путаница.** Tiered search кажется сложной: «зачем два прохода, если один работает». Реально на масштабе это стандартный паттерн (Google search, Bing, modern semantic search).
>
>     **Если бы это было правдой.** Pinecone, Qdrant не имели бы dedicated tiered search APIs — но они активно их продвигают.
>
>     **Как было бы правильно.** Tiered с Matryoshka — golden combo для large-scale retrieval; cost saving и latency reduction без существенной потери quality.

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


> [!mcq] Что такое quantization embeddings и какой trade-off у каждого уровня?
>
> - [ ] A. Binary quantization непригодна для semantic search из-за катастрофической потери качества.
>
>     **Что на самом деле.** Binary quantization (1 bit/dim) даёт 5–10% recall loss при 32× compression. Для корпусов 10M+ это часто приемлемо, особенно с reranking. Без quantization 100M × 1536d × 4 bytes = 600GB — на одну vector DB реплику слишком много.
>
>     **Откуда путаница.** «1 bit вместо 32 bits — теряем 31/32 информации» — наивная интуиция. Реально direction вектора сохраняется через знак каждого dim, что несёт большую часть семантики.
>
>     **Если бы это было правдой.** Yandex, Pinterest, Cohere не использовали бы binary quantization для гигантских индексов — но они активно её применяют.
>
>     **Как было бы правильно.** Binary quantization приемлема для масштаба, особенно с tiered/reranking подходом.
>
> - [x] B. Quantization снижает precision вектора: fp32→fp16 (2× compression), int8 (4×), binary (32×); binary использует Hamming distance (XOR+popcount), очень быстро; binary даёт 5–10% recall loss при 32× экономии.
>
>     **Развёрнутое объяснение.** Embeddings обычно хранятся как float32 (4 bytes/dim). Quantization сокращает это: (1) **fp16** — half-precision, 2 bytes/dim, ~0.5% recall loss. Безопасный default для большинства production. (2) **int8** — линейная квантизация в [-128, 127], 1 byte/dim, ~1–2% recall loss. Также называется scalar quantization. (3) **binary** — 1 bit/dim (знак значения), 0.125 bytes/dim. Comparison через Hamming distance (XOR + popcount) — в 10–100× быстрее float dot product. ~5–10% recall loss. (4) **Product Quantization (PQ)** — разбивает вектор на subvectors, каждый кодируется в codebook; используется в FAISS. Quantization применяется именно при хранении в vector DB — это её основная цель.
>
>     **Пример.** Cohere для своего production retrieval API использует int8 quantization: 1024d × 1 byte = 1KB per vector (vs 4KB при fp32). Для 1B документов: 1TB вместо 4TB. На retrieval benchmarks int8 даёт 0.96–0.98 от fp32 recall — приемлемо. Pinecone и Qdrant поддерживают binary quantization для гигантских индексов: Pinterest перешёл на binary для 1B+ image embeddings, экономия RAM в 32×.
>
>     **Когда применять.** Большие индексы (>10M vectors) — quantization обязательна для cost/RAM. fp16 — почти бесплатно для всех; int8 — стандарт production; binary — для extreme scale (>100M vectors). Также — на edge devices (mobile, embedded) где RAM ограничена.
>
>     **Подводные камни.** Quantization-aware retrieval (rescore с full precision на top-K) часто улучшает quality — стоит подключать reranking. Binary quantization несовместима с большинством vector DBs — поддерживается в Pinecone, Qdrant; pgvector только-только начал поддержку. Scalar (int8) quantization требует training данных для калибровки min/max диапазона — наивный подход даёт overflow/underflow.
>
>     **Связанные вопросы.** [[embeddings-interview#Q12]] dimensions; [[embeddings-interview#Q22]] cost optimization; [[embeddings-interview#Q24]] Matryoshka.
>
> - [ ] C. int8 quantization даёт абсолютно те же результаты, что и fp32 — нет потери качества.
>
>     **Что на самом деле.** int8 даёт ~1–2% recall loss из-за дискретизации. Приемлемо для большинства задач, но не zero loss. Для критичных применений (legal e-discovery, medical retrieval) могут предпочесть fp16 или fp32.
>
>     **Откуда путаница.** int8 quantization в LLMs (GPTQ, AWQ) — почти lossless, что переносится на embedding.
>
>     **Если бы это было правдой.** Не было бы trade-off между int8 и fp16 — но они оба используются в зависимости от quality requirements.
>
>     **Как было бы правильно.** int8 ~ 1–2% recall loss; fp16 ~ 0.5% — выбор по quality SLA.
>
> - [ ] D. Quantization применяется только при inference (compute query embedding), не при хранении документ-векторов.
>
>     **Что на самом деле.** Главная цель quantization — сократить storage и память для индекса (миллионы/миллиарды документ-векторов). Inference compute обычно не bottleneck для одного query. Quantization применяется к векторам **в vector DB**, не к query embedding.
>
>     **Откуда путаница.** В LLM-контексте quantization применяется к весам модели для быстрого inference — переносится на embedding некорректно.
>
>     **Если бы это было правдой.** Pinecone/Qdrant не рекламировали бы quantization как способ снижения cost индекса — но это их главное benefit.
>
>     **Как было бы правильно.** Quantization при хранении: документ-векторы в vector DB сжимаются; query можно квантизовать или нет (зависит от quantization symmetry).

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


> [!mcq] Когда стоит делать fine-tuning embedding-модели, а когда — нет?
>
> - [ ] A. Fine-tuning всегда улучшает качество и обязателен для любой production embedding-системы.
>
>     **Что на самом деле.** Fine-tuning стоит $500–5000 на обучение + инженерное время на data preparation + ongoing maintenance. Для generic content (news, blogs, общие документы) современные pretrained модели (OpenAI, Cohere, BGE) дают отличный recall — fine-tuning не окупится. Большинство production RAG-систем используют off-the-shelf модели без fine-tuning.
>
>     **Откуда путаница.** ML-mindset: «больше custom = лучше». На практике для embedding-моделей pretrained scale (миллиарды pairs) обходит почти любой in-house fine-tuning на десятках тысяч пар.
>
>     **Если бы это было правдой.** Каждый стартап с RAG делал бы fine-tuning сразу — реально это редкая практика, появляющаяся на втором году эксплуатации при достижении production scale.
>
>     **Как было бы правильно.** Сначала off-the-shelf модель + правильный chunking + reranker; fine-tuning только при measurable gap recall.
>
> - [ ] B. Fine-tuning имеет смысл даже при <100 domain-specific documents — главное начать собирать данные.
>
>     **Что на самом деле.** Эффективный contrastive fine-tuning требует минимум 1000–5000 golden pairs (query, relevant document). С <100 примерами модель overfits на конкретные тексты, ухудшая generalization. Лучше потратить время на сбор данных.
>
>     **Откуда путаница.** Few-shot learning в LLM (5–10 примеров → улучшение) переносится на embedding fine-tuning, где работает иначе.
>
>     **Если бы это было правдой.** Все стартапы делали бы fine-tuning на 50 примерах с первой недели — реально качество хуже off-the-shelf.
>
>     **Как было бы правильно.** Минимум 1000 golden pairs; lower bound — overfitting катастрофичен.
>
> - [x] C. Fine-tuning оправдан при domain-specific vocabulary (medical, legal, code) и измеримом recall gap у generic модели; не нужен для generic content или при <1000 training examples.
>
>     **Развёрнутое объяснение.** Decision tree: (1) Оценить recall@10 generic модели на 200+ golden pairs от своих данных. (2) Если recall > 0.7 — оставаться на pretrained, оптимизировать другим (better chunking, reranker, hybrid search). (3) Если recall < 0.7 и есть domain-specific терминология (ICD-коды для медицины, юридические термины, internal jargon) — fine-tuning оправдан. (4) Собрать минимум 1000 golden pairs (query → relevant document), желательно 5000–20K. (5) Fine-tune с MultipleNegativesRankingLoss на 3–10 epochs. Cost: $500–5000 на single GPU rent + 1–2 weeks инженерного времени. Maintenance: re-fine-tune при significant content drift (раз в 6–12 месяцев).
>
>     **Пример.** Harvey AI (legal tech) fine-tuned voyage-large-2 на 30K golden pairs из юридических документов и реальных запросов юристов. Recall@10 вырос с 0.62 до 0.81 — критично для качества RAG. Cost обучения: $3K на 8× A100 за 12 часов. ROI: precision retrieval напрямую снижает hallucinations LLM-ответов на legal questions.
>
>     **Когда применять.** Domain-specific корпуса (medical EHR, legal contracts, code repositories, financial reports), where off-the-shelf models показывают measurable gap; стабильные продукты с volume для генерации golden pairs (через user feedback, click-through, expert annotation); среды с MLOps capabilities для maintenance.
>
>     **Подводные камни.** Golden pairs labeling expensive (юрист час $200–500); synthetic generation через LLM экономит, но добавляет шум. Fine-tuned модель несовместима с pretrained — все embeddings нужно re-generate. Model drift: данные меняются (новые юридические термины, новые продукты) — нужен periodic re-tune. Лицензии: некоторые модели (Cohere) не разрешают fine-tuning без enterprise license.
>
>     **Связанные вопросы.** [[embeddings-interview#Q27]] как fine-tune; [[embeddings-interview#Q11]] MTEB; [[embeddings-interview#Q10]] выбор модели.
>
> - [ ] D. Fine-tuning устраняет необходимость в правильном chunking, normalization и других preprocessing-шагах.
>
>     **Что на самом деле.** Fine-tuning улучшает domain understanding embedding-модели. Chunking, normalization, deduplication, hybrid search — независимые инфраструктурные практики. Fine-tuned модель так же требует proper preprocessing, как и off-the-shelf.
>
>     **Откуда путаница.** «Custom модель = всё решает» — наивный взгляд. Реально preprocessing и retrieval architecture не менее важны, чем выбор модели.
>
>     **Если бы это было правдой.** Команды с fine-tuned моделями игнорировали бы chunking. На практике fine-tuning — последний шаг оптимизации после vetted preprocessing.
>
>     **Как было бы правильно.** Сначала наладить preprocessing + retrieval architecture; fine-tuning — для остаточного quality gap.

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


> [!mcq] Как правильно fine-tune embedding-модель через sentence-transformers?
>
> - [ ] A. Для fine-tuning достаточно собрать negative pairs (нерелевантные пары query-document) — positive pairs модель найдёт сама.
>
>     **Что на самом деле.** Без positive pairs модель не знает, что считать «похожим». Contrastive learning требует именно positive pairs (query, relevant document); negatives обычно берутся автоматически из batch (in-batch negatives) или mined через hard negative mining.
>
>     **Откуда путаница.** Слово «contrastive» включает «negatives» — может казаться, что они первичны. Реально positive pairs — обязательное ядро.
>
>     **Если бы это было правдой.** Sentence-transformers API не имел бы `InputExample(texts=[anchor, positive])` — но именно это его базовый формат.
>
>     **Как было бы правильно.** Собрать positive pairs; negatives возьмутся автоматически через MultipleNegativesRankingLoss.
>
> - [x] B. Базовый подход: `InputExample(texts=[query, relevant_doc])` для positive pairs + MultipleNegativesRankingLoss (in-batch negatives); главная работа — собрать 1000+ golden pairs.
>
>     **Развёрнутое объяснение.** Pipeline: (1) Подготовить данные как list of `InputExample(texts=[query, positive_document])`. (2) Загрузить базовую модель: `model = SentenceTransformer('sentence-transformers/all-mpnet-base-v2')`. (3) Выбрать loss: `MultipleNegativesRankingLoss(model)` — самый эффективный для retrieval, использует in-batch negatives (для каждой позитивной пары все остальные документы в batch служат негативами). (4) `DataLoader` с batch_size 16–64 (бóльший batch → больше негативов → лучше). (5) `model.fit(train_objectives=[(loader, loss)], epochs=3, warmup_steps=100)`. (6) Сохранить и провалидировать на eval set. Альтернативные losses: **TripletLoss** (нужны triplets anchor/positive/negative — больше аннотации), **CosineSimilarityLoss** (для pairs с similarity score 0–1). Главная работа — не код, а сбор данных.
>
>     **Пример.** Команда внутреннего search в банке fine-tuned all-mpnet-base-v2 на 5K golden pairs (запрос сотрудника → правильная страница из internal wiki). Собирали через click data: запрос → top-clicked результат (после filtering ad-hoc запросов). 3 epoch на single A100, batch_size 32, lr=2e-5. Recall@10 вырос с 0.58 (off-the-shelf) до 0.81 (fine-tuned) на их domain.
>
>     **Когда применять.** Domain-specific retrieval (medical, legal, internal company); измеренный recall gap у off-the-shelf модели; наличие данных для golden pairs (через click logs, expert annotation, synthetic gen через LLM); готовность к ongoing maintenance.
>
>     **Подводные камни.** Batch size критичен: больше = больше негативов = лучше; но больше = больше VRAM. Hard negative mining (специальный pipeline для извлечения сложных негативов) даёт +5–10% качества, но усложняет setup. Overfitting на маленьких datasets — отслеживать val loss, early stopping. Не использовать тот же dataset для train и eval — splitting обязателен 80/10/10.
>
>     **Связанные вопросы.** [[embeddings-interview#Q26]] когда fine-tuning; [[embeddings-interview#Q3]] обучение embedding моделей; [[embeddings-interview#Q8]] sentence-transformers.
>
> - [ ] C. CrossEntropyLoss — стандартный loss для fine-tuning embedding моделей.
>
>     **Что на самом деле.** CrossEntropyLoss используется для classification (предсказать class label). Для contrastive embedding learning используют specialized losses: MultipleNegativesRankingLoss, TripletLoss, CosineSimilarityLoss, ContrastiveLoss. Они оптимизируют именно distance между парами в embedding space.
>
>     **Откуда путаница.** CrossEntropyLoss — most-known loss в deep learning (для классификации). Перенос на embedding-задачу некорректен.
>
>     **Если бы это было правдой.** Sentence-transformers не предлагал бы 10+ specialized losses — но они активно документированы и используются.
>
>     **Как было бы правильно.** Для embedding fine-tuning — contrastive losses (MNRL, Triplet, Cosine); CrossEntropy — для classification head.
>
> - [ ] D. Одного epoch обучения достаточно для эффективного fine-tuning — больше приводит к переобучению.
>
>     **Что на самом деле.** 1 epoch на маленьком dataset (1000–5000 pairs) даёт underfitting — модель не успевает адаптировать веса. Стандарт: 3–10 epochs с validation set и early stopping. Переобучение возникает при 15+ epochs на маленьких datasets — отслеживается через val loss.
>
>     **Откуда путаница.** Pretrained модели хорошо адаптируются — кажется, что и 1 epoch достаточно. Реально для contrastive learning нужно несколько проходов через data.
>
>     **Если бы это было правдой.** Все туториалы рекомендовали бы epochs=1 — но стандартная рекомендация 3–10.
>
>     **Как было бы правильно.** 3–5 epochs с val loss monitoring; early stopping при росте val loss; saved best checkpoint.

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


> [!mcq] Какие применения embeddings есть помимо RAG?
>
> - [ ] A. Embeddings применяются только для search и RAG-задач — других применений нет.
>
>     **Что на самом деле.** Embeddings — fundamental technology с десятками применений: recommendations (Netflix, Spotify), anomaly detection (fraud, security), clustering (user segmentation), classification (sentiment, intent), deduplication (DocSearch, Google Docs), personalization (user profile как embedding), knowledge graph entity linking, plagiarism detection.
>
>     **Откуда путаница.** RAG-хайп с 2023 года сделал embedding синонимом «retrieval для LLM». Реально embeddings шире.
>
>     **Если бы это было правдой.** Netflix, Spotify, Amazon не использовали бы embeddings — реально они на них построены.
>
>     **Как было бы правильно.** RAG — одно из многих применений; embeddings — широкая foundational technology.
>
> - [x] B. RAG — лишь одно из многих применений; embeddings также используются для recommendations, clustering, anomaly detection, deduplication, personalization (user profile = avg liked items), classification, knowledge graphs.
>
>     **Развёрнутое объяснение.** Spectrum применений: (1) **Recommendations** — Netflix, Spotify, Amazon: embed users (как avg of liked items) и products → nearest neighbor matching. (2) **Semantic Search** — Google, Bing, Notion: embed queries + documents, retrieve closest. (3) **Clustering** — group documents/users без labels (K-means на embeddings); customer segmentation. (4) **Anomaly Detection** — outliers в embedding space (fraud detection, network security). (5) **Classification** — embed text + linear classifier (sentiment analysis, spam detection). (6) **Deduplication** — Google Docs, GitHub: find near-duplicate documents через cosine threshold. (7) **Personalization** — user = embedding (среднее эмбеддингов interacted items), персонализированный feed. (8) **Plagiarism detection** — Turnitin: embed paragraphs, find similar в database. (9) **Entity linking** — knowledge graph construction.
>
>     **Пример.** Spotify Discover Weekly: для каждого user считается embedding профиля (взвешенное среднее эмбеддингов прослушанных треков). Каждую неделю система ищет треки, embedding которых ближе всего к user embedding, но которые user ещё не слышал. То же ANN-поиск на 100M треков, что и в RAG, но семантика — personalization.
>
>     **Когда применять.** Любая задача типа «найти похожее» — embeddings первый выбор: similar products, similar users, similar incidents, similar customer support tickets. Также — feature extraction для downstream ML (embeddings как input для classifier даёт +10–20% accuracy против raw text features).
>
>     **Подводные камни.** Для personalization (user embedding) важна стратегия aggregation: simple average smooth-out preferences, weighted by recency captures shift. Anomaly detection требует tuning threshold; embedding distribution может быть skewed. Classification через embeddings уступает full fine-tuned model на ~5% accuracy — берите тогда, когда нужна цена/скорость.
>
>     **Связанные вопросы.** [[embeddings-interview#Q2]] зачем embeddings; [[embeddings-interview#Q19]] image embeddings; [[embeddings-interview#Q22]] cost optimization.
>
> - [ ] C. Embeddings для classification всегда хуже, чем fine-tuned BERT с classification head — разница принципиальная.
>
>     **Что на самом деле.** Embedding + linear classifier — это, по сути, и есть fine-tuned encoder с classification head, просто разделённый на два шага: extract embedding → train shallow classifier. Качество сопоставимо; разница в гибкости (один embedding model для multiple downstream tasks) и cost (embedding можно кешировать).
>
>     **Откуда путаница.** Terminology suggests, что это разные подходы. Реально — две упаковки одной идеи.
>
>     **Если бы это было правдой.** Никто не использовал бы embeddings для classification — реально это широко распространено (HuggingFace pipelines, Cohere classifier API).
>
>     **Как было бы правильно.** Embedding + linear ≈ fine-tuned encoder + classification head; выбор по operational considerations.
>
> - [ ] D. Deduplication требует exact byte-match; embeddings для near-duplicate detection не подходят.
>
>     **Что на самом деле.** Near-duplicate detection — классическое embedding-применение. «Одинаковое по смыслу, разные формулировки» (`"Refund policy" vs "Money-back guarantee"`) — задача для cosine similarity, не для byte hash. Threshold обычно 0.9+ similarity. Используется в Google Docs, GitHub (duplicate issues), academic plagiarism.
>
>     **Откуда путаница.** Слово «duplicate» часто ассоциируется с exact copy. Near-duplicate — отдельный термин, требующий semantic similarity.
>
>     **Если бы это было правдой.** Google Docs не предлагал бы «похожие документы» — реально это feature, основанная на embeddings.
>
>     **Как было бы правильно.** Exact dedup — через hash; near-dup detection — через embeddings + threshold.

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


> [!mcq] Какие частые проблемы у embedding pipelines в production?
>
> - [ ] A. Главная и единственная проблема — неправильный выбор dimensions; всё остальное вторично.
>
>     **Что на самом деле.** Dimensions — один из tradeoffs, но не топ-проблема production. Топ-проблемы: query/doc model mismatch (silent retrieval degradation), embedding drift при смене модели (несовместимость старых vectors), плохой chunking (потеря recall), отсутствие normalization (inconsistent scores), cost runaway без cache. Dimensions можно перебрать на этапе выбора; остальное — operational.
>
>     **Откуда путаница.** Dimensions — самое часто обсуждаемое в туториалах. Реально operational issues важнее.
>
>     **Если бы это было правдой.** Production incidents были бы только из-за dimensions — реально большинство из-за model mismatch и drift.
>
>     **Как было бы правильно.** Dimensions — выбор на старте; основные проблемы production — consistency и drift.
>
> - [x] B. Типичные production-проблемы: embedding drift при смене модели (нужен полный re-embed), query/document model mismatch, плохое chunking (короткие или огромные chunks), отсутствие normalization, cost runaway без cache, и обработка низко-ресурсных языков для multilingual.
>
>     **Развёрнутое объяснение.** Полный production checklist: (1) **Embedding drift** — при смене модели старые vectors несовместимы с новыми; обязательно dual-write + re-embed корпуса перед switch. (2) **Query/doc mismatch** — индексировали Cohere, ищут OpenAI embeddings → случайные результаты. Версионирование model в metadata. (3) **Chunking** — too short (50 токенов, нет контекста) или too long (1500+ токенов, теряются детали); оптимально 200–1000. (4) **Normalization** — половина index normalized, половина нет → inconsistent similarity scores; контроль через `assert np.linalg.norm(vec) ≈ 1`. (5) **Short queries** — 1–2 слова дают шумные embeddings; для autocomplete нужны другие техники. (6) **Cost runaway** — embed everything, never cache; bill месяц $5000+ вместо $500. (7) **Distance metric mismatch** — Euclidean для нормализованных embeddings vs cosine; ставит wrong ranking. (8) **Treating embeddings as compression** — нельзя reconstruct text из embedding, нужен исходный.
>
>     **Пример.** Команда мигрировала с ada-002 на text-embedding-3-small для cost-cutting. Забыли re-embed существующий индекс на 50M документов. Production retrieval начал давать случайные результаты — query (новая модель) и doc embeddings (старая модель) в разных spaces. Заметили через падение конверсии на 30% спустя 2 недели. Решение: emergency rollback к ada-002 + параллельный re-embed корпуса в новую модель + atomic switch.
>
>     **Когда применять.** Production checklist при запуске embedding-системы; periodic audit (раз в квартал); инцидент-анализ при странных retrieval-результатах. Также — onboarding документация для новых ML-инженеров команды.
>
>     **Подводные камни.** Многие проблемы «silent» — нет exception, просто плохой recall. Нужны метрики: NDCG@10, recall@K на golden set; alerting на их деградацию. Также: embedding pipeline часто не тестируется как обычный код — добавьте integration tests на смену моделей и chunking parameters.
>
>     **Связанные вопросы.** [[embeddings-interview#Q10]] выбор модели; [[embeddings-interview#Q13]] normalization; [[embeddings-interview#Q23]] caching.
>
> - [ ] C. Embedding drift — маловероятная проблема, поскольку embedding модели стабильны и редко обновляются.
>
>     **Что на самом деле.** OpenAI менял модели регулярно: ada-001 → ada-002 → text-embedding-3. Hugging Face модели часто обновляются (sentence-transformers релизы). При каждой смене старые vectors несовместимы с новой моделью — silent retrieval degradation. Это real-world issue.
>
>     **Откуда путаница.** Embedding модели менее на виду, чем LLM (GPT-3.5 → GPT-4 → GPT-4o заметная новость). Тихие апгрейды embedding моделей пропускаются.
>
>     **Если бы это было правдой.** Не нужны были бы migration playbooks — реально OpenAI документация явно про migration steps от ada-002 к text-embedding-3.
>
>     **Как было бы правильно.** Embedding drift — реальная operational задача; миграция требует careful planning (dual-write, re-embed, atomic switch).
>
> - [ ] D. Normalization при наличии векторов уже в vector DB больше не важна — DB сама обработает.
>
>     **Что на самом деле.** Если в существующий нормализованный индекс добавить ненормализованный vector → inconsistent similarity scores. Длинные ненормализованные векторы получают несправедливо высокие dot product, короткие — низкие. Vector DB не «исправит» это автоматически (зависит от metric).
>
>     **Откуда путаница.** «Уже в DB — значит OK» — наивная интуиция. Без consistency mechanism inserts могут испортить index.
>
>     **Если бы это было правдой.** Не нужны были бы guides по миграции normalization — реально это частый pitfall при mixing моделей или upgrading pipeline.
>
>     **Как было бы правильно.** Все vectors в одном индексе должны быть обработаны идентично; integrity check `||v||=1` для случайной выборки в monitoring.

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
