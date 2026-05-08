---
title: "Вопросы на собеседовании: Spring AI"
description: "Spring AI для интеграции LLM: ChatClient, EmbeddingModel, VectorStore, Function Calling, RAG pipeline, Advisors, тестирование с поддержкой OpenAI/Anthropic/Ollama"
tags:
  - interview
  - spring
  - spring-ai-interview
type: "interview"
difficulty: "intermediate"
aliases:
  - "Вопросы на собеседовании"
  - "Spring AI"
  - "Spring AI interview"
  - "Spring AI собеседование"
prerequisites:
  - "[[spring-ai]]"
next: []
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
> - [ ] Spring AI — wrapper вокруг OpenAI SDK только для GPT моделей | ❌ ПОСЛЕДСТВИЕ: Spring AI поддерживает 10+ провайдеров; привязка только к OpenAI → рефакторинг при смене на Anthropic/Ollama
> - [ ] ChatClient в Spring AI — это HTTP-клиент для REST запросов | ❌ ПОСЛЕДСТВИЕ: ChatClient — высокоуровневый DSL для AI-моделей с .prompt().user().call(); обычные REST запросы делают WebClient или RestClient
> - [x] Spring AI предоставляет унифицированный API для разных AI-провайдеров: ChatClient, EmbeddingModel, VectorStore — переключение между OpenAI/Anthropic/Ollama без изменения кода | ✓ ПРИМЕНЯТЬ: абстракция над провайдером → Spring AI; прямой API только при специфичных функциях провайдера 📋 ПРАВИЛО: один интерфейс = много провайдеров 🔗 См. Q13
> - [ ] Spring AI заменяет Spring WebFlux для streaming API | ❌ ПОСЛЕДСТВИЕ: Spring AI использует WebFlux/Reactor для streaming — они дополняют друг друга; полная замена WebFlux на Spring AI сломает всю reactive инфраструктуру

## Q2. Как настроить Spring AI для работы с OpenAI?

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
> - [ ] OpenAI API ключ хранится в application.properties напрямую в открытом виде | ❌ ПОСЛЕДСТВИЕ: api ключ в коммите → утечка credentials → счёт на тысячи долларов; используй ${OPENAI_API_KEY} с env variable или Spring Vault
> - [x] Конфигурация через spring.ai.openai.api-key + starter; ChatClient инжектируется как Spring Bean; fluent API через .prompt().user().call().content() | ✓ ПРИМЕНЯТЬ: стартер + yaml конфигурация + инжекция ChatClient 📋 ПРАВИЛО: API ключ через env variable — никогда не hardcode в yaml 🔗 См. Q1
> - [ ] temperature=0 означает максимально случайный ответ | ❌ ПОСЛЕДСТВИЕ: temperature=0 — детерминированный ответ; temperature=1+ — максимальная случайность; инверсия сломает предсказуемость тестов
> - [ ] Для разных провайдеров нужен разный код сервиса | ❌ ПОСЛЕДСТВИЕ: Spring AI унифицирует API — ChatClient работает одинаково для OpenAI/Anthropic/Ollama; смена провайдера через конфигурацию без изменения кода

## Q3. Как использовать Prompt Templates?

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
> - [ ] Промпт всегда должен быть hardcoded строкой в коде | ❌ ПОСЛЕДСТВИЕ: hardcoded промпты → нельзя переиспользовать и тестировать; шаблоны через .param() позволяют параметризацию и mock в unit-тестах
> - [ ] .call().entity(CodeReview.class) требует явного ObjectMapper bean | ❌ ПОСЛЕДСТВИЕ: Spring AI через BeanOutputConverter автоматически добавляет JSON-схему в промпт и парсит ответ; явный ObjectMapper не нужен
> - [ ] system() и user() — одно и то же в Spring AI | ❌ ПОСЛЕДСТВИЕ: system() — инструкции/контекст для AI (роль); user() — сообщение от пользователя; смешение ломает ролевую модель и качество ответов
> - [x] Промпт-шаблоны через .system() и .user() с параметрами {name}; структурированный ответ через .call().entity(Class.class) автоматически парсит JSON | ✓ ПРИМЕНЯТЬ: шаблон + параметры через .param("key", value); структурированный вывод через .entity() 📋 ПРАВИЛО: entity() = auto-JSON parsing без ObjectMapper вручную 🔗 См. Q2

