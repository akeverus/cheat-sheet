# AI architecture — продукты и ёмкость: авторская проверка теории

Дата: 2026-11-05. Область: **Q32/Q33**, включая уже существовавшее незакоммиченное добавление Q33 и его TOC. Остальные 31 Q не принимались. Это авторская проверка с видимыми MCQ-ключами, не независимый blind review, Phase A/B или FINAL.

Теория: [ai-application-architecture-interview.md](../../cheatsheets/interview/ai-ml/ai-application-architecture-interview.md), SHA-256 `f2d50c37c0a82c367e5538546ca2ea24071ab0401b29aecf81b7724aaf39dc3d`.
MCQ, только чтение: `modules/quiz-app/src/main/resources/seed/mcq/ai-ml/ai-application-architecture-interview.json`, SHA-256 `43c9b45feb7c9b7a5165bd439a5f24a02563595d4f5db8f3413bbeefa235e4d0`.

## Что исправлено

Q32 теперь различает документированные возможности конкретного режима и архитектурную интерпретацию. Оставлены четыре продукта из заголовка с первичными ссылками. Удалены универсальное «все серьёзные продукты используют RAG+tools+FT», неподтверждённая физическая схема Notion index per workspace, внутренний self-reflection Devin и лишние строки Copilot/Claude/ChatGPT с неподтверждёнными обобщениями. Наличие инструмента поиска не доказывает векторный индекс или FT.

Текущая [Cursor Search](https://cursor.com/docs/agent/tools/search) описывает локальный Instant Grep без embeddings для этого поиска; [Agent overview](https://cursor.com/docs/agent/overview) — инструменты поиска/редактирования/терминала. Старый security page описывал embeddings: его нельзя без даты/версии выдавать за универсальное текущее устройство продукта. [Notion Enterprise Search](https://www.notion.com/help/enterprise-search) подтверждает поиск и цитирование по workspace/подключённым источникам. [Perplexity Pro Search](https://www.perplexity.ai/help-center/en/articles/10352903-what-is-pro-search) описывает несколько поисков и синтез со ссылками именно для указанного режима; это не доказательство web retrieval каждого сообщения во всех режимах. [Devin Session Tools](https://docs.devin.ai/work-with-devin/devin-session-tools) подтверждает Shell/IDE/Browser и участие человека, а не частную реализацию self-reflection.

Q33 проверен по [OpenAI rate limits](https://developers.openai.com/api/docs/guides/rate-limits), [Claude rate limits](https://platform.claude.com/docs/en/api/rate-limits), [vLLM metrics](https://docs.vllm.ai/en/latest/usage/metrics/) и [Kubernetes HPA](https://kubernetes.io/docs/concepts/workloads/autoscaling/horizontal-pod-autoscale/). Сохранены лимиты RPM/токенов, конечная очередь/дедлайн и concurrency. Добавлены учёт встроенных retries SDK в общем бюджете, отказ/откладывание при Retry-After за дедлайном и согласование общей квоты реплик либо безопасное разделение бюджетов. Последнее — проектный вывод, не утверждение о внутреннем алгоритме провайдера; согласовано с LLM Integration Q14.

## Передать владельцу MCQ — OPEN Q32 B0

В прочитанном snapshot правильный **D.text**, **A.what_actually/how_it_should_be** утверждают Cursor embedding search, code-specific FT и speculative decoding. D также утверждает Notion per-workspace index, Devin self-reflection и Copilot edge autocomplete. Для этих деталей нужны отдельные подтверждения с областью применимости; текущий Search опровергает универсальность старой модели поиска.

**B.what_actually**, **D.example** утверждают online retrieval на каждый запрос. **C.what_actually/how_it_should_be**, **D.explanation/edge_cases** распространяют RAG+tools+немного FT на все серьёзные продукты. Переписать вопрос/варианты и sections под проверяемые возможности и явно обозначенные выводы; перенос старого D в новую теорию не подходит. JSON здесь не менялся. Q33 B0 B и прочие варианты в рассмотренном snapshot принципиального противоречия обновлённой теории не содержат; это не формальная приёмка их неугадываемости.

## Проверки и ограничения

Сохранены IDs/порядок 33 Q, явные anchors и весь текст вне Q32/Q33; header относительно исходного рабочего снимка меняет только updated на 2026-11-05. Относительно HEAD дополнительно присутствуют ранее созданные TOC/Q33. YAML frontmatter, fences, локальные пути/anchors изменённых Q и whitespace PASS. Структурная проверка трёх связанных seeds: 79 Q / 79 блоков PASS, semantic review NOT_RUN. Ни приложение провайдера, ни rate limiter, ни vLLM/HPA/SDK retries не запускались. Все источники читались 2026-11-05. Полная согласованность Q32 с MCQ пока OPEN.
