---
title: "Вопросы на собеседовании: RAG (Retrieval-Augmented Generation)"
description: "RAG: загрузка документов, chunking, embeddings, retrieval (vector search, hybrid, reranking), prompt templates, evaluation, продвинутые паттерны (HyDE, Self-RAG, GraphRAG)"
tags:
  - interview
  - ai-ml
  - rag-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "RAG"
  - "Retrieval-Augmented Generation"
  - "RAG interview"
prerequisites: []
next: []
updated: "2026-05-19"
---
# Вопросы на собеседовании: `RAG (Retrieval-Augmented Generation)`

**RAG** — паттерн, при котором LLM **получает релевантные документы** в context перед генерацией ответа. Главный способ дать модели **специфичные знания** (документация компании, knowledge base) без fine-tuning. Стек: chunking → embeddings → vector DB → retrieval → reranking → LLM. Появился в 2020, стал mainstream в 2023.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [RAG paper (Lewis et al., 2020)](https://arxiv.org/abs/2005.11401)
- [LangChain RAG Documentation](https://python.langchain.com/docs/use_cases/question_answering/)
- [LlamaIndex RAG Guide](https://docs.llamaindex.ai/en/stable/optimizing/production_rag/)
- [Anthropic Contextual Retrieval](https://www.anthropic.com/news/contextual-retrieval)
- [Cohere RAG Guide](https://docs.cohere.com/docs/retrieval-augmented-generation-rag)
- [HyDE Paper](https://arxiv.org/abs/2212.10496)
- [Self-RAG Paper](https://arxiv.org/abs/2310.11511)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые понятия**
- [Q1. (!) Что такое RAG?](#q1--что-такое-rag)
- [Q2. (!) Зачем RAG если можно просто давать всё в context?](#q2--зачем-rag-если-можно-просто-давать-всё-в-context)
- [Q3. (!) Чем RAG отличается от fine-tuning?](#q3--чем-rag-отличается-от-fine-tuning)
- [Q4. Архитектура RAG системы?](#q4-архитектура-rag-системы)

**Indexing pipeline**
- [Q5. (!) Loading документов?](#q5--loading-документов)
- [Q6. (!) Chunking — что это и как делать?](#q6--chunking--что-это-и-как-делать)
- [Q7. (!) Размер chunk — как выбрать?](#q7--размер-chunk--как-выбрать)
- [Q8. Overlap между chunks?](#q8-overlap-между-chunks)
- [Q9. (!) Metadata в chunks?](#q9--metadata-в-chunks)
- [Q10. Embeddings — генерация для chunks?](#q10-embeddings--генерация-для-chunks)

**Retrieval**
- [Q11. (!) Vector search (similarity search)?](#q11--vector-search-similarity-search)
- [Q12. (!) k (число retrieved docs) — как выбрать?](#q12--k-число-retrieved-docs--как-выбрать)
- [Q13. (!) Hybrid search (vector + keyword)?](#q13--hybrid-search-vector--keyword)
- [Q14. (!) Reranking — что и зачем?](#q14--reranking--что-и-зачем)
- [Q15. Filters / metadata-based search?](#q15-filters--metadata-based-search)

**Generation**
- [Q16. (!) Prompt template для RAG?](#q16--prompt-template-для-rag)
- [Q17. Citations / sources — как делать?](#q17-citations--sources--как-делать)
- [Q18. Что делать, если retrieval не нашёл ничего?](#q18-что-делать-если-retrieval-не-нашёл-ничего)

**Продвинутые паттерны**
- [Q19. (!) Query rewriting / expansion?](#q19--query-rewriting--expansion)
- [Q20. (!) HyDE (Hypothetical Document Embeddings)?](#q20--hyde-hypothetical-document-embeddings)
- [Q21. Self-RAG — динамическое решение нужен ли retrieval?](#q21-self-rag--динамическое-решение-нужен-ли-retrieval)
- [Q22. Multi-query retrieval?](#q22-multi-query-retrieval)
- [Q23. (!) Contextual Retrieval (Anthropic)?](#q23--contextual-retrieval-anthropic)
- [Q24. (!) GraphRAG?](#q24--graphrag)
- [Q25. Agentic RAG?](#q25-agentic-rag)

**Evaluation**
- [Q26. (!) Как тестировать RAG систему?](#q26--как-тестировать-rag-систему)
- [Q27. (!) Метрики (precision, recall, faithfulness, answer relevance)?](#q27--метрики-precision-recall-faithfulness-answer-relevance)
- [Q28. RAGAS, TruLens?](#q28-ragas-trulens)

**Production**
- [Q29. (!) Какие подводные камни RAG в production?](#q29--какие-подводные-камни-rag-в-production)
- [Q30. Какой стек выбрать (LangChain, LlamaIndex, custom)?](#q30-какой-стек-выбрать-langchain-llamaindex-custom)

## Q1. (!) Что такое RAG?

**RAG (Retrieval-Augmented Generation)** — паттерн, при котором перед вызовом LLM:
1. **Retrieve** релевантные документы из knowledge base
2. **Augment** prompt этими документами
3. **Generate** ответ на основе документов + question

```
User: "Какая политика по отпускам в нашей компании?"
  ↓
Retrieve: vector search в HR documents → top-3 chunks
  ↓
Prompt LLM: "Documents: [chunks]. Question: ... Answer based on documents."
  ↓
Response: "Согласно документу X (стр. 5), сотрудники могут..."
```

**Зачем:**
- Дать LLM **актуальные и приватные** данные
- Уменьшить hallucinations
- Citations (откуда взят ответ)
- Без переобучения модели


## Q2. (!) Зачем RAG если можно просто давать всё в context?

**Проблемы "все в context":**

1. **Context limit** — даже 1M tokens не хватает на TBs документов
2. **Cost** — больше tokens = дороже
3. **Latency** — большой context медленнее обрабатывается
4. **"Lost in the middle"** — модели хуже находят информацию в середине context
5. **Отсутствие фильтрации** — модель видит много нерелевантного

**RAG решает:** даёт **только релевантные** chunks (3-10 штук), вместо всего корпуса.


## Q3. (!) Чем RAG отличается от fine-tuning?

| Критерий | RAG | Fine-tuning |
|----------|-----|-------------|
| Что меняет | Контент в prompt | Параметры модели |
| Свежесть данных | Real-time (обновил vector DB) | Frozen (нужно retrain) |
| Cost | Per-query (vector search + LLM) | One-time training + cheaper inference |
| Сложность setup | Средняя | Высокая |
| Источники цитирования | Легко (есть chunks) | Нет (модель "знает") |
| Privacy | Локальные docs не уходят в OpenAI | Аналогично |
| Изменения модели | Легко поменять (RAG works с любой LLM) | Привязан к конкретной модели |

**Best practice:** для большинства задач — **RAG**. Fine-tuning — для **style** (как писать) и **special skills** (классификация). Часто **combine** оба.


## Q4. Архитектура RAG системы?

```mermaid
graph TD
    subgraph "Indexing (offline)"
        Docs[Documents] --> Chunker[Chunker]
        Chunker --> Embedder[Embedding model]
        Embedder --> VDB[(Vector DB)]
    end

    subgraph "Query (online)"
        Q[User query] --> QEmbed[Embedding]
        QEmbed --> Retrieve[Vector search]
        Retrieve --> VDB
        VDB --> Rerank[Reranker]
        Rerank --> Prompt[Build prompt]
        Prompt --> LLM
        LLM --> Answer[Answer]
    end
```

**Compoenents:**
1. **Chunker** — разбивает документы
2. **Embedder** — text → vector
3. **Vector DB** — хранит embeddings
4. **Retriever** — finds top-k similar
5. **Reranker** (optional) — точная ранжировка
6. **Prompt builder** — формирует prompt
7. **LLM** — генерирует ответ


## Q5. (!) Loading документов?

**Источники:**
- PDF, Word, Markdown, HTML
- Confluence, Notion, Google Docs
- Slack, Discord history
- GitHub repos
- Database tables, structured data
- Audio (через transcription) → text

**Tools:**
- **Unstructured** — multi-format parser
- **PyMuPDF, pdfplumber** — PDF
- **BeautifulSoup, trafilatura** — HTML
- **Docling** (IBM) — современный multi-format

**Подвох:** парсеры по-разному обрабатывают tables, images, footnotes. Quality of parsing critically важен для RAG.


## Q6. (!) Chunking — что это и как делать?

**Chunking** — разбивка документов на маленькие куски (chunks) для индексации.

**Стратегии:**

1. **Fixed-size** — N tokens / N chars
2. **Recursive** — splits по separator hierarchy ("\n\n", "\n", ". ", " ")
3. **Semantic chunking** — по смысловым границам (через embedding similarity)
4. **Markdown-aware** — sections, headers
5. **Code-aware** — functions, classes
6. **Sentence-based** — по предложениям

```python
# LangChain RecursiveCharacterTextSplitter
splitter = RecursiveCharacterTextSplitter(
    chunk_size=1000,
    chunk_overlap=200,
    separators=["\n\n", "\n", ". ", " ", ""]
)
chunks = splitter.split_text(document)
```

**Critical:** хороший chunking — половина успеха RAG.


## Q7. (!) Размер chunk — как выбрать?

**Trade-off:**
- **Маленькие** chunks (200-500 tokens) — точный retrieval, но мало context
- **Большие** chunks (1000-2000 tokens) — больше context, но менее precise

**Рекомендации:**
- **Q&A на конкретные facts:** 200-500 tokens
- **Длинные ответы / synthesis:** 800-1500 tokens
- **Code:** function/class boundaries
- **Tables / structured:** keep table whole

**Эксперимент** — нет one-size-fits-all. Тестируй с **golden dataset**.


## Q8. Overlap между chunks?

```
Chunk 1: tokens 0-1000
Chunk 2: tokens 800-1800  (200 token overlap)
Chunk 3: tokens 1600-2600
```

**Зачем:** информация на границах chunks не теряется.

**Размер overlap:** обычно **10-20%** от chunk size (100-300 tokens).

**Trade-off:** больше overlap = больше storage, больше cost.


## Q9. (!) Metadata в chunks?

```python
chunk = {
    "text": "...",
    "embedding": [0.12, -0.45, ...],
    "metadata": {
        "source": "policy_2025.pdf",
        "page": 5,
        "section": "Vacation Policy",
        "date": "2025-01-15",
        "department": "HR",
        "language": "en"
    }
}
```

**Зачем metadata:**
- **Filtering** — "только HR documents" / "за 2025 год"
- **Citations** — return source в ответе
- **Multi-tenancy** — фильтр по `tenant_id`
- **Hybrid search** — combine vector + metadata filters

Vector DBs поддерживают metadata filters: Pinecone, Weaviate, Qdrant.


## Q10. Embeddings — генерация для chunks?

```python
from openai import OpenAI
client = OpenAI()

# OpenAI embeddings
response = client.embeddings.create(
    model="text-embedding-3-large",
    input=chunk_text
)
vector = response.data[0].embedding  # 3072-dim вектор

# Сохраняем в vector DB вместе с metadata
```

**Embedding models:**
- **OpenAI** `text-embedding-3-large` (3072d), `text-embedding-3-small` (1536d)
- **Cohere** embed-english-v3 (1024d)
- **Voyage AI** (specialized для RAG)
- **Open-source:** sentence-transformers (`all-MiniLM-L6-v2`, `bge-large`)

Подробнее — в [Embeddings](embeddings-interview.md).


## Q11. (!) Vector search (similarity search)?

```python
# 1. Embed query
query_emb = embedder.embed("Сколько дней отпуска?")

# 2. Search в vector DB
results = vector_db.search(
    vector=query_emb,
    top_k=5,
    filter={"department": "HR"}
)

# 3. Получаем top-5 chunks с similarity scores
for result in results:
    print(result.text, result.score)
```

**Метрики similarity:**
- **Cosine similarity** — самая частая (нечувствительна к magnitude)
- **Dot product** — быстрее (если вектора нормализованы — равно cosine)
- **Euclidean distance** — менее частая

**Под капотом** — ANN (Approximate Nearest Neighbor) algorithms: HNSW, IVF, ScaNN. Подробнее — [Vector Databases](vector-databases-interview.md).


## Q12. (!) k (число retrieved docs) — как выбрать?

**Trade-off:**
- **Маленькое k** (3-5) — быстро, мало context, рискуем потерять релевантное
- **Большое k** (10-20) — больше шанса найти, больше cost, "lost in the middle"

**Best practice:**
- Базовая RAG: **k=5**
- С reranking: **retrieve k=20-50, rerank → top 5**
- С большим context (Claude 200K): **k=15-30**


## Q13. (!) Hybrid search (vector + keyword)?

**Vector search** хорош для семантики. **Keyword search** (BM25) хорош для exact matches.

**Hybrid:**
1. Vector search → top-50
2. BM25 keyword search → top-50
3. **Reciprocal Rank Fusion (RRF)** или weighted merge

```python
def rrf(rankings, k=60):
    scores = {}
    for ranking in rankings:
        for i, doc in enumerate(ranking):
            scores[doc] = scores.get(doc, 0) + 1 / (k + i)
    return sorted(scores.items(), key=lambda x: -x[1])
```

**Когда нужен hybrid:**
- Технические термины, names, IDs (BM25 лучше)
- Code search
- Compliance / legal (точные термины)

Поддерживают: Weaviate, Qdrant, Elasticsearch, Pinecone.


## Q14. (!) Reranking — что и зачем?

**Reranking** — после initial retrieval, **более точная** модель пересматривает порядок.

```
1. Vector search → top-50 candidates (быстро, грубо)
2. Reranker (cross-encoder) → top-5 (медленно, точно)
3. Send top-5 в LLM
```

**Cross-encoder vs bi-encoder:**
- **Bi-encoder** (для embeddings): query и doc embeddings отдельно, потом similarity
- **Cross-encoder** (reranker): берёт `(query, doc)` пару, выдаёт single score — точнее, но медленнее

**Models:**
- **Cohere Rerank** (managed)
- **Jina Reranker**
- **bge-reranker** (open-source)
- **Voyage Rerank**

**Effect:** обычно +10-20% к relevance метрикам. **Always use reranker** в production RAG.


## Q15. Filters / metadata-based search?

```python
results = vector_db.search(
    vector=query_emb,
    top_k=5,
    filter={
        "tenant_id": "acme_corp",
        "date": {"$gte": "2024-01-01"},
        "language": "en"
    }
)
```

**Применения:**
- **Multi-tenancy** — каждый tenant видит только свои данные
- **Permissions** — только разрешённые docs
- **Time-based** — recent docs
- **Domain filters** — HR vs Engineering

**Подвох:** жёсткие filters могут сильно сузить выбор → no results.


## Q16. (!) Prompt template для RAG?

```
Ты ассистент компании. Ответь на вопрос пользователя на основе предоставленных документов.
Если ответ не найден в документах, скажи "Не нашёл информации в доступных источниках."
Всегда указывай источник в формате [Source: filename, page X].

<documents>
[Doc 1] policy_2025.pdf, p.5: Сотрудники имеют 28 дней оплачиваемого отпуска...
[Doc 2] handbook.pdf, p.12: Дополнительные дни предоставляются...
</documents>

<question>
{user_question}
</question>

Ответ:
```

**Best practices:**
- Чёткие инструкции (не отвечать без источников)
- Явное разделение docs и question (XML tags / маркеры)
- Запрос на citations
- Instruction "если не знаю — скажи"
- Format ответа


## Q17. Citations / sources — как делать?

**Подходы:**

1. **Inline citations:** "Согласно [policy_2025.pdf:5], сотрудники..."
2. **References секция:** "...текст ответа.\n\nSources: [1] policy_2025.pdf, [2] handbook.pdf"
3. **Structured output:** JSON `{"answer": "...", "sources": [{"doc": "...", "page": 5}]}`

**Verification:** после генерации проверить, что **все факты в ответе** действительно в retrieved docs (через secondary LLM или string matching).

```python
# Anthropic Claude — нативная поддержка citations
response = client.messages.create(
    model="claude-opus-4-5",
    messages=[{"role": "user", "content": [
        {"type": "document", "source": doc_data, "citations": {"enabled": true}},
        {"type": "text", "text": question}
    ]}]
)
# response.content включает сгенерированные citations
```


## Q18. Что делать, если retrieval не нашёл ничего?

**Scenarios:**
1. **Empty retrieval results** (например, threshold не пройден)
2. **Retrieved docs не содержат ответ** (LLM сам распознаёт)

**Стратегии:**
- LLM говорит "Не знаю" (instruction в prompt)
- **Fallback** — общий LLM ответ без RAG (с дисклеймером)
- **Query rewriting** — может user спросил неудачно?
- **Web search** fallback (если разрешено)
- **Escalation** к human (для chatbots)
- **Return empty** + предложить уточнить

**Не давать ложных уверенных ответов** — это потеря trust.


## Q19. (!) Query rewriting / expansion?

Иногда query пользователя плохой для retrieval.

```
User: "Сколько отпуск?"
Bad для retrieval (мало контекста)

→ LLM rewrite: "Какова продолжительность ежегодного оплачиваемого отпуска для сотрудников?"
→ Лучше для vector search
```

**Multi-query:**

```python
# LLM генерирует 3-5 вариантов query
queries = [
    "Сколько дней отпуска у сотрудников?",
    "Vacation policy duration",
    "Annual leave entitlement"
]
results = []
for q in queries:
    results.extend(vector_db.search(embed(q), top_k=5))
deduplicated = dedupe(results)
```

**Query expansion** — добавить synonyms, related terms.


## Q20. (!) HyDE (Hypothetical Document Embeddings)?

**Идея:** вместо embed query — попросить LLM **сгенерировать hypothetical answer**, и embed его.

```
User query: "Какая политика по удалёнке?"
  ↓ LLM generates hypothetical answer
"В компании разрешена удалённая работа. Сотрудники могут работать..."
  ↓ embed hypothetical answer
  ↓ vector search
```

**Зачем:** hypothetical answer семантически ближе к **актуальным документам**, чем сама query (queries обычно короткие, документы длинные).

**Trade-off:** один extra LLM call. Но улучшает recall в 10-20% случаев.


## Q21. Self-RAG — динамическое решение нужен ли retrieval?

**Self-RAG** — LLM сама решает, нужен ли retrieval (special tokens).

```
[Retrieve] → trigger retrieval
[No-Retrieve] → ответить из training knowledge
[Relevant] → этот retrieved doc релевантен
[Irrelevant] → пропустить
```

Заменяет always-retrieve подход. Полезно для **открытых вопросов** (когда retrieval может ничего не дать).

Требует **fine-tuned LLM** (специально обученный распознавать эти tokens).


## Q22. Multi-query retrieval?

LLM генерирует **несколько reformulations** query → retrieve по каждой → merge.

```python
queries = llm.generate_queries(original_query, n=5)
all_results = []
for q in queries:
    all_results.extend(vector_db.search(embed(q), top_k=5))
final = dedupe_and_rerank(all_results)
```

Improves **recall** для ambiguous queries.


## Q23. (!) Contextual Retrieval (Anthropic)?

**Anthropic Contextual Retrieval** (2024) — улучшение embeddings через **chunk context**.

**Идея:** перед embed каждого chunk — попросить Claude **сгенерировать short context**:

```
Original chunk: "...this transaction was reversed."

Context (Claude generates):
"This is from Q3 2024 financial report, section on customer refunds.
The transaction refers to..."

Combined для embedding:
[Context] + [Original chunk]
```

**Эффект:** retrieval faliure rate снижается **на 49%** (по measurement Anthropic).

Cost: Claude API для генерации context на каждый chunk (можно делать batch). Но prompt caching делает это дёшево.


## Q24. (!) GraphRAG?

**GraphRAG** (Microsoft, 2024) — RAG поверх **knowledge graph**, не просто vector search.

**Pipeline:**
1. **LLM extracts entities and relationships** из документов → graph
2. **Cluster graph** в communities
3. **LLM generates summaries** для каждой community
4. При query — retrieve relevant communities + entities

**Зачем:** для вопросов **глобального уровня** ("в чём основные темы документа?"), где обычный RAG плох (видит только chunks, не overall structure).

**Минус:** дорого построить (много LLM calls для extraction).

В **2025** — растущая адопция в enterprise.


## Q25. Agentic RAG?

**Agentic RAG** — LLM как **agent**, который может:
- Решать когда retrieve
- Из каких источников retrieve (несколько vector DBs)
- Reformulate queries
- Verify результаты
- Re-retrieve если нужно

```python
# Псевдо-код
def agentic_rag(query):
    while not satisfied:
        decision = llm.decide_action(query, context)
        if decision == "retrieve":
            chunks = retrieve(query, source=decision.source)
            context.append(chunks)
        elif decision == "rewrite":
            query = llm.rewrite(query, feedback)
        elif decision == "answer":
            return llm.answer(query, context)
```

Подробнее — в [AI Agents](ai-agents-interview.md).


## Q26. (!) Как тестировать RAG систему?

**Golden dataset** — manually curated `(question, expected_answer, source_chunks)`:

```json
{
  "question": "Сколько дней отпуска?",
  "expected_answer": "28 дней",
  "expected_sources": ["policy_2025.pdf:5"],
  "topics": ["HR", "vacation"]
}
```

**Тестируем:**
1. **Retrieval** — нашли ли expected_sources среди retrieved chunks?
2. **Generation** — содержит ли answer expected_answer?
3. **Faithfulness** — все ли утверждения в answer основаны на retrieved chunks?
4. **Answer relevance** — отвечает ли на question?

**LLM-as-judge** — другая LLM оценивает quality (с rubric).


## Q27. (!) Метрики (precision, recall, faithfulness, answer relevance)?

**Retrieval metrics:**
- **Precision@k** — какая доля retrieved chunks релевантна?
- **Recall@k** — какая доля релевантных chunks была retrieved?
- **MRR (Mean Reciprocal Rank)** — насколько высоко в списке релевантный?
- **NDCG** — discounted cumulative gain

**Generation metrics:**
- **Faithfulness** — не выдумала ли модель факты?
- **Answer relevance** — отвечает ли на question?
- **Context utilization** — использовала ли retrieved chunks?
- **Answer correctness** — правильный ли факт?


## Q28. RAGAS, TruLens?

**RAGAS** — Python framework для evaluation RAG:

```python
from ragas import evaluate
from ragas.metrics import faithfulness, answer_relevancy, context_precision

result = evaluate(
    dataset,  # questions, answers, contexts
    metrics=[faithfulness, answer_relevancy, context_precision]
)
```

**TruLens** — observability + evaluation для LLM/RAG. Tracking, dashboards.

**Phoenix (Arize)** — open-source LLM observability.

В **production RAG** — обязательно непрерывная evaluation. Без неё не понять, deteriorate ли система.


## Q29. (!) Какие подводные камни RAG в production?

1. **Bad chunking** — теряется context, splits sentences
2. **Embedding model mismatch** — query embedding vs doc embedding (разные models)
3. **Stale data** — vector DB не synced с источниками
4. **Multi-tenancy leak** — RAG возвращает чужие data
5. **Hallucinations** — даже с RAG модель может выдумывать
6. **Cost runaway** — каждый query = embedding + retrieval + LLM
7. **Slow retrieval** — большая vector DB без индексов
8. **Too many docs in context** — модель путается ("lost in the middle")
9. **Prompt injection** — через документы (instructions внутри)
10. **No evaluation** — не знаем quality, regressions проходят незамеченно


## Q30. Какой стек выбрать (LangChain, LlamaIndex, custom)?

| Стек | Pros | Cons |
|------|------|------|
| **LangChain** | Богатые tools, agents | Сложный API, частые breaking changes |
| **LlamaIndex** | RAG-first, удобный | Меньше agents tools |
| **Haystack** (Deepset) | Production-ready | Меньше популярен |
| **Custom** | Полный control, понятно что происходит | Больше кода |

**Best practice 2025:**
- **LlamaIndex** для RAG-heavy систем
- **LangChain** для agents с инструментами
- **Custom** для production-critical (контроль = надёжность)

Многие переходят на **custom** после прохождения через LangChain pain.


---

## See also

- [LLM Basics](llm-basics-interview.md) — основа
- [Vector Databases](vector-databases-interview.md) — storage для embeddings
- [Embeddings](embeddings-interview.md) — основа vector search
- [Prompt Engineering](prompt-engineering-interview.md) — для RAG prompts
- [AI Agents](ai-agents-interview.md) — agentic RAG
- [LLM Integration Patterns](llm-integration-patterns-interview.md) — production
- [MLOps](mlops-interview.md) — operations для RAG
- [Model Serving](model-serving-interview.md) — для embedding models
- [Caching](../architecture/caching-strategies-interview.md) — для embeddings cache
- [Микросервисы](../architecture/microservices-interview.md) — где RAG живёт
- [Elasticsearch](../databases/elasticsearch-interview.md) — для hybrid search