## Q4. Что такое Embedding и как получить векторное представление текста?

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
> - [ ] Embedding — это сжатый hash текста для дедупликации | ❌ ПОСЛЕДСТВИЕ: embedding — не hash; семантически разные тексты могут иметь похожие хэши; embedding отражает смысл, hash — только идентичность
> - [x] EmbeddingModel.embed(text) возвращает float[] вектор; семантически близкие тексты дают близкие векторы; используется для RAG, семантического поиска, кластеризации | ✓ ПРИМЕНЯТЬ: семантический поиск → embed + cosine similarity; RAG → embed и сохрани в VectorStore 📋 ПРАВИЛО: embedding = семантика в числах; текстовый match → keyword search 🔗 См. Q5
> - [ ] EmbeddingModel работает только с моделью text-embedding-ada-002 | ❌ ПОСЛЕДСТВИЕ: Spring AI поддерживает text-embedding-3-small, text-embedding-3-large, Vertex AI, Mistral; смена через yaml без изменения кода
> - [ ] Косинусное сходство возвращает значение от 0 до 100 | ❌ ПОСЛЕДСТВИЕ: косинусное сходство от -1 до 1 (0 до 1 для нормализованных); withSimilarityThreshold(0.7) означает 70% схожесть в диапазоне 0-1, не 70 из 100

## Q5. Что такое VectorStore и как его использовать?

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
> - [ ] VectorStore — это обычная SQL база данных с full-text search | ❌ ПОСЛЕДСТВИЕ: VectorStore хранит векторы и выполняет ANN (approximate nearest neighbor) поиск; full-text search ищет по ключевым словам, а не по семантике
> - [ ] vectorStore.add() автоматически обновляет существующие документы | ❌ ПОСЛЕДСТВИЕ: add() только добавляет; без дедупликации при повторном индексировании — дублирующиеся документы в результатах поиска
> - [x] VectorStore хранит документы с векторами; similaritySearch с withTopK и withSimilarityThreshold находит семантически близкие документы; поддерживаются PgVector, ChromaDB, Pinecone | ✓ ПРИМЕНЯТЬ: RAG → индексировать документы → similaritySearch перед генерацией 📋 ПРАВИЛО: withSimilarityThreshold(0.7) отсекает нерелевантные; без порога = мусор в контексте 🔗 См. Q6
> - [ ] withFilterExpression использует SQL WHERE синтаксис | ❌ ПОСЛЕДСТВИЕ: filterExpression использует Spring AI Filter Expression Language (metadata == 'value'), не SQL; SQL-синтаксис вызовет ParseException

## Q6. Как построить RAG-pipeline в Spring AI?

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
> - [ ] RAG означает отправку всей базы знаний в каждый запрос | ❌ ПОСЛЕДСТВИЕ: вся база → превышение context window LLM + огромные затраты токенов; RAG выбирает только topK=5 наиболее релевантных документов
> - [ ] VectorStore.similaritySearch всегда возвращает только один документ | ❌ ПОСЛЕДСТВИЕ: topK управляет количеством — withTopK(5) возвращает 5 документов; по умолчанию 4; настройка зависит от задачи
> - [ ] QuestionAnswerAdvisor требует ручного написания контекстного промпта | ❌ ПОСЛЕДСТВИЕ: QuestionAnswerAdvisor автоматически строит RAG prompt — добавляет найденные документы как контекст без ручного кода
> - [x] RAG: 1) similaritySearch в VectorStore, 2) формирование контекста из документов, 3) генерация с контекстом в system() промпте; QuestionAnswerAdvisor автоматизирует этот pipeline | ✓ ПРИМЕНЯТЬ: вопрос → similaritySearch → context в prompt → answer 📋 ПРАВИЛО: QuestionAnswerAdvisor = автоматический RAG без ручного кода 🔗 См. Q5

