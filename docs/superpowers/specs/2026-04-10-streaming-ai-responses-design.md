# Стриминг AI-ответов через SSE — Дизайн-спецификация

## Цель

Рефакторинг AI-пайплайна для потоковой передачи ответов LLM токен-за-токеном в браузер через Server-Sent Events (SSE). Существующие блокирующие вызовы остаются без изменений. Прямое проксирование от LLM-провайдера с пост-валидацией.

## Архитектура

```
Браузер (EventSource)
  ↓ SSE (text/event-stream)
StreamingController (/api/stream/*)
  ↓ Flux<ServerSentEvent<String>>
Стриминг-методы Insight-сервисов
  ↓ Flux<String>
AbstractAiClient.streamChatRequest()
  ↓ SSE-прокси (stream: true)
OpenAI / DeepSeek SSE endpoint
```

Два пути сосуществуют:
- **Блокирующий путь** (текущий): `sendChatRequest()` → `Optional<String>`. Используется PreloadService, DiagramService, QuestionExpansionService, AIQuestionService, QuestionGenerationService.
- **Стриминг-путь** (новый): `streamChatRequest()` → `Flux<String>`. Используется StreamingController → insight/hint сервисы.

## Компоненты

### 1. LLM-слой

**Интерфейс LlmClient** — новый метод:

```java
Flux<String> streamChatRequest(ChatRequest request, Duration timeout);
```

**Интерфейс AiQuestionClient** — новый метод:

```java
Flux<String> streamText(String prompt);
```

**AbstractAiClient** — новая реализация:

```java
public Flux<String> streamChatRequest(ChatRequest request, Duration timeout) {
    // 1. POST к провайдеру с "stream": true в теле запроса
    // 2. Получение ответа как Flux<String> (SSE data-строки)
    // 3. Парсинг каждой строки: извлечение choices[0].delta.content
    // 4. Фильтрация пустых дельт и сигнала [DONE]
    // 5. Таймаут через Flux.timeout(timeout)
    // 6. Обработка ошибок: маппинг ошибок WebClient в читаемые сообщения
}
```

`ChatRequest` получает поле `stream` (boolean). Когда true, провайдер возвращает SSE вместо единого JSON-блоба.

**streamText()** — реализация по умолчанию в AbstractAiClient:

```java
public Flux<String> streamText(String prompt) {
    if (isApiKeyMissing() || prompt == null || prompt.isBlank()) {
        return Flux.empty();
    }
    ChatRequest request = buildChatRequest(prompt, temperature());
    request.setStream(true);
    return streamChatRequest(request, requestTimeout());
}
```

### 2. SSE-контроллер

Новый `StreamingController` — все эндпоинты возвращают `SseEmitter`:

| Метод | Путь | Параметры |
|-------|------|-----------|
| GET | `/api/stream/wrong-feedback` | questionId, optionId |
| GET | `/api/stream/takeaway` | questionId |
| GET | `/api/stream/comparison` | questionId, optionId |
| GET | `/api/stream/code-trace` | questionId |
| GET | `/api/stream/hint` | questionId, level |

**Протокол SSE-событий:**

```
event: token
data: <фрагмент текста>

event: done
data: {}

event: error
data: {"message": "ошибка валидации"}
```

Каждый эндпоинт:
1. Вызывает стриминг-вариант insight-сервиса
2. Оборачивает каждый элемент `Flux<String>` в `event: token`
3. По завершении → отправляет `event: done`
4. При ошибке → отправляет `event: error`
5. Таймаут SseEmitter = `app.ai.timeoutSeconds`

### 3. Стриминг-методы в Insight-сервисах

Каждый insight-сервис получает стриминг-метод рядом с существующим блокирующим:

**HintService:**
```java
public Flux<String> streamHint(long questionId, int level) {
    // Проверка кэша в БД → если есть, эмитим всё разом
    // Если нет → streamText(prompt), аккумулируем, сохраняем в БД по завершении
}
```

**TakeawayService:**
```java
public Flux<String> streamOrGet(Question question) {
    // Если question.takeaway() есть → Flux.just(takeaway)
    // Иначе → streamText(prompt), сохранение по завершении через doOnComplete
}
```

**WrongAnswerFeedbackService:**
```java
public Flux<String> streamFeedback(long questionId, long optionId) {
    // Проверка пре-сгенерированного кэша → если есть, эмитим всё разом
    // Если нет → streamText(prompt)
}

public Flux<String> streamComparison(long questionId, long selectedOptionId) {
    // Всегда стримит свежее от AI (без кэша)
}
```

