---
title: "Вопросы на собеседовании: Function Calling / Tool Use"
description: "Function calling в LLM: OpenAI Tools, Anthropic Tool Use, Gemini, parallel tools, structured output, JSON schema, prompt caching, MCP."
tags:
  - interview
  - ai-ml
  - function-calling
type: "interview"
difficulty: "intermediate"
aliases:
  - "Function Calling interview"
  - "Tool Use собеседование"
  - "OpenAI Tools Anthropic"
  - "Structured Output JSON Schema"
updated: "2026-05-23"
---
# Вопросы на собеседовании: `Function Calling / Tool Use`

**Function calling** (она же **tool use**) — способность LLM вместо обычного текстового ответа вернуть **structured JSON** с указанием «вызови такую-то функцию с такими-то аргументами». Появилось у OpenAI в июне 2023 (`functions`), переехало в `tools` в ноябре 2023 (multi-tool + parallel), Anthropic подключилась в 2024 (`tool_use`), Gemini — параллельно. С конца 2024 — стандартизация поверх через **MCP**. Это фундамент почти всех современных LLM-агентов, RAG-роутеров и structured-output пайплайнов.

## Полезные ссылки

### Официальная документация

- [OpenAI Function Calling Guide](https://platform.openai.com/docs/guides/function-calling)
- [OpenAI Structured Outputs](https://platform.openai.com/docs/guides/structured-outputs)
- [Anthropic Tool Use](https://docs.anthropic.com/en/docs/build-with-claude/tool-use)
- [Anthropic Tool Use & Prompt Caching](https://docs.anthropic.com/en/docs/build-with-claude/prompt-caching)
- [Gemini Function Calling](https://ai.google.dev/gemini-api/docs/function-calling)
- [Vercel AI SDK Tools](https://sdk.vercel.ai/docs/foundations/tools)
- [JSON Schema Specification](https://json-schema.org/)
- [Model Context Protocol](https://modelcontextprotocol.io/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Основы**
- [Q1. (!) Что такое function calling / tool use?](#q1--что-такое-function-calling--tool-use)
- [Q2. (!) Чем function calling отличается от обычного prompt-engineering под JSON?](#q2--чем-function-calling-отличается-от-обычного-prompt-engineering-под-json)
- [Q3. Эволюция function calling в индустрии?](#q3-эволюция-function-calling-в-индустрии)
- [Q4. (!) Как выглядит multi-turn loop при tool use?](#q4--как-выглядит-multi-turn-loop-при-tool-use)

**OpenAI / Anthropic / Gemini API**
- [Q5. (!) OpenAI Tools API — структура запроса и ответа?](#q5--openai-tools-api--структура-запроса-и-ответа)
- [Q6. (!) Anthropic Tool Use API — структура?](#q6--anthropic-tool-use-api--структура)
- [Q7. Gemini Function Calling API?](#q7-gemini-function-calling-api)
- [Q8. (!) Сравнить OpenAI vs Anthropic vs Gemini API shape?](#q8--сравнить-openai-vs-anthropic-vs-gemini-api-shape)

**JSON Schema и Structured Output**
- [Q9. (!) Как описывается tool через JSON Schema?](#q9--как-описывается-tool-через-json-schema)
- [Q10. (!) Что такое Structured Output и чем отличается от JSON mode?](#q10--что-такое-structured-output-и-чем-отличается-от-json-mode)
- [Q11. Strict mode у OpenAI — что гарантирует?](#q11-strict-mode-у-openai--что-гарантирует)
- [Q12. Structured output без `response_format` (Anthropic-way)?](#q12-structured-output-без-response_format-anthropic-way)

**Parallel и tool_choice**
- [Q13. (!) Parallel function calling?](#q13--parallel-function-calling)
- [Q14. (!) `tool_choice` — какие режимы и зачем?](#q14--tool_choice--какие-режимы-и-зачем)
- [Q15. Forced tool call — как и когда?](#q15-forced-tool-call--как-и-когда)

**Frameworks (LangChain / LlamaIndex / Vercel AI)**
- [Q16. (!) LangChain `bind_tools()` — что под капотом?](#q16--langchain-bind_tools--что-под-капотом)
- [Q17. LlamaIndex `FunctionTool`?](#q17-llamaindex-functiontool)
- [Q18. Vercel AI SDK `tool({parameters, execute})`?](#q18-vercel-ai-sdk-toolparameters-execute)
- [Q19. Pydantic / Zod для tool schemas — зачем?](#q19-pydantic--zod-для-tool-schemas--зачем)

**MCP и Function Calling**
- [Q20. (!) Как MCP связан с function calling?](#q20--как-mcp-связан-с-function-calling)
- [Q21. OpenAI Assistants tools vs raw function calling?](#q21-openai-assistants-tools-vs-raw-function-calling)

**Production patterns и anti-patterns**
- [Q22. (!) Token cost от tool definitions — как оптимизировать?](#q22--token-cost-от-tool-definitions--как-оптимизировать)
- [Q23. (!) Anthropic prompt caching + tools?](#q23--anthropic-prompt-caching--tools)
- [Q24. (!) Распространённые ошибки: hallucinated functions, wrong types, missing tool_result?](#q24--распространённые-ошибки-hallucinated-functions-wrong-types-missing-tool_result)
- [Q25. Дизайн tool definitions — clear description, enums, naming?](#q25-дизайн-tool-definitions--clear-description-enums-naming)
- [Q26. (!) Error handling — что возвращать в `tool_result` при exception?](#q26--error-handling--что-возвращать-в-tool_result-при-exception)
- [Q27. Recursive tool calls — как защититься от infinite loop?](#q27-recursive-tool-calls--как-защититься-от-infinite-loop)
- [Q28. (!) Anti-patterns: слишком много tools, sensitive params, неконтролируемая стоимость?](#q28--anti-patterns-слишком-много-tools-sensitive-params-неконтролируемая-стоимость)
- [Q29. Production checklist: rate limit, timeout, audit, RBAC?](#q29-production-checklist-rate-limit-timeout-audit-rbac)
- [Q30. Связывание tools с реальным кодом — Python dispatch / TS Zod?](#q30-связывание-tools-с-реальным-кодом--python-dispatch--ts-zod)

---

## Q1. (!) Что такое function calling / tool use?

**Function calling** — режим работы LLM, в котором модель вместо обычного текста возвращает **structured JSON** с описанием вызова функции: имя и аргументы. Сам вызов выполняет приложение — модель только **предлагает** что вызвать.

**Базовая идея цикла:**

```mermaid
sequenceDiagram
    participant U as User
    participant L as LLM
    participant T as Tool / Function
    U->>L: вопрос ("какая погода в Москве?")
    L->>L: решает: нужен tool
    L-->>U: tool_call get_weather(city="Moscow")
    Note over U,L: приложение перехватывает,<br/>само вызывает функцию
    U->>T: get_weather("Moscow")
    T-->>U: {"temp": -3, "wind": 5}
    U->>L: tool_result {"temp": -3, "wind": 5}
    L-->>U: "В Москве -3°C, ветер 5 м/с"
```

**Что важно:** LLM **не** исполняет код. Она только заполняет аргументы по JSON Schema и возвращает их клиенту. Безопасность, retry, RBAC — всё на стороне приложения.

Зачем:
- Доступ к **актуальным данным** (LLM знает мир только до cutoff).
- Доступ к **действиям** (создать запись в БД, отправить письмо).
- **Структурированный output** даже когда никакой внешней функции нет — просто способ «зафиксировать формат ответа».

## Q2. (!) Чем function calling отличается от обычного prompt-engineering под JSON?

| Подход | Гарантии | Cost | Когда |
|---|---|---|---|
| `"Ответь JSON: {...}"` в prompt | никаких, модель может уйти в текст или сломать схему | 0 | прототип, дёшево |
| JSON mode (`response_format: json_object`) | валидный JSON, **схему не гарантирует** | низкий | когда схема простая или валидируете сами |
| Function calling / Structured Output | **JSON по schema** (constrained decoding) | tool definitions добавляют токены к prompt | продакшен, где формат критичен |

Под капотом у OpenAI/Anthropic structured output работает через **constrained decoding** — сэмплер на каждом шаге отсекает токены, которые ведут к невалидному JSON-у. Поэтому никаких retry на «модель ответила HTML вместо JSON» не нужно.

Кейс: вытащить `{name, email, phone}` из письма. Через prompt — 5-10% брака. Через structured output / function — практически 0%.

## Q3. Эволюция function calling в индустрии?

```mermaid
timeline
    title Tool use / Function calling: ключевые вехи
    2023 Jun : OpenAI Functions (1 функция, gpt-3.5/4-0613)
    2023 Nov : OpenAI Tools (multi-tool, parallel calls, переименование)
    2024 Apr : Anthropic Tool Use (beta -> GA)
    2024 May : Gemini Function Calling GA
    2024 Aug : OpenAI Structured Outputs + strict mode
    2024 Nov : Anthropic выпускает MCP (стандарт поверх tool use)
    2025     : MCP становится отраслевым стандартом, native в Claude/Cursor/Windsurf
```

Сегодня три «уровня»:
1. **Raw function calling** провайдера — OpenAI Tools, Anthropic tool_use, Gemini function_declarations.
2. **Framework abstractions** — LangChain `bind_tools`, LlamaIndex `FunctionTool`, Vercel AI `tool()`.
3. **MCP** — переносимое описание tools, которое любой клиент-агент может подключить.

## Q4. (!) Как выглядит multi-turn loop при tool use?

Это не «один запрос — один ответ», а **цикл** до момента, когда LLM перестаёт просить tools.

```mermaid
flowchart TD
    A[User message] --> B[Add to messages]
    B --> C[LLM call с tools]
    C --> D{stop_reason?}
    D -->|stop / end_turn| E[Текст ответа -> User]
    D -->|tool_use / tool_calls| F[Парсим tool_calls]
    F --> G[Выполняем функции локально]
    G --> H[Добавляем tool_result в messages]
    H --> C
    style D fill:#fff4d6
```

Псевдокод цикла:

```python
messages = [{"role": "user", "content": user_q}]
while True:
    resp = client.chat.completions.create(model=..., tools=tools, messages=messages)
    msg = resp.choices[0].message
    messages.append(msg)
    if not msg.tool_calls:
        return msg.content                       # финальный ответ
    for call in msg.tool_calls:
        result = dispatch(call.function.name, json.loads(call.function.arguments))
        messages.append({
            "role": "tool",
            "tool_call_id": call.id,
            "content": json.dumps(result),
        })
```

Обязательно держать **лимит итераций** (см. Q27) — иначе модель может зациклиться.

## Q5. (!) OpenAI Tools API — структура запроса и ответа?

**Запрос:**

```python
tools = [{
    "type": "function",
    "function": {
        "name": "get_weather",
        "description": "Returns current weather for a city.",
        "parameters": {
            "type": "object",
            "properties": {
                "city": {"type": "string", "description": "City name in English"},
                "units": {"type": "string", "enum": ["metric", "imperial"]}
            },
            "required": ["city"],
            "additionalProperties": False
        },
        "strict": True
    }
}]

resp = client.chat.completions.create(
    model="gpt-4o",
    messages=[{"role": "user", "content": "Погода в Москве?"}],
    tools=tools,
    tool_choice="auto",          # auto | none | required | {type, function}
    parallel_tool_calls=True
)
```

**Ответ (когда LLM решает вызвать tool):**

```json
{
  "choices": [{
    "finish_reason": "tool_calls",
    "message": {
      "role": "assistant",
      "content": null,
      "tool_calls": [{
        "id": "call_abc123",
        "type": "function",
        "function": {
          "name": "get_weather",
          "arguments": "{\"city\":\"Moscow\",\"units\":\"metric\"}"
        }
      }]
    }
  }]
}
```

**Reply от приложения** добавляется в `messages` как:

```python
{"role": "tool", "tool_call_id": "call_abc123", "content": "{\"temp\": -3}"}
```

`arguments` всегда **строка JSON** (нужно `json.loads`). При `strict: True` OpenAI гарантирует, что аргументы соответствуют schema.

## Q6. (!) Anthropic Tool Use API — структура?

У Anthropic единый messages-API, `tool_use` и `tool_result` — это просто content-блоки.

**Запрос:**

```python
tools = [{
    "name": "get_weather",
    "description": "Returns current weather for a city.",
    "input_schema": {                                # не "parameters"!
        "type": "object",
        "properties": {
            "city": {"type": "string"},
            "units": {"type": "string", "enum": ["metric", "imperial"]}
        },
        "required": ["city"]
    }
}]

resp = client.messages.create(
    model="claude-opus-4-7",
    max_tokens=1024,
    tools=tools,
    tool_choice={"type": "auto"},
    messages=[{"role": "user", "content": "Погода в Москве?"}]
)
```

**Ответ (stop_reason = `tool_use`):**

```json
{
  "stop_reason": "tool_use",
  "content": [
    {"type": "text", "text": "Сейчас уточню."},
    {"type": "tool_use", "id": "toolu_01abc",
     "name": "get_weather",
     "input": {"city": "Moscow", "units": "metric"}}
  ]
}
```

**Reply:**

```python
messages.append({"role": "assistant", "content": resp.content})
messages.append({
    "role": "user",
    "content": [{
        "type": "tool_result",
        "tool_use_id": "toolu_01abc",
        "content": json.dumps({"temp": -3})
    }]
})
```

**Отличия от OpenAI:**
- `input_schema` вместо `parameters`.
- `input` уже **dict** (не string).
- `tool_result` идёт в `role: "user"` как content-блок, а не в отдельном `role: "tool"`.
- `stop_reason: "tool_use"` вместо `finish_reason: "tool_calls"`.

## Q7. Gemini Function Calling API?

Gemini ближе к OpenAI, но имена другие:

```python
from google import genai
from google.genai import types

weather_func = types.FunctionDeclaration(
    name="get_weather",
    description="Returns current weather for a city.",
    parameters={
        "type": "object",
        "properties": {
            "city": {"type": "string"},
            "units": {"type": "string", "enum": ["metric", "imperial"]}
        },
        "required": ["city"]
    }
)

tools = types.Tool(function_declarations=[weather_func])

resp = client.models.generate_content(
    model="gemini-2.0-flash",
    contents="Погода в Москве?",
    config=types.GenerateContentConfig(tools=[tools])
)

for part in resp.candidates[0].content.parts:
    if fc := part.function_call:
        result = dispatch(fc.name, dict(fc.args))
        # ... send back as function_response part
```

**Особенности:**
- `tools: [{function_declarations: [...]}]` — два уровня вложенности.
- `function_call` / `function_response` как `parts`.
- В Python SDK есть **automatic function calling**: передаёшь Python-функцию напрямую, SDK сам делает loop.

## Q8. (!) Сравнить OpenAI vs Anthropic vs Gemini API shape?

| Аспект | OpenAI | Anthropic | Gemini |
|---|---|---|---|
| Поле schema | `function.parameters` | `input_schema` | `parameters` |
| Список tools | `tools: [{type: "function", function: {...}}]` | `tools: [{name, description, input_schema}]` | `tools: [{function_declarations: [...]}]` |
| Аргументы в ответе | **string** (`arguments`) — нужен `json.loads` | **dict** (`input`) | **dict** (`args`) |
| Reply role | `role: "tool"` + `tool_call_id` | `role: "user"` + content `tool_result` | `function_response` part |
| Parallel calls | Да (`parallel_tool_calls: true`) | Да (несколько `tool_use` блоков) | Да |
| Forced tool | `tool_choice: {type: "function", function: {name}}` | `tool_choice: {type: "tool", name}` | `tool_config: {mode: "ANY"/"NONE"/"AUTO"}` |
| Strict schema | `strict: true` (constrained decoding) | через `tool_choice` + структуру | через `response_schema` |
| Structured output | `response_format: {type: "json_schema"}` | через single-tool trick | `response_schema` параметр |
| Prompt caching | через `cached_tokens` (auto) | явный `cache_control` (включая tools) | контекстное кэширование |
| Streaming | да, `tool_calls` приходят по частям | да, через `content_block_delta` | да |

Главный практический вывод: **аргументы у OpenAI — строка**, у остальных — dict. Это типичный баг переноса кода между провайдерами.

## Q9. (!) Как описывается tool через JSON Schema?

Минимум — `type: "object"` + `properties` + `required`.

```json
{
  "type": "object",
  "properties": {
    "order_id": {
      "type": "string",
      "description": "Order UUID, format 8-4-4-4-12"
    },
    "reason": {
      "type": "string",
      "enum": ["damaged", "wrong_item", "no_longer_needed"],
      "description": "Refund reason category"
    },
    "amount": {
      "type": "number",
      "minimum": 0,
      "description": "Refund amount in EUR"
    },
    "partial": {
      "type": "boolean",
      "default": false
    }
  },
  "required": ["order_id", "reason"],
  "additionalProperties": false
}
```

Что использовать активно:
- `enum` — закрытый список значений вместо free-form string. LLM почти никогда не промахивается мимо enum.
- `description` на **каждом** property — модель читает это, чтобы понять что туда писать.
- `required` — отделяет «обязательные» от опциональных. У OpenAI с `strict: true` **все** properties должны быть в `required` (опциональные делаются через `["string", "null"]`).
- `additionalProperties: false` — запрещает «лишние» поля. Сильно стабилизирует strict mode.
- `oneOf` / `anyOf` — для variant-типов (но не все провайдеры поддерживают полностью).
- `minimum/maximum`, `minLength/maxLength`, `pattern` — base-level валидация.

Что **не** работает / плохо работает: `$ref`, рекурсивные schemas, циклические зависимости — у OpenAI strict mode часть из этого запрещена явно.

## Q10. (!) Что такое Structured Output и чем отличается от JSON mode?

Три уровня «дайте мне JSON»:

| Режим | Что гарантирует | Как |
|---|---|---|
| Plain prompt | ничего, модель может сломаться | «Ответь JSON» в системе |
| JSON mode | валидный JSON синтаксис | `response_format: {type: "json_object"}` |
| Structured Output | валидный JSON **по схеме** | `response_format: {type: "json_schema", json_schema: {...}}` |

OpenAI пример:

```python
resp = client.chat.completions.create(
    model="gpt-4o-2024-08-06",
    messages=[{"role": "user", "content": "Извлеки данные клиента из письма: ..."}],
    response_format={
        "type": "json_schema",
        "json_schema": {
            "name": "customer",
            "strict": True,
            "schema": {
                "type": "object",
                "properties": {
                    "name": {"type": "string"},
                    "email": {"type": "string"},
                    "phone": {"type": ["string", "null"]}
                },
                "required": ["name", "email", "phone"],
                "additionalProperties": False
            }
        }
    }
)
data = json.loads(resp.choices[0].message.content)
```

**JSON mode** — это «обещаю валидный JSON»; модель может вернуть `{"answer": "..."}` хотя ждали `{"name", "email"}`. **Structured Output** — это уже **constrained decoding**: сэмплер физически не может выйти за рамки schema.

## Q11. Strict mode у OpenAI — что гарантирует?

`strict: true` (в tool definition или в `json_schema`) включает **constrained decoding на грамматике из вашей JSON Schema**.

Гарантии:
- Все обязательные ключи присутствуют.
- Типы и enums соблюдены.
- Нет «лишних» полей если `additionalProperties: false`.
- Refusal — отдельное поле `refusal` если модель отказалась (вместо мусорного JSON).

Ограничения strict mode:
- Schema должна быть **полной** — все properties в `required`. Опциональность делается через nullable: `{"type": ["string", "null"]}`.
- Запрещены `oneOf` на root, `$ref` к самому себе, некоторые формы рекурсии.
- Первый запрос с новой схемой может быть медленнее — компиляция грамматики.

Без `strict` модель **обычно** соблюдает schema, но не всегда — особенно на edge-cases.

## Q12. Structured output без `response_format` (Anthropic-way)?

У Anthropic нет аналога `response_format: json_schema`, но есть рабочий трюк — **одиночный обязательный tool**:

```python
extract_tool = {
    "name": "extract_customer",
    "description": "Extract customer info from the email.",
    "input_schema": {
        "type": "object",
        "properties": {
            "name": {"type": "string"},
            "email": {"type": "string"}
        },
        "required": ["name", "email"]
    }
}

resp = client.messages.create(
    model="claude-opus-4-7",
    tools=[extract_tool],
    tool_choice={"type": "tool", "name": "extract_customer"},  # форсим вызов
    messages=[{"role": "user", "content": email_text}],
    max_tokens=512
)

data = resp.content[0].input        # уже dict, готовый JSON
```

`tool_choice: {type: "tool", name: ...}` заставляет модель обязательно вызвать именно этот tool. Получаем гарантированный JSON по schema — структурированный output без отдельного API.

## Q13. (!) Parallel function calling?

LLM может в **одном** ответе вернуть **несколько** `tool_calls` сразу, если задача требует независимых данных.

Пример: «Какая погода в Москве и СПб?»

```json
{
  "tool_calls": [
    {"id": "c1", "function": {"name": "get_weather", "arguments": "{\"city\":\"Moscow\"}"}},
    {"id": "c2", "function": {"name": "get_weather", "arguments": "{\"city\":\"SPb\"}"}}
  ]
}
```

```mermaid
sequenceDiagram
    participant L as LLM
    participant A as App
    participant T1 as get_weather (Moscow)
    participant T2 as get_weather (SPb)
    L-->>A: tool_calls = [c1, c2]
    par parallel
        A->>T1: city=Moscow
        T1-->>A: {-3, 5}
    and
        A->>T2: city=SPb
        T2-->>A: {-1, 7}
    end
    A->>L: tool_result c1 + tool_result c2
    L-->>A: "В Москве -3, в Питере -1"
```

Что важно:
- Возвращать **все** `tool_result` за один следующий вызов, в том же порядке id-шников.
- Запускать функции **параллельно** (`asyncio.gather`, `ThreadPoolExecutor`) — иначе ничего не выиграете.
- У OpenAI можно выключить: `parallel_tool_calls: false` — иногда это лучше для предсказуемости агентов.
- У Anthropic параллельность работает «из коробки», просто несколько `tool_use`-блоков в одном ответе.

## Q14. (!) `tool_choice` — какие режимы и зачем?

Контроль над тем, **должна** ли модель использовать tools.

| Режим | OpenAI | Anthropic | Когда |
|---|---|---|---|
| LLM сама решает | `"auto"` (default) | `{"type": "auto"}` (default) | большинство кейсов |
| Никогда не вызывать | `"none"` | `{"type": "none"}` (через отсутствие tools или явно) | финальный ответ пользователю, summarization |
| Обязательно вызвать **что-то** | `"required"` | `{"type": "any"}` | structured pipeline, где tool обязателен |
| Обязательно конкретный tool | `{"type": "function", "function": {"name": "X"}}` | `{"type": "tool", "name": "X"}` | structured output через tool (см. Q12), форс-роутинг |

Главная боль `auto`: модель может ответить текстом «ой, я не знаю» вместо того чтобы вызвать tool. `required` форсит хотя бы один вызов. Конкретный tool — гарантированный JSON.

## Q15. Forced tool call — как и когда?

Когда нужно:
- **Structured extraction** — единственный tool, обязательный (Q12).
- **Router**: первый шаг агента всегда обязан выбрать категорию задачи (`tool_choice: required`).
- **Тестирование** конкретного tool без надежды на «модель захочет».

Подводный камень: если форсите tool, у которого все аргументы опциональны и есть `enum`, модель может выбрать «случайный» enum-вариант чтобы заполнить. Поэтому форсить стоит только осмысленные tools, где модель видит достаточно контекста.

## Q16. (!) LangChain `bind_tools()` — что под капотом?

```python
from langchain_openai import ChatOpenAI
from langchain_core.tools import tool

@tool
def get_weather(city: str, units: str = "metric") -> str:
    """Returns current weather for a city."""
    return f"{-3 if city == 'Moscow' else 0}°C"

llm = ChatOpenAI(model="gpt-4o").bind_tools([get_weather])
resp = llm.invoke("Погода в Москве?")
# resp.tool_calls — уже распарсенный список
```

Что делает `bind_tools`:
1. Берёт **docstring** функции как `description`.
2. Берёт **type hints + Pydantic** как `parameters` (генерирует JSON Schema).
3. Передаёт правильный shape провайдеру (OpenAI / Anthropic / Gemini — разные API-формы абстрагированы).
4. Парсит ответ в единый формат `tool_calls: [{name, args, id}]` независимо от провайдера.

Дальше комбинируется с `ToolNode` (LangGraph) или ручным dispatch — `langchain` предоставляет цикл через `AgentExecutor` или новый `create_react_agent`.

## Q17. LlamaIndex `FunctionTool`?

```python
from llama_index.core.tools import FunctionTool
from llama_index.llms.openai import OpenAI
from llama_index.core.agent import ReActAgent

def get_weather(city: str) -> str:
    """Returns current weather for a city."""
    return "..."

tool = FunctionTool.from_defaults(fn=get_weather)
agent = ReActAgent.from_tools([tool], llm=OpenAI(model="gpt-4o"))
print(agent.chat("Погода в Москве?"))
```

LlamaIndex сильнее заточен под RAG, но `FunctionTool` — стандартный примитив для агентов. Поддерживает sync/async, автоматически генерирует schema из сигнатуры или принимает явную Pydantic-модель.

## Q18. Vercel AI SDK `tool({parameters, execute})`?

TypeScript-первый подход. Тут execute уже внутри tool, и SDK сам гонит loop.

```typescript
import { tool, generateText } from "ai";
import { openai } from "@ai-sdk/openai";
import { z } from "zod";

const result = await generateText({
  model: openai("gpt-4o"),
  tools: {
    getWeather: tool({
      description: "Returns current weather for a city.",
      parameters: z.object({
        city: z.string().describe("City name in English"),
        units: z.enum(["metric", "imperial"]).default("metric"),
      }),
      execute: async ({ city, units }) => {
        const r = await fetch(`https://api.weather/?q=${city}&u=${units}`);
        return await r.json();
      },
    }),
  },
  maxSteps: 5,                      // защита от infinite loop
  prompt: "Погода в Москве?",
});
```

Плюсы: Zod вместо raw JSON Schema → type-safe, IDE-completion на `({city, units})`. `maxSteps` встроен. Минусы: жёсткая привязка к Vercel-стеку (хотя сам SDK работает где угодно).

## Q19. Pydantic / Zod для tool schemas — зачем?

Писать руками JSON Schema в коде — рутина и source of truth расползается. Идея:

```python
from pydantic import BaseModel, Field
from typing import Literal

class GetWeatherArgs(BaseModel):
    city: str = Field(..., description="City name in English")
    units: Literal["metric", "imperial"] = "metric"

schema = GetWeatherArgs.model_json_schema()
# {'type': 'object', 'properties': {...}, 'required': [...]}
```

Профит:
- **Type-safety**: после `GetWeatherArgs(**args)` уже типизированные поля.
- **Валидация**: `ValidationError` ловит мусор от LLM.
- **Авто-генерация**: schema всегда соответствует типам, не разъезжается.

Zod в TypeScript даёт ровно то же: `z.object(...)` → JSON Schema + runtime check + типы.

## Q20. (!) Как MCP связан с function calling?

**MCP (Model Context Protocol)** — стандарт от Anthropic (Nov 2024) для **переносимого описания tools / resources / prompts**. Function calling — это **транспорт внутри одной LLM-сессии**. MCP — это **протокол между клиентом-агентом и любым MCP-сервером**.

```mermaid
flowchart LR
    subgraph Host[Claude / Cursor / любой MCP-client]
        Agent[LLM Agent]
    end
    Agent -- tool_use --> Bridge[MCP bridge]
    Bridge -- list_tools / call_tool --> MCP1[MCP Server: filesystem]
    Bridge -- list_tools / call_tool --> MCP2[MCP Server: github]
    Bridge -- list_tools / call_tool --> MCP3[MCP Server: postgres]
```

Поток:
1. Host подключается к MCP-серверам, получает их `list_tools`.
2. Эти tools конвертируются в формат function calling провайдера (OpenAI/Anthropic/...).
3. Когда LLM вернула `tool_use`, host через MCP вызывает `tools/call` на нужном сервере.
4. Результат возвращается как `tool_result`.

То есть function calling — это **LLM-side контракт**, MCP — **app-side универсальный коннектор**. Один MCP-сервер сразу работает в Claude Desktop, Cursor, Windsurf, OpenAI Agents SDK — потому что все они умеют конвертировать `list_tools` в нативный function calling.

## Q21. OpenAI Assistants tools vs raw function calling?

OpenAI Assistants API (2023) принёс **server-side built-in tools**, которых нет в raw Chat Completions:

| Tool | Описание | Аналог у Anthropic |
|---|---|---|
| `code_interpreter` | Песочница с Python, OpenAI сама исполняет код | нет (нужно поднимать самим) |
| `file_search` (retrieval) | Встроенный RAG поверх загруженных файлов | нет, делайте на эмбеддингах |
| `function` | Обычное function calling, ваш код | `tool_use` |

Плюс — Assistants держит thread state на стороне OpenAI. Минус — vendor lock-in, ограниченный контроль, выше цена. С появлением **Responses API** и **Agents SDK** (2025) OpenAI постепенно мигрирует в сторону более явных контрактов, но идея «server-side tools, которые провайдер исполняет за вас» осталась.

У Anthropic своих server-side tools меньше (есть computer use, web search, code execution — но как явные tools, контролируемые клиентом).

## Q22. (!) Token cost от tool definitions — как оптимизировать?

Tool definitions **добавляются в каждый запрос** как системный контекст. Большой набор tools легко съедает 2-5k токенов на запрос.

Что делать:
- **Сократить descriptions**: писать кратко, но информативно. Убрать «This function is used to...» — оставить «Returns ...».
- **Не передавать все tools сразу** — фильтровать по контексту (router-агент сначала решает категорию задачи).
- **Кэшировать** tool definitions:
  - OpenAI — автоматически (если префикс повторяется ≥1024 токенов).
  - Anthropic — явный `cache_control` (см. Q23).
- **Удалить мёртвые tools** — часто остаются 3-4 неиспользуемых функции с длинными описаниями.
- **Считать токены**: `tiktoken` для OpenAI, Anthropic API возвращает `usage`.

Грубая оценка: 1 простой tool с 5 properties и `enum` — около 100-200 токенов. 30 tools — 3-6k токенов **на каждый** запрос в цикле.

## Q23. (!) Anthropic prompt caching + tools?

Anthropic позволяет явно кэшировать tool definitions и system prompts. Кэш живёт 5 минут (или 1 час с extended caching), повторные запросы в этом окне берут **cached tokens по цене 10%** от обычных input-токенов.

```python
resp = client.messages.create(
    model="claude-opus-4-7",
    system=[
        {"type": "text", "text": "Вы — ассистент компании ACME...",
         "cache_control": {"type": "ephemeral"}}
    ],
    tools=[
        {"name": "get_weather", "description": "...",
         "input_schema": {...},
         "cache_control": {"type": "ephemeral"}}        # последний tool в массиве
    ],
    messages=[...]
)
```

Правило: `cache_control` ставится на **последний элемент** того блока, который должен попасть в кэш. Кэш строится **префиксом** — кэшируется всё до и включая отмеченный элемент.

Экономия: при цикле агента из 10 шагов, где tools и system одни и те же, экономия на input-токенах от 5x до 10x. Это в разы дешевле итераций без кэша.

## Q24. (!) Распространённые ошибки: hallucinated functions, wrong types, missing tool_result?

| Проблема | Симптом | Лечение |
|---|---|---|
| **Hallucinated function name** | LLM вызывает `getWeather` вместо `get_weather` | `strict: true`, проверка по белому списку, прозрачные имена + description |
| **Wrong arg types** | `{"amount": "100"}` вместо `100` | `strict: true`, ручная валидация Pydantic/Zod, retry с ошибкой |
| **Missing tool_result** | повторный запрос без результата → LLM начинает галлюцинировать или ругается на API | API-валидация: для каждого `tool_use` обязан быть `tool_result` с тем же id |
| **Не тот id `tool_use_id`** | API ошибка `tool_use_id not found` | копировать id 1-в-1, не генерировать новые |
| **JSON в content вместо tool_call** | модель «забыла» про tools и просто описала действие текстом | force `tool_choice: required` или улучшить prompt |
| **Argument string vs dict** | TypeError в коде после миграции с Anthropic на OpenAI | помнить: OpenAI — string, Anthropic — dict |
| **Поля null vs missing** | `email = None` падает в downstream | в strict mode объявлять nullable: `["string", "null"]` |
| **enum case-sensitive** | LLM возвращает `"Damaged"` вместо `"damaged"` | строгий `enum`, normalize на стороне приложения |

## Q25. Дизайн tool definitions — clear description, enums, naming?

Хороший tool — это маленькая документация для LLM.

**Хорошо:**

```json
{
  "name": "refund_order",
  "description": "Issues a refund for a delivered order. Use when customer explicitly asks for a refund and order status is DELIVERED. Do NOT use for cancellations of pending orders — use cancel_order instead.",
  "parameters": {
    "type": "object",
    "properties": {
      "order_id": {"type": "string", "description": "UUID of the order"},
      "reason": {"type": "string", "enum": ["damaged", "wrong_item", "expired"], "description": "Refund category. Use damaged for visible defects."}
    },
    "required": ["order_id", "reason"]
  }
}
```

**Плохо:**

```json
{
  "name": "refund",
  "description": "Refund.",
  "parameters": {
    "type": "object",
    "properties": {
      "id": {"type": "string"},
      "type": {"type": "string"}
    }
  }
}
```

Правила:
- Имя `snake_case`, глагол + объект: `get_weather`, `create_invoice`.
- В description — **когда использовать**, **когда НЕ использовать**, edge cases.
- Описание у каждого property с примером значения.
- `enum` вместо free-form там, где список конечен.
- Не больше 5-7 параметров на tool — иначе бить на несколько.

## Q26. (!) Error handling — что возвращать в `tool_result` при exception?

При ошибке tool возвращайте **в самом `tool_result`** структурированную ошибку — модель часто умеет её обработать (попробовать другой tool, переформулировать аргументы, спросить пользователя).

```python
try:
    result = call_external_api(args)
    content = json.dumps(result)
    is_error = False
except ApiNotFound as e:
    content = json.dumps({"error": "not_found", "message": str(e)})
    is_error = True
except RateLimitError:
    content = json.dumps({"error": "rate_limited", "retry_after": 5})
    is_error = True
```

OpenAI:

```python
{"role": "tool", "tool_call_id": "...", "content": content}
```

Anthropic — поддерживает явный флаг:

```python
{"type": "tool_result", "tool_use_id": "...",
 "content": content, "is_error": True}
```

Что **не** делать:
- Кидать exception дальше и обрывать loop — LLM не узнает, что произошло.
- Возвращать stack trace целиком — мусор в контексте, утечка путей/секретов.
- Возвращать `null` — модель решит, что всё нормально.

## Q27. Recursive tool calls — как защититься от infinite loop?

Сценарий: модель зацикливается — после каждого `tool_result` снова просит тот же tool. Причины: плохой prompt, противоречивый результат, баг tool-а.

Защита:
- **max_iterations** на цикл (5-15 типично):
  ```python
  for step in range(MAX_STEPS):
      resp = call_llm(...)
      if not resp.tool_calls:
          break
      # ... dispatch
  else:
      raise AgentLoopExceeded()
  ```
- **Detect repeating calls**: хэш `(tool_name, arguments)`; если совпало 2+ раза подряд — прервать или подсказать модели «уже пробовал, попробуй другое».
- **Wall-clock timeout** на весь цикл.
- **Token budget**: если суммарный `usage` превысил лимит — abort.
- **Reflection-step**: после N шагов вставлять в контекст «осталось K итераций, сформулируй финальный ответ».

LangChain/LangGraph дают `recursion_limit`, Vercel AI — `maxSteps`. В своих циклах **всегда** ограничиваете явно.

## Q28. (!) Anti-patterns: слишком много tools, sensitive params, неконтролируемая стоимость?

| Anti-pattern | Почему плохо | Что делать |
|---|---|---|
| **>30 tools одновременно** | LLM путается, выбирает не то, токены растут линейно | router-агент или namespace по контексту; динамическая фильтрация |
| **Sensitive params в args** (`api_key`, `password`) | попадают в логи, MLOps tracing, потенциально в training | хранить secrets server-side, в tool args — только `account_id` |
| **Tool без идемпотентности** | retry создаёт дубликаты заказов | `idempotency_key` в схеме, dedupe на стороне tool |
| **Tools, изменяющие prod без подтверждения** | hallucination = удалённая БД | для destructive — confirmation step, RBAC, dry-run |
| **Возврат гигантских JSON** в tool_result | контекст забивается, дорого | summary + опциональный `details_url` |
| **Динамические descriptions** | кэш не работает | держать tools detection статическим |
| **Один tool на всё** (`do_anything(query)`) | теряется JSON Schema-валидация, LLM придумывает что угодно | дробить на маленькие tools с чёткой ответственностью |
| **Бесконтрольная цена** | агент в loop = десятки $ за сессию | hard token-budget, max_iterations, alerting |

## Q29. Production checklist: rate limit, timeout, audit, RBAC?

Что должно быть в любом prod tool-runtime:

- **Rate limit per tool / per user**: Redis-based bucket. Например, `send_email` — 10/min/user.
- **Timeout per call**: 5-30 сек. Без таймаута один зависший tool блокирует loop.
- **Retries with backoff** для transient errors (5xx, 429) — но не для 4xx.
- **Audit log**: `(user_id, session_id, tool_name, args_redacted, result_status, latency_ms, cost_tokens)`.
- **RBAC**: какие tools доступны какому пользователю/тенанту. Не все могут вызывать `refund_order`.
- **Sandboxing** для code-execution tools — Firecracker, gVisor, Docker.
- **PII redaction** в логах: маскировать email/phone/cards перед записью.
- **Cost tracking**: метрики по tokens / tools / requests; бюджет на сессию.
- **Observability**: trace на каждый шаг loop (Langfuse, LangSmith, OpenTelemetry).
- **Confirmation для destructive actions**: `delete_*`, `transfer_*`, `send_email` — human-in-the-loop.
- **Versioning tools**: если меняется schema — версия в имени (`get_weather_v2`).
- **Schema валидация на входе/выходе**: даже если LLM «строгая» — не доверять.

## Q30. Связывание tools с реальным кодом — Python dispatch / TS Zod?

Простой Python:

```python
TOOL_REGISTRY = {
    "get_weather": get_weather,
    "refund_order": refund_order,
    "send_email": send_email,
}

def dispatch(name: str, args: dict):
    fn = TOOL_REGISTRY.get(name)
    if fn is None:
        return {"error": "unknown_tool"}
    try:
        validated = TOOL_SCHEMAS[name](**args)         # Pydantic
        return fn(**validated.model_dump())
    except ValidationError as e:
        return {"error": "invalid_args", "details": e.errors()}
```

TypeScript с Zod:

```typescript
const tools = {
  getWeather: {
    schema: z.object({ city: z.string(), units: z.enum(["metric", "imperial"]).default("metric") }),
    execute: async ({ city, units }: { city: string; units: "metric" | "imperial" }) =>
      await fetchWeather(city, units),
  },
} as const;

async function dispatch(name: keyof typeof tools, raw: unknown) {
  const def = tools[name];
  const parsed = def.schema.safeParse(raw);
  if (!parsed.success) return { error: "invalid_args", issues: parsed.error.issues };
  return await def.execute(parsed.data);
}
```

Идеи:
- Registry — один dict/object, source of truth.
- Validate **до** вызова — никогда не доверять аргументам LLM.
- Sync/async aware: для параллельных tool_calls — `asyncio.gather` / `Promise.all`.
- Логируйте `(name, args_redacted, result_status, latency)` для каждого dispatch.

---

## See also

- [mcp-interview.md](./mcp-interview.md)
- [ai-agents-interview.md](./ai-agents-interview.md)
- [llm-basics-interview.md](./llm-basics-interview.md)
- [llm-integration-patterns-interview.md](./llm-integration-patterns-interview.md)
- [prompt-engineering-interview.md](./prompt-engineering-interview.md)
- [rag-interview.md](./rag-interview.md)
- [embeddings-interview.md](./embeddings-interview.md)
- [../api/http-rest-interview.md](../api/http-rest-interview.md)
- [../system-design/system-design-interview.md](../system-design/system-design-interview.md)
