---
title: "Вопросы на собеседовании: Spring AI"
description: "Spring AI для интеграции LLM: ChatClient, EmbeddingModel, VectorStore, Function Calling, RAG pipeline, Advisors, тестирование с поддержкой OpenAI/Anthropic/Ollama"
tags:
  - interview
  - spring
  - spring-ai-interview
aliases:
  - "Spring AI interview"
  - "Spring AI собеседование"
  - "Spring AI вопросы"
  - "LLM Spring Boot interview"
  - "ChatClient interview"
difficulty: "intermediate"
updated: "2026-04-25"
---
# Вопросы на собеседовании: `Spring AI`

`Spring AI` — официальный модуль экосистемы Spring для интеграции AI-моделей. Унифицированный API поверх разных провайдеров (OpenAI, Anthropic, Vertex AI, Mistral, Ollama). Предоставляет `ChatClient`, `EmbeddingModel`, `VectorStore` и поддержку RAG. Горячая тема в 2024-2025 интервью.

Дата последнего обновления: 2026-04-20

## Полезные ссылки

### Официальная документация

- [Spring AI Docs](https://docs.spring.io/spring-ai/reference/) — официальная документация
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai) — репозиторий с примерами
- [Baeldung: Spring AI](https://www.baeldung.com/spring-ai) — практическое введение

## Содержание

- [Полезные ссылки](#полезные-ссылки)
- [See also](#see-also)

## Q1. Что такое Spring AI и какие задачи он решает?

**Spring AI** — официальный модуль экосистемы Spring для интеграции AI-моделей в приложения. Предоставляет унифицированный API поверх разных провайдеров (OpenAI, Anthropic, Vertex AI, Mistral, Ollama и др.).

Ключевые абстракции:
- **`ChatClient`** — взаимодействие с chat-моделями (GPT-4, Claude, Gemini).
- **`EmbeddingModel`** — получение векторных представлений текста.
- **`VectorStore`** — хранение и поиск по embedding-векторам.
- **`ImageModel`** — генерация изображений (DALL-E, Stable Diffusion).
- **`AudioTranscriptionModel`** — транскрипция аудио (Whisper).

Главная ценность: переключение между провайдерами без изменения кода приложения.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q2. Как настроить Spring AI для работы с OpenAI?

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>
```

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o
          temperature: 0.7
          max-tokens: 2000
      embedding:
        options:
          model: text-embedding-3-small
```

```java
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatClient chatClient;

    public String ask(String question) {
        return chatClient.prompt()
            .user(question)
            .call()
            .content();
    }
}
```

```java
// Fluent builder для ChatClient
@Bean
public ChatClient chatClient(ChatClient.Builder builder) {
    return builder
        .defaultSystem("You are a helpful Java developer assistant.")
        .build();
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q3. Как использовать Prompt Templates?

```java
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ChatClient chatClient;

    public String reviewCode(String code, String language) {
        return chatClient.prompt()
            .system("You are a senior {language} developer. Be concise.")
            .user(u -> u.text("Review this code:\n```{language}\n{code}\n```")
                .param("code", code)
                .param("language", language))
            .call()
            .content();
    }

    // Структурированный ответ через BeanOutputConverter
    public CodeReview getStructuredReview(String code) {
        return chatClient.prompt()
            .user("Review this Java code and provide structured feedback: " + code)
            .call()
            .entity(CodeReview.class);  // автоматически парсит JSON ответ в POJO
    }
}

public record CodeReview(
    List<String> issues,
    List<String> suggestions,
    int score,  // 1-10
    String summary
) {}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q4. Что такое Embedding и как получить векторное представление текста?

**Embedding** — числовой вектор, кодирующий семантический смысл текста. Семантически близкие тексты имеют близкие векторы.

```java
@Service
@RequiredArgsConstructor
public class EmbeddingService {
    private final EmbeddingModel embeddingModel;

    public float[] embed(String text) {
        return embeddingModel.embed(text);
    }

    public List<float[]> embedBatch(List<String> texts) {
        EmbeddingResponse response = embeddingModel.embedForResponse(
            List.of(new TextSegment(texts.get(0)), new TextSegment(texts.get(1)))
        );
        return response.getResults().stream()
            .map(r -> r.getOutput())
            .toList();
    }

    // Косинусное сходство двух эмбеддингов
    public double similarity(float[] a, float[] b) {
        return EmbeddingUtil.cossineSimilarity(a, b);
    }
}
```

Типичные применения: семантический поиск, поиск дубликатов, кластеризация, RAG.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q5. Что такое VectorStore и как его использовать?

**VectorStore** — база данных, хранящая документы вместе с их embedding-векторами и позволяющая находить похожие по семантике документы.

```java
// Поддерживаемые: PgVector, ChromaDB, Weaviate, Pinecone, Redis, Elasticsearch, Milvus
@Bean
public VectorStore vectorStore(EmbeddingModel embeddingModel, JdbcTemplate jdbc) {
    return new PgVectorStore(jdbc, embeddingModel);
}
```

```java
@Service
@RequiredArgsConstructor
public class DocumentService {
    private final VectorStore vectorStore;

    // Индексирование документов
    public void indexDocuments(List<String> texts, Map<String, Object> metadata) {
        List<Document> docs = texts.stream()
            .map(t -> new Document(t, metadata))
            .toList();
        vectorStore.add(docs);
    }

    // Семантический поиск
    public List<Document> search(String query, int topK) {
        return vectorStore.similaritySearch(
            SearchRequest.query(query)
                .withTopK(topK)
                .withSimilarityThreshold(0.7)
        );
    }

    // Поиск с метаданным фильтром
    public List<Document> searchByTopic(String query, String topic) {
        return vectorStore.similaritySearch(
            SearchRequest.query(query)
                .withTopK(5)
                .withFilterExpression("topic == '" + topic + "'")
        );
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q6. Как построить RAG-pipeline в Spring AI?

**RAG (Retrieval-Augmented Generation)** — паттерн: сначала ищем релевантные документы в VectorStore, затем добавляем их в контекст промпта.

```java
@Service
@RequiredArgsConstructor
public class RagService {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public String answerWithContext(String question) {
        // 1. Поиск релевантных документов
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.query(question).withTopK(5)
        );

        // 2. Формирование контекста
        String context = docs.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n\n"));

        // 3. Генерация ответа с контекстом
        return chatClient.prompt()
            .system("""
                Answer based solely on the provided context.
                If the answer is not in the context, say "I don't know".
                Context:
                """ + context)
            .user(question)
            .call()
            .content();
    }
}
```

```java
// Автоматический RAG через QuestionAnswerAdvisor
@Bean
public ChatClient ragChatClient(ChatClient.Builder builder, VectorStore vectorStore) {
    return builder
        .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore,
            SearchRequest.defaults().withTopK(5)))
        .build();
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q7. Что такое Function Calling и как его использовать?