**CodeTraceService:**
```java
public Flux<String> streamTrace(long questionId) {
    // Всегда стримит свежее от AI
}
```

**Паттерн для кэшированных ответов:** Когда кэшированное значение существует, эмитим его как единый `Flux.just()` элемент — UI видит мгновенный рендер, тот же протокол.

**Пост-валидация:** Каждый стриминг-метод использует общую утилиту:

```java
public class StreamingValidator {
    // Оборачивает Flux<String> для аккумуляции токенов и валидации по завершении
    public static Flux<String> withPostValidation(Flux<String> source, Predicate<String> validator) {
        // Аккумулируем все токены в StringBuilder
        // По завершении: запускаем валидатор на полном тексте
        // Если невалидно: эмитим error-сигнал
        // Токены эмитятся подписчику в реальном времени (pass-through)
    }
}
```

### 4. Фронтенд (app.js)

**Новая функция: `streamToElement(url, targetEl, options)`**

Универсальный SSE-потребитель:
```javascript
function streamToElement(url, targetEl, options = {}) {
    const source = new EventSource(url);
    let accumulated = '';

    source.addEventListener('token', (e) => {
        accumulated += e.data;
        targetEl.textContent = accumulated; // сырой текст во время стриминга
        options.onToken?.(e.data);
    });

    source.addEventListener('done', () => {
        source.close();
        options.onDone?.(accumulated);
    });

    source.addEventListener('error', (e) => {
        source.close();
        // Фолбэк на блокирующий эндпоинт
        options.onError?.(e);
    });

    return source; // для ручного закрытия при необходимости
}
```

**Изменения в `initResultPageExtraAnalysis()`:**
- Замена 4x `fetchWithPlaceholder()` на 4x `streamToElement()`
- Каждая панель получает прогрессивный рендеринг текста вместо спиннер → полный результат
- При ошибке SSE → фолбэк на существующий `fetchWithPlaceholder()` блокирующий вызов

**Изменения в `initHintButton()`:**
- Замена `apiPost(API.HINT)` на `streamToElement('/api/stream/hint?...')`
- Прогрессивный рендеринг текста подсказки

**Рендеринг markdown во время стриминга:**
- Во время стрима — сырой текст (`textContent`)
- По событию `done` — полный ре-рендер через markdown
- Проще и надёжнее, чем рендер partial-markdown с артефактами

### 5. Изменения в ChatRequest

Добавление поля `stream` в `ChatRequest`:

```java
public record ChatRequest(
    String model,
    List<ChatMessage> messages,
    double temperature,
    boolean stream  // новое поле, по умолчанию false
) {
    // Существующий конструктор остаётся (stream=false)
    public ChatRequest(String model, List<ChatMessage> messages, double temperature) {
        this(model, messages, temperature, false);
    }
}
```

## Обработка ошибок

| Сценарий | Поведение |
|----------|----------|
| Нет токенов в течение таймаута | `event: error` с сообщением о таймауте, SseEmitter завершается |
| Провайдер возвращает HTTP-ошибку | `event: error` с кодом статуса, SseEmitter завершается |
| Соединение обрывается в середине стрима | Браузер `onerror` срабатывает, фолбэк на блокирующий эндпоинт |
| Пост-валидация не прошла | `event: error` после отправки всех токенов, UI показывает предупреждение |
| API-ключ отсутствует | `Flux.empty()` → `event: done` немедленно (без контента) |
| Кэшированный результат есть | Эмит полного текста как единый `event: token`, затем `event: done` |

## Что НЕ меняется

- Блокирующие вызовы: PreloadService, DiagramService, QuestionExpansionService, AIQuestionService, QuestionGenerationService
- WrongAnswerFeedbackService: Observer-паттерн через AnswerEvent и Caffeine пре-кэш
- Схема БД — миграции не нужны
- Промпты и prompt builders
- Правила безопасности — стриминг-эндпоинты следуют тем же правилам `/api/**`
- Старые блокирующие эндпоинты — остаются как фолбэк

## Стратегия тестирования

- **Юнит-тесты:** AbstractAiClient.streamChatRequest() с замоканным WebClient, возвращающим Flux SSE-строк
- **Юнит-тесты:** StreamingValidator с валидным/невалидным аккумулированным текстом
- **Юнит-тесты:** Каждый стриминг-метод insight-сервиса с замоканным AI-клиентом
- **Интеграционный тест:** StreamingController возвращает правильный формат SSE-событий (MockMvc с поддержкой async)
- **Ручной тест:** Открыть страницу результата, проверить рендеринг токен-за-токеном в браузере
