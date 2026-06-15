---
title: "Вопросы на собеседовании: LLM Integration Patterns"
description: "Production patterns: streaming responses, retry/fallback, model routing, caching, batching, rate limiting, observability, cost tracking, multi-provider abstractions, semantic cache"
tags:
  - interview
  - ai-ml
  - llm-integration-patterns-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "LLM Integration Patterns"
  - "LLM production interview"
  - "LLM gateway interview"
prerequisites: []
next: []
updated: "2026-05-19"
---
# Вопросы на собеседовании: `LLM Integration Patterns`

Интеграция LLM в production — это больше, чем просто `client.chat.completions.create()`. На собеседовании спрашивают про streaming, retry/fallback, маршрутизацию моделей, кэширование, rate limiting, наблюдаемость, учёт затрат, абстракции над несколькими провайдерами, семантическое кэширование и асинхронные паттерны.

## Полезные ссылки

### Официальная документация и авторитетные источники

- [LiteLLM Documentation](https://docs.litellm.ai/) — multi-provider LLM SDK
- [Helicone (LLM observability)](https://helicone.ai/)
- [Langfuse (open-source observability)](https://langfuse.com/)
- [LangSmith Documentation](https://docs.smith.langchain.com/)
- [OpenRouter — model routing](https://openrouter.ai/)
- [Portkey (AI gateway)](https://portkey.ai/)

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

**Базовые паттерны**
- [Q1. (!) Что такое LLM gateway?](#q1--что-такое-llm-gateway)
- [Q2. (!) Как реализовать streaming-ответы от LLM?](#q2--как-реализовать-streaming-ответы-от-llm)
- [Q3. (!) Что такое SSE (Server-Sent Events) и зачем он для streaming?](#q3--что-такое-sse-server-sent-events-и-зачем-он-для-streaming)
- [Q4. WebSocket или SSE для LLM — что выбрать?](#q4-websocket-или-sse-для-llm--что-выбрать)

**Reliability**
- [Q5. (!) Как сделать retry с exponential backoff?](#q5--как-сделать-retry-с-exponential-backoff)
- [Q6. (!) Зачем circuit breaker для LLM API и как он работает?](#q6--зачем-circuit-breaker-для-llm-api-и-как-он-работает)
- [Q7. (!) Как сделать fallback между провайдерами (Claude → GPT)?](#q7--как-сделать-fallback-между-провайдерами-claude--gpt)
- [Q8. Как настраивать таймауты для LLM-вызовов?](#q8-как-настраивать-таймауты-для-llm-вызовов)

**Cost optimization**
- [Q9. (!) Что такое model routing (эскалация small → large)?](#q9--что-такое-model-routing-эскалация-small--large)
- [Q10. (!) Что такое semantic caching (семантическое кэширование)?](#q10--что-такое-semantic-caching-семантическое-кэширование)
- [Q11. Что такое prompt caching на стороне провайдера?](#q11-что-такое-prompt-caching-на-стороне-провайдера)
- [Q12. Что даёт батчинг запросов к LLM API?](#q12-что-даёт-батчинг-запросов-к-llm-api)
- [Q13. (!) Как трекать затраты по пользователю и по фиче?](#q13--как-трекать-затраты-по-пользователю-и-по-фиче)

**Rate limiting**
- [Q14. (!) Как применить token bucket к LLM API?](#q14--как-применить-token-bucket-к-llm-api)
- [Q15. Как сделать rate limit на каждого пользователя?](#q15-как-сделать-rate-limit-на-каждого-пользователя)
- [Q16. Как сделать распределённый rate limiting на Redis?](#q16-как-сделать-распределённый-rate-limiting-на-redis)

**Multi-provider**
- [Q17. (!) Зачем нужна абстракция над провайдером?](#q17--зачем-нужна-абстракция-над-провайдером)
- [Q18. (!) Чем различаются LiteLLM, OpenRouter, Portkey?](#q18--чем-различаются-litellm-openrouter-portkey)
- [Q19. Что такое нормализация параметров модели?](#q19-что-такое-нормализация-параметров-модели)

**Observability**
- [Q20. (!) Что нужно трекать в LLM-системах?](#q20--что-нужно-трекать-в-llm-системах)
- [Q21. (!) Как трассировать цепочки промптов (prompt chains)?](#q21--как-трассировать-цепочки-промптов-prompt-chains)
- [Q22. Чем различаются Helicone, Langfuse, LangSmith?](#q22-чем-различаются-helicone-langfuse-langsmith)
- [Q23. Что означают метрики задержки TTFT и TPOT?](#q23-что-означают-метрики-задержки-ttft-и-tpot)

**Async patterns**
- [Q24. (!) Синхронные или асинхронные вызовы LLM — что выбрать?](#q24--синхронные-или-асинхронные-вызовы-llm--что-выбрать)
- [Q25. Зачем выносить долгие генерации в background jobs?](#q25-зачем-выносить-долгие-генерации-в-background-jobs)

**Безопасность и compliance**
- [Q26. (!) Как устроен pipeline модерации контента?](#q26--как-устроен-pipeline-модерации-контента)
- [Q27. Зачем нужен audit logging?](#q27-зачем-нужен-audit-logging)
- [Q28. (!) Какие частые проблемы LLM в production?](#q28--какие-частые-проблемы-llm-в-production)

## Q1. (!) Что такое LLM gateway?

**LLM gateway** — это middleware-слой между приложением и LLM-провайдерами. Вместо того чтобы каждый сервис самостоятельно ходил в OpenAI, Anthropic и т. д., весь трафик идёт через единую точку, которая снимает кросс-срезные заботы с прикладного кода.

Что он централизует:
- **Маршрутизация** — выбор подходящей модели под запрос
- **Кэширование** — не платить дважды за одинаковые запросы
- **Rate limiting** — не упереться в лимиты провайдера
- **Наблюдаемость** — единое место для метрик, логов, трейсов
- **Fallback** — переключение на запасного провайдера при сбое
- **Учёт затрат** — кто и сколько потратил

Поток выглядит так: приложение (`App`) обращается не к провайдерам напрямую, а к единому слою `LLM Gateway`. От gateway расходятся все направления:

- `App` → `LLM Gateway` — весь трафик идёт через одну точку
- `LLM Gateway` → `Cache` — кэш ответов
- `LLM Gateway` → `OpenAI` — провайдер
- `LLM Gateway` → `Anthropic` — провайдер
- `LLM Gateway` → `Self-hosted Llama` — локально развёрнутая модель
- `LLM Gateway` → `Observability` — логи, метрики, трейсы

Идея та же, что у обычного API gateway: вынести инфраструктурную логику из приложения в один управляемый слой.

**Инструменты:** Portkey, Helicone, LiteLLM, OpenRouter или собственное решение.

**Когда применять:** в enterprise-системах с множеством LLM-фич, где без централизации каждая команда переизобретает retry, кэш и учёт затрат по-своему.


## Q2. (!) Как реализовать streaming-ответы от LLM?

Модель генерирует ответ токен за токеном, поэтому не нужно ждать весь ответ — токены можно отдавать клиенту по мере готовности. Технически это делается флагом `stream=True` в SDK провайдера: вместо одного объекта приходит итератор чанков, каждый с очередным куском текста (`delta`).

**Сторона клиента SDK (OpenAI / Anthropic):**

```python
stream = client.chat.completions.create(
    model="gpt-4o",
    messages=[...],
    stream=True
)

for chunk in stream:
    delta = chunk.choices[0].delta.content
    if delta:
        yield delta  # send to client
```

**Серверная сторона (FastAPI):** оборачиваем стрим в `StreamingResponse` и форматируем каждый чанк как SSE-событие (`data: ...\n\n`):

```python
from fastapi.responses import StreamingResponse

@app.post("/chat")
async def chat(req: ChatRequest):
    async def generate():
        stream = client.chat.completions.create(stream=True, ...)
        for chunk in stream:
            yield f"data: {chunk.choices[0].delta.content}\n\n"
    return StreamingResponse(generate(), media_type="text/event-stream")
```

**Зачем это нужно:**
- **Ниже воспринимаемая задержка** — пользователь видит первые слова сразу (важен TTFT, а не общее время генерации)
- **Можно прервать** генерацию на середине, если ответ уже не нужен — экономит токены
- **Меньше риск таймаутов** — соединение «живое», данные текут постоянно, а не висят молча до конца


## Q3. (!) Что такое SSE (Server-Sent Events) и зачем он для streaming?

**SSE** — это HTTP-стандарт для одностороннего потока данных server → client поверх обычного долгоживущего HTTP-соединения. Сервер не закрывает ответ, а шлёт события строками `data: ...`, разделёнными пустой строкой. Это естественная транспортная основа для LLM-стриминга: каждый токен (или чанк) становится отдельным событием.

```
HTTP/1.1 200 OK
Content-Type: text/event-stream

data: Hello

data: world

data: [DONE]
```

**Сторона браузера (JavaScript):** встроенный `EventSource` сам держит соединение и парсит события:
```javascript
const evtSource = new EventSource("/chat?msg=hi");
evtSource.onmessage = (e) => {
    if (e.data === "[DONE]") {
        evtSource.close();
        return;
    }
    appendToChat(e.data);
};
```

**Почему SSE подходит для LLM:**
- **Простота** — это обычный HTTP, не нужен отдельный протокол и апгрейд соединения
- **Автоматический reconnect** из коробки — `EventSource` сам переподключается при обрыве
- **Дружит с инфраструктурой** — проходит через CDN и прокси (Cloudflare и т. п.), которые знают HTTP
- **Однонаправленность = меньше состояния** на сервере, чем у двусторонних соединений

Не случайно сами **OpenAI и Anthropic** отдают стриминговые ответы именно в формате SSE.


## Q4. WebSocket или SSE для LLM — что выбрать?

Главное различие: SSE — односторонний канал (только server → client), WebSocket — полнодуплексный (обе стороны шлют данные в любой момент). Для LLM-стриминга данные текут в одну сторону, поэтому более простой SSE обычно выигрывает.

| Критерий | SSE | WebSocket |
|----------|-----|-----------|
| Направление | Server → Client | Двунаправленный |
| Протокол | HTTP | Свой (апгрейд HTTP) |
| Reconnect | Автоматический | Вручную |
| Прокси/CDN | Хорошо работает | Сложнее |
| Сценарий | LLM-стриминг | Чат в реальном времени (peer-to-peer) |

**Вывод:** для типичного LLM-стриминга (ответ течёт только к клиенту) достаточно SSE — он проще и лучше дружит с прокси/CDN. WebSocket берут, когда клиенту нужно интерактивно вмешиваться в ходе генерации: прерывать, дослать контекст, голосовой дуплекс.


## Q5. (!) Как сделать retry с exponential backoff?

LLM API то и дело отдают временные ошибки (429, 5xx, таймауты). Повтор с **экспоненциально растущей паузой** между попытками даёт перегруженному провайдеру время прийти в себя и не добивает его шквалом одинаковых ретраев. В Python это удобно навесить декоратором `tenacity`:

```python
from tenacity import retry, stop_after_attempt, wait_exponential, retry_if_exception_type

@retry(
    stop=stop_after_attempt(5),
    wait=wait_exponential(multiplier=1, min=2, max=60),
    retry=retry_if_exception_type((RateLimitError, APIError, Timeout))
)
def call_llm(prompt):
    return client.chat.completions.create(...)
```

**Схема ожидания:** 2s → 4s → 8s → 16s → 60s (max). Каждая пауза примерно вдвое длиннее предыдущей, пока не упрётся в потолок.

Ключевой момент — **ретраить только то, что может пройти со второго раза**:

**Повторяем (временные сбои):**
- HTTP 429 (rate limit)
- HTTP 500–599 (ошибки сервера)
- Таймауты
- Ошибки соединения

**Не повторяем (ошибка на нашей стороне — retry бесполезен):**
- HTTP 400 (bad request) — кривой запрос, повтор даст ту же ошибку
- HTTP 401, 403 (авторизация) — нет прав, ретрай не добавит
- HTTP 422 (валидация)


## Q6. (!) Зачем circuit breaker для LLM API и как он работает?

**Circuit breaker** («предохранитель») защищает от ситуации, когда провайдер уже лёг, а мы продолжаем слать запросы и ждать таймаутов. Логика как у электрического предохранителя: после нескольких ошибок подряд цепь **размыкается**, и какое-то время мы вообще не дёргаем сбойный сервис — запросы падают мгновенно. Через паузу пробуем снова.

```python
from pybreaker import CircuitBreaker

llm_breaker = CircuitBreaker(fail_max=5, reset_timeout=60)

@llm_breaker
def call_openai(prompt):
    return openai_client.chat.completions.create(...)
```

**Три состояния:**
- **Closed** — норма, вызовы проходят, а счётчик ошибок копится
- **Open** — после `fail_max` ошибок цепь разомкнута: вызовы падают сразу, не доходя до API
- **Half-open** — по истечении `reset_timeout` пропускаем 1–2 пробных вызова; успех → возвращаемся в closed, провал → снова open

**Зачем:** не добивать уже мёртвый сервис, не тратить потоки и таймауты на заведомо провальные вызовы, **падать быстро** (fail fast) и освобождать ресурсы для запасного пути (fallback).

Подробнее — в [Resilience Patterns](../architecture/resilience-patterns-interview.md).


## Q7. (!) Как сделать fallback между провайдерами (Claude → GPT)?

Идея простая: держим **упорядоченный список провайдеров** и идём по нему сверху вниз. Первый, кто ответит без ошибки, и даёт результат; если все упали — кидаем общую ошибку. Так временный сбой одного провайдера не роняет продукт целиком.

```python
def chat_with_fallback(messages):
    providers = [
        lambda: anthropic.messages.create(model="claude-opus-4-7", messages=messages),
        lambda: openai.chat.completions.create(model="gpt-4o", messages=messages),
        lambda: google.generate(model="gemini-1.5-pro", messages=messages)
    ]

    for provider in providers:
        try:
            return provider()
        except (RateLimitError, APIError, Timeout) as e:
            log.warning(f"Provider failed: {e}, trying next")
    raise AllProvidersFailedError()
```

**Подводный камень:** у разных провайдеров **разный формат API** (имена методов, параметры, структура ответа). Чтобы единый `provider()` работал, нужен слой-адаптер, приводящий вызовы к общему виду — это и берёт на себя LiteLLM.

**Сценарий применения:** основной провайдер недоступен — переключаемся на резервный и не роняем продукт.


## Q8. Как настраивать таймауты для LLM-вызовов?

Таймаут можно задать на весь клиент или переопределить на отдельный запрос:

```python
client = OpenAI(timeout=30.0)  # default
# или per-request
response = client.chat.completions.create(timeout=60.0, ...)
```

Подбирают его под характер вызова:
- **Стриминг:** ставят больше (5–10 мин) — модель может долго «думать», но соединение всё это время живое
- **Без стриминга:** обычно 30–60 сек, дальше ждать смысла мало
- **TTFT (Time-To-First-Token):** держать < 3 сек; если первый токен идёт дольше — сигнал проблемы

**Подводный камень:** дефолтные таймауты SDK OpenAI/Anthropic могут быть слишком большими для UX — пользователь будет минутами смотреть на спиннер вместо честной ошибки. Лучше выставить свои значения явно.


## Q9. (!) Что такое model routing (эскалация small → large)?

**Суть:** не гонять самую дорогую модель на каждом запросе. Большинство запросов простые — их закрывает дешёвая быстрая модель, а к тяжёлой эскалируем только то, с чем дешёвая не справилась. Самый простой каскад — «сначала small, при сомнении → large»:

```python
def smart_route(query):
    # Try small model first
    response = small_model(query)

    if response.confidence < 0.7 or "I'm not sure" in response.text:
        # Escalate to large model
        response = large_model(query)

    return response
```

Здесь триггер эскалации — низкая уверенность или явное «не знаю» в ответе.

**Более продвинутые варианты:**
- **Classifier** перед LLM заранее оценивает сложность запроса и сразу выбирает нужную модель (без двойного вызова)
- **Mixture of Experts** — пул специализированных моделей, каждая под свой класс задач

**Эффект:** экономия 50–80% затрат на приложениях, где запросы сильно разнятся по сложности — основной поток уходит на дешёвую модель.


## Q10. (!) Что такое semantic caching (семантическое кэширование)?

Обычный кэш срабатывает только на **дословное** совпадение запроса, поэтому «как сбросить пароль?» и «забыл пароль, что делать?» для него — два разных ключа, и hit rate остаётся низким. Семантический кэш решает это, кэшируя по **смыслу**: ключом становится embedding запроса, и попадание засчитывается, когда новый запрос достаточно **похож** на уже виденный (по косинусной близости векторов).

- **Кэш по точному совпадению:** ключ = полный prompt → низкий hit rate
- **Семантический кэш:** ключ = embedding запроса → попадание на похожих по смыслу запросах

```python
def semantic_cache_lookup(query, threshold=0.95):
    query_emb = embed(query)
    similar_queries = vector_db.search(query_emb, top_k=1)
    if similar_queries[0].score >= threshold:
        return similar_queries[0].metadata["response"]
    return None

def chat(query):
    if cached := semantic_cache_lookup(query):
        return cached  # instant + free
    response = call_llm(query)
    store_cache(query, response)
    return response
```

**Подводный камень — выбор порога схожести:** это компромисс между точностью и hit rate.
- **Слишком высокий порог** → почти ничего не совпадает, hit rate падает
- **Слишком низкий порог** → ложные срабатывания: формулировки похожи, но намерение разное → пользователь получает чужой, неверный ответ

**Инструменты:** GPTCache, Redis Vector Search.


## Q11. Что такое prompt caching на стороне провайдера?

В отличие от семантического кэша (Q10), который мы держим у себя, **prompt caching** делает сам провайдер. **Anthropic и OpenAI** умеют кэшировать неизменный **префикс** промпта: если начало запроса совпадает с прошлым, провайдер не пересчитывает его заново, а берёт из своего кэша. У Anthropic это включается явной пометкой блока:

```python
{"role": "system", "content": [
    {"type": "text", "text": LARGE_SYSTEM_PROMPT, "cache_control": {"type": "ephemeral"}}
]}
```

**Экономия:** до 90% на закэшированной части промпта.

**Сценарий применения:** RAG с большими статичными документами. Документы кладут в **начало** промпта (префикс) → они кэшируются между запросами. Меняется только вопрос пользователя в конце → каждый следующий запрос дешевле, потому что тяжёлый общий контекст не оплачивается повторно. Поэтому стабильную часть всегда ставят в начало, а изменчивую — в конец.


## Q12. Что даёт батчинг запросов к LLM API?

**Batch API** (OpenAI, Anthropic) — это асинхронный режим для несрочной нагрузки: ты загружаешь сразу пачку запросов одним файлом, провайдер обрабатывает её в фоне (часами, когда у него есть свободные мощности), а ты потом забираешь готовые результаты. Платой за отсутствие realtime становится **скидка 50%** против обычного синхронного API.

- Асинхронно: отправляешь батч → ждёшь несколько часов → забираешь результаты
- **Скидка 50%** по сравнению с синхронным API
- Подходит для сценариев, где задержка в часах не критична

```python
batch = client.batches.create(
    input_file_id="...",  # JSONL файл с requests
    endpoint="/v1/chat/completions",
    completion_window="24h"
)
# wait few hours
result = client.batches.retrieve(batch.id)
```

**Сценарии применения** — всё, что считается оффлайн:
- Массовая классификация
- Фоновый ETL
- Генерация embedding-ов (тоже батчем)
- Несрочная суммаризация


## Q13. (!) Как трекать затраты по пользователю и по фиче?

Затраты на LLM считаются из usage-полей ответа: берём число input/output-токенов, умножаем на цену модели — и сохраняем не одну общую сумму, а **разметку**: кто (user_id) и в какой фиче (feature) потратил. Без этой детализации общий счёт растёт, а понять *что именно* его раздувает невозможно.

```python
def call_llm_with_tracking(user_id, feature, messages):
    response = llm.chat.completions.create(...)

    cost = calculate_cost(
        model=response.model,
        input_tokens=response.usage.prompt_tokens,
        output_tokens=response.usage.completion_tokens
    )

    metrics.track({
        "user_id": user_id,
        "feature": feature,
        "model": response.model,
        "input_tokens": response.usage.prompt_tokens,
        "output_tokens": response.usage.completion_tokens,
        "cost_usd": cost,
        "timestamp": time.time()
    })
    return response
```

**Зачем такая детализация:**
- **Бизнес-метрики** — затраты на активного пользователя (unit economics)
- **Выявление дорогих фич** — видно, какая функция съедает бюджет
- **Контроль квот** — разделить free tier и платный
- **Детектирование аномалий** — резкий всплеск трат у одного пользователя как сигнал злоупотребления

**Инструменты:** Helicone, Langfuse или собственное решение на Postgres + Grafana.


## Q14. (!) Как применить token bucket к LLM API?

**Token bucket** — это «ведро», которое наполняется разрешениями с фиксированной скоростью (`rate`) и имеет ёмкость (`capacity`). Каждый запрос забирает разрешение; пусто — запрос отклоняется. Скорость задаёт средний поток, а ёмкость — допустимый burst (короткий всплеск). Мы ставим этот лимит **у себя**, чтобы не упереться в лимит провайдера со стороны 429.

```python
from token_bucket import TokenBucket

bucket = TokenBucket(rate=100, capacity=200)  # 100 RPS, max burst 200

def call_llm(prompt):
    if not bucket.consume(1):
        raise RateLimitedError()
    return llm.chat.completions.create(...)
```

**Зачем:** держать свой поток ниже лимитов провайдера → меньше HTTP 429 и связанных с ними ретраев.

**Важный нюанс для LLM:** считать стоит не только запросы. У OpenAI лимит есть и по токенам (TPM — tokens per minute), поэтому отдельное ведро имеет смысл вести по output-токенам, а не только по числу вызовов.


## Q15. Как сделать rate limit на каждого пользователя?

Если лимит из Q14 защищает систему в целом, то per-user-лимит ограничивает **каждого** пользователя по отдельности. Простейшая реализация — счётчик в Redis с ключом, привязанным к пользователю и текущей минуте; первый запрос минуты ставит TTL, дальше счётчик инкрементится, превышение — ошибка:

```python
def rate_limit_user(user_id, max_per_min=10):
    key = f"ratelimit:{user_id}:{int(time.time() / 60)}"
    count = redis.incr(key)
    if count == 1:
        redis.expire(key, 60)
    if count > max_per_min:
        raise UserRateLimitedError()
```

Ключ включает номер минуты (`time() / 60`), поэтому каждую минуту окно обнуляется само — это fixed window.

**Зачем:**
- **Защита от злоупотреблений** — один пользователь не выжжет всю квоту провайдера
- **Тарифные уровни** — free: 10 RPM, premium: 100 RPM
- **Контроль затрат** — бюджет в пересчёте на пользователя


## Q16. Как сделать распределённый rate limiting на Redis?

Когда приложение крутится в несколько инстансов, локальный счётчик в памяти не работает: каждый инстанс видит только свою долю трафика, и суммарно лимит превышается. Решение — хранить состояние в **общем** Redis, тогда все инстансы согласованно видят единый лимит. Ниже — алгоритм sliding window log: храним метки времени запросов в sorted set, на каждом запросе выкидываем всё старше окна и считаем оставшееся:

```python
# Sliding window log
def is_allowed(user_id, max_per_min=10):
    key = f"requests:{user_id}"
    now = time.time()
    redis.zremrangebyscore(key, 0, now - 60)  # cleanup old
    count = redis.zcard(key)
    if count >= max_per_min:
        return False
    redis.zadd(key, {str(uuid.uuid4()): now})
    redis.expire(key, 60)
    return True
```

В отличие от fixed window из Q15, скользящее окно считает запросы за последние 60 секунд в любой момент, без скачка на границе минуты.

**Инструменты:** `redis-py-cluster`, `aioredis`.


## Q17. (!) Зачем нужна абстракция над провайдером?

У каждого провайдера свой API: разные методы, разные параметры, разный формат ответа. Если код напрямую завязан на конкретный SDK, он жёстко привязан к одному вендору. Абстракция (единый интерфейс `gateway.chat(...)`) прячет эти различия, и приложение работает с любым провайдером одинаково.

**Без абстракции** — три разных вызова под три провайдера:
```python
# OpenAI
openai_client.chat.completions.create(model="gpt-4", messages=[...])

# Anthropic
anthropic_client.messages.create(model="claude-opus-4", messages=[...], max_tokens=1024)

# Google
google_client.generate(model="gemini-1.5-pro", contents=[...])
```

Разные API, разные параметры, разные форматы ответов — и всё это просачивается в прикладной код.

**С абстракцией** — один интерфейс на всех:
```python
gateway.chat(model="gpt-4", messages=[...])
gateway.chat(model="claude-opus-4", messages=[...])
# Same interface
```

**Что это даёт:**
- **Лёгкая смена провайдеров** — нет vendor lock-in, меняется одна строка
- **A/B-тестирование** разных моделей на одном коде
- **Fallback** между провайдерами становится тривиальным (см. Q7)
- **Централизованные логирование и кэширование** — навешиваются в одном месте


## Q18. (!) Чем различаются LiteLLM, OpenRouter, Portkey?

Все три закрывают мульти-провайдерность из Q17, но на разных уровнях: LiteLLM — библиотека в твоём коде, OpenRouter — внешний прокси-маркетплейс, Portkey/Helicone — полноценные gateway-сервисы.

**LiteLLM** — Python SDK, унифицирует **100+ моделей** под единый вызов `completion(...)`:

```python
from litellm import completion

response = completion(
    model="gpt-4o",  # или "claude-opus-4-7", "gemini-1.5-pro"
    messages=[...]
)
```

**OpenRouter** — прокси/маркетплейс для LLM: один API-ключ, выбор из десятков моделей, общий биллинг.

**Portkey** — AI gateway: маршрутизация, fallback, наблюдаемость, кэширование, guardrails в одном сервисе.

**Helicone** — тот же подход через прокси, но с фокусом на наблюдаемости и кэшировании.

**Когда что брать:**
- **LiteLLM** — нужна минимальная абстракция прямо в коде, без внешнего сервиса
- **OpenRouter** — хочешь быстро экспериментировать с разными моделями под одним ключом
- **Portkey / Helicone** — enterprise: нужны governance, наблюдаемость и контроль из коробки


## Q19. Что такое нормализация параметров модели?

Проблема в том, что общие на словах параметры у провайдеров расходятся: `temperature` есть у всех, а `presence_penalty` поддерживает OpenAI, но не Anthropic; у Google свой `top_k`. Нормализация — это приведение этих параметров к единому набору, чтобы прикладной код задавал их одинаково независимо от провайдера.

```python
# Different providers — different params
openai_params = {"temperature": 0.7, "presence_penalty": 0.5}
anthropic_params = {"temperature": 0.7}  # no presence_penalty
google_params = {"temperature": 0.7, "top_k": 40}
```

**Что должен делать слой абстракции:**
- **Маппить общие параметры** (temperature, max_tokens) в формат каждого провайдера
- **Обрабатывать особенности** — отбрасывать или эмулировать неподдерживаемые параметры
- **Валидировать вход** до отправки в API

LiteLLM делает эту нормализацию автоматически.


## Q20. (!) Что нужно трекать в LLM-системах?

LLM-вызов непредсказуем по стоимости, задержке и качеству, поэтому наблюдаемость строят на трёх уровнях: отдельный запрос, система целиком и поведение пользователя. Каждый уровень отвечает на свой вопрос — «что пошло не так в этом вызове», «сколько мы тратим и держим ли нагрузку», «довольны ли люди».

**По каждому запросу** (отладка и атрибуция затрат):
- Какая модель использована
- Input/output-токены
- Стоимость
- Latency (TTFT, TTFC, TPOT, total)
- HTTP-статус / ошибка
- User ID, фича, request ID
- Попадание/промах кэша

**По системе в целом** (здоровье и расходы):
- RPS, доля ошибок
- Расход токенов (input/output)
- Затраты в час/сутки
- Затраты на фичу
- Распределение использования по провайдерам

**На уровне пользователя** (качество и продукт):
- Удовлетворённость (лайк/дизлайк)
- Использование фич
- Затраты на пользователя


## Q21. (!) Как трассировать цепочки промптов (prompt chains)?

Реальный LLM-ответ обычно собирается из нескольких шагов: достать контекст, переранжировать, вызвать модель. Когда что-то тормозит или ломается, по одному логу не понять, *какой именно* шаг виноват. **Trace** связывает все эти вызовы в одну запись с вложенными span-ами, и сразу видно, где ушло время.

```python
with tracer.span("rag_pipeline") as root:
    with tracer.span("retrieval"):
        chunks = vector_db.search(query)
    with tracer.span("rerank"):
        ranked = rerank(chunks)
    with tracer.span("llm_call", attributes={"model": "gpt-4o"}):
        answer = llm(prompt)
```

**Визуализация:**
```
rag_pipeline (2.5s)
├── retrieval (0.2s)
├── rerank (0.3s)
└── llm_call (2.0s)
```

По такому дереву сразу видно, что львиную долю 2.5 с съел сам вызов модели, а retrieval и rerank — мелочь.

**Инструменты:** OpenTelemetry, LangSmith, Langfuse, Phoenix.

Подробнее — [Observability](../monitoring/observability-interview.md).


## Q22. Чем различаются Helicone, Langfuse, LangSmith?

Это инструменты наблюдаемости из Q20–Q21, но с разным акцентом: Helicone подключается как прокси одной строкой, Langfuse и LangSmith дают полноценную трассировку с датасетами и управлением промптами.

**Helicone** (open-source/SaaS) — минимум усилий:
- Просто **прокси** перед OpenAI API — меняешь base URL, и всё
- Автоматически трекает все вызовы
- Умеет кэширование и rate limiting

**Langfuse** (open-source) — полный observability-стек:
- Трассировка цепочек промптов
- Датасеты для оценки
- Управление промптами

**LangSmith** (от LangChain) — то же, но в экосистеме LangChain:
- Тесно интегрирован с LangChain
- Трассировка, датасеты, оценки
- Версионирование промптов

**Phoenix (Arize):** open-source, фокус на embedding-ах и трассировке.

**Как выбрать:** в 2025 решает стек. Если приложение на LangChain — естественный выбор LangSmith; без LangChain удобнее Langfuse или Helicone.


## Q23. Что означают метрики задержки TTFT и TPOT?

Латентность LLM нельзя описать одним числом: важно и то, как быстро пошёл первый токен (TTFT), и как быстро текут остальные (TPOT/TPS). Для интерактивного чата критичен первый, для пакетной обработки — второй.

| Метрика | Расшифровка | Описание |
|---------|-------------|----------|
| **TTFT** | Time To First Token | Задержка до первого слова (важно для UX) |
| **TPOT** | Time Per Output Token | Сколько времени уходит на каждый токен |
| **TTFC** | Time To First Chunk | Похоже на TTFT |
| **TPS** | Tokens Per Second | Пропускная способность по выводу |
| **E2E** | End-to-end latency | Общее время |

**Как читать эти метрики:**
- **TTFT** — ключевая для UX чата: пользователь оценивает отзывчивость по первому слову
- **TPS** — пропускная способность, важна для пакетной обработки
- **E2E = TTFT + tokens × TPOT** — общее время складывается из старта плюс генерации каждого токена

Типичные значения у OpenAI / Anthropic:
- TTFT: 0.5–3 сек
- TPS: 50–150 токенов/сек


## Q24. (!) Синхронные или асинхронные вызовы LLM — что выбрать?

LLM-вызов — это долгое ожидание сети (секунды), в течение которого процесс ничего не делает. Синхронный вызов на это время блокирует поток; асинхронный — освобождает event loop, позволяя в это же время обрабатывать другие запросы. Для веб-сервера под нагрузкой это разница между «держим десятки одновременных запросов» и «упёрлись в пул потоков».

**Синхронно** — поток ждёт ответа:
```python
response = client.chat.completions.create(...)
```

**Асинхронно** — `await` отпускает loop на время ожидания:
```python
response = await async_client.chat.completions.create(...)
```

**Чем хорош async:**
- **Параллелит много вызовов** одновременно (см. `gather` ниже)
- **Не блокирует event loop** (FastAPI, asyncio) — сервер остаётся отзывчивым
- **Выше пропускная способность** при высокой конкурентности

```python
# Parallel calls
responses = await asyncio.gather(
    client.chat.completions.create(...),
    client.chat.completions.create(...),
    client.chat.completions.create(...)
)
```

**Эмпирическое правило:** в веб-серверах LLM-вызовы делают **всегда асинхронными** — иначе на каждом долгом запросе впустую висит поток.


## Q25. Зачем выносить долгие генерации в background jobs?

Если ответ генерируется **минутами** (длинные тексты, reasoning-модели, агенты), держать ради него открытым HTTP-запрос плохо: соединение рвётся по таймауту, а пользователь сидит на «вечном спиннере». Решение — не ждать синхронно: поставить задачу в очередь, сразу вернуть `job_id`, а клиент пусть опрашивает статус (или ждёт пуш):

```python
@app.post("/generate")
async def generate(req):
    job_id = uuid.uuid4()
    queue.enqueue(generate_task, req, job_id)
    return {"job_id": job_id, "status": "pending"}

@app.get("/status/{job_id}")
async def status(job_id):
    return jobs.get(job_id)  # {"status": "complete", "result": "..."}
```

**Инструменты:** Celery, RQ, Sidekiq или собственное решение.

**Сценарии применения:**
- Генерация длинных текстов (эссе, отчёты)
- Reasoning-модели (o1, o3) — могут думать минутами
- Многошаговые агенты


## Q26. (!) Как устроен pipeline модерации контента?

Модерацию ставят с **двух сторон** от модели. На входе — чтобы не дать токсичному или запрещённому запросу вообще дойти до LLM (а заодно не платить за такой вызов). На выходе — чтобы поймать вредный ответ, даже если запрос выглядел безобидно. Классификатор помечает текст (`flagged`), и при срабатывании мы отдаём безопасную заглушку вместо реального ответа.

**На входе** (до LLM) — фильтруем запрос пользователя:
```python
moderation = openai.moderations.create(input=user_message)
if moderation.results[0].flagged:
    return "I cannot help with that request."
```

**На выходе** (после LLM) — проверяем сгенерированный ответ:
```python
response = llm(prompt)
moderation = openai.moderations.create(input=response)
if moderation.results[0].flagged:
    return generic_safe_response()
```

**Категории классификации:** ненависть, сексуальный контент, насилие, self-harm, харассмент.

**Инструменты:**
- OpenAI Moderations API (бесплатно)
- Anthropic Constitutional AI (встроено)
- Perspective API (Google)
- Свои self-hosted классификаторы


## Q27. Зачем нужен audit logging?

**Audit log** — неизменяемая (append-only) запись каждого взаимодействия с LLM: кто, когда, с каким промптом обратился, что ответила модель, сколько это стоило и прошло ли модерацию. Ключевое слово — «неизменяемая»: записи нельзя задним числом править, иначе лог бесполезен для compliance и разбора инцидентов. Для регуляторики промпт хранят и в исходном, и в обезличенном (redacted) виде.

```python
audit_log({
    "request_id": uuid,
    "user_id": ...,
    "timestamp": now,
    "input_prompt": prompt,
    "input_redacted": redact_pii(prompt),  # для compliance
    "model": "gpt-4o",
    "output": response,
    "tokens": ...,
    "cost": ...,
    "moderation": {...},
    "purpose": "customer_support"
})
```

**Зачем это нужно:**
- **Compliance** — требования GDPR, HIPAA по хранению и обезличиванию данных
- **Отладка** проблем в production — восстановить, что именно отвечала модель
- **Анализ качества** — прогон логов через LLM-as-judge
- **Сбор датасетов для fine-tuning** — с согласия пользователя

**Где хранить:** S3 + Athena, BigQuery или Postgres с аналитическими инструментами.


## Q28. (!) Какие частые проблемы LLM в production?

Сводный чек-лист — фактически зеркало паттернов из всей шпаргалки: каждая проблема ниже закрывается одним из разобранных приёмов.

1. **Неконтролируемый рост затрат** — без мониторинга счёт раздувается незаметно (→ cost tracking, Q13)
2. **Rate limits** — лимиты API игнорируются, ловим 429 (→ token bucket, Q14)
3. **Недоступность провайдера** — нет fallback, продукт падает с провайдером (→ Q7)
4. **Медленные запросы** — нет мониторинга TTFT (→ Q23)
5. **Prompt injection** — вход не валидируется
6. **Утечка PII** — чувствительные данные попадают в логи/промпты (→ redaction, Q27)
7. **Регрессия при обновлении модели** — провайдер обновил версию → просадка качества
8. **Нет стриминга** — пользователь ждёт весь ответ, UX страдает (→ Q2)
9. **Синхронные вызовы** — блокируют потоки, низкая пропускная способность (→ async, Q24)
10. **Нет кэширования** — переплата за одинаковые запросы (→ Q10, Q11)
11. **Галлюцинации без guardrails** — неверные ответы уходят в production
12. **Vendor lock-in** — невозможно сменить провайдера (→ абстракция, Q17)

**Вывод:** production-ready LLM-система — это в основном инфраструктура (надёжность, затраты, наблюдаемость, безопасность) поверх собственно вызова API. Сам `client.create()` — лишь малая её часть.


---

## See also

- [LLM Basics](llm-basics-interview.md) — основа
- [RAG](rag-interview.md) — popular pattern
- [Prompt Engineering](prompt-engineering-interview.md) — prompts design
- [AI Agents](ai-agents-interview.md) — autonomous LLM systems
- [MLOps](mlops-interview.md) — operations
- [Model Serving](model-serving-interview.md) — для self-hosted
- [Caching](../architecture/caching-strategies-interview.md) — semantic cache
- [Resilience Patterns](../architecture/resilience-patterns-interview.md) — circuit breaker
- [Observability](../monitoring/observability-interview.md) — tracing, metrics
- [Микросервисы](../architecture/microservices-interview.md) — gateway pattern
- [Application Security](../security/application-security-interview.md) — prompt injection
- [API Gateway](../architecture/api-gateway-interview.md) — generalized pattern
