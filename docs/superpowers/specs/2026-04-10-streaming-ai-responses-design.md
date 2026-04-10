# Streaming AI Responses via SSE — Design Spec

## Goal

Refactor the AI pipeline to stream LLM responses token-by-token to the browser via Server-Sent Events (SSE), while keeping existing blocking callers unchanged. Direct proxy from LLM provider with post-validation.

## Architecture

```
Browser (EventSource)
  ↓ SSE (text/event-stream)
StreamingController (/api/stream/*)
  ↓ Flux<ServerSentEvent<String>>
Streaming Insight Services
  ↓ Flux<String>
AbstractAiClient.streamChatRequest()
  ↓ SSE proxy (stream: true)
OpenAI / DeepSeek SSE endpoint
```

Two paths coexist:
- **Blocking path** (existing): `sendChatRequest()` → `Optional<String>`. Used by PreloadService, DiagramService, QuestionExpansionService, AIQuestionService, QuestionGenerationService.
- **Streaming path** (new): `streamChatRequest()` → `Flux<String>`. Used by StreamingController → insight/hint services.

## Components

### 1. LLM Layer

**LlmClient interface** — new method:

```java
Flux<String> streamChatRequest(ChatRequest request, Duration timeout);
```

**AiQuestionClient interface** — new method:

```java
Flux<String> streamText(String prompt);
```

**AbstractAiClient** — new implementation:

```java
public Flux<String> streamChatRequest(ChatRequest request, Duration timeout) {
    // 1. POST to provider with "stream": true in request body
    // 2. Receive response as Flux<String> (SSE data lines)
    // 3. Parse each line: extract choices[0].delta.content
    // 4. Filter out empty deltas and [DONE] signal
    // 5. Timeout per Flux.timeout(timeout)
    // 6. Error handling: map WebClient errors to readable messages
}
```

The `ChatRequest` needs a `stream` field (boolean). When true, provider returns SSE instead of a single JSON blob.

**streamText()** default implementation in AbstractAiClient:

```java
public Flux<String> streamText(String prompt) {
    if (isApiKeyMissing() || prompt == null || prompt.isBlank()) {
        return Flux.empty();
    }
    ChatRequest request = buildChatRequest(prompt, temperature());
    request.setStream(true);  // or new ChatRequest with stream=true
    return streamChatRequest(request, requestTimeout());
}
```

### 2. SSE Controller

New `StreamingController` — all endpoints return `SseEmitter`:

| Method | Path | Parameters |
|--------|------|-----------|
| GET | `/api/stream/wrong-feedback` | questionId, optionId |
| GET | `/api/stream/takeaway` | questionId |
| GET | `/api/stream/comparison` | questionId, optionId |
| GET | `/api/stream/code-trace` | questionId |
| GET | `/api/stream/hint` | questionId, level |

**SSE event protocol:**

```
event: token
data: <text chunk>

event: done
data: {}

event: error
data: {"message": "validation failed"}
```

Each endpoint:
1. Calls the streaming variant of the insight service
2. Wraps each `Flux<String>` element as `event: token`
3. On completion → sends `event: done`
4. On error → sends `event: error`
5. SseEmitter timeout = `app.ai.timeoutSeconds`

### 3. Streaming Insight Services

Each insight service gets a streaming method alongside the existing blocking one:

**HintService:**
```java
public Flux<String> streamHint(long questionId, int level) {
    // Check DB cache first → if exists, emit all at once
    // If not cached → streamText(prompt), accumulate, save to DB on completion
}
```

**TakeawayService:**
```java
public Flux<String> streamOrGet(Question question) {
    // If question.takeaway() exists → Flux.just(takeaway)
    // Else → streamText(prompt), save on completion via doOnComplete
}
```

**WrongAnswerFeedbackService:**
```java
public Flux<String> streamFeedback(long questionId, long optionId) {
    // Check pre-generated cache first → if exists, emit all at once
    // If not cached → streamText(prompt)
}

public Flux<String> streamComparison(long questionId, long selectedOptionId) {
    // Always streams fresh from AI (no cache)
}
```