**Function Calling** — механизм, при котором модель решает вызвать внешнюю функцию вместо генерации текстового ответа.

```java
// Определяем функцию как Spring Bean
@Bean
@Description("Get current weather for a given city")
public Function<WeatherRequest, WeatherResponse> weatherFunction(WeatherService service) {
    return request -> service.getWeather(request.city(), request.unit());
}

public record WeatherRequest(String city, String unit) {}
public record WeatherResponse(double temperature, String conditions) {}
```

```java
@Service
@RequiredArgsConstructor
public class AssistantService {
    private final ChatClient chatClient;

    public String askWithTools(String question) {
        return chatClient.prompt()
            .user(question)
            .functions("weatherFunction")  // имя Spring bean
            .call()
            .content();
        // Если вопрос про погоду → модель вызовет weatherFunction,
        // получит результат и сформирует финальный ответ
    }
}
```

Модель сама решает, когда и какую функцию вызвать. Можно передавать несколько функций.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q8. Как работает потоковая генерация (Streaming)?

```java
@Service
@RequiredArgsConstructor
public class StreamingChatService {
    private final ChatClient chatClient;

    // Streaming через Project Reactor Flux
    public Flux<String> streamAnswer(String question) {
        return chatClient.prompt()
            .user(question)
            .stream()
            .content();  // Flux<String> — каждый элемент = токен
    }
}
```

