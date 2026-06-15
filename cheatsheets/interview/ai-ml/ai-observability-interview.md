---
title: "Вопросы на собеседовании: AI/LLM Observability"
description: "LLM observability в production: LangSmith, Langfuse, Phoenix, OpenTelemetry GenAI, tracing, evals, cost tracking, alerting, A/B testing."
tags:
  - interview
  - ai-ml
  - ai-observability
type: "interview"
difficulty: "intermediate"
aliases:
  - "AI Observability interview"
  - "LLM Observability собеседование"
  - "LangSmith Langfuse Phoenix"
  - "OpenTelemetry GenAI"
updated: "2026-05-23"
---
# Вопросы на собеседовании: `AI/LLM Observability`

**LLM Observability** — это специализированная ветка observability для систем на базе больших языковых моделей. Классические APM-инструменты (Datadog, New Relic) видят только поверхность — latency и status code, но не отвечают на главные вопросы: **«а ответ-то хороший?»**, **«почему модель галлюцинирует на этом промпте?»**, **«куда ушло $20 000 за неделю?»**. LLM-специфичный observability закрывает эту дыру: tracing цепочек и агентов, метрики токенов и стоимости, evals прямо в проде, версионирование промптов и A/B-тесты.

**Дата актуализации:** 2026-05-23

## Полезные ссылки

### Frameworks и платформы

