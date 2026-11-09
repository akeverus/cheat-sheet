# Function Calling — редакция MCQ

- Дата: 2026-11-09; модель: GPT-6.
- Статус: `LLM_READY`; независимое слепое ревью и `FINAL PASS` не проводились.
- Охват: Q1–Q30, 30 блоков, все 120 A–D, sections и парная теория.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/ai-ml/function-calling-interview.json`; SHA-256 `e0da08968a54aa09dc13ca2e09acff9e424e106368e53dd2bd505f09f46d2c48`.
- Markdown: `cheatsheets/interview/ai-ml/function-calling-interview.md`; SHA-256 `a608de70a20290761d9851552dcc9c9a598ba139c0188ebbff9fd6d07086e95d`.
- Fact-freshness: `docs/mcq-quality/fact-freshness/function-calling-interview.json`; SHA-256 `4027d5be52260b2eda7dd909595999df3c228281d5282251df827499119529eb`.

После `LLM_READY` проведена отдельная авторская [ревизия визуальной симметрии A–D](function-calling-visual-parity-2026-11-09.md). Она обновила production JSON и его SHA, но не является независимым экзаменационным ревью или `FINAL PASS`.

## Смысловые правки

Разделены tool use, JSON mode и строгий структурированный вывод. Убраны безусловные обещания соответствия схеме для любого function calling и выдуманные проценты брака. Отказ, прерывание, бизнес-проверка и права доступа отделены от проверки формы JSON. Исправлен пример OpenAI `strict: true`: все поля включены в `required`, а необязательное значение задано через nullable.

Обновлены текущие API: Anthropic `output_config.format` и `strict: true` у tool; AI SDK `inputSchema` и `stopWhen: stepCountIs(...)`; исторический Assistants API отмечен закрытым с переходом на Responses. Примеры Gemini привязаны к Generate Content, а MCP описан как отдельный протокол подключения серверов. Убраны неподтверждённые фиксированные пороги числа tools, параметров, токенов, стоимости и таймаутов. Обработка ошибок различает восстановимые случаи и критические сбои. Правильные буквы сохранены; неверные варианты и их пояснения сверены с обновлённой теорией.

В дополнительной приёмке Q16 уточнены формы входа `bind_tools`: функция, Pydantic-класс, `BaseTool` или готовый словарь. Вывод схемы из сигнатуры относится к подходящим формам входа, а нормализация `tool_calls` — к поддерживающей это интеграции модели. В Q30 простая маршрутизация через `if/elif` признана допустимой; отвлекающий вариант теперь ошибается в отказе от проверки прав пользователя. Связанные sections и пример dispatch приведены к этому правилу.

## Источники и проверка

Сверены первичные руководства [OpenAI function calling](https://developers.openai.com/api/docs/guides/function-calling), [OpenAI Structured Outputs](https://developers.openai.com/api/docs/guides/structured-outputs), [миграция Assistants](https://developers.openai.com/api/docs/assistants/migration), [Anthropic Structured Outputs](https://platform.claude.com/docs/en/build-with-claude/structured-outputs), [Anthropic prompt caching](https://platform.claude.com/docs/en/build-with-claude/prompt-caching), [Gemini Generate Content](https://ai.google.dev/gemini-api/docs/generate-content/function-calling), [AI SDK](https://ai-sdk.dev/docs/ai-sdk-core/tools-and-tool-calling), [LangChain](https://docs.langchain.com/oss/python/integrations/chat/amazon_nova), [справочник `bind_tools`](https://reference.langchain.com/python/langchain-openai/chat_models/base/BaseChatOpenAI/bind_tools), [LlamaIndex](https://github.com/run-llama/llama_index/blob/main/llama-index-core/llama_index/core/tools/function_tool.py) и [MCP](https://modelcontextprotocol.io/specification/2025-11-25). Версионные риски записаны в fact-freshness.

Адресный валидатор: `PASS: files=1 questions=30 blocks=30; semantic review NOT_RUN`. Дополнительно сверены 30 заголовков, 120 вариантов, единственный ключ и полнота sections каждого блока; `git diff --check` прошёл. Для `FINAL` требуется независимый слепой просмотр Q/A–D и раскрытая проверка sections на зафиксированных SHA.