```java
// В WebFlux контроллере — Server-Sent Events
@RestController
@RequiredArgsConstructor
public class ChatController {

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String question) {
        return streamingChatService.streamAnswer(question);
    }
}
```

Streaming критичен для UX: пользователь видит ответ по мере генерации, а не ждёт завершения.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q9. Что такое Advisors в Spring AI?

**Advisors** — middleware для ChatClient, перехватывают запросы/ответы и добавляют cross-cutting поведение.

```java
// Встроенные Advisors
@Bean
public ChatClient chatClient(ChatClient.Builder builder, VectorStore vectorStore,
                              ChatMemory chatMemory) {
    return builder
        // RAG: автоматически добавляет релевантные документы в контекст
        .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
        // Память: сохраняет историю разговора
        .defaultAdvisors(new MessageChatMemoryAdvisor(chatMemory))
        // Логирование: выводит запросы/ответы
        .defaultAdvisors(new SimpleLoggerAdvisor())
        .build();
}
```

```java
// Кастомный Advisor (например, для Content Moderation)
@Component
public class ModerationAdvisor implements CallAroundAdvisor {

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest request, CallAroundAdvisorChain chain) {
        String userInput = request.userText();
        if (containsProhibitedContent(userInput)) {
            return AdvisedResponse.from(request,
                ChatResponse.builder()
                    .withGenerations(List.of(new Generation("I cannot help with that.")))
                    .build());
        }
        return chain.nextAroundCall(request);
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q10. Как реализовать многоходовой диалог (Chat Memory)?

```java
// InMemoryChatMemory — для разработки/тестирования
@Bean
public ChatMemory chatMemory() {
    return new InMemoryChatMemory();
}

// CassandraChatMemory, JdbcChatMemory — для production
@Bean
public ChatMemory persistentChatMemory(JdbcTemplate jdbc) {
    return new JdbcChatMemory(jdbc);
}
```

```java
@Service
@RequiredArgsConstructor
public class ConversationService {
    private final ChatClient chatClient;

    public String chat(String conversationId, String message) {
        return chatClient.prompt()
            .user(message)
            .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, conversationId)
                           .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
            .call()
            .content();
    }
}
```

`conversationId` изолирует историю разных пользователей. `RETRIEVE_SIZE` ограничивает количество сообщений в контексте.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q11. Как тестировать Spring AI приложения?

```java
// Мок ChatModel для unit-тестов
@SpringBootTest
class ChatServiceTest {

    @MockBean
    private ChatModel chatModel;

    @Autowired
    private ChatService chatService;