**CodeTraceService:**
```java
public Flux<String> streamTrace(long questionId) {
    // Always streams fresh from AI
}
```

**Pattern for cached responses:** When a cached value exists, emit it as a single `Flux.just()` element — the UI sees instant render, same protocol.

**Post-validation:** Each streaming method uses a shared utility:

```java
public class StreamingValidator {
    // Wraps a Flux<String> to accumulate tokens and validate on completion
    public static Flux<String> withPostValidation(Flux<String> source, Predicate<String> validator) {
        // Accumulate all tokens in StringBuilder
        // On complete: run validator on full text
        // If invalid: emit error signal
        // Tokens are emitted to subscriber in real-time (pass-through)
    }
}
```

### 4. Frontend (app.js)

**New function: `streamToElement(url, targetEl, options)`**

Generic SSE consumer:
```javascript
function streamToElement(url, targetEl, options = {}) {
    const source = new EventSource(url);
    let accumulated = '';

    source.addEventListener('token', (e) => {
        accumulated += e.data;
        targetEl.textContent = accumulated; // raw text during streaming
        options.onToken?.(e.data);
    });

    source.addEventListener('done', () => {
        source.close();
        options.onDone?.(accumulated);
    });

    source.addEventListener('error', (e) => {
        source.close();
        // Fallback to blocking endpoint
        options.onError?.(e);
    });

    return source; // for manual close if needed
}
```

**Changes to `initResultPageExtraAnalysis()`:**
- Replace 4x `fetchWithPlaceholder()` with 4x `streamToElement()`
- Each panel gets progressive text rendering instead of spinner → full result
- On SSE error → fallback to existing `fetchWithPlaceholder()` blocking call

**Changes to `initHintButton()`:**
- Replace `apiPost(API.HINT)` with `streamToElement('/api/stream/hint?...')`
- Progressive hint text rendering

**Markdown rendering during streaming:**
- Use `MarkdownRenderService` on the server side (pre-rendered HTML chunks), OR
- Render raw text during stream, then re-render full markdown on `done` event
- Recommendation: emit raw text during stream, full markdown re-render on done. Simpler, avoids partial-markdown rendering artifacts.

### 5. ChatRequest Changes

Add `stream` field to `ChatRequest`:

```java
public record ChatRequest(
    String model,
    List<ChatMessage> messages,
    double temperature,
    boolean stream  // new field, defaults to false
) {
    // Existing constructor stays (stream=false)
    public ChatRequest(String model, List<ChatMessage> messages, double temperature) {
        this(model, messages, temperature, false);
    }
}
```

## Error Handling

| Scenario | Behavior |
|----------|---------|
| No tokens within timeout | `event: error` with timeout message, SseEmitter completes |
| Provider returns HTTP error | `event: error` with status code, SseEmitter completes |
| Connection drops mid-stream | Browser `onerror` fires, falls back to blocking endpoint |
| Post-validation fails | `event: error` after all tokens sent, UI shows warning |
| API key missing | `Flux.empty()` → `event: done` immediately (no content) |
| Cached result exists | Emit full text as single `event: token`, then `event: done` |

## What's NOT Changing

- Blocking callers: PreloadService, DiagramService, QuestionExpansionService, AIQuestionService, QuestionGenerationService
- WrongAnswerFeedbackService AnswerEvent observer pattern and Caffeine pre-cache
- Database schema — no migrations needed
- Prompts and prompt builders
- Security rules — streaming endpoints follow same `/api/**` rules
- Old blocking endpoints — kept as fallback

## Testing Strategy

- **Unit tests:** AbstractAiClient.streamChatRequest() with mocked WebClient returning Flux of SSE lines
- **Unit tests:** StreamingValidator with valid/invalid accumulated text
- **Unit tests:** Each streaming insight service method with mocked AI client
- **Integration test:** StreamingController returns proper SSE event format (MockMvc with async support)
- **Manual test:** Open result page, verify token-by-token rendering in browser
