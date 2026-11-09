# AI Agents — редакция MCQ

- Дата: 2026-11-09; модель: GPT-6.
- Авторский статус после отдельной ревизии: `VISUAL_PARITY_REVIEWED`. [Отчёт о визуальной симметрии](ai-agents-visual-parity-2026-11-09.md). Независимого слепого ревью и `FINAL PASS` нет.
- Охват: Q1–Q28, 28 блоков, все видимые вопросы, варианты A–D, sections и парная теория.
- Production JSON: `modules/quiz-app/src/main/resources/seed/mcq/ai-ml/ai-agents-interview.json`; SHA-256 `943fbef357a39da62109e525e09d704b8c4cea32636aa17c9146ecb3e05ef127`.
- Парный Markdown: `cheatsheets/interview/ai-ml/ai-agents-interview.md`; SHA-256 `9f0b0cdf36b7b7d59f0e0470fa71b2445580662c6fc4af0cd9b6521520eaaaa7`.
- Fact-freshness: `docs/mcq-quality/fact-freshness/ai-agents-interview.json`; SHA-256 `3270269695aace236f78ad1c92fb50aa0ddee538ac0fe44ad1b5356853a9c51f`.

## Что исправлено

Вычитаны все 112 видимых вариантов и их sections. Уточнены полные ответы для правильных и ошибочных вариантов, убраны категоричные или непроверяемые обещания о скорости, цене и надёжности. Правильные ключи сохранены. В Q25 вопрос и теория теперь отдельно объясняют, когда подтверждение человека обязательно: это определяется полномочиями и правилами операции.

Теория разделяет заранее заданный workflow и агентный маршрут, ReAct и Plan-and-Execute, краткосрочный контекст и внешнюю память. Для циклов описаны условия остановки, лимиты и проверка действий у исполнителя. При оценке агента разделены результат и наблюдаемый трейс; внутреннее reasoning модели не считается обязательной частью журнала. Для стоимости и задержки убраны постоянные численные оценки, не зависящие от задачи.

Обновлены примеры версионных API. MCP привязан к спецификации транспорта 2025-11-25 со `stdio` и `Streamable HTTP`; прежний HTTP+SSE указан как старый вариант. Описание AutoGen опирается на текущие AgentChat/Core, а импорты AutoGen 0.2 названы историческими. LangGraph и CrewAI описаны по их документации, без сведения их возможностей к одной линейной цепочке. Для Claude computer use различены ранний `computer_20241022` и актуальный в проверенной документации `computer_toolset_20260801`.

## Проверка и границы результата

Версионные факты сверены по первичным источникам: [Anthropic: Building effective agents](https://www.anthropic.com/engineering/building-effective-agents), [ReAct paper](https://arxiv.org/abs/2210.03629), [MCP transports 2025-11-25](https://modelcontextprotocol.io/specification/2025-11-25/basic/transports), [OpenAI function calling](https://developers.openai.com/api/docs/guides/function-calling), [LangGraph](https://docs.langchain.com/oss/python/langgraph/overview), [AutoGen AgentChat](https://microsoft.github.io/autogen/stable/user-guide/agentchat-user-guide/tutorial/index.html), [CrewAI Crews](https://docs.crewai.com/en/concepts/crews) и [Claude computer use](https://platform.claude.com/docs/en/agents-and-tools/tool-use/computer-use-tool). Полный перечень ссылок и рисков устаревания сохранён в fact-freshness.

Структурная проверка: `PASS: files=1 questions=28 blocks=28; semantic review NOT_RUN`. Дополнительно сверены 28 заголовков Markdown, 112 полных вариантов A–D, единственные правильные ключи и знаки окончания вариантов; `git diff --check` по тематическим файлам прошёл. Примеры не запускались против внешних API. Для `FINAL PASS` нужен независимый слепой просмотр всех Q/A–D и затем раскрытая проверка sections на этих SHA.
