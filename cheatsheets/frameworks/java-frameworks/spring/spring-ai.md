---
title: "Spring AI"
description: "Кратко: Spring AI - фреймворк для интеграции искусственного интеллекта в Spring приложения. Поддержка различных AI моделей, промпты, embedding, vector stores, RAG."
tags:
  - frameworks
  - java-frameworks
  - spring-ai
difficulty: "intermediate"
prerequisites: []
next: []
updated: "2026-02-11"
---
# Spring `AI`

Кратко: **Spring** `AI` - фреймворк для интеграции искусственного интеллекта в **Spring** приложения. Поддержка различных `AI` моделей, промпты, **embedding**, **vector stores**, **RAG**.

## Полезные ссылки

### Официальная документация
- [**Spring AI** Documentation](https://docs.spring.io/spring-ai/reference/)
- [**Spring AI** GitHub](https://github.com/spring-projects/spring-ai)
- [**Spring AI** Examples](https://github.com/spring-projects/spring-ai-examples)

### **Baeldung**
- [**Spring AI** Tutorials](https://www.baeldung.com/spring-ai)

### См. также
- [[java-basics|**Java** Basics]] — основы **Java**
- [[spring-boot|**Spring Boot**]] — **Spring Boot**
- [[jackson|Jackson]] — **JSON** обработка

## Содержание

- [Введение в Spring AI](#введение-в-spring-ai)
  - [Основные возможности](#основные-возможности)
  - [Поддерживаемые провайдеры](#поддерживаемые-провайдеры)
- [Установка и настройка](#установка-и-настройка)
  - [Maven зависимости](#maven-зависимости)
  - [Конфигурация](#конфигурация)
- [application.yml](#applicationyml)
- [Chat Client](#chat-client)
  - [Базовое использование](#базовое-использование)
  - [Конфигурация Chat Client](#конфигурация-chat-client)
  - [Структурированные промпты](#структурированные-промпты)
- [Embedding Client](#embedding-client)
  - [Создание embeddings](#создание-embeddings)
  - [Поиск по similarity](#поиск-по-similarity)
- [Image Generation](#image-generation)
- [Vector Stores](#vector-stores)
  - [ChromaDB](#chromadb)
  - [Другие Vector Stores](#другие-vector-stores)
- [Retrieval-Augmented Generation (RAG)](#retrieval-augmented-generation-rag)
  - [Базовый RAG](#базовый-rag)
  - [Advanced RAG с metadata filtering](#advanced-rag-с-metadata-filtering)
- [Prompt Engineering](#prompt-engineering)
  - [Template-based промпты](#template-based-промпты)
  - [Few-shot learning](#few-shot-learning)
- [Function Calling](#function-calling)
- [Streaming Responses](#streaming-responses)
- [Observability](#observability)
  - [Metrics и tracing](#metrics-и-tracing)
  - [Логирование промптов и ответов](#логирование-промптов-и-ответов)
- [Security](#security)
  - [API Key management](#api-key-management)
  - [Input validation и sanitization](#input-validation-и-sanitization)
- [Integration Patterns](#integration-patterns)
  - [AI-powered microservices](#ai-powered-microservices)
  - [Content moderation](#content-moderation)
- [Лучшие практики](#лучшие-практики)
- [Примеры](#примеры)
  - [Полный RAG pipeline](#полный-rag-pipeline)
  - [AI-powered REST API](#ai-powered-rest-api)

## Введение в **Spring** `AI`

**Spring** `AI` - это фреймворк, который упрощает интеграцию искусственного интеллекта в **Spring** приложения. Он предоставляет унифицированный **API** для работы с различными `AI` провайдерами и моделями.

### Основные возможности

- **Унифицированный API**: Единый интерфейс для разных `AI` провайдеров
- **Поддержка чата**: Интеграция с **chat** моделями (**GPT, `Claude`, etc.**)
- **Embeddings**: Работа с векторными представлениями текста
- **Image Generation**: Создание изображений через `AI`
- **Vector Stores**: Хранение и поиск векторов
- **RAG**: **Retrieval-Augmented Generation** для улучшения ответов
- **Streaming**: Потоковая обработка ответов
- **Observability**: Мониторинг и трассировка

### Поддерживаемые провайдеры

- **OpenAI**: **GPT-3.5**, **GPT-4**, **DALL-E**
- **Azure OpenAI**: **Azure** версии **OpenAI** моделей
- **Anthropic**: **Claude** модели
- **Google**: **Gemini**, **PaLM**
- **Ollama**: Локальные модели через **Ollama**
- **Hugging Face**: Открытые модели

## Установка и настройка

### **Maven** зависимости

Ниже — **Maven**-зависимости **Spring AI** (**core**, **OpenAI**, **Chroma**, **PDF reader**) в формате **XML**.
```xml
<dependencies>
    <!-- Core Spring AI -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-core</artifactId>
        <version>1.0.0-M1</version>
    </dependency>
    
    <!-- OpenAI Chat -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
        <version>1.0.0-M1</version>
    </dependency>
    
    <!-- Vector Store (ChromaDB) -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-chroma-store-spring-boot-starter</artifactId>
        <version>1.0.0-M1</version>
    </dependency>
    
    <!-- PDF Documents -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-pdf-document-reader</artifactId>
        <version>1.0.0-M1</version>
    </dependency>
</dependencies>
```

### Конфигурация

```yaml
# application.yml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
          temperature: 0.7
      embedding:
        options:
          model: text-embedding-ada-002
    chroma:
      client:
        host: localhost
        port: 8000
```

## Chat Client

**Chat Client** позволяет взаимодействовать с `AI` моделями для текстового общения.

### Базовое использование

```java
// Сервис чата с ChatClient (Spring AI)
@Service
public class ChatService {
    
    private final ChatClient chatClient;
    
    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    public String getResponse(String message) {
        return chatClient.call(message);
    }
}
```

### Конфигурация **Chat Client**

```java
// Конфигурация Chat Client (модель, temperature, maxTokens)
@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
            .defaultOptions(ChatOptions.builder()
                .model("gpt-4")
                .temperature(0.7)
                .maxTokens(200)
                .build())
            .build();
    }
}
```

### Структурированные промпты

```java
public class ChatService {
    
    public String analyzeCode(String code) {
        String systemMessage = """
            You are a senior Java developer. Analyze the provided code and:
            1. Identify potential bugs or issues
            2. Suggest improvements for performance and readability
            3. Follow Java best practices
            Be concise but thorough in your analysis.
            """;
            
        UserMessage userMessage = new UserMessage(code);
        SystemMessage systemPrompt = new SystemMessage(systemMessage);
        
        ChatResponse response = chatClient.call(
            new Prompt(List.of(systemPrompt, userMessage))
        );
        
        return response.getResult().getOutput().getContent();
    }
}
```

## Embedding Client

**Embeddings** позволяют преобразовать текст в векторные представления для семантического поиска.

### Создание **embeddings**

```java
@Service
public class EmbeddingService {
    
    private final EmbeddingClient embeddingClient;
    
    public EmbeddingService(EmbeddingClient embeddingClient) {
        this.embeddingClient = embeddingClient;
    }
    
    public List<Double> embed(String text) {
        EmbeddingResponse response = embeddingClient.embed(
            new EmbeddingRequest(List.of(text), EmbeddingOptions.empty())
        );
        return response.getResult().getOutput();
    }
    
    public List<List<Double>> embedMultiple(List<String> texts) {
        EmbeddingResponse response = embeddingClient.embed(
            new EmbeddingRequest(texts, EmbeddingOptions.empty())
        );
        return response.getResults().stream()
            .map(EmbeddingResult::getOutput)
            .collect(Collectors.toList());
    }
}
```

### Поиск по **similarity**

```java
public class SimilaritySearchService {
    
    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;
    
    public List<Document> searchSimilar(String query, int topK) {
        List<Double> queryEmbedding = embeddingService.embed(query);
        
        return vectorStore.similaritySearch(
            SearchRequest.query(queryEmbedding).withTopK(topK)
        );
    }
}
```

## Image Generation

**Spring** `AI` поддерживает генерацию изображений через **DALL-E** и другие модели.

```java
@Service
public class ImageGenerationService {
    
    private final ImageClient imageClient;
    
    public ImageGenerationService(ImageClient imageClient) {
        this.imageClient = imageClient;
    }
    
    public String generateImage(String prompt) {
        ImagePrompt imagePrompt = new ImagePrompt(prompt);
        ImageResponse response = imageClient.call(imagePrompt);
        
        return response.getResult().getOutput().getUrl();
    }
    
    public String generateImageWithOptions(String prompt) {
        ImageOptions options = ImageOptions.builder()
            .model("dall-e-3")
            .quality("hd")
            .size("1024x1024")
            .style("vivid")
            .build();
            
        ImagePrompt imagePrompt = new ImagePrompt(prompt, options);
        ImageResponse response = imageClient.call(imagePrompt);
        
        return response.getResult().getOutput().getUrl();
    }
}
```

## Vector Stores

**Vector Stores** позволяют хранить и искать векторные представления документов.

### **ChromaDB**

```yaml
# application.yml
spring:
  ai:
    chroma:
      client:
        host: localhost
        port: 8000
```

```java
@Service
public class DocumentService {
    
    private final VectorStore vectorStore;
    private final EmbeddingClient embeddingClient;
    
    public DocumentService(VectorStore vectorStore, EmbeddingClient embeddingClient) {
        this.vectorStore = vectorStore;
        this.embeddingClient = embeddingClient;
    }
    
    public void addDocument(String content, Map<String, Object> metadata) {
        Document document = new Document(content, metadata);
        vectorStore.add(List.of(document));
    }
    
    public List<Document> searchDocuments(String query, int topK) {
        List<Double> queryEmbedding = embeddingClient.embed(query);
        
        return vectorStore.similaritySearch(
            SearchRequest.query(queryEmbedding).withTopK(topK)
        );
    }
}
```

### Другие **Vector Stores**

**Spring** `AI` поддерживает:**
- **ChromaDB**: **Open-source vector database**
- **PgVector**: **PostgreSQL** с расширением **pgvector**
- **Redis**: **Redis** как **vector store**
- **Weaviate**: Нативный **vector search engine**
- **Pinecone**: **Managed vector database**
- **Milvus**: **Distributed vector database**

## Retrieval-Augmented Generation (RAG)

**RAG** комбинирует **retrieval** (**поиск**) с **generation** (**генерацией**) для более точных ответов.

### Базовый **RAG**

```java
@Service
public class RagService {
    
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final EmbeddingClient embeddingClient;
    
    public RagService(ChatClient chatClient, VectorStore vectorStore, EmbeddingClient embeddingClient) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.embeddingClient = embeddingClient;
    }
    
    public String answerQuestion(String question) {
        // 1. Найти релевантные документы
        List<Document> relevantDocs = vectorStore.similaritySearch(
            SearchRequest.query(embeddingClient.embed(question)).withTopK(3)
        );
        
        // 2. Создать контекст из найденных документов
        String context = relevantDocs.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n\n"));
            
        // 3. Сформировать промпт с контекстом
        String prompt = String.format("""
            Based on the following context, answer the question:
            
            Context:
            %s
            
            Question: %s
            
            Answer:
            """, context, question);
            
        // 4. Получить ответ от AI
        return chatClient.call(prompt);
    }
}
```

### **Advanced RAG** с **metadata filtering**

```java
public List<Document> searchWithFilter(String query, String category) {
    List<Double> queryEmbedding = embeddingClient.embed(query);
    
    return vectorStore.similaritySearch(
        SearchRequest.query(queryEmbedding)
            .withTopK(5)
            .withFilterExpression("category == '" + category + "'")
    );
}
```

## Prompt Engineering

### **Template-based** промпты

```java
@Configuration
public class PromptConfig {
    
    @Bean
    public PromptTemplate codeReviewTemplate() {
        return new PromptTemplate("""
            You are a senior software engineer conducting a code review.
            
            Code to review:
            {code}
            
            Programming language: {language}
            
            Please provide:
            1. Code quality assessment
            2. Potential bugs or issues
            3. Performance considerations
            4. Security concerns
            5. Suggestions for improvement
            
            Be specific and provide examples where applicable.
            """);
    }
}
```

```java
@Service
public class CodeReviewService {
    
    private final ChatClient chatClient;
    private final PromptTemplate codeReviewTemplate;
    
    public CodeReviewService(ChatClient chatClient, PromptTemplate codeReviewTemplate) {
        this.chatClient = codeReviewTemplate;
        this.codeReviewTemplate = codeReviewTemplate;
    }
    
    public String reviewCode(String code, String language) {
        Prompt prompt = codeReviewTemplate.create(Map.of(
            "code", code,
            "language", language
        ));
        
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }
}
```

### **Few-shot learning**

```java
public String classifySentiment(String text) {
    String prompt = """
        Classify the sentiment of the following text as positive, negative, or neutral.
        
        Examples:
        Text: "I love this product!"
        Sentiment: positive
        
        Text: "This is terrible."
        Sentiment: negative
        
        Text: "It's okay."
        Sentiment: neutral
        
        Text: "{text}"
        Sentiment:
        """.replace("{text}", text);
        
    return chatClient.call(prompt);
}
```

## Function Calling

**Function calling** позволяет `AI` вызывать функции в вашем приложении.

```java
public class WeatherFunction implements FunctionCallback {
    
    @Override
    public String getName() {
        return "get_weather";
    }
    
    @Override
    public String getDescription() {
        return "Get current weather for a city";
    }
    
    @Override
    public String getInputTypeSchema() {
        return """
            {
                "type": "object",
                "properties": {
                    "city": {
                        "type": "string",
                        "description": "The city name"
                    }
                },
                "required": ["city"]
            }
            """;
    }
    
    @Override
    public String call(String inputJson) {
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(inputJson);
            String city = jsonNode.get("city").asText();
            
            // Получить погоду для города
            return getWeatherForCity(city);
        } catch (Exception e) {
            return "Error getting weather: " + e.getMessage();
        }
    }
}
```

```java
@Configuration
public class AiConfig {
    
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
            .defaultFunctions("get_weather")
            .build();
    }
    
    @Bean
    public FunctionCallback weatherFunction() {
        return new WeatherFunction();
    }
}
```

```java
@Service
public class AssistantService {
    
    private final ChatClient chatClient;
    
    public AssistantService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    public String askAssistant(String question) {
        // AI может автоматически вызвать функцию get_weather
        return chatClient.call(question);
    }
}
```

## Streaming Responses

**Streaming** позволяет получать ответы по частям, улучшая пользовательский опыт.

```java
@Service
public class StreamingChatService {
    
    private final StreamingChatClient chatClient;
    
    public StreamingChatService(StreamingChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    public Flux<String> streamResponse(String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        
        return chatClient.stream(prompt)
            .map(chatResponse -> chatResponse.getResult().getOutput().getContent());
    }
}
```

```java
@RestController
public class ChatController {
    
    private final StreamingChatService streamingChatService;
    
    public ChatController(StreamingChatService streamingChatService) {
        this.streamingChatService = streamingChatService;
    }
    
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String message) {
        return streamingChatService.streamResponse(message);
    }
}
```

## Observability

### **Metrics** и **tracing**

```java
@Configuration
public class ObservabilityConfig {
    
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, MeterRegistry meterRegistry) {
        return builder
            .defaultOptions(ChatOptions.builder()
                .model("gpt-4")
                .build())
            .build();
    }
}
```

### Логирование промптов и ответов

```java
@Component
public class AiLoggingAspect {
    
    private final Logger logger = LoggerFactory.getLogger(AiLoggingAspect.class);
    
    @Around("execution(* org.springframework.ai.chat.ChatClient.call(..))")
    public Object logChatInteraction(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            
            long duration = System.currentTimeMillis() - startTime;
            logger.info("AI Chat completed in {}ms", duration);
            
            return result;
        } catch (Exception e) {
            logger.error("AI Chat failed", e);
            throw e;
        }
    }
}
```

## Security

### **API Key management**

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
            .defaultOptions(ChatOptions.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .build())
            .build();
    }
}
```

### **Input validation** и **sanitization**

```java
@Service
public class SecureChatService {
    
    private final ChatClient chatClient;
    
    public SecureChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    public String safeChat(String userInput) {
        // Валидация и sanitization
        String sanitizedInput = sanitizeInput(userInput);
        
        // Проверка на вредоносный контент
        if (containsHarmfulContent(sanitizedInput)) {
            throw new SecurityException("Harmful content detected");
        }
        
        // Добавление safety instructions
        String safePrompt = """
            You are a helpful AI assistant. Always provide safe, appropriate responses.
            Never assist with harmful, illegal, or unethical requests.
            
            User request: %s
            """.formatted(sanitizedInput);
            
        return chatClient.call(safePrompt);
    }
    
    private String sanitizeInput(String input) {
        // Удаление потенциально опасных символов
        return input.replaceAll("[<>\"']", "");
    }
    
    private boolean containsHarmfulContent(String input) {
        // Проверка на вредоносные паттерны
        return false; // Реализовать логику проверки
    }
}
```

## Integration Patterns

### **AI-powered microservices**

```java
@Service
@Profile("ai-enabled")
public class AiEnhancedUserService extends UserService {
    
    private final ChatClient chatClient;
    
    public AiEnhancedUserService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    @Override
    public User createUser(CreateUserRequest request) {
        // Базовая валидация
        User user = super.createUser(request);
        
        // AI-powered enhancements
        String profileSummary = chatClient.call(
            "Create a brief professional summary for: " + request.getBio()
        );
        
        user.setProfileSummary(profileSummary);
        return userRepository.save(user);
    }
}
```

### **Content moderation**

```java
@Service
public class ContentModerationService {
    
    private final ChatClient chatClient;
    
    public ContentModerationService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }
    
    public ModerationResult moderateContent(String content) {
        String prompt = """
            Analyze the following content for:
            1. Inappropriate language
            2. Hate speech
            3. Spam content
            4. Harmful content
            
            Content: %s
            
            Return JSON with fields: safe (boolean), categories (array), confidence (0-1)
            """.formatted(content);
            
        String response = chatClient.call(prompt);
        
        // Парсинг JSON ответа
        return parseModerationResult(response);
    }
}
```

## Лучшие практики

1. **Используйте подходящие модели**: Выбирайте модель по задаче и стоимости
2. **Кэшируйте embeddings**: Избегайте повторных вычислений
3. **Валидируйте входные данные**: Проверяйте промпты перед отправкой
4. **Обрабатывайте ошибки**: `AI` может давать неожиданные ответы
5. **Мониторьте использование**: Следите за затратами и производительностью
6. **Тестируйте `AI` интеграции**: **Unit** и **integration** тесты
7. **Используйте streaming**: Для лучшего `UX` с длинными ответами
8. **Кэшируйте ответы**: Для часто задаваемых вопросов
9. **Обеспечивайте fallback**: Когда `AI` недоступен
10. **Следите за обновлениями**: `AI` модели постоянно улучшаются

## Примеры

### Полный **RAG pipeline**

```java
@Configuration
@EnableScheduling
public class RagPipelineConfig {
    
    @Bean
    public CommandLineRunner initDocuments(
            VectorStore vectorStore, 
            DocumentReader documentReader) {
        
        return args -> {
            // Загрузка документов
            List<Document> documents = documentReader.read(
                new FileSystemResource("classpath:documents/")
            );
            
            // Разбиение на chunks
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> chunks = textSplitter.split(documents);
            
            // Добавление в vector store
            vectorStore.add(chunks);
        };
    }
}

@Service
public class IntelligentAssistant {
    
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final EmbeddingClient embeddingClient;
    
    public IntelligentAssistant(ChatClient chatClient, 
                              VectorStore vectorStore,
                              EmbeddingClient embeddingClient) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.embeddingClient = embeddingClient;
    }
    
    public String answerQuestion(String question, String userId) {
        // 1. Embed question
        List<Double> questionEmbedding = embeddingClient.embed(question);
        
        // 2. Find relevant documents
        List<Document> relevantDocs = vectorStore.similaritySearch(
            SearchRequest.query(questionEmbedding)
                .withTopK(5)
                .withFilterExpression("userId == '" + userId + "'")
        );
        
        // 3. Build context
        String context = relevantDocs.stream()
            .map(doc -> doc.getContent())
            .collect(Collectors.joining("\n\n"));
            
        // 4. Create prompt with context
        String prompt = String.format("""
            You are a helpful assistant. Use the following context to answer the question.
            If the context doesn't contain enough information, say so.
            
            Context:
            %s
            
            Question: %s
            
            Answer:
            """, context, question);
            
        // 5. Get AI response
        ChatResponse response = chatClient.call(new Prompt(prompt));
        
        return response.getResult().getOutput().getContent();
    }
}
```

### **AI-powered REST API**

```java
@RestController
@RequestMapping("/api/assistant")
public class AssistantController {
    
    private final ChatClient chatClient;
    private final StreamingChatClient streamingChatClient;
    private final ImageClient imageClient;
    
    public AssistantController(ChatClient chatClient,
                             StreamingChatClient streamingChatClient,
                             ImageClient imageClient) {
        this.chatClient = chatClient;
        this.streamingChatClient = streamingChatClient;
        this.imageClient = imageClient;
    }
    
    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        try {
            String response = chatClient.call(request.getMessage());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Sorry, I'm having trouble responding right now.");
        }
    }
    
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestParam String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        
        return streamingChatClient.stream(prompt)
            .map(chatResponse -> chatResponse.getResult().getOutput().getContent());
    }
    
    @PostMapping("/image")
    public ResponseEntity<String> generateImage(@RequestBody ImageRequest request) {
        try {
            ImagePrompt prompt = new ImagePrompt(request.getPrompt());
            ImageResponse response = imageClient.call(prompt);
            
            return ResponseEntity.ok(response.getResult().getOutput().getUrl());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Failed to generate image.");
        }
    }
    
    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResult> analyzeCode(@RequestBody CodeAnalysisRequest request) {
        String prompt = String.format("""
            Analyze the following %s code for:
            1. Code quality and best practices
            2. Potential bugs or issues
            3. Performance optimizations
            4. Security concerns
            
            Code:
            %s
            
            Provide a JSON response with fields: quality, issues, suggestions, security
            """, request.getLanguage(), request.getCode());
            
        String analysisJson = chatClient.call(prompt);
        
        // Parse JSON response
        AnalysisResult result = parseAnalysisResult(analysisJson);
        return ResponseEntity.ok(result);
    }
}

@Data
class ChatRequest {
    private String message;
}

@Data
class ImageRequest {
    private String prompt;
}

@Data
class CodeAnalysisRequest {
    private String code;
    private String language;
}

@Data
class AnalysisResult {
    private String quality;
    private List<String> issues;
    private List<String> suggestions;
    private String security;
}
```

Этот файл содержит детальное описание **Spring** `AI`: от основных концепций до полной интеграции, включая **chat**, **embeddings**, **RAG**, **function calling**, **streaming** и **best practices**. Он охватывает все ключевые аспекты работы с `AI` в **Spring** приложениях.