## Q7. Что такое Function Calling и как его использовать?

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
> - [x] Function Calling: Spring Bean типа Function<Request, Response> с @Description; передаётся по имени через .functions("beanName"); модель сама решает когда вызвать | ✓ ПРИМЕНЯТЬ: агент с внешними данными → Function Calling с named beans 📋 ПРАВИЛО: модель выбирает функцию сама; @Description — подсказка модели что делает функция 🔗 См. Q15
> - [ ] Function Calling требует явного указания в промпте когда вызывать функцию | ❌ ПОСЛЕДСТВИЕ: модель сама решает когда вызвать функцию на основе @Description и контекста; явное указание в промпте избыточно и снижает адаптивность
> - [ ] Функция должна возвращать String — иначе Spring AI не сможет её использовать | ❌ ПОСЛЕДСТВИЕ: функция возвращает любой POJO — Spring AI автоматически сериализует в JSON для передачи модели; ограничение только String теряет типобезопасность
> - [ ] .functions() принимает Class<?> объекты, не имена бинов | ❌ ПОСЛЕДСТВИЕ: .functions() принимает имена Spring бинов (строки); для регистрации функции нужен @Bean с именем и Function<Request, Response> тип

## Q8. Как работает потоковая генерация (Streaming)?

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
> - [ ] Streaming работает только с WebFlux и несовместим с обычным Spring MVC | ❌ ПОСЛЕДСТВИЕ: Flux<String> можно использовать и в MVC через subscribe(); TEXT_EVENT_STREAM_VALUE поддерживается в обоих; полное переключение на WebFlux не обязательно
> - [ ] .stream().content() возвращает полный ответ после завершения генерации | ❌ ПОСЛЕДСТВИЕ: .stream() = Flux с каждым токеном по мере генерации; .call().content() = блокирующий ответ после завершения; смешение разрушает UX streaming
> - [x] .stream().content() возвращает Flux<String> — каждый токен по мере генерации; в WebFlux контроллере с produces=TEXT_EVENT_STREAM_VALUE → Server-Sent Events | ✓ ПРИМЕНЯТЬ: длинные ответы LLM → streaming; пользователь видит ответ мгновенно 📋 ПРАВИЛО: .call() = blocking; .stream() = reactive Flux 🔗 См. Q2
> - [ ] Streaming увеличивает стоимость токенов по сравнению с обычным вызовом | ❌ ПОСЛЕДСТВИЕ: streaming не меняет количество токенов — тот же prompt + completion; разница только в UX; стоимость идентична

## Q9. Что такое Advisors в Spring AI?

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
> - [ ] Advisors заменяют всю бизнес-логику приложения | ❌ ПОСЛЕДСТВИЕ: Advisors — для cross-cutting concerns (logging, memory, RAG); бизнес-логика в Service слое; размытие ответственности → нетестируемый код
> - [ ] Кастомный Advisor нельзя написать — только встроенные | ❌ ПОСЛЕДСТВИЕ: Spring AI предоставляет CallAroundAdvisor интерфейс для кастомных advisors (moderation, caching, A/B testing); ограничение встроенными сужает возможности
> - [ ] defaultAdvisors применяются только к первому запросу | ❌ ПОСЛЕДСТВИЕ: defaultAdvisors применяются ко ВСЕМ запросам через данный ChatClient; для конкретного запроса используется .advisors() на уровне промпта
> - [x] Advisors — middleware для ChatClient: QuestionAnswerAdvisor (RAG), MessageChatMemoryAdvisor (история), SimpleLoggerAdvisor; кастомные через CallAroundAdvisor | ✓ ПРИМЕНЯТЬ: cross-cutting concerns → Advisor; бизнес-логика → Service 📋 ПРАВИЛО: Advisor = AOP для AI запросов 🔗 См. Q6