- [LangSmith (LangChain)](https://docs.smith.langchain.com/) — observability + evals от LangChain
- [Langfuse](https://langfuse.com/) — open-source LLM observability (self-host + cloud)
- [Arize Phoenix](https://docs.arize.com/phoenix) — OpenTelemetry-native LLM observability
- [Helicone](https://www.helicone.ai/) — proxy-based observability, no SDK changes
- [W&B Prompts](https://wandb.ai/site/prompts/) — Weights & Biases для LLM
- [OpenLLMetry](https://www.traceloop.com/openllmetry) — OTel-based vendor-neutral SDK
- [TruLens](https://www.trulens.org/) — eval-first observability
- [Datadog LLM Observability](https://www.datadoghq.com/product/llm-observability/) — enterprise APM + LLM

### OpenTelemetry GenAI

- [OTel GenAI Semantic Conventions](https://opentelemetry.io/docs/specs/semconv/gen-ai/) — официальная спека
- [OTel GenAI events](https://opentelemetry.io/docs/specs/semconv/gen-ai/gen-ai-events/) — span events для prompts/completions
- [OpenInference](https://github.com/Arize-ai/openinference) — реализация конвенций (Arize)

### Production patterns

- [Continuous eval (Google paper)](https://arxiv.org/abs/2308.05374)
- [LLM Monitoring Best Practices (Anthropic)](https://www.anthropic.com/news/safety-bulletin)
- [PII redaction (Microsoft Presidio)](https://microsoft.github.io/presidio/)

## Содержание

1. **Зачем LLM observability отдельно** — Q1, Q2
2. **Tracing и spans** — Q3, Q4, Q5
3. **OpenTelemetry GenAI** — Q6, Q7
4. **Frameworks** — Q8, Q9, Q10, Q11, Q12
5. **Метрики и alerting** — Q13, Q14, Q15, Q16
6. **PII / Privacy** — Q17, Q18
7. **Continuous eval** — Q19, Q20, Q21, Q22
8. **Agent / RAG tracing** — Q23, Q24, Q25
9. **A/B testing и cost optimization** — Q26, Q27
10. **Анти-паттерны** — Q28

---

## Q1. Почему для LLM нужен отдельный observability, мало классического APM? (!)

Классический APM измеряет, *доехал* ли запрос; LLM-observability измеряет, *хорош* ли ответ. Это два разных вопроса, и второй классическими инструментами не покрывается.

**Классический APM** (Datadog APM, New Relic, Dynatrace) заточен под детерминированные сервисы: запрос → ответ → код 200/500, метрики latency / throughput / error rate, логи со stack trace при ошибке. Здесь «200 OK» означает «всё хорошо».

**Почему этого не хватает для LLM** — у языковых моделей «всё хорошо» не выводится из HTTP-кода:

1. **Недетерминированный вывод.** Один и тот же prompt при `temperature > 0` даёт разные ответы, поэтому HTTP 200 не равно «ответ корректен». APM не отличит `"Париж — столица Франции"` от `"Берлин — столица Франции"` — для него оба «успех».
2. **Качество — это бизнес-метрика.** В обычном сервисе всё бинарно: работает или нет. В LLM нужна **шкала качества** — faithfulness, relevancy, hallucination rate, удовлетворённость пользователя.
3. **Многошаговые цепочки.** Один агентский запрос — это, например, 10 вызовов модели + 5 tool calls + 3 retrieval. APM увидит только внешний HTTP, но не внутреннюю структуру и не поймёт, на каком шаге всё пошло не так.
4. **Контекст и эмбеддинги.** Для RAG критично знать, какие чанки достали из базы, что модель реально использовала, а что проигнорировала. Классический трейс этого не хранит.
5. **Стоимость как первоклассная метрика.** В обычном APM нет понятия «$ за запрос». В LLM один плохо настроенный prompt сжигает тысячи долларов за день — стоимость нужно видеть в реальном времени.
6. **Промпты живут как код.** Версионирование промпта — это версионирование кода: нужны diff между версиями и A/B-сравнение, чего APM не предлагает.

**Вывод:** LLM observability — это **APM + eval-платформа + cost-tracker + prompt-management** в одном продукте.

---

## Q2. Какие три pillars у LLM observability? Чем отличается от классических metrics/logs/traces? (!)

Классические три столпа — **metrics, logs, traces**. В LLM-мире набор смещается: логи растворяются внутри трейсов, а на их место встаёт новый столп — **evals** (оценки качества). Получается **tracing, metrics, evals**.

**LLM-специфичные три столпа:**

1. **Tracing** — структура запроса: chain → spans (вызов модели, retrieval, tool call), иерархия parent-child, атрибуты (model, tokens, latency, cost).
2. **Metrics** — агрегаты по трафику: tokens/min, $/hour, p95 latency, eval-оценки, cache hit rate.
3. **Evals** — оценки качества ответа: LLM-as-judge, RAGAS, обратная связь пользователей, регрессия против golden dataset. Этого столпа в классике нет вообще.

**Куда делись логи?** В LLM сама пара `prompt + completion` и есть лог — она кладётся прямо в spans как `gen_ai.prompt` / `gen_ai.completion` (с маскировкой PII). Поэтому отдельный текстовый лог дублировал бы трейс и не нужен.

| Классика     | LLM эквивалент                                  |
| ------------ | ----------------------------------------------- |
| `INFO log`   | Span с `gen_ai.system.message`                  |
| `ERROR log`  | Span с `status=error` + `gen_ai.response.finish_reasons` |
| HTTP-метрики | Token metrics, cost, latency per span           |
| APM trace    | LLM trace (chain + spans)                       |
| —            | **Eval scores** (новый pillar)                  |

---

## Q3. Что такое trace и span в контексте LLM? (!)

**Trace** — это вся жизнь одного пользовательского запроса от начала до конца. Пример: пользователь спросил «расскажи про Spring» и получил ответ — всё, что произошло между этими двумя точками, и есть один trace.

**Span** — отдельный шаг внутри trace, его «кадр». В LLM-системе шаги типизированы:

- **Root span** — внешняя точка входа: HTTP-запрос или сессия чата.
- **LLM call span** — вызов модели (`gpt-4o`, `claude-3-7`).
- **Retrieval span** — поход в vector store за контекстом.
- **Tool call span** — выполнение функции (web search, calculator).
- **Embedding span** — генерация вектора.
- **Guardrail span** — проверка safety/PII.

Spans складываются в **дерево** через ссылку `parent_span_id` — это и даёт картину «что внутри чего»:

```
trace_id=abc123
└── chat_request (root)
    ├── retrieve_context (vector search)
    │   └── embedding (text → vector)
    ├── llm_call (gpt-4o)
    │   └── stream_tokens
    └── guardrail (PII check)
```

**Атрибуты span:**

- `gen_ai.system` = `openai`
- `gen_ai.request.model` = `gpt-4o`
- `gen_ai.usage.input_tokens` = 1500
- `gen_ai.usage.output_tokens` = 230
- `latency_ms` = 1200
- `cost_usd` = 0.012
- `trace.user_id` = `user_42`
- `trace.session_id` = `sess_xyz`

---

## Q4. Как устроена parent-child иерархия в trace для агента?

У агента каждый шаг рассуждения и каждый инструмент — отдельный span, вложенный в корневой `agent_invoke`. Иерархия и есть та структура, по которой потом считают стоимость, ищут затыки и ловят зацикливания.

Для **ReAct-агента** с tool calls trace выглядит так:

```
trace_id=t1
└── agent_invoke (parent=null, type=AGENT)
    ├── llm_call #1 (parent=agent_invoke, decides tool)
    │   → output: "use search('Spring AOP')"
    ├── tool_call: search (parent=agent_invoke)
    │   ├── http_get (parent=tool_call)
    │   └── result: [docs]
    ├── llm_call #2 (parent=agent_invoke, reasoning)
    │   → output: "use calculator(2+2)"
    ├── tool_call: calculator (parent=agent_invoke)
    │   → result: 4
    └── llm_call #3 (parent=agent_invoke, final answer)
        → output: "ответ пользователю"
```

**Зачем нужна иерархия:**

1. **Свёртка стоимости.** Стоимость `agent_invoke` = сумма стоимостей всех вложенных span'ов — её не нужно считать отдельно, она «всплывает» из детей.
2. **Разбор latency.** Сразу видно, где затык: модель, tool или retrieval.
3. **Отладка циклов.** Если агент зациклился, в дереве будут десятки child-вызовов модели — это видно глазом.
4. **Воспроизведение.** По сохранённому дереву можно восстановить все шаги для разбора инцидента.

**Span links** (между разными trace) нужны для асинхронных границ. Если агент A зовёт агента B через очередь, span B ссылается на span A через `links`, а не через `parent` — так сохраняется причинно-следственная связь, но без ложной вложенности (B выполняется в своём trace, а не «внутри» A).

---

## Q5. Какие атрибуты обязательны для LLM-span? (!)

Атрибуты делятся на два слоя: **метаданные запроса/ответа** (что попросили, что получили, сколько токенов) кладут в атрибуты span'а; **сам текст** prompt/completion — в span events, потому что он большой и часто содержит PII. Разделение даёт аналитику по токенам и стоимости без раздувания каждого span'а текстом.

**Минимальный набор** (OTel GenAI conventions):

| Атрибут                          | Пример          | Зачем                              |
| -------------------------------- | --------------- | ---------------------------------- |
| `gen_ai.system`                  | `openai`        | Какой провайдер                    |
| `gen_ai.operation.name`          | `chat`          | Тип операции (chat/embeddings)     |
| `gen_ai.request.model`           | `gpt-4o`        | Что попросили                      |
| `gen_ai.response.model`          | `gpt-4o-2024-08-06` | Что реально ответило           |
| `gen_ai.request.temperature`     | `0.7`           | Параметры запроса                  |
| `gen_ai.request.max_tokens`      | `1000`          |                                    |
| `gen_ai.usage.input_tokens`      | `1523`          | Cost calc                          |
| `gen_ai.usage.output_tokens`     | `412`           |                                    |
| `gen_ai.response.finish_reasons` | `["stop"]`      | `stop` / `length` / `tool_calls`   |
| `gen_ai.response.id`             | `chatcmpl-xxx`  | Для корреляции с провайдером       |

**Опциональные, но полезные:**

- `gen_ai.conversation.id` — для сессий
- `gen_ai.prompt` — full prompt (как span event, не attribute — большой)
- `gen_ai.completion` — ответ модели
- `cost_usd` — посчитанная стоимость
- `eval.score` — асинхронный eval result

---

## Q6. Что такое OpenTelemetry GenAI Semantic Conventions? Зачем они? (!)

**OpenTelemetry GenAI Semantic Conventions** — это утверждённый CNCF **стандартный словарь атрибутов** для LLM-трейсов (статус прошёл путь Experimental → Stable в 2025). Проще говоря, это договорённость, как именовать поля трейса, чтобы все инструменты понимали их одинаково.

**Зачем нужны:** без общего словаря каждый SDK называл бы «модель» по-своему, и трейсы были бы несовместимы между инструментами.

1. **Независимость от вендора.** Один и тот же код кладёт `gen_ai.request.model` независимо от бэкенда — Datadog, Jaeger, Langfuse, Phoenix понимают это поле одинаково.
2. **Аналитика поверх любого инструмента.** Запрос `sum(gen_ai.usage.input_tokens) by gen_ai.request.model` работает в любом OTel-совместимом хранилище.
3. **Защита от смены вендора.** Меняешь backend — трейсы остаются валидными, переписывать инструментацию не нужно.

**Ключевые namespaces:**

```yaml
gen_ai.system: openai | anthropic | google | cohere | azure | ...
gen_ai.operation.name: chat | text_completion | embeddings
gen_ai.request.*: model, temperature, top_p, max_tokens, presence_penalty
gen_ai.response.*: model, id, finish_reasons
gen_ai.usage.*: input_tokens, output_tokens
gen_ai.conversation.id: <session id>
```

**Span events** хранят содержимое сообщений — отдельно от атрибутов, чтобы текст не раздувал каждый span:

```yaml
gen_ai.system.message      # system prompt
gen_ai.user.message        # user input
gen_ai.assistant.message   # assistant output
gen_ai.tool.message        # tool call result
gen_ai.choice              # модельный choice (n>1)
```

**Захват содержимого можно отключить** флагом `OTEL_INSTRUMENTATION_GENAI_CAPTURE_MESSAGE_CONTENT=false` — это штатный способ не писать в трейсы PII.

---

## Q7. Как использовать OTel GenAI на практике (Python пример)?

На практике есть два пути. **Автоинструментация** оборачивает SDK провайдера и сама создаёт span'ы с правильными атрибутами — менять код вызовов не нужно. **Ручные span'ы** нужны там, где автоинструментации нет или хочется добавить свои поля.

Автоинструментация (OpenLLMetry-style) — три шага: настроить OTel pipeline, навесить инструментор на SDK, дальше писать обычный код:

```python
from opentelemetry import trace
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
from openinference.instrumentation.openai import OpenAIInstrumentor

# 1. Настроить OTel pipeline
provider = TracerProvider()
provider.add_span_processor(
    BatchSpanProcessor(OTLPSpanExporter(endpoint="http://otel-collector:4317"))
)
trace.set_tracer_provider(provider)

# 2. Авто-инструментировать OpenAI SDK
OpenAIInstrumentor().instrument()

# 3. Обычный код OpenAI — спаны создаются автоматически
import openai
client = openai.OpenAI()
resp = client.chat.completions.create(
    model="gpt-4o",
    messages=[{"role": "user", "content": "Hello"}]
)
# Span создан с атрибутами:
#   gen_ai.system=openai
#   gen_ai.request.model=gpt-4o
#   gen_ai.usage.input_tokens=8
#   gen_ai.usage.output_tokens=12
```

Ручное создание span:

```python
tracer = trace.get_tracer(__name__)

with tracer.start_as_current_span("custom_llm_call") as span:
    span.set_attribute("gen_ai.system", "anthropic")
    span.set_attribute("gen_ai.request.model", "claude-3-7-sonnet")
    span.set_attribute("gen_ai.request.temperature", 0.7)

    response = anthropic_client.messages.create(...)

    span.set_attribute("gen_ai.usage.input_tokens", response.usage.input_tokens)
    span.set_attribute("gen_ai.usage.output_tokens", response.usage.output_tokens)
    span.add_event("gen_ai.completion", {"content": response.content[0].text})
```

---

## Q8. Что такое LangSmith? Когда выбирать? (!)

**LangSmith** — observability- и eval-платформа от создателей LangChain. Главная её фишка — бесшовная связка с экосистемой LangChain: если вы уже на этом стеке, трейсинг и оценки включаются почти без кода.

**Сильные стороны:**

- **Автоинструментация LangChain/LangGraph** — нулевая настройка, если уже на этом стеке.
- **Datasets и experiments** — встроенное версионируемое хранилище тестовых кейсов.
- **Оценки как код** — готовые evaluator'ы (correctness, conciseness) плюс свой LLM-judge.
- **Prompts hub** — версии промптов с diff и откатом.
- **Annotation queue** — UI для разметки человеком.
- **A/B-тестирование** — сравнение цепочек на одном датасете.

**Декоратор `@traceable`** работает и вне LangChain — навешиваешь на любую функцию, и её вызовы попадают в трейс:

```python
from langsmith import traceable

@traceable(run_type="llm", name="answer_question")
def answer(question: str) -> str:
    response = openai.chat.completions.create(
        model="gpt-4o",
        messages=[{"role": "user", "content": question}]
    )
    return response.choices[0].message.content
```

**Минусы:**

- **Привязка к вендору** — лучше всего раскрывается в экосистеме LangChain (хотя SDK есть и для другого стека).
- **По умолчанию только облако** — self-host существует, но он платный, в enterprise-тарифе.
- **Цена** — Plus tier для команд начинается от $39/seat.

**Когда выбирать:** уже сидите на LangChain/LangGraph, хотите eval и observability в одном продукте и готовы платить за SaaS.

---

## Q9. Что такое Langfuse? Чем отличается от LangSmith? (!)

**Langfuse** — open-source LLM observability под лицензией MIT, который можно поднять у себя (Docker / k8s). Его ключевое отличие от LangSmith: open-source и self-host из коробки плюс generic SDK, не привязанный к одному фреймворку.

**Модель данных** (как Langfuse раскладывает запрос на сущности):

- **Traces** — корневые запросы с `user_id`, `session_id`, `tags`.
- **Observations** — аналог span'а (типы `LLM`, `GENERATION`, `SPAN`, `EVENT`).
- **Sessions** — группировка трейсов одной беседы.
- **Users** — группировка по конечному пользователю.
- **Prompts** — версионируемые шаблоны с метками (`production`, `staging`).
- **Scores** — результаты оценок (числовые, категориальные, boolean).

**Пример SDK:**

```python
from langfuse import Langfuse
from langfuse.decorators import observe, langfuse_context

langfuse = Langfuse(
    public_key="pk-lf-...",
    secret_key="sk-lf-...",
    host="https://cloud.langfuse.com"
)

@observe(as_type="generation")
def call_llm(question: str) -> str:
    response = openai.chat.completions.create(
        model="gpt-4o",
        messages=[{"role": "user", "content": question}]
    )
    langfuse_context.update_current_observation(
        model="gpt-4o",
        input=question,
        output=response.choices[0].message.content,
        usage={"input": response.usage.prompt_tokens,
               "output": response.usage.completion_tokens}
    )
    return response.choices[0].message.content
```

**Чем отличается от LangSmith:**

| Критерий           | LangSmith              | Langfuse                       |
| ------------------ | ---------------------- | ------------------------------ |
| Open-source        | Нет                    | **Да (MIT)**                   |
| Self-host          | Enterprise only        | **Free, Docker compose**       |
| Vendor-lock        | LangChain-centric      | **Generic SDK**                |
| Prompt management  | Да (Hub)               | Да (с labels)                  |
| Eval               | Сильнее, code-first    | Есть, но проще                 |
| Cost               | $39+/seat              | Free self-host / $29+ cloud    |
| EU-hosting / GDPR  | Опционально            | EU region из коробки           |

**Когда выбирать Langfuse:** нужен контроль над данными (self-host), бюджет ограничен или вы не на LangChain.

---

## Q10. Что такое Arize Phoenix? Чем уникален?

**Arize Phoenix** — open-source LLM observability от Arize AI с упором на **глубину оценок и кластерный анализ ошибок**. Если LangSmith — про экосистему, а Langfuse — про self-host, то Phoenix — про то, чтобы находить *целые сегменты* плохих ответов, а не разбирать их по одному.

**Чем выделяется:**

- **Нативный OpenTelemetry.** Использует OpenInference (расширение OTel GenAI), поэтому встраивается в уже существующий OTel-pipeline без отдельного агента.
- **Кластеризация эмбеддингов.** Группирует ответы в embedding-пространстве и подсвечивает «плохие» кластеры — например, видно, что все запросы про API-документацию стабильно получают низкий score, и проблема не в отдельных ответах, а в целом классе.
- **RAG-дашборды.** Context relevance, retrieval precision/recall — первоклассные метрики, а не самописные.
- **Локальный запуск.** `phoenix.launch_app()` поднимает UI прямо в notebook — внешний backend не нужен, удобно для исследования.
- **Готовые evaluator'ы** — `HallucinationEvaluator`, `QAEvaluator`, `RelevanceEvaluator`.

**Пример:**

```python
import phoenix as px
from phoenix.otel import register

tracer_provider = register(project_name="my-llm-app")

# Запустить UI локально (или Phoenix Cloud)
session = px.launch_app()
# Открой http://localhost:6006

# Eval после сбора трейсов
from phoenix.evals import HallucinationEvaluator, run_evals
df = px.Client().get_spans_dataframe()
results = run_evals(df, [HallucinationEvaluator(eval_model="gpt-4o")])
```

**Когда выбирать Phoenix:** команда серьёзно занимается оценками качества, нужна кластеризация ошибок, инфраструктура уже на OTel.

---

## Q11. Что такое Helicone? Когда стоит выбирать?

**Helicone** — observability через прокси. Идея в том, что вместо правки кода вы перенаправляете запросы к модели через прокси Helicone (меняете `base_url`), и он трейсит каждый вызов «по пути». Это самый дешёвый по усилиям способ подключить observability, но ценой лишнего сетевого хопа.

```python
import openai

client = openai.OpenAI(
    api_key="sk-...",
    base_url="https://oai.helicone.ai/v1",  # вместо api.openai.com
    default_headers={"Helicone-Auth": f"Bearer {HELICONE_KEY}"}
)
# Всё, дальше обычный SDK — Helicone видит каждый запрос
```

**Плюсы:**

- **Нулевая правка кода** — поменял URL и заголовок, и всё.
- **Работает с любым LLM-SDK**, совместимым с OpenAI API.
- **Кэш из коробки** — повторяющиеся запросы возвращаются мгновенно и бесплатно.
- **Rate limiting на пользователя** прямо на уровне прокси.

**Минусы:**

- **Лишний сетевой хоп** — добавляет +30-80 мс латентности.
- **Единая точка отказа** — если Helicone лёг, вместе с ним лёг и ваш доступ к модели.
- **Меньше глубины** — полноценного eval/dataset-процесса нет.

**Когда выбирать:** нужен быстрый старт без рефакторинга, встроенный кэш и rate limit, и вы готовы заплатить за это лишним хопом.

---

## Q12. Сравни популярные LLM observability frameworks. (!)

Главные оси выбора — open-source vs проприетарный, способ подключения (SDK / OTel / прокси) и баланс между глубиной оценок и простотой старта. Таблица ниже расставляет инструменты по этим осям.

| Tool          | Open-source | Self-host    | Approach          | Сильная сторона                | Слабость                  |
| ------------- | ----------- | ------------ | ----------------- | ------------------------------ | ------------------------- |
| **LangSmith** | Нет         | Enterprise   | SDK / @traceable  | LangChain ecosystem, evals     | Vendor-lock, цена         |
| **Langfuse**  | Да (MIT)    | Free Docker  | Generic SDK       | Self-host, prompt mgmt         | Глубина оценок чуть слабее |
| **Phoenix**   | Да          | Free         | OTel-native       | Eval, cluster analysis         | UX чуть инженерный        |
| **Helicone**  | Частично    | Да           | Proxy             | Zero code change, cache        | Extra hop, мало eval      |
| **OpenLLMetry** | Да (Apache) | Free         | OTel SDK          | Vendor-neutral, экспорт куда угодно | Не самостоятельный backend |
| **W&B Prompts** | Нет        | Cloud        | SDK               | Combined с ML training         | Дорого, тяжёлый           |
| **TruLens**   | Да          | Free         | SDK + decorators  | RAG-eval first                 | Меньше production-features|
| **Datadog LLM** | Нет        | Cloud        | SDK + APM         | Уже в Datadog → 1 UI           | Дорого, vendor-lock       |

**Правило выбора:**

- **LangChain-стек + бюджет есть** → LangSmith.
- **Контроль данных / GDPR / self-host** → Langfuse.
- **Серьёзный eval + кластеризация** → Phoenix.
- **Минимум усилий, нет SDK-доступа** → Helicone.
- **Уже OTel-инфра** → OpenLLMetry → любой OTel-backend (Tempo, Jaeger, Datadog).

---

## Q13. Какие метрики обязательно собирать с LLM-системы? (!)

Метрики LLM-системы группируются по шести измерениям: токены/стоимость, задержки, качество, надёжность, эффективность, бизнес. Ключевая мысль — классических latency/error rate мало: без метрик качества и стоимости вы слепы к двум самым дорогим проблемам (галлюцинации и сжигание бюджета).

**Токены и стоимость:**

- `input_tokens_total`, `output_tokens_total` — в разрезе модели, пользователя, эндпоинта.
- `cost_usd_total` = `input_tokens × $price_in` + `output_tokens × $price_out`.
- `cost_per_user_hourly`, `cost_per_endpoint_daily` — основа для алертов.

**Задержки:**

- **TTFT** (time-to-first-token) — критично для streaming UX: пользователь видит первое слово.
- **Total latency** — время полного ответа.
- Перцентили **p50 / p95 / p99** — именно перцентили, среднее (mean) скрывает хвост и врёт.

**Качество:**

- `eval_score` (faithfulness, relevancy) — посчитанные асинхронно.
- `user_feedback_thumbs` — `up`/`down`/null.
- `hallucination_rate` — доля ответов с галлюцинациями (по результатам eval).

**Надёжность:**

- `error_rate` — суммарно и в разбивке 4xx / 5xx / timeout / rate-limit.
- `retry_count` — сколько раз повторяли запрос.
- `fallback_triggered_count` — сколько раз переключались на резервную модель.

**Эффективность:**

- `cache_hit_rate` — доля запросов из кэша (semantic / exact).
- `prompt_token_overhead` — отношение system prompt к user input; если 80% контекста — это служебный system prompt, это плохо.
- `tool_call_avg` — среднее число tool calls на запрос (для агентов).

**Бизнес:**

- `tasks_completed_rate`, `user_retention`, `escalation_to_human` — если за системой стоит пользовательский продукт.

---

## Q14. Как считать стоимость LLM-вызова и отслеживать spike?

Стоимость вызова линейна по токенам: умножаем входные и выходные токены на цену за миллион для конкретной модели. Главное в проде — не сама формула, а отслеживание всплесков (spike) в реальном времени, потому что зацикленный агент или забытый `max_tokens` сжигают бюджет за минуты, а счёт от провайдера придёт только в конце месяца.

**Формула:**

```
cost_usd = (input_tokens / 1_000_000) × price_per_million_input
        + (output_tokens / 1_000_000) × price_per_million_output
```

Цены либо зашиты в библиотеку, либо подтягиваются из конфига (чтобы менять без релиза):

```python
PRICING = {
    "gpt-4o":            {"in": 2.50, "out": 10.00},
    "gpt-4o-mini":       {"in": 0.15, "out": 0.60},
    "claude-3-7-sonnet": {"in": 3.00, "out": 15.00},
    "claude-3-5-haiku":  {"in": 0.80, "out": 4.00},
    "deepseek-chat":     {"in": 0.27, "out": 1.10},
}

def calc_cost(model: str, in_tok: int, out_tok: int) -> float:
    p = PRICING[model]
    return (in_tok / 1e6) * p["in"] + (out_tok / 1e6) * p["out"]
```

**Алерт на всплеск** (Prometheus / VictoriaMetrics) — сравниваем среднечасовую стоимость с недельной нормой:

```promql
# Среднечасовая стоимость за последний час > 3× ср. за неделю
( sum(rate(llm_cost_usd[1h])) )
/
( sum(rate(llm_cost_usd[7d])) )
> 3
```

**Что обычно вызывает всплеск:**

1. Зацикленный агент — 10 000 вызовов модели за минуту.
2. Кто-то выкатил prompt с `max_tokens=16000`.
3. Бот-абуз публичного API.
4. Случайный переход с дешёвой `gpt-4o-mini` на дорогую `gpt-4o`.
5. Перестройка RAG-индекса с эмбеддингами без батчинга.

---

## Q15. Какие алерты обязательно настроить для LLM в проде? (!)

Алерты раскладывают по трём уровням приоритета: критические (бьют по деньгам и доступности), деградация качества (ответы стали хуже, но сервис «жив») и операционные (эффективность поплыла). Главная мысль ответа — для LLM мало алертить на 5xx, потому что «200 OK с галлюцинацией» хуже падения.

**Уровень 1 — критические:**

1. **Всплеск стоимости** — `hourly_cost > 3× weekly_avg` → SMS дежурному.
2. **Latency p99** — `p99 > 30s` или `p99 > 2× baseline`.
3. **Error rate** — `5xx > 5%` за 5 минут.
4. **Упёрлись в rate limit** — `429 > N` → сигнал расширить квоту или включить очередь.
5. **Просадка eval-оценки** — `avg_faithfulness < threshold` за 1 час → вероятная регрессия промпта.

**Уровень 2 — деградация качества:**

6. **Всплеск галлюцинаций** — `hallucination_rate > baseline + 3σ`.
7. **Доля дизлайков** — `negative_feedback / total > 10%`.
8. **Пустые ответы и отказы** — модель массово отказывается отвечать.

**Уровень 3 — операционные:**

9. **Падение cache hit** — `cache_hit_rate < 30%` (было 60%) → кэш инвалидируется слишком часто.
10. **Цикл tool calls** — `tool_calls_per_request > 20` → агент зациклился.
11. **Деградация провайдера** — `latency by provider` показывает рост у OpenAI → пора включать fallback.

**Главный анти-паттерн:** алертить только на 5xx. Для LLM «200 OK с галлюцинацией» опаснее честной 500-ки, потому что проходит мимо мониторинга.

---

## Q16. Что такое sampling в трейсинге и какую стратегию выбрать?

**Семплирование** — это выбор, какую долю запросов трейсить полностью. На масштабе писать в хранилище 100% трейсов дорого (storage) и тормозит сеть, поэтому пишут выборку. Ключевой выбор — *когда* принимать решение о сохранении: до запроса (head-based, дёшево, но можно пропустить редкую ошибку) или после ответа (tail-based, дороже, зато гарантированно ловит медленные и дорогие запросы).

**Стратегии:**

| Стратегия            | Описание                                                | Когда                              |
| -------------------- | ------------------------------------------------------- | ---------------------------------- |
| **Always-on (100%)** | Все запросы трейсятся                                   | Low-volume (<100 req/min), dev     |
| **Head-based**       | Решение принимается до запроса (random N%)              | High-volume, простой uniform       |
| **Tail-based**       | Решение после ответа (на основе latency / error / cost) | Хочешь все медленные / дорогие      |
| **Adaptive**         | Динамический процент по нагрузке                         | Spike-prone системы                |
| **Always-errors**    | Все ошибки + N% успешных                                 | Default рекомендация               |

**Рекомендуемый рецепт для прода** — гарантированно ловим всё «интересное», обычный трафик семплируем на 5%:

```python
# Sampling rules
SAMPLE_RULES = [
    ("error", 1.0),              # все ошибки
    ("slow", 1.0),               # все > p99
    ("high_cost", 1.0),          # все > $0.50
    ("eval_flagged", 1.0),       # all low-score
    ("default", 0.05),           # 5% обычных
]
```

**Подводный камень tail-based:** решение принимается после ответа, значит span'ы надо буферизировать до конца trace. Это требует collector'а с tail-sampling-процессором (OTel Collector это умеет) и памяти под буфер.

---

## Q17. Как защитить PII при трейсинге prompts/completions? (!)

**Проблема:** prompt и ответ часто содержат персональные данные (email, номер карты, паспорт). Без защиты они попадают в span как обычный текст — и любой, у кого есть доступ к obs-платформе, получает доступ к ПД. Это утечка.

**Стратегии** (от самой надёжной аналитики до самой жёсткой защиты):

1. **Редакция на уровне SDK.** Перед отправкой span'а прогоняем `gen_ai.prompt` через redactor, который заменяет ПД на плейсхолдеры.

```python
from presidio_analyzer import AnalyzerEngine
from presidio_anonymizer import AnonymizerEngine

analyzer = AnalyzerEngine()
anonymizer = AnonymizerEngine()

def redact(text: str) -> str:
    results = analyzer.analyze(text=text, language="en")
    return anonymizer.anonymize(text=text, analyzer_results=results).text

# Hook в span processor
class RedactingSpanProcessor(BatchSpanProcessor):
    def on_end(self, span):
        for event in span.events:
            if event.name in ("gen_ai.user.message", "gen_ai.completion"):
                event.attributes["content"] = redact(event.attributes["content"])
        super().on_end(span)
```

2. **Regex-редакция до отправки** (быстрее ML-движка) — для известных форматов: email, номер карты, IP.

3. **Вообще не логировать содержимое** — флаг `OTEL_INSTRUMENTATION_GENAI_CAPTURE_MESSAGE_CONTENT=false`. Тогда в span'е остаются только метаданные, текста нет.

4. **Хеш вместо сырого текста** — `sha256(prompt)` даёт уникальность запроса (можно дедуплицировать, кэшировать) без раскрытия содержимого.

5. **По уровням доступа** — для VIP-пользователей содержимое не сохраняется вовсе, для остальных — частично.

6. **Право на удаление (GDPR).** Система обязана уметь по `user_id` стереть все связанные трейсы. Langfuse и Phoenix поддерживают это штатно.

**Аудит:** регулярно сэмплируем ~1% трейсов и проверяем, не просочились ли в них ПД (для детекции тоже можно использовать LLM-as-judge).

---

## Q18. Что такое masked prompts и зачем GDPR-режим?

**Masked prompts** (маскированные промпты) — это хранение структуры промпта без значений переменных. Шаблон остаётся, конкретные данные пользователя заменяются на плейсхолдеры:

```text
Original:  "Find restaurants near 123 Main St for John Doe (john@example.com)"
Masked:    "Find restaurants near {ADDRESS} for {NAME} ({EMAIL})"
```

**Зачем это нужно:**

- Аналитика «какие шаблоны промптов чаще всего использует пользователь» работает без хранения ПД.
- A/B-тесты сравнивают шаблоны, а не индивидуальные запросы — и не требуют доступа к сырым данным.
- Соответствие GDPR: даже при утечке наружу попадёт только структура, без персональных данных.

**Что включает GDPR-режим в Langfuse / LangSmith:**

- Хранение данных только в ЕС-регионе (EU data residency).
- Шифрование at rest и in transit (TLS 1.3, AES-256).
- DSAR API — экспорт всех данных по `user_id` за срок до 30 дней.
- API права на удаление — удаление за срок до 30 дней.
- Соглашения с субподрядчиками (Vercel, AWS и т.д.).
- Подписанный DPA (Data Processing Agreement).

**Приём для прода:** хранить `user_id` как `sha256(raw_user_id + salt)` и держать **обратное соответствие** (reverse map) в отдельном защищённом хранилище. При запросе на удаление достаточно стереть одну строку в этом хранилище — после этого `user_id` в трейсах больше не связан с реальным человеком (фактическое удаление без переписывания всех трейсов).

---

## Q19. Что такое continuous eval в production? (!)

**Continuous eval** — это постоянное измерение качества LLM-системы прямо на живом трафике, а не только во время релиза. Зачем непрерывно: поведение модели дрейфует и после деплоя (провайдер незаметно обновил модель, в трафике появился новый класс запросов), поэтому одной проверки на релизе мало.

**Конвейер** (по шагам):

1. `Production request` → собирается trace (`Trace collected`).
2. Развилка `Sample N%`: семплируем ли этот запрос?
   - **Yes** → trace уходит в асинхронную очередь оценки (`Async eval queue`).
   - **No** → просто сохраняем trace без оценки (`Store trace only`).
3. Из очереди оценка идёт в `LLM-as-judge / RAGAS`.
4. Полученный eval-score прикрепляется к trace (`Eval score attached to trace`).
5. Дальше score попадает в агрегацию метрик (`Metrics aggregation`) → на дашборд / в алерт (`Dashboard / Alert`).
6. Развилка `Regression?` — есть ли просадка качества?
   - **Yes** → `PagerDuty + auto-rollback` (алерт дежурному и авто-откат).
   - **No** → `Store for analysis` (откладываем для разбора).

**Ключевые принципы:**

1. **Семплируем 1-5%.** Сам eval — это ещё один вызов модели, он стоит денег, поэтому оценивать весь трафик нерационально.
2. **Асинхронно.** Eval не должен блокировать ответ пользователю — он идёт в фоне после отдачи ответа.
3. **Независимая judge-модель.** Оценивает другая модель (Claude, если основная GPT, и наоборот) — это снижает self-bias, когда модель завышает оценку собственным ответам.
4. **Несколько метрик сразу.** Не одна оценка, а набор: faithfulness + relevancy + harmfulness — одна метрика не ловит все виды деградации.
5. **Оценка → метрика → алерт.** Каждый score уходит в Prometheus, а оттуда уже срабатывает алерт на просадку.

**Пример** (LangSmith feedback API):

```python
from langsmith import Client
client = Client()

# После каждого ответа — асинхронный eval
@async_task
def evaluate_response(run_id, question, answer, context):
    score = llm_judge_faithfulness(question, answer, context)
    client.create_feedback(
        run_id=run_id,
        key="faithfulness",
        score=score,
        comment="async LLM-as-judge"
    )
```

---

## Q20. Как интегрировать user feedback в observability?

Обратную связь пользователя привязывают к конкретному trace через `trace_id` и сохраняют как score — тогда жалобу можно связать с реальным запросом и разобрать. Сигналы бывают трёх типов, от прямых до косвенных.

**Каналы обратной связи:**

1. **Явные** — лайк/дизлайк, оценка по 5 звёздам, текстовый комментарий.
2. **Неявные** — поведение пользователя: переформулировал вопрос (значит, ответ не понял), скопировал ответ (значит, пригодился), резко закрыл чат (возможна фрустрация).
3. **Отложенные бизнес-сигналы** — конверсия, retention, доля возвратов (refund rate).

**Привязка к trace:**

```python
# Frontend отправляет feedback с trace_id
POST /api/feedback
{
  "trace_id": "abc123",
  "rating": "thumbs_down",
  "comment": "Wrong product link"
}

# Backend пишет в Langfuse / LangSmith
langfuse.score(
    trace_id="abc123",
    name="user_feedback",
    value=0,  # 0 для thumbs_down, 1 для up
    comment="Wrong product link"
)
```

**Что выводить на дашборд:**

- `thumbs_down_rate by endpoint` — какой эндпоинт чаще всего собирает негатив.
- `thumbs_down by model` — какая модель получает больше дизлайков (A против B).
- `correlation(eval_score, user_rating)` — главный индикатор калибровки: если judge ставит 0.9, а пользователь жмёт дизлайк, значит judge откалиброван плохо и ему нельзя доверять.

**Превращение разбора в датасет:**

```text
Thumbs-down trace → ручной разбор → если eval подтверждает плохой ответ
→ добавляем в golden-dataset → используем для регресс-тестов будущих версий промпта
```

---

## Q21. Как из production-трейсов собрать golden dataset для регрессионного тестирования?

**Golden dataset** — это вручную выверенный набор пар `(вход, ожидаемый ответ / критерий)`, по которому проверяют, не сломала ли новая версия промпта то, что работало раньше. Ценность production-трафика в том, что реальные запросы покрывают случаи, которые разработчик не придумает за столом.

**Что брать из прода** (приоритет — по тому, что чаще бьёт по пользователю):

1. **Частые запросы** — топ-100 самых частых (выделяются кластеризацией эмбеддингов).
2. **Низкие оценки** — все трейсы с `eval_score < 0.5`, подтверждённые человеком.
3. **Отмеченные пользователями** — все дизлайки с разметкой.
4. **Граничные случаи** — самые длинные и самые короткие промпты, нестандартные языки.
5. **Состязательные** — попытки jailbreak, prompt injection, вопросы вне распределения (OOD).

**Процесс** (Langfuse / LangSmith):

```python
# 1. Curate из трейсов
from langfuse import Langfuse
lf = Langfuse()

traces = lf.fetch_traces(
    tags=["thumbs_down"],
    from_timestamp="2026-04-01",
    limit=500
)

# 2. Создать dataset
dataset = lf.create_dataset(name="regression-v2", description="...")
for trace in traces:
    lf.create_dataset_item(
        dataset_name="regression-v2",
        input=trace.input,
        expected_output=trace.input_human_corrected,  # курируется человеком
        metadata={"source_trace": trace.id}
    )

# 3. Запустить новую версию промпта на dataset
results = run_experiment(
    dataset="regression-v2",
    prompt_version="v3",
    evaluators=[faithfulness, relevancy]
)

# 4. Сравнить с baseline v2 — pass/fail regression test
```

**Встройка в CI/CD:** при попытке смержить изменение промпта запускается `evaluate` против этого датасета; если средний score упал больше чем на 5% — мерж блокируется.

---

## Q22. Как регрессионный пайплайн встроен в CI/CD для промптов?

Идея простая: **промпт — это код**, значит у него должен быть тот же конвейер, что у кода — PR, проверка качества на голден-датасете, постепенная раскатка через canary и авто-откат при просадке. Так правка промпта не уезжает в прод «на глазок».

Поток выглядит так:

1. `Dev меняет prompt v3` → пушит PR (`Push PR`).
2. CI прогоняет eval на голден-датасете (`CI: run eval on golden dataset`).
3. Развилка `Avg score >= baseline - 5%?` — средний score не упал больше чем на 5%?
   - **No** → мерж блокируется, diff постится в PR (`Block merge, post diff to PR`).
   - **Yes** → мерж в staging (`Merge to staging`).
4. Дальше canary на 5% трафика (`Canary 5% traffic`) → 24 часа continuous eval (`Continuous eval 24h`).
5. Развилка `Quality stable?` — качество стабильно?
   - **No** → авто-откат (`Auto-rollback`).
   - **Yes** → раскатка на 100% (`Promote 100%`) → версия `v3` помечается production в реестре (`Tag v3 production in registry`).

**GitHub Actions пример:**

```yaml
name: prompt-regression
on: [pull_request]
jobs:
  eval:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Run eval
        run: |
          python scripts/evaluate.py \
            --prompt-file prompts/customer_support.txt \
            --dataset langfuse://datasets/regression-v2 \
            --baseline-prompt main:prompts/customer_support.txt \
            --threshold 0.05
      - name: Comment results
        uses: actions/github-script@v7
        with:
          script: |
            // Postаем таблицу score'ов в PR
```

**Алерт после canary** (в стиле Prometheus) — сравниваем eval-оценку новой версии со старой за тот же час:

```promql
( avg_over_time(eval_score{prompt_version="v3"}[1h]) )
< ( avg_over_time(eval_score{prompt_version="v2"}[1h]) * 0.95 )
```

---

## Q23. Как трейсить агент и находить infinite loops? (!)

**Проблема:** ReAct-агент может зациклиться — `think → tool → think → tool → ...` без конца — и сжечь огромное число токенов. Поскольку HTTP-ответа всё ещё нет, классический мониторинг этого не видит; ловить зацикливание надо по форме трейса.

**По каким сигналам в трейсе видно зацикливание:**

1. **Большая глубина** — `span.depth > 20`.
2. **Повторяющиеся tool calls** — один и тот же инструмент вызывается с похожими аргументами много раз подряд.
3. **Всплеск токенов на trace** — `total_tokens > 100_000` в одном trace.
4. **Аномальная длительность** — `duration > 5 min`.

**Метрика для алерта:**

```promql
# Алерт: средняя глубина агентского trace выросла
avg(llm_agent_depth) by (agent_name) > 15
```

**Дашборд для агентов** (Langfuse / Phoenix) — что отслеживать:

- `avg_tool_calls_per_task` — растёт ли среднее число вызовов инструментов на задачу.
- `cost_per_completed_task` — растёт ли стоимость одной выполненной задачи.
- `infinite_loop_count` — сколько трейсов было принудительно прервано по `max_iterations`.
- `tool_call_distribution` — какой инструмент вызывается чаще, чем ожидалось.

**Как разметить span'ы агента:**

```python
@traceable(run_type="chain", name="react_agent")
def react_agent(question, max_iter=10):
    for i in range(max_iter):
        with tracer.start_as_current_span(f"iter_{i}") as span:
            thought = think(question)
            span.set_attribute("agent.thought", thought)
            tool, args = decide_action(thought)
            span.set_attribute("agent.tool", tool)
            span.set_attribute("agent.args", str(args))
            result = execute_tool(tool, args)
            if is_done(result):
                return result
    raise MaxIterationsExceeded(f"agent={i+1} iterations")
```

**Жёсткие лимиты в проде:** `max_iter`, `max_total_tokens`, `max_cost_per_request` — на каждый навешен алерт, чтобы зацикленный агент упёрся в потолок и не сжёг бюджет.

---

## Q24. Как трейсить RAG-pipeline? Какие метрики ловить? (!)

В RAG ошибка может быть в любом из двух звеньев: плохо *достали* контекст (retrieval) или хорошо достали, но модель *плохо им воспользовалась* (generation). Поэтому RAG-трейс разбивают на отдельные span'ы (эмбеддинг → поиск → реранк → генерация → проверка), а метрики группируют по этим звеньям — иначе непонятно, что чинить.

**Из чего состоит RAG-trace:**

```
chat_request
├── embed_query (text → vector)
├── vector_search (top-k)
│   └── attribute: retrieved_chunks=[id1, id2, ...]
├── rerank (опционально)
├── llm_call (prompt = query + context)
│   └── attribute: gen_ai.context.chunks_used=[id1, id3]
└── post_check (citation validation)
```

**Ключевые RAG-метрики** (по звеньям конвейера):

1. **Качество retrieval — то, что достали:**
   - `retrieval_precision@k` — сколько из top-k чанков действительно релевантны (по эвалу).
   - `retrieval_recall` — сколько релевантных нашли из всех существующих в базе.
   - `mrr` (mean reciprocal rank) — насколько высоко в выдаче оказался нужный чанк.

2. **Использование контекста — что из достатого пригодилось:**
   - `chunks_retrieved` против `chunks_cited_in_answer` — если из 10 чанков в ответ вошли 2, контекст избыточен и мы зря за него платим.
   - `context_relevance_score` (RAGAS).

3. **Качество ответа:**
   - `faithfulness` — опирается ли ответ на контекст, а не выдуман (RAGAS).
   - `answer_relevancy` — отвечает ли вообще на заданный вопрос.
   - `citation_correctness` — действительно ли цитируемый чанк поддерживает утверждение.

4. **Операционные:**
   - `embedding_latency_p95`, `vector_search_latency_p95`.
   - `cache_hit_rate` для эмбеддингов.
   - `unfound_query_rate` — доля запросов, где retrieval вернул меньше порога релевантных чанков; растущее значение указывает на пробел в базе знаний.

**Дашборд кластеризации** (Phoenix): группирует эмбеддинги запросов и подсвечивает кластеры с `avg(faithfulness) < 0.5` — это целые сегменты запросов, на которых система стабильно плоха, а не отдельные сбои.

**Пример атрибутов span'а для retrieval-шага:**

```python
span.set_attribute("retrieval.top_k", 10)
span.set_attribute("retrieval.chunks_returned", 8)
span.set_attribute("retrieval.avg_similarity", 0.78)
span.set_attribute("rag.chunks_cited", ["doc1#p3", "doc2#p1"])
span.set_attribute("rag.context_tokens", 4200)
```

---

## Q25. Как считать «context utilization» в RAG и зачем?

**Context utilization** (утилизация контекста) — это доля переданного модели контекста, который реально вошёл в ответ. По сути метрика отвечает на вопрос: не платим ли мы за чанки, которые модель проигнорировала.

**Зачем измерять:**

- Если из 10 чанков использовано только 2 — мы зря оплачиваем обработку 8 лишних (это и деньги, и латентность).
- Зная это, можно безопасно уменьшить `top_k` без потери качества.
- А если использовано 0 из 10 — модель вообще проигнорировала контекст, и ответ почти наверняка галлюцинация.

**Как считать** (от точного к приблизительному):

1. **По цитированию.** Модель обязана ссылаться на чанки (`[1]`, `[2]`); после ответа парсим эти ссылки и считаем, сколько уникальных чанков задействовано.

```python
def context_utilization(retrieved_chunks, citations_in_answer):
    if not retrieved_chunks:
        return 0.0
    used = set(citations_in_answer) & set(c.id for c in retrieved_chunks)
    return len(used) / len(retrieved_chunks)
```

2. **Через LLM-as-judge** — отдельным промптом спрашиваем модель-судью: «какие из чанков реально использованы в ответе»?

3. **Через attention** (если есть доступ к весам модели) — анализируем, на какие chunk-токены модель смотрела при генерации.

**Метрика в проде:**

```promql
avg(rag_context_utilization) by (endpoint) < 0.3
# → alert: top_k слишком большой, уменьшаем
```

---

## Q26. Как делать A/B тестирование промптов в проде? (!)

**Цель:** доказать, что prompt v2 действительно лучше v1, а не «мне так показалось на паре примеров». Суть подхода — детерминированно делим трафик по хешу `user_id` на две группы, тегируем трейсы вариантом, копим оценки и принимаем решение по статистической значимости, а не по тому, у кого среднее выше.

**Конвейер** (по шагам):

1. `Request приходит` → развилка `Bucket by user_id_hash` (раскладываем по хешу `user_id`):
   - **50%** → `Prompt v1 - control` (контрольная группа).
   - **50%** → `Prompt v2 - treatment` (тестовая группа).
2. Каждый trace тегируется вариантом: `Trace tagged variant=v1` либо `Trace tagged variant=v2`.
3. Оба потока сходятся в `Continuous eval`, затем в агрегацию метрик по варианту (`Aggregated metrics by variant`).
4. Развилка `Stat sig diff?` — есть ли статистически значимая разница?
   - **Yes, v2 wins** → раскатываем v2 на 100% (`Promote v2 to 100%`).
   - **No** → продолжаем копить данные (`Continue collecting`).
   - **v1 wins** → отбрасываем v2, оставляем v1 (`Discard v2, keep v1`).

**Реализация:**

```python
def get_prompt(user_id: str) -> tuple[str, str]:
    bucket = hash(user_id) % 100
    if bucket < 50:
        return prompt_v1, "v1"
    else:
        return prompt_v2, "v2"

prompt, variant = get_prompt(request.user_id)
span.set_attribute("experiment.variant", variant)
response = llm.call(prompt + request.question)
```

**По каким метрикам решать:**

- `avg(eval_score) by variant` — основной критерий качества.
- `avg(thumbs_up_rate) by variant` — пользовательская оценка.
- `avg(latency_ms) by variant`, `avg(cost_usd) by variant` — операционная цена варианта.
- **Статистическая значимость** — Mann-Whitney U test или байесовский подход, а не просто «среднее больше».

**Размер выборки:** чтобы поймать эффект в 5% при мощности 80%, обычно нужно 1000-3000 примеров на вариант. Посчитать можно через `statsmodels.stats.power`.

**Анти-паттерн:** «за день v2 дала score выше» — это могло быть случайностью или дневной аномалией. Ждать минимум 7 дней и проверять недельную сезонность (weekly seasonality).

---

## Q27. Как obs помогает оптимизировать costs? (!)

Observability превращает оптимизацию стоимости из догадок в адресную работу: сначала находим, *где* утекают деньги (топ дорогих эндпоинтов), затем разбираем *почему* (раздутый prompt, отсутствие `max_tokens`, слишком дорогая модель) и применяем точечные приёмы — кэш, downgrade модели, батчинг, prompt caching.

**Пошагово:**

1. **Найти топ-10 самых дорогих эндпоинтов:**
   ```sql
   SELECT endpoint, SUM(cost_usd) as total
   FROM traces WHERE date > now() - 7d
   GROUP BY endpoint ORDER BY total DESC LIMIT 10
   ```
   → видим: 80% бюджета — на `/api/summarize`.

2. **Разобрать профиль одного запроса** в дорогом эндпоинте — куда уходят токены:
   - input_tokens 12 000 (раздутый system prompt) → **сократить system prompt**.
   - output_tokens 3 000 (нет `max_tokens`) → **поставить лимит**.
   - используется `gpt-4o` → **попробовать gpt-4o-mini для некритичных запросов**.

3. **Поискать возможности для кэша:**
   - `cache_hit_rate=15%` → много повторов, стоит добавить semantic cache (Redis + эмбеддинги).
   - 30% запросов одинаковы по структуре и отличаются только датой → вынести в шаблон с параметром.

4. **Понизить модель по маршруту запроса:**
   - 70% запросов — простой Q&A → перевести на дешёвую модель.
   - 30% — сложный reasoning → оставить флагманскую.
   - Логика маршрутизации в коде, контроль через метрику `model_distribution`.

5. **Батчинг и асинхронность:**
   - Считать эмбеддинги для 1000 документов поодиночке дорого; батчинг даёт примерно пятикратную экономию.

6. **Prompt caching (Anthropic / OpenAI):**
   - Длинный неизменный system prompt можно закэшировать → до -90% стоимости на закэшированных токенах.
   - Контролируется метриками `cache_read_tokens` против `cache_create_tokens`.

**Что держать на дашборде оптимизации стоимости:**

- `cost_per_endpoint` — снижается ли по тренду.
- `cost_per_active_user` — хорошо ли масштабируется с ростом аудитории.
- `cost_per_completed_task` — для агентов: падает ли стоимость одной задачи.
- `cache_savings_usd` — сколько денег реально сэкономил кэш.

---

## Q28. Какие анти-паттерны в LLM observability? (!)

Сквозная мысль всех ошибок ниже: команды переносят на LLM привычки классического APM, где «работает = 200 OK», и упускают то, что специфично для моделей — качество, стоимость и структуру цепочки. Каждый пункт — типичная ошибка и её исправление.

**1. Логировать полные промпты без редакции PII.** Прямая утечка персональных данных. Решение: Presidio / regex или вообще отключить захват содержимого для нужных уровней доступа.

**2. Семплировать 100% на масштабе.** При 10 000 req/min трейсы съедают хранилище и денег больше, чем сам LLM. Решение: tail-based sampling.

**3. Хранить сырые эмбеддинги в трейсе.** Один эмбеддинг — 1500 float × 4 байта = 6 КБ; при 1 млн запросов в день это 6 ГБ только под векторы. Решение: хранить только `embedding_id` (ссылку в vector store) или хеш.

**4. Нет связи между LLM-трейсом и бизнес-контекстом.** Пользователь жалуется «15 минут назад ответ был плохой» — без `user_id` / `session_id` / `request_id` этот трейс не найти. Решение: протаскивать контекст через все span'ы.

**5. Мерить только латентность, без качества.** «p99 = 800ms» ничего не говорит о галлюцинациях. Решение: eval-метрики обязательны наравне с латентностью.

**6. Оценивать только в CI, не в проде.** После деплоя поведение модели дрейфует (провайдер обновил модель, появился новый класс запросов), и CI этого уже не ловит. Решение: continuous eval с асинхронным judge.

**7. Один judge на всё.** Если основная модель GPT и judge тоже GPT, возникает self-preference bias — модель завышает оценку «своим» ответам. Решение: cross-judge — оценивать моделью другого семейства.

**8. Промпты захардкожены в коде.** Любая правка требует деплоя, а A/B невозможен в принципе. Решение: держать промпты в hub (LangSmith / Langfuse) и подтягивать по имени и версии в рантайме.

**9. Считать стоимость только по счёту провайдера в конце месяца.** За месяц можно незаметно сжечь $50 000. Решение: учёт стоимости в реальном времени плюс почасовой алерт.

**10. Игнорировать tool calls агента в трейсе.** Цепочка из 50 tool calls сворачивается в один root span без деталей — отлаживать нечего. Решение: каждый tool call — отдельный span.

**11. Нет SLO для LLM-системы.** «У нас всё хорошо» неизмеримо. Решение: формальные цели вроде `99% запросов с eval_score > 0.8`, `p95 latency < 3s`, `cost_per_request < $0.01`.

**12. Доверять judge без калибровки.** Judge ставит 0.9, а пользователь — 1 из 5. Решение: периодически размечать N трейсов вручную, считать корреляцию judge ↔ человек и перекалибровывать судью.

---

## Структура LLM trace

Пример дерева одного trace (`trace_id=abc123`, `user_id=u42`):

- `chat_request` (root, `trace_id=abc123`, `user_id=u42`) — корневой запрос, ветвится на три дочерних span'а:
  - `retrieve_context` (`top_k=10`) — поход за контекстом, внутри два шага:
    - `embed_query` (`model=text-embedding-3`) — эмбеддинг запроса.
    - `vector_search` (`store=qdrant`) — поиск по вектор-стору.
  - `llm_call: chat` (`model=gpt-4o`, `in=1500`, `out=412`, `cost=0.012`) — вызов модели:
    - `stream_tokens` — стриминг токенов ответа.
    - асинхронно (`async eval`) запускается `llm_judge` (`faithfulness=0.87`) → результат `Score attached to trace abc123` (score прикрепляется к тому же trace `abc123`).
  - `guardrail` (`pii_check + safety`) — проверка PII и safety.

---

## Continuous eval pipeline

Поток оценки на проде по шагам:

1. `Prod LLM call` → span экспортируется в OTel (`OTel span exported`).
2. Развилка `Sample 5%?` — семплируем ли этот запрос?
   - **No** → просто сохраняем (`Store only`).
   - **Yes** → span идёт в очередь оценки (`Eval queue`).
3. Из очереди — в `LLM-as-judge` (`faithfulness + relevancy`).
4. Полученный score прикрепляется к trace (`Score → trace`) и далее идёт по двум веткам:
   - в `Prometheus metric`, оттуда:
     - на `Grafana dashboard`;
     - на развилку `score < threshold?` — score ниже порога?
       - **Yes** → `PagerDuty alert` (алерт дежурному).
       - **No** → `Trend monitoring` (наблюдение за трендом).
   - в курирование (`Curate low-score to dataset`) — низкие оценки складываются в датасет → `Regression test in CI` (регресс-тест в CI).

---

## OTel GenAI attributes

Один `LLM Span (gen_ai)` несёт четыре группы атрибутов:

- **System** — `gen_ai.system=openai`, `gen_ai.operation.name=chat`.
- **Request** — `gen_ai.request.model=gpt-4o`, `gen_ai.request.temperature=0.7`, `gen_ai.request.max_tokens=1000`.
- **Response** — `gen_ai.response.model=gpt-4o-2024-08-06`, `gen_ai.response.id=chatcmpl-xxx`, `gen_ai.response.finish_reasons=stop`.
- **Usage** — `gen_ai.usage.input_tokens=1523`, `gen_ai.usage.output_tokens=412`.

К этому же span'у привязаны `Span Events` — содержимое сообщений:

- `gen_ai.system.message`
- `gen_ai.user.message`
- `gen_ai.assistant.message`
- `gen_ai.tool.message`

---

## See also

- [MLOps](mlops-interview.md) — общие practices ML lifecycle, model registry, drift
- [LLM Evaluation](llm-evaluation-interview.md) — RAGAS, LLM-as-judge, golden datasets, benchmarks
- [AI Agents](ai-agents-interview.md) — ReAct, tool calling, agent loops для tracing
- [Multi-agent Orchestration](multi-agent-orchestration-interview.md) — distributed tracing для multi-agent
- [RAG](rag-interview.md) — retrieval pipeline, context utilization
- [Model Serving](model-serving-interview.md) — deployment, A/B, canary
- [Observability](../monitoring/observability-interview.md) — общие принципы 3 pillars и SLO
- [OpenTelemetry](../monitoring/opentelemetry-interview.md) — OTel API/SDK/collector
- [Distributed Systems](../architecture/distributed-systems-interview.md) — distributed tracing, correlation
- [Prometheus & Grafana](../monitoring/prometheus-grafana-interview.md) — метрики и дашборды
- [Jaeger / Zipkin](../monitoring/jaeger-zipkin-interview.md) — backend для OTel-traces
