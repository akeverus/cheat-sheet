# Векторные базы данных — редакция MCQ

- Дата: 2026-11-04; модель: GPT-6.
- Статус: `LLM_READY`. Это содержательная редакция одной модели и структурная проверка, не независимая приёмка и не `FINAL PASS` прежнего процесса.
- Охват: видимые Q1–Q28, все 28 блоков B0, все варианты A–D и их `sections`.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/ai-ml/vector-databases-interview.json` — SHA-256 `21535023af360f6ac1e4111c6fd8683ab7c8c150b7dc0d2a268076862959213f`.
- Парный Markdown: `cheatsheets/interview/ai-ml/vector-databases-interview.md` — SHA-256 `06e089de077bb419fe44eab48ffc6b8306f697c5e80573d83f21088c6cb0d3c9`.

## Что исправлено

- Во всех 28 блоках согласованы ключ, варианты и объяснения с видимым вопросом и теорией. Убраны ложные гарантии по скорости, recall, памяти и порогам числа векторов.
- Q1–Q10: различены точный поиск и ANN; уточнены размерность embedding, нормализация и метрики, HNSW, IVF и размер только PQ-кодов.
- Q11–Q18: разведены библиотеки и сервисы; учтены сохранение Faiss, режимы Chroma, возможности `pgvector`, модульность Weaviate и настраиваемая согласованность Milvus. Убраны неподтверждённые внутренний алгоритм и фиксированная цена Pinecone.
- Q19–Q28: уточнены RRF, влияние фильтра на recall, изоляция tenants, шардирование и репликация, квантование, расчёт полного объёма и перенос embeddings без обязательного пересчёта.
- В Markdown обновлены пролог и источники, содержание привязано к стабильным якорям Q1–Q28, удалены редакционные маркеры `(!)`.

После редакции выборочно перечитаны Q3, Q17 и Q20 вместе с вариантами и объяснениями. Это внутренняя калибровка, а не слепое ревью. 28 блоков содержательно изменены; один возврат был вызван ошибкой схемы Q2 и исправлен. Отдельный расход и точное время для этой редакции не измерялись.

Повторный авторский проход охватил варианты всех 28 блоков. Неверные ответы переписаны как конкретные технические заблуждения вместо очевидных гарантий и запретов; в Q5, Q10 и Q23 дополнительно обновлены все четыре поля `sections` каждого изменённого варианта. В остальных блоках сохранён прежний смысл ошибки и сверены соответствующие пояснения. Независимой проверки после этого прохода не было.

## Проверенные первичные источники

- [pgvector README](https://github.com/pgvector/pgvector): точный поиск по умолчанию, HNSW/IVFFlat, фильтрация и iterative scans.
- [Faiss: Index IO](https://github.com/facebookresearch/faiss/wiki/Index-IO%2C-cloning-and-hyper-parameter-tuning) и [IDSelector](https://github.com/facebookresearch/faiss/wiki/Setting-search-parameters-for-one-query): сохранение индексов и ограничение поиска по ID.
- [Chroma: клиент-серверный режим](https://docs.trychroma.com/docs/run-chroma/client-server) и [клиенты](https://docs.trychroma.com/reference/python): локальный, серверный и облачный варианты.
- [Weaviate: модули](https://docs.weaviate.io/weaviate/configuration/modules): векторизаторы и генеративные интеграции подключаются отдельно.
- [Qdrant: payload и индексы](https://qdrant.tech/documentation/concepts/payload/), [query points](https://api.qdrant.tech/api-reference/search/query-points): фильтры и актуальный API.
- [Milvus: согласованность](https://milvus.io/docs/consistency.md): четыре уровня, по умолчанию bounded staleness.
- [Elasticsearch: dense_vector](https://www.elastic.co/docs/reference/elasticsearch/mapping-reference/dense-vector) и [RRF](https://www.elastic.co/docs/reference/elasticsearch/rest-apis/reciprocal-rank-fusion): индексированный векторный поиск и слияние по рангам.
- [Pinecone: обзор](https://docs.pinecone.io/guides/get-started/overview) и [backup](https://docs.pinecone.io/guides/manage-data/back-up-an-index): управляемый поиск, фильтры, namespaces и резервные копии.

## Открытые вопросы и проверка

Критических неразрешённых фактов или второго защитимого ключа после редакции не выявлено. Производительность, стоимость и пригодность конкретной БД для production остаются предметом измерений на заданном корпусе и конфигурации.

`bash scripts/verify-mcq-json.sh modules/quiz-app/src/main/resources/seed/mcq/ai-ml/vector-databases-interview.json` → `PASS: files=1 questions=28 blocks=28; semantic review NOT_RUN`.