## Q10. Как реализовать многоходовой диалог (Chat Memory)?

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
> - [ ] InMemoryChatMemory подходит для production с высокой нагрузкой | ❌ ПОСЛЕДСТВИЕ: InMemoryChatMemory теряет историю при restart/deploy и не масштабируется между инстансами; для production нужен JdbcChatMemory или CassandraChatMemory
> - [x] ChatMemory хранит историю по conversationId; InMemoryChatMemory для разработки, JdbcChatMemory для production; RETRIEVE_SIZE ограничивает контекст чтобы не превысить context window LLM | ✓ ПРИМЕНЯТЬ: multi-turn диалог → MessageChatMemoryAdvisor + conversationId 📋 ПРАВИЛО: conversationId изолирует истории разных пользователей 🔗 См. Q9
> - [ ] Без ограничения RETRIEVE_SIZE история не растёт — Spring AI обрезает автоматически | ❌ ПОСЛЕДСТВИЕ: без RETRIEVE_SIZE история растёт до превышения context window LLM → BadRequestException "context too long"; RETRIEVE_SIZE=10 безопасный лимит
> - [ ] conversationId — это JWT токен пользователя | ❌ ПОСЛЕДСТВИЕ: conversationId — произвольный UUID диалога; использование JWT как conversationId создаёт утечку безопасности при логировании истории

## Q11. Как тестировать Spring AI приложения?

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
> - [ ] Spring AI тесты всегда требуют реального API ключа | ❌ ПОСЛЕДСТВИЕ: нет — @MockBean ChatModel или TestChatModel позволяют тестировать без реального API; реальные вызовы = нестабильные тесты + затраты на токены
> - [x] Тестирование через @MockBean ChatModel + when().thenReturn(); или TestChatModel stub в @Profile("test") — без реальных API вызовов | ✓ ПРИМЕНЯТЬ: unit тест → @MockBean; интеграционный тест → TestChatModel; никогда не вызывай реальный OpenAI в тестах 📋 ПРАВИЛО: мок ChatModel = предсказуемые тесты без затрат токенов 🔗 См. Q2
> - [ ] TestChatModel возвращает один одинаковый ответ для всех запросов | ❌ ПОСЛЕДСТВИЕ: TestChatModel принимает Function<Prompt, ChatResponse> — логика динамическая, зависящая от содержимого запроса; один ответ — лишь частный случай
> - [ ] @MockBean ChatModel нужно инжектировать вручную в ChatService | ❌ ПОСЛЕДСТВИЕ: @MockBean регистрирует мок как Spring Bean — Spring автоматически инжектирует через DI; явная инжекция избыточна

## Q12. Какова структура DocumentETL пайплайна для индексирования?

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
> - [ ] TokenTextSplitter разбивает документ только по предложениям | ❌ ПОСЛЕДСТВИЕ: TokenTextSplitter разбивает по количеству токенов; предложение может быть разорвано; для sentence-aware splitting нужен SentenceTransformersTextSplitter
> - [ ] Overlap в TextSplitter означает количество повторяющихся документов | ❌ ПОСЛЕДСТВИЕ: overlap — количество токенов из конца предыдущего чанка, повторяющихся в начале следующего; это сохраняет контекст между чанками, не дублирует документы
> - [ ] vectorStore.add() самостоятельно генерирует embeddings без EmbeddingModel | ❌ ПОСЛЕДСТВИЕ: vectorStore.add() использует EmbeddingModel под капотом; если EmbeddingModel недоступен → исключение при индексировании; в тестах нужен мок EmbeddingModel
> - [x] DocumentETL: DocumentReader → TokenTextSplitter(512, overlap=50) → vectorStore.add(); chunk size и overlap критичны для качества RAG-ответов | ✓ ПРИМЕНЯТЬ: PDF → PagePdfDocumentReader; Markdown → MarkdownDocumentReader; chunk 256-512 токенов с 50 overlap 📋 ПРАВИЛО: большой chunk = нерелевантный контекст; маленький = потеря связи 🔗 См. Q5

## Q13. Чем Spring AI отличается от прямого использования OpenAI SDK?

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
> - [ ] Spring AI и OpenAI SDK несовместимы — нельзя использовать оба в проекте | ❌ ПОСЛЕДСТВИЕ: совместимы — Spring AI может работать поверх OpenAI SDK; при необходимости низкоуровневого доступа используют оба одновременно
> - [x] Spring AI: портируемость на 10+ провайдеров, auto-configuration, встроенные RAG/VectorStore/ChatMemory, декларативный Function Calling; OpenAI SDK: прямой доступ к специфичным функциям | ✓ ПРИМЕНЯТЬ: новый проект → Spring AI; специфика провайдера → прямой SDK 📋 ПРАВИЛО: Spring AI = портируемость + экосистема; прямой SDK = максимальный контроль 🔗 См. Q1
> - [ ] Spring AI требует больше конфигурации чем прямой OpenAI SDK | ❌ ПОСЛЕДСТВИЕ: Spring AI меньше конфигурации — auto-configuration, starters; прямой SDK требует ручной настройки retry, клиента, ChatMemory
> - [ ] Переключение провайдера в Spring AI требует рефакторинга кода | ❌ ПОСЛЕДСТВИЕ: переключение через конфигурацию — смена стартера + yaml; код приложения не меняется; это главное преимущество Spring AI над прямым SDK