    @Test
    void shouldReturnAiResponse() {
        ChatResponse mockResponse = ChatResponse.builder()
            .withGenerations(List.of(new Generation("Mocked answer")))
            .build();

        when(chatModel.call(any(Prompt.class))).thenReturn(mockResponse);

        String result = chatService.ask("What is Spring AI?");

        assertThat(result).isEqualTo("Mocked answer");
    }
}
```

```java
// TestChatModel — Spring AI тестовый stub
@Bean
@Profile("test")
public ChatModel testChatModel() {
    return new TestChatModel(request -> {
        String userMessage = request.getInstructions().get(0).getContent();
        return new ChatResponse(List.of(new Generation("Test response for: " + userMessage)));
    });
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q12. Какова структура DocumentETL пайплайна для индексирования?

**ETL (Extract-Transform-Load)** для векторного поиска:

```java
@Service
@RequiredArgsConstructor
public class DocumentIndexingService {
    private final VectorStore vectorStore;

    // Извлечение из разных источников
    public void indexPdfDocuments(Resource pdfFile) {
        // Extract
        DocumentReader reader = new PagePdfDocumentReader(pdfFile,
            PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                    .withNumberOfBottomTextLinesToDelete(3)
                    .build())
                .build());

        // Transform (разбивка на чанки)
        TextSplitter splitter = new TokenTextSplitter(512, 50);  // 512 токенов, 50 overlap

        // Load (индексирование с автоматическим embedding)
        List<Document> chunks = splitter.apply(reader.get());
        vectorStore.add(chunks);
    }

    // Загрузка из URL или текстовых файлов
    public void indexMarkdown(Resource mdFile, Map<String, Object> metadata) {
        DocumentReader reader = new MarkdownDocumentReader(mdFile);
        List<Document> docs = reader.get().stream()
            .map(d -> new Document(d.getContent(), metadata))
            .toList();
        TextSplitter splitter = new TokenTextSplitter();
        vectorStore.add(splitter.apply(docs));
    }
}
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q13. Чем Spring AI отличается от прямого использования OpenAI SDK?

| Критерий | OpenAI SDK (прямой) | Spring AI |
|----------|---------------------|-----------|
| Портируемость | Только OpenAI | 10+ провайдеров |
| Интеграция с Spring | Вручную | Автоконфигурация |
| RAG / VectorStore | Нет | Встроено |
| Function Calling | Вручную | Декларативно (`@Description`) |
| Тестируемость | Сложнее (HTTP клиент) | Легко (мок ChatModel) |
| Retry / Resilience | Вручную | Встроено через Spring |
| Chat Memory | Нет | Встроено |

Spring AI абстрагирует инфраструктуру, позволяя сосредоточиться на бизнес-логике.


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q14. Как управлять стоимостью и токенами?

```java
// Получение метаданных использования токенов
ChatResponse response = chatClient.prompt()
    .user(question)
    .call()
    .chatResponse();

Usage usage = response.getMetadata().getUsage();
log.info("Tokens: input={}, output={}, total={}",
    usage.getPromptTokens(),
    usage.getGenerationTokens(),
    usage.getTotalTokens());
```

```yaml
# Ограничение токенов в конфигурации
spring:
  ai:
    openai:
      chat:
        options:
          max-tokens: 1000      # лимит генерации
          temperature: 0.3      # детерминированность (0=детерм, 1=креативно)
          top-p: 0.9            # nucleus sampling
          presence-penalty: 0.1 # штраф за повторение тем
```


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление## Q15. Как реализовать AI агент с Spring AI?

```java
// Простой агент с инструментами
@Service
@RequiredArgsConstructor
public class AgentService {
    private final ChatClient chatClient;

    public String runAgent(String task) {
        return chatClient.prompt()
            .system("""
                You are an autonomous agent. You have access to these tools:
                - searchOrders: search customer orders
                - getProductInfo: get product details
                - createTicket: create support ticket
                Use them to accomplish the task.
                """)
            .user(task)
            .functions("searchOrders", "getProductInfo", "createTicket")
            .call()
            .content();
    }
}

// ReAct pattern — цикл рассуждений
@Bean
@Description("Search orders by customer ID")
public Function<OrderSearchRequest, OrderSearchResult> searchOrders(OrderRepository repo) {
    return req -> new OrderSearchResult(repo.findByCustomerId(req.customerId()));
}
```

Модели GPT-4 / Claude поддерживают параллельный вызов нескольких функций в один запрос (parallel function calling).

## See also


> [!mcq]
> - [x] Правильный ответ | Объяснение концепции 2-3 предложения
> - [ ] Неправильный вариант 1 | Почему ошибка в этом подходе
> - [ ] Неправильный вариант 2 | Это смежное, но отличное понятие
> - [ ] Неправильный вариант 3 | Противоположное направление- [LLM Basics](../../ai-ml/llm-basics-interview.md) — основы работы LLM, prompting, context window
- [RAG](../../ai-ml/rag-interview.md) — Retrieval-Augmented Generation, основной паттерн Spring AI
- [Vector Databases](../../ai-ml/vector-databases-interview.md) — PostgreSQL pgvector, Chroma, Pinecone
- [Embeddings](../../ai-ml/embeddings-interview.md) — векторные представления текста
- [AI Agents](../../ai-ml/ai-agents-interview.md) — паттерны агентов, ReAct, Chain of Thought
- [LLM Integration Patterns](../../ai-ml/llm-integration-patterns-interview.md) — интеграция LLM в приложения
- [Prompt Engineering](../../ai-ml/prompt-engineering-interview.md) — работа с промптами
- [Spring Boot](spring-boot-interview.md) — auto-configuration для Spring AI
- [Spring WebFlux](spring-webflux-interview.md) — streaming ответов через Flux
- [PostgreSQL](../../databases/postgresql-interview.md) — pgvector как VectorStore