## Q14. Как управлять стоимостью и токенами?

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
> - [ ] max-tokens в yaml ограничивает количество входных токенов | ❌ ПОСЛЕДСТВИЕ: max-tokens ограничивает только выходные токены (generationTokens); входные токены не ограничены; для ограничения контекста нужно усекать промпт вручную
> - [ ] temperature не влияет на качество ответа — только на стоимость | ❌ ПОСЛЕДСТВИЕ: temperature=0 для детерминированных задач (JSON, SQL), temperature=0.7 для творческих; неверная настройка → нестабильные или однообразные ответы
> - [ ] presence-penalty и frequency-penalty — одно и то же | ❌ ПОСЛЕДСТВИЕ: presence-penalty штрафует за любое повторение темы (была ли хоть раз); frequency-penalty штрафует пропорционально частоте; разные алгоритмы и эффекты на вывод
> - [x] response.getMetadata().getUsage() возвращает promptTokens/generationTokens/totalTokens; max-tokens ограничивает ВЫХОДНЫЕ токены; temperature=0 = детерминированный | ✓ ПРИМЕНЯТЬ: логировать usage для cost monitoring; max-tokens = лимит генерации, не input 📋 ПРАВИЛО: логируй totalTokens на каждый запрос → видишь аномальные всплески 🔗 См. Q2

## Q15. Как реализовать AI агент с Spring AI?

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

> [!mcq]
> - [ ] Агент в Spring AI требует явного цикла if-else для выбора функции | ❌ ПОСЛЕДСТВИЕ: нет — модель автоматически выбирает функцию на основе @Description; явный if-else превращает агента в детерминированный flowchart, теряя гибкость LLM
> - [ ] Агент может вызвать только одну функцию за запрос | ❌ ПОСЛЕДСТВИЕ: GPT-4 и Claude поддерживают parallel function calling — несколько функций одновременно; без этого агент делает N последовательных запросов вместо одного
> - [x] AI-агент: ChatClient + набор Function beans + системный промпт; модель выбирает нужные функции и может параллельно вызывать несколько; без max iterations → риск бесконечного цикла | ✓ ПРИМЕНЯТЬ: автономный агент = ChatClient + @Description functions + ReAct loop 📋 ПРАВИЛО: без max iterations в prompt → бесконечный цикл при плохом промпте 🔗 См. Q7
> - [ ] Функции агента должны быть статическими методами | ❌ ПОСЛЕДСТВИЕ: функции агента — Spring Bean с типом Function<Request, Response>; поддерживают DI, @Transactional, Spring-инфраструктуру; static методы теряют Spring context

## See also

- [LLM Basics](../../ai-ml/llm-basics-interview.md) — основы работы LLM, prompting, context window
- [RAG](../../ai-ml/rag-interview.md) — Retrieval-Augmented Generation, основной паттерн Spring AI
- [Vector Databases](../../ai-ml/vector-databases-interview.md) — PostgreSQL pgvector, Chroma, Pinecone
- [Embeddings](../../ai-ml/embeddings-interview.md) — векторные представления текста
- [AI Agents](../../ai-ml/ai-agents-interview.md) — паттерны агентов, ReAct, Chain of Thought
- [LLM Integration Patterns](../../ai-ml/llm-integration-patterns-interview.md) — интеграция LLM в приложения
- [Prompt Engineering](../../ai-ml/prompt-engineering-interview.md) — работа с промптами
- [Spring Boot](spring-boot-interview.md) — auto-configuration для Spring AI
- [Spring WebFlux](spring-webflux-interview.md) — streaming ответов через Flux
- [PostgreSQL](../../databases/postgresql-interview.md) — pgvector как VectorStore
