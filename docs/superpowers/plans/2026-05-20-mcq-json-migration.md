# MCQ JSON Migration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Перенести хранение MCQ из inline-callout в `.md` в структурированные JSON-сидеры в `modules/quiz-app/src/main/resources/seed/mcq/`. Markdown-файлы остаются только для теории. Парсер чище, валидация строже, presentation отделён от data.

**Architecture:** `.md` зеркально парится `.json` (один сидер на тему). При старте `QuestionImportService` импортирует `Question`'ы из markdown, затем `McqJsonLoader` читает соответствующий JSON, валидирует против JSON Schema, заполняет `answer_options`. Парсер `MarkdownQuestionParser` теряет всю MCQ-логику. Pre-commit hook валидирует JSON по schema и запрещает `> [!mcq]` в `.md`.

**Tech Stack:** Java 17 / Spring Boot 3, Jackson, `com.networknt:json-schema-validator` (draft-07), SQLite (dev) + PostgreSQL (prod), JUnit 5, AssertJ, awk (миграционные скрипты).

**Reference spec:** `docs/superpowers/specs/2026-05-20-mcq-v3-design.md`

---

## Section A: Foundation — Schema, dependency, DTOs

### Task 1: Add JSON Schema validator dependency

**Files:**
- Modify: `modules/quiz-app/build.gradle.kts`
- Modify: `gradle/libs.versions.toml`

- [ ] **Step 1: Add version + alias in version catalog**

Edit `gradle/libs.versions.toml`. Add under `[versions]`:

```toml
json-schema-validator = "1.4.0"
```

Under `[libraries]`:

```toml
json-schema-validator = { module = "com.networknt:json-schema-validator", version.ref = "json-schema-validator" }
```

- [ ] **Step 2: Reference in build.gradle.kts**

Edit `modules/quiz-app/build.gradle.kts`. Add inside `dependencies { }` (after `implementation(libs.jackson.datatype.jsr310)`):

```kotlin
implementation(libs.json.schema.validator)
```

- [ ] **Step 3: Verify gradle build**

```bash
./gradlew :quiz-app:compileJava --refresh-dependencies
```

Expected: `BUILD SUCCESSFUL`, no missing-class errors.

- [ ] **Step 4: Commit**

```bash
git add gradle/libs.versions.toml modules/quiz-app/build.gradle.kts
git commit -m "build: add networknt json-schema-validator dependency"
```

---

### Task 2: Create JSON Schema for MCQ seeds

**Files:**
- Create: `modules/quiz-app/src/main/resources/seed/mcq-schema.json`

- [ ] **Step 1: Write schema file**

Content of `modules/quiz-app/src/main/resources/seed/mcq-schema.json`:

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "$id": "https://cheat-sheet.local/seed/mcq-schema.json",
  "type": "object",
  "required": ["topic_slug", "questions"],
  "additionalProperties": false,
  "properties": {
    "topic_slug": { "type": "string", "pattern": "^[a-z][a-z0-9-]*$" },
    "questions": {
      "type": "array",
      "items": {
        "type": "object",
        "required": ["q_number", "blocks"],
        "additionalProperties": false,
        "properties": {
          "q_number": { "type": "integer", "minimum": 1 },
          "blocks": {
            "type": "array",
            "minItems": 1,
            "items": {
              "type": "object",
              "required": ["block_idx", "question_text", "options"],
              "additionalProperties": false,
              "properties": {
                "block_idx": { "type": "integer", "minimum": 0 },
                "question_text": { "type": "string", "minLength": 1 },
                "options": {
                  "type": "array",
                  "minItems": 4,
                  "maxItems": 4,
                  "items": {
                    "oneOf": [
                      {
                        "type": "object",
                        "required": ["order", "label", "text", "correct", "sections"],
                        "additionalProperties": false,
                        "properties": {
                          "order": { "type": "integer", "minimum": 0, "maximum": 3 },
                          "label": { "enum": ["A", "B", "C", "D"] },
                          "text": { "type": "string", "minLength": 1 },
                          "correct": { "const": true },
                          "sections": {
                            "type": "object",
                            "required": ["explanation", "example", "when_to_apply", "edge_cases", "related"],
                            "additionalProperties": false,
                            "properties": {
                              "explanation": { "type": "string", "minLength": 1 },
                              "example": { "type": "string", "minLength": 1 },
                              "when_to_apply": { "type": "string", "minLength": 1 },
                              "edge_cases": { "type": "string", "minLength": 1 },
                              "related": { "type": "string", "minLength": 1 }
                            }
                          }
                        }
                      },
                      {
                        "type": "object",
                        "required": ["order", "label", "text", "correct", "sections"],
                        "additionalProperties": false,
                        "properties": {
                          "order": { "type": "integer", "minimum": 0, "maximum": 3 },
                          "label": { "enum": ["A", "B", "C", "D"] },
                          "text": { "type": "string", "minLength": 1 },
                          "correct": { "const": false },
                          "sections": {
                            "type": "object",
                            "required": ["what_actually", "source_of_confusion", "if_it_were_true", "how_it_should_be"],
                            "additionalProperties": false,
                            "properties": {
                              "what_actually": { "type": "string", "minLength": 1 },
                              "source_of_confusion": { "type": "string", "minLength": 1 },
                              "if_it_were_true": { "type": "string", "minLength": 1 },
                              "how_it_should_be": { "type": "string", "minLength": 1 }
                            }
                          }
                        }
                      }
                    ]
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
```

- [ ] **Step 2: Commit**

```bash
git add modules/quiz-app/src/main/resources/seed/mcq-schema.json
git commit -m "feat(mcq): add json-schema for mcq seed files"
```

---

### Task 3: Create DTO records

**Files:**
- Create: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/dto/McqSeed.java`
- Create: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/dto/McqSeedQuestion.java`
- Create: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/dto/McqSeedBlock.java`
- Create: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/dto/McqSeedOption.java`

- [ ] **Step 1: McqSeed.java**

```java
package com.cheatsheet.quiz.service.imports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record McqSeed(
        @JsonProperty("topic_slug") String topicSlug,
        List<McqSeedQuestion> questions
) {}
```

- [ ] **Step 2: McqSeedQuestion.java**

```java
package com.cheatsheet.quiz.service.imports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record McqSeedQuestion(
        @JsonProperty("q_number") int qNumber,
        List<McqSeedBlock> blocks
) {}
```

- [ ] **Step 3: McqSeedBlock.java**

```java
package com.cheatsheet.quiz.service.imports.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record McqSeedBlock(
        @JsonProperty("block_idx") int blockIdx,
        @JsonProperty("question_text") String questionText,
        List<McqSeedOption> options
) {}
```

- [ ] **Step 4: McqSeedOption.java**

```java
package com.cheatsheet.quiz.service.imports.dto;

import java.util.Map;

public record McqSeedOption(
        int order,
        String label,
        String text,
        boolean correct,
        Map<String, String> sections
) {}
```

- [ ] **Step 5: Build to verify**

```bash
./gradlew :quiz-app:compileJava
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 6: Commit**

```bash
git add modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/dto/
git commit -m "feat(mcq): add dto records for json mcq seed"
```

---

### Task 4: Create InvalidMcqSeedException + McqLoadResult

**Files:**
- Create: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/InvalidMcqSeedException.java`
- Create: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/McqLoadResult.java`

- [ ] **Step 1: InvalidMcqSeedException.java**

```java
package com.cheatsheet.quiz.service.imports;

import com.networknt.schema.ValidationMessage;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class InvalidMcqSeedException extends RuntimeException {
    public InvalidMcqSeedException(Path path, Set<ValidationMessage> errors) {
        super("Invalid MCQ seed " + path + ": " + errors.stream()
                .map(ValidationMessage::getMessage)
                .collect(Collectors.joining("; ")));
    }

    public InvalidMcqSeedException(Path path, String message) {
        super("Invalid MCQ seed " + path + ": " + message);
    }
}
```

- [ ] **Step 2: McqLoadResult.java**

```java
package com.cheatsheet.quiz.service.imports;

public record McqLoadResult(
        boolean found,
        int optionsInserted,
        int questionsSkipped
) {
    public static McqLoadResult notFound() {
        return new McqLoadResult(false, 0, 0);
    }

    public static McqLoadResult ok(int optionsInserted, int questionsSkipped) {
        return new McqLoadResult(true, optionsInserted, questionsSkipped);
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/InvalidMcqSeedException.java \
        modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/McqLoadResult.java
git commit -m "feat(mcq): add exception + result types for json mcq loader"
```

---

## Section B: McqJsonLoader (TDD)

### Task 5: Add `findIdByTopicAndQuestionNumber` to QuestionRepository

**Files:**
- Modify: `modules/quiz-persistence/src/main/java/com/cheatsheet/quiz/persistence/QuestionRepository.java`

Reason: `McqJsonLoader` needs to resolve `(topic_slug, q_number)` → `question_id`. `questions` table has `slug` (UNIQUE) and `topic` (TEXT). Convention: existing slug is generated as `<topic>-q<number>` (verify by checking `QuestionImportService.upsert*` if unsure).

- [ ] **Step 1: Add lookup method**

Insert into `QuestionRepository.java` after the `findBySlug` method (around line 70):

```java
/**
 * Возвращает id вопроса по топику и порядковому номеру (как в `## Q<N>` заголовке).
 * Конвенция slug-а: `<topic>-q<number>`.
 */
public Optional<Long> findIdByTopicAndQuestionNumber(String topic, int qNumber) {
    String slug = topic + "-q" + qNumber;
    return jdbcTemplate.query(
            "SELECT id FROM questions WHERE slug = ?",
            (rs, rowNum) -> rs.getLong("id"),
            slug
    ).stream().findFirst();
}
```

- [ ] **Step 2: Confirm slug-pattern matches existing data**

Run a quick check:

```bash
./gradlew :quiz-app:bootRun --args='--app.preload.startup-preload=false' &
sleep 30
sqlite3 ~/.cheat-sheet/quiz.db "SELECT slug FROM questions LIMIT 5;"
kill %1
```

Expected: slugs like `aws-interview-q1`, `aws-interview-q2`, etc. If pattern differs, adjust the `slug` construction in step 1 accordingly.

- [ ] **Step 3: Commit**

```bash
git add modules/quiz-persistence/src/main/java/com/cheatsheet/quiz/persistence/QuestionRepository.java
git commit -m "feat(repo): add findIdByTopicAndQuestionNumber for mcq json loader"
```

---

### Task 6: Write McqJsonLoaderTest first (TDD)

**Files:**
- Create: `modules/quiz-app/src/test/java/com/cheatsheet/quiz/service/imports/McqJsonLoaderTest.java`
- Test resources: `modules/quiz-app/src/test/resources/seed/mcq/test/sample-interview.json`

- [ ] **Step 1: Create test JSON fixture**

Create `modules/quiz-app/src/test/resources/seed/mcq/test/sample-interview.json`:

```json
{
  "topic_slug": "sample-interview",
  "questions": [
    {
      "q_number": 1,
      "blocks": [
        {
          "block_idx": 0,
          "question_text": "Test question?",
          "options": [
            {
              "order": 0,
              "label": "A",
              "text": "Correct option",
              "correct": true,
              "sections": {
                "explanation": "Why correct",
                "example": "Example here",
                "when_to_apply": "When to apply",
                "edge_cases": "Edge cases",
                "related": "[[sample-interview#Q2]] note"
              }
            },
            {
              "order": 1,
              "label": "B",
              "text": "Wrong option 1",
              "correct": false,
              "sections": {
                "what_actually": "Reality",
                "source_of_confusion": "Confusion",
                "if_it_were_true": "Symptom",
                "how_it_should_be": "Bridge"
              }
            },
            {
              "order": 2,
              "label": "C",
              "text": "Wrong option 2",
              "correct": false,
              "sections": {
                "what_actually": "Reality 2",
                "source_of_confusion": "Confusion 2",
                "if_it_were_true": "Symptom 2",
                "how_it_should_be": "Bridge 2"
              }
            },
            {
              "order": 3,
              "label": "D",
              "text": "Wrong option 3",
              "correct": false,
              "sections": {
                "what_actually": "Reality 3",
                "source_of_confusion": "Confusion 3",
                "if_it_were_true": "Symptom 3",
                "how_it_should_be": "Bridge 3"
              }
            }
          ]
        }
      ]
    }
  ]
}
```

- [ ] **Step 2: Write test class**

Create `modules/quiz-app/src/test/java/com/cheatsheet/quiz/service/imports/McqJsonLoaderTest.java`:

```java
package com.cheatsheet.quiz.service.imports;

import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class McqJsonLoaderTest {

    private QuestionRepository questionRepository;
    private AnswerOptionRepository answerOptionRepository;
    private McqJsonLoader loader;

    @BeforeEach
    void setUp() {
        questionRepository = mock(QuestionRepository.class);
        answerOptionRepository = mock(AnswerOptionRepository.class);
        loader = new McqJsonLoader(new ObjectMapper(), questionRepository, answerOptionRepository);
    }

    @Test
    void loadsValidJsonAndInsertsOptions() {
        when(questionRepository.findIdByTopicAndQuestionNumber("sample-interview", 1))
                .thenReturn(Optional.of(42L));

        McqLoadResult result = loader.loadForTopic("test", "sample-interview");

        assertThat(result.found()).isTrue();
        assertThat(result.optionsInserted()).isEqualTo(4);
        assertThat(result.questionsSkipped()).isEqualTo(0);

        ArgumentCaptor<List<AnswerOptionCreate>> captor = ArgumentCaptor.forClass(List.class);
        verify(answerOptionRepository).deleteByQuestionId(42L);
        verify(answerOptionRepository).insertAll(eq(42L), captor.capture());
        List<AnswerOptionCreate> creates = captor.getValue();
        assertThat(creates).hasSize(4);
        assertThat(creates.get(0).correct()).isTrue();
        assertThat(creates.get(0).optionText()).isEqualTo("Correct option");
        assertThat(creates.get(0).explanation()).contains("**Развёрнутое объяснение.**", "Why correct");
        assertThat(creates.get(1).correct()).isFalse();
        assertThat(creates.get(1).explanation()).contains("**Что на самом деле.**", "Reality");
    }

    @Test
    void noJsonFileForTopicReturnsNotFound() {
        McqLoadResult result = loader.loadForTopic("test", "nonexistent");
        assertThat(result.found()).isFalse();
        verify(answerOptionRepository, times(0)).insertAll(anyLong(), any());
    }

    @Test
    void missingQuestionInDbIncrementsSkipped() {
        when(questionRepository.findIdByTopicAndQuestionNumber("sample-interview", 1))
                .thenReturn(Optional.empty());

        McqLoadResult result = loader.loadForTopic("test", "sample-interview");

        assertThat(result.found()).isTrue();
        assertThat(result.questionsSkipped()).isEqualTo(1);
        assertThat(result.optionsInserted()).isEqualTo(0);
        verify(answerOptionRepository, times(0)).insertAll(anyLong(), any());
    }

    @Test
    void topicSlugMismatchThrows() {
        assertThatThrownBy(() -> loader.loadForTopic("test", "wrong-slug"))
                .isInstanceOf(InvalidMcqSeedException.class)
                .hasMessageContaining("topic_slug mismatch");
    }
}
```

Also need a test fixture with mismatched topic_slug — create `modules/quiz-app/src/test/resources/seed/mcq/test/wrong-slug.json` containing `"topic_slug": "completely-different"` and one valid `questions` block.

- [ ] **Step 3: Run test to verify it fails (class doesn't exist yet)**

```bash
./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.service.imports.McqJsonLoaderTest"
```

Expected: COMPILATION ERROR — `McqJsonLoader` class not found.

---

### Task 7: Implement McqJsonLoader

**Files:**
- Create: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/McqJsonLoader.java`

- [ ] **Step 1: Implement loader**

```java
package com.cheatsheet.quiz.service.imports;

import com.cheatsheet.quiz.domain.OptionSource;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository;
import com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.imports.dto.McqSeed;
import com.cheatsheet.quiz.service.imports.dto.McqSeedBlock;
import com.cheatsheet.quiz.service.imports.dto.McqSeedOption;
import com.cheatsheet.quiz.service.imports.dto.McqSeedQuestion;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
public class McqJsonLoader {

    private static final Map<String, String> CORRECT_SECTION_LABELS = Map.of(
            "explanation", "Развёрнутое объяснение",
            "example", "Пример",
            "when_to_apply", "Когда применять",
            "edge_cases", "Подводные камни",
            "related", "Связанные вопросы"
    );

    private static final Map<String, String> WRONG_SECTION_LABELS = Map.of(
            "what_actually", "Что на самом деле",
            "source_of_confusion", "Откуда путаница",
            "if_it_were_true", "Если бы это было правдой",
            "how_it_should_be", "Как было бы правильно"
    );

    private static final List<String> CORRECT_ORDER = List.of(
            "explanation", "example", "when_to_apply", "edge_cases", "related"
    );

    private static final List<String> WRONG_ORDER = List.of(
            "what_actually", "source_of_confusion", "if_it_were_true", "how_it_should_be"
    );

    private final ObjectMapper objectMapper;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final JsonSchema schema;

    public McqJsonLoader(ObjectMapper objectMapper,
                        QuestionRepository questionRepository,
                        AnswerOptionRepository answerOptionRepository) {
        this.objectMapper = objectMapper;
        this.questionRepository = questionRepository;
        this.answerOptionRepository = answerOptionRepository;
        this.schema = loadSchema();
    }

    private JsonSchema loadSchema() {
        try (InputStream in = new ClassPathResource("seed/mcq-schema.json").getInputStream()) {
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            return factory.getSchema(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load mcq-schema.json", e);
        }
    }

    public McqLoadResult loadForTopic(String categoryPath, String topicSlug) {
        Path resourcePath = Paths.get("seed/mcq", categoryPath, topicSlug + ".json");
        Resource resource = new ClassPathResource(resourcePath.toString());
        if (!resource.exists()) {
            return McqLoadResult.notFound();
        }
        try (InputStream in = resource.getInputStream()) {
            JsonNode tree = objectMapper.readTree(in);
            Set<ValidationMessage> errors = schema.validate(tree);
            if (!errors.isEmpty()) {
                throw new InvalidMcqSeedException(resourcePath, errors);
            }
            McqSeed seed = objectMapper.treeToValue(tree, McqSeed.class);
            if (!seed.topicSlug().equals(topicSlug)) {
                throw new InvalidMcqSeedException(resourcePath,
                        "topic_slug mismatch: expected " + topicSlug + " got " + seed.topicSlug());
            }
            return upsertOptions(seed);
        } catch (IOException e) {
            throw new InvalidMcqSeedException(resourcePath, e.getMessage());
        }
    }

    private McqLoadResult upsertOptions(McqSeed seed) {
        int totalInserted = 0;
        int skipped = 0;
        for (McqSeedQuestion question : seed.questions()) {
            Optional<Long> questionId = questionRepository.findIdByTopicAndQuestionNumber(
                    seed.topicSlug(), question.qNumber());
            if (questionId.isEmpty()) {
                log.warn("No question found for topic={}, q_number={}", seed.topicSlug(), question.qNumber());
                skipped++;
                continue;
            }
            long qId = questionId.get();
            answerOptionRepository.deleteByQuestionId(qId);
            for (McqSeedBlock block : question.blocks()) {
                List<AnswerOptionCreate> creates = new ArrayList<>(block.options().size());
                for (McqSeedOption opt : block.options()) {
                    creates.add(new AnswerOptionCreate(
                            opt.label() + ". " + opt.text(),
                            opt.correct(),
                            opt.order(),
                            OptionSource.MARKDOWN.name(),
                            renderExplanation(opt),
                            1, 2, block.blockIdx()));
                }
                answerOptionRepository.insertAll(qId, creates);
                totalInserted += creates.size();
            }
        }
        return McqLoadResult.ok(totalInserted, skipped);
    }

    private String renderExplanation(McqSeedOption opt) {
        Map<String, String> labels = opt.correct() ? CORRECT_SECTION_LABELS : WRONG_SECTION_LABELS;
        List<String> order = opt.correct() ? CORRECT_ORDER : WRONG_ORDER;
        StringBuilder sb = new StringBuilder();
        for (String key : order) {
            String content = opt.sections().get(key);
            if (content == null) continue;
            if (sb.length() > 0) sb.append("\n\n");
            sb.append("**").append(labels.get(key)).append(".** ").append(content);
        }
        return sb.toString();
    }
}
```

- [ ] **Step 2: Run test — should pass**

```bash
./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.service.imports.McqJsonLoaderTest"
```

Expected: PASS for all 4 tests.

- [ ] **Step 3: Commit**

```bash
git add modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/McqJsonLoader.java \
        modules/quiz-app/src/test/java/com/cheatsheet/quiz/service/imports/McqJsonLoaderTest.java \
        modules/quiz-app/src/test/resources/seed/mcq/test/
git commit -m "feat(mcq): implement McqJsonLoader with schema validation"
```

---

## Section C: Integration into QuestionImportService

### Task 8: Wire McqJsonLoader into QuestionImportService

**Files:**
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/QuestionImportService.java`

- [ ] **Step 1: Inject loader**

In `QuestionImportService.java` near the constructor field declarations (around line 50), add:

```java
private final McqJsonLoader mcqJsonLoader;
```

(The field will be auto-injected by Lombok `@RequiredArgsConstructor`.)

- [ ] **Step 2: Replace `upsertOptionsFromMd` call with JSON loader**

Find the call to `upsertOptionsFromMd(questionId, options)` in `QuestionImportService` (the only place using `ParsedQuestion.options()`). Replace with:

```java
// after question upsert + before next question:
String categoryPath = relativePath.getParent() == null ? "" : relativePath.getParent().toString();
String topicSlug = relativePath.getFileName().toString().replaceFirst("\\.md$", "");
McqLoadResult result = mcqJsonLoader.loadForTopic(categoryPath, topicSlug);
if (result.found()) {
    log.info("Loaded {} options for topic {} ({} questions skipped)",
            result.optionsInserted(), topicSlug, result.questionsSkipped());
}
```

**Note:** `loadForTopic` is called ONCE PER FILE (not per Q), because JSON contains all Q's for the topic. Place this call after the loop that imports all Q's for one file, not inside per-Q logic.

- [ ] **Step 3: Remove now-obsolete `upsertOptionsFromMd` private method**

Delete lines 289-299 in `QuestionImportService.java` (the entire `private void upsertOptionsFromMd(...)` method).

- [ ] **Step 4: Update import statements**

Remove `import com.cheatsheet.quiz.domain.OptionSource;` if no longer used.

- [ ] **Step 5: Build + run existing import tests**

```bash
./gradlew :quiz-app:test --tests "*ImportService*" --tests "*Import*"
```

Expected: PASS. If tests reference `options()` from `ParsedQuestion`, they may fail — these will be cleaned up in Task 9.

- [ ] **Step 6: Commit**

```bash
git add modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/QuestionImportService.java
git commit -m "feat(mcq): wire McqJsonLoader into QuestionImportService"
```

---

## Section D: Remove MCQ from MarkdownQuestionParser

### Task 9: Strip MCQ logic from parser + clean tests

**Files:**
- Modify: `modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/MarkdownQuestionParser.java`
- Modify: `modules/quiz-app/src/test/java/com/cheatsheet/quiz/service/imports/MarkdownQuestionParserTest.java`

- [ ] **Step 1: Remove MCQ-related fields/methods from parser**

Delete from `MarkdownQuestionParser.java`:
- Patterns: `MCQ_CALLOUT_START`, `MCQ_OPTION_LINE`, `MCQ_SECTION_LINE`, `MCQ_CONTINUATION_LINE`.
- Method: `extractMcqFromAnswer(...)`.
- Inner classes: `OptionBuilder`, `ParsedOption`, `McqParseResult`.
- Field `List<ParsedOption> options` from `ParsedQuestion` record.
- `hasMcqOptions()` method.
- Inside `ParsedQuestionBuilder.build(...)`: remove `mcq = extractMcqFromAnswer(...)` and use `answerMarkdown` directly.

Updated `ParsedQuestion` record signature:

```java
public record ParsedQuestion(
        String questionNumber,
        String questionText,
        String answerMarkdown,
        String rawAnswer,
        boolean important,
        QuestionType questionType,
        String codeSnippet
) {}
```

- [ ] **Step 2: Updated `ParsedQuestionBuilder.build`**

```java
ParsedQuestion build(Path sourceFile) {
    CodeExtractionResult extraction = extractCodeAndType(answerMarkdown);
    return new ParsedQuestion(
            questionNumber, questionText,
            answerMarkdown,
            answerMarkdown,
            important,
            extraction.type(), extraction.codeSnippet());
}
```

- [ ] **Step 3: Add warning log if legacy `> [!mcq]` found in `.md`**

At the top of `parse(...)` method, after `Files.readAllLines`, add:

```java
boolean hasLegacyMcq = lines.stream().anyMatch(l -> l.trim().startsWith("> [!mcq]"));
if (hasLegacyMcq) {
    log.warn("Legacy `> [!mcq]` callout found in {} — MCQ must be in seed JSON now",
            filePath.getFileName());
}
```

- [ ] **Step 4: Delete obsolete tests in `MarkdownQuestionParserTest.java`**

Remove these test methods:
- `parsesMcqBlock`
- `noWarningEmittedForMultipleMcqBlocksAfterV15Migration`
- `capturesAllMcqBlocksWithBlockIndex`
- `capturesMultiLineExplanationFromIndentedSections`
- `inlinePipeExplanationStillWorksForLegacyFormat`
- `mcqCalloutWithInlineQuestionHeaderStillRecognized`

Keep:
- `parsesImportantQuestionAndDetectsCodeSnippet`

Remove unused imports (`ch.qos.logback.*`, `org.slf4j.LoggerFactory`).

- [ ] **Step 5: Add one new test**

```java
@Test
void parsesMarkdownWithoutMcqBlocks() throws IOException {
    Path file = tempDir.resolve("clean.md");
    Files.writeString(file, """
            ## Q1. (!) What is X?

            X is a thing that does Y.

            ## Q2. Why use Z?

            Z is useful because of W.
            """);

    List<MarkdownQuestionParser.ParsedQuestion> questions = parser().parse(file);

    assertThat(questions).hasSize(2);
    assertThat(questions.get(0).important()).isTrue();
    assertThat(questions.get(0).answerMarkdown()).contains("X is a thing");
    assertThat(questions.get(1).important()).isFalse();
}
```

- [ ] **Step 6: Run parser tests**

```bash
./gradlew :quiz-app:test --tests "com.cheatsheet.quiz.service.imports.MarkdownQuestionParserTest"
```

Expected: PASS.

- [ ] **Step 7: Run full test suite**

```bash
./gradlew :quiz-app:test
```

Expected: PASS. If `AnswerOption`/`ParsedOption` referenced elsewhere (tests, controllers), fix the call sites.

- [ ] **Step 8: Commit**

```bash
git add modules/quiz-app/src/main/java/com/cheatsheet/quiz/service/imports/MarkdownQuestionParser.java \
        modules/quiz-app/src/test/java/com/cheatsheet/quiz/service/imports/MarkdownQuestionParserTest.java
git commit -m "refactor(parser): remove mcq extraction logic (now in McqJsonLoader)"
```

---

## Section E: Verify scripts

### Task 10: Create verify-mcq-json.sh

**Files:**
- Create: `scripts/verify-mcq-json.sh`

- [ ] **Step 1: Write script**

```bash
#!/usr/bin/env bash
# verify-mcq-json.sh — валидирует MCQ-сидеры против JSON Schema.
#
# Usage:
#   bash scripts/verify-mcq-json.sh <file1.json> [file2.json ...]
#   bash scripts/verify-mcq-json.sh                            # staged files режим
#
# Требует ajv-cli (npm install -g ajv-cli) или Python jsonschema.

set -euo pipefail

SCHEMA="modules/quiz-app/src/main/resources/seed/mcq-schema.json"

if [[ -t 1 ]]; then
    RED='\033[0;31m' GRN='\033[0;32m' NC='\033[0m'
else
    RED='' GRN='' NC=''
fi

declare -a FILES
if [[ $# -gt 0 ]]; then
    FILES=("$@")
else
    mapfile -t FILES < <(git diff --cached --name-only --diff-filter=AM \
        | grep -E '^modules/quiz-app/src/main/resources/seed/mcq/.*\.json$' || true)
fi

if [[ ${#FILES[@]} -eq 0 ]]; then
    echo "verify-mcq-json: no json files to check"
    exit 0
fi

# Detect validator
if command -v ajv >/dev/null 2>&1; then
    VALIDATE_CMD=("ajv" "validate" "--spec=draft7" "--strict=false" "-s" "$SCHEMA" "-d")
elif command -v jsonschema >/dev/null 2>&1; then
    VALIDATE_CMD=("jsonschema" "-i")
else
    echo "ERROR: install 'ajv-cli' (npm i -g ajv-cli) or 'jsonschema' (pip install jsonschema)" >&2
    exit 2
fi

ERR=0
for file in "${FILES[@]}"; do
    [[ -f "$file" ]] || { echo "skip non-existent $file"; continue; }
    if "${VALIDATE_CMD[@]}" "$file" "$SCHEMA" >/dev/null 2>&1; then
        echo -e "${GRN}OK${NC}   $file"
    else
        echo -e "${RED}FAIL${NC} $file"
        "${VALIDATE_CMD[@]}" "$file" "$SCHEMA" 2>&1 | sed 's/^/    /'
        ERR=$((ERR + 1))
    fi
done

if [[ $ERR -gt 0 ]]; then
    echo -e "${RED}verify-mcq-json: $ERR error(s)${NC}"
    exit 1
fi
echo -e "${GRN}verify-mcq-json: all $((${#FILES[@]})) file(s) valid${NC}"
```

- [ ] **Step 2: Make executable**

```bash
chmod +x scripts/verify-mcq-json.sh
```

- [ ] **Step 3: Test on the existing test fixture**

```bash
bash scripts/verify-mcq-json.sh modules/quiz-app/src/test/resources/seed/mcq/test/sample-interview.json
```

Expected: `OK ...` exit 0.

If ajv is missing — `npm install -g ajv-cli` or skip and rely on Java schema validation at startup.

- [ ] **Step 4: Commit**

```bash
git add scripts/verify-mcq-json.sh
git commit -m "feat(scripts): add verify-mcq-json.sh for json seed validation"
```

---

### Task 11: Create verify-md-no-mcq.sh

**Files:**
- Create: `scripts/verify-md-no-mcq.sh`

- [ ] **Step 1: Write script**

```bash
#!/usr/bin/env bash
# verify-md-no-mcq.sh — гарантирует что .md не содержит legacy MCQ-конструкций.
# MCQ теперь живут в json-сидерах под modules/quiz-app/src/main/resources/seed/mcq/.

set -euo pipefail

if [[ -t 1 ]]; then
    RED='\033[0;31m' GRN='\033[0;32m' NC='\033[0m'
else
    RED='' GRN='' NC=''
fi

declare -a FILES
if [[ $# -gt 0 ]]; then
    FILES=("$@")
else
    mapfile -t FILES < <(git diff --cached --name-only --diff-filter=AM \
        | grep -E '^cheatsheets/interview/.*\.md$' || true)
fi

if [[ ${#FILES[@]} -eq 0 ]]; then
    echo "verify-md-no-mcq: no md files to check"
    exit 0
fi

declare -a RULES=(
    '^>\s*\[!mcq\]|^==BANNED== > [!mcq] callout — MCQ должен быть в seed/mcq/*.json'
    '❌ ПОСЛЕДСТВИЕ:|^==BANNED== эмодзи-маркер старого формата'
    '✓ ПРИМЕНЯТЬ:|^==BANNED== эмодзи-маркер старого формата'
    '📋 ПРАВИЛО:|^==BANNED== эмодзи-маркер старого формата'
    '🔗 См\. Q[0-9]|^==BANNED== эмодзи-маркер старого формата'
    '\[\[Q[0-9]+\]\]|^==BANNED== legacy [[Q<N>]] — use [[<file>#Q<N>]]'
)

ERR=0
for file in "${FILES[@]}"; do
    [[ -f "$file" ]] || continue
    file_err=0
    for rule in "${RULES[@]}"; do
        regex="${rule%%|*}"
        desc="${rule#*|}"
        if grep -nE "$regex" "$file" >/dev/null 2>&1; then
            echo -e "${RED}FAIL${NC} $file: ${desc#==BANNED== }"
            grep -nE "$regex" "$file" | head -3 | sed 's/^/    /'
            file_err=$((file_err + 1))
        fi
    done
    [[ $file_err -eq 0 ]] && echo -e "${GRN}OK${NC}   $file"
    ERR=$((ERR + file_err))
done

if [[ $ERR -gt 0 ]]; then
    echo -e "${RED}verify-md-no-mcq: $ERR error(s)${NC}"
    exit 1
fi
echo -e "${GRN}verify-md-no-mcq: all clean${NC}"
```

- [ ] **Step 2: Make executable + smoke test**

```bash
chmod +x scripts/verify-md-no-mcq.sh
echo "test content" > /tmp/clean.md
bash scripts/verify-md-no-mcq.sh /tmp/clean.md
```

Expected: `OK ...` exit 0.

```bash
echo "> [!mcq]" > /tmp/dirty.md
bash scripts/verify-md-no-mcq.sh /tmp/dirty.md
```

Expected: `FAIL ... > [!mcq] callout`.

- [ ] **Step 3: Commit**

```bash
git add scripts/verify-md-no-mcq.sh
git commit -m "feat(scripts): add verify-md-no-mcq.sh to block legacy mcq in .md"
```

---

### Task 12: Replace pre-commit hook + delete old verify-mcq.sh

**Files:**
- Modify: `.pre-commit-config.yaml`
- Delete: `scripts/verify-mcq.sh`

- [ ] **Step 1: Update `.pre-commit-config.yaml`**

Find the entry referencing `verify-mcq.sh` and replace with two entries:

```yaml
  - repo: local
    hooks:
      - id: verify-mcq-json
        name: verify mcq json seeds against schema
        entry: bash scripts/verify-mcq-json.sh
        language: system
        files: ^modules/quiz-app/src/main/resources/seed/mcq/.*\.json$
        pass_filenames: true

      - id: verify-md-no-mcq
        name: verify .md files have no legacy mcq
        entry: bash scripts/verify-md-no-mcq.sh
        language: system
        files: ^cheatsheets/interview/.*\.md$
        pass_filenames: true
```

- [ ] **Step 2: Delete obsolete script**

```bash
git rm scripts/verify-mcq.sh
```

- [ ] **Step 3: Run pre-commit on full repo to sanity-check**

```bash
pre-commit run --all-files || true
```

(May FAIL because 226 legacy .md files still have MCQ — that's expected; those are migrated in later tasks.)

- [ ] **Step 4: Commit**

```bash
git add .pre-commit-config.yaml
git commit -m "build(precommit): switch to verify-mcq-json + verify-md-no-mcq"
```

---

## Section F: Migration tool

### Task 13: Create awk-based extraction helper `scripts/extract-mcq-blocks.awk`

**Files:**
- Create: `scripts/extract-mcq-blocks.awk`

- [ ] **Step 1: Write awk script**

```awk
# extract-mcq-blocks.awk — извлекает MCQ блоки из markdown в TSV для последующей обработки.
#
# Output (TSV): qNumber<TAB>blockIdx<TAB>optionLabel<TAB>correct<TAB>text<TAB>sectionName<TAB>sectionContent
# Один файл = одна или более записей; запускать через `awk -f extract-mcq-blocks.awk <file.md>`.

BEGIN { current_q = ""; in_mcq = 0; block_idx = 0; opt_label = ""; correct = ""; opt_text = "" }

/^## Q[0-9]+\./ {
    match($0, /^## Q([0-9]+)\./, m); current_q = m[1]; block_idx = 0; in_mcq = 0; next
}

/^> \[!mcq\]/ { in_mcq = 1; next }

in_mcq && /^> - \[[ xX]\]/ {
    match($0, /^> - \[([ xX])\] ?([A-D])\. ?(.+?)$/, m)
    correct = (m[1] == "x" || m[1] == "X") ? "true" : "false"
    opt_label = m[2]
    opt_text = m[3]
    # Strip trailing | <legacy> if present
    sub(/[[:space:]]*\|.*$/, "", opt_text)
    next
}

in_mcq && /^>[[:space:]]+\*\*[^*]+\*\*/ {
    # Section line: capture name + content
    match($0, /^>[[:space:]]+\*\*([^*]+)\*\*[[:space:]]*(.*)$/, m)
    sec_name = m[1]; gsub(/\.$/, "", sec_name)
    sec_content = m[2]
    printf "%s\t%d\t%s\t%s\t%s\t%s\t%s\n", current_q, block_idx, opt_label, correct, opt_text, sec_name, sec_content
    next
}

in_mcq && /^[[:space:]]*$/ { next }
in_mcq && !/^>/ { in_mcq = 0; block_idx++; next }
```

- [ ] **Step 2: Test on a known v2 file**

```bash
awk -f scripts/extract-mcq-blocks.awk \
    cheatsheets/interview/ai-ml/embeddings-interview.md | head -10
```

Expected: 10 rows of TSV with q_number, block_idx, option label, correct flag, text, section, content. If output empty or malformed — adjust awk patterns.

- [ ] **Step 3: Commit**

```bash
git add scripts/extract-mcq-blocks.awk
git commit -m "feat(scripts): add awk helper to extract mcq blocks from md"
```

---

### Task 14: Create `scripts/migrate-md-to-json.sh` orchestrator

**Files:**
- Create: `scripts/migrate-md-to-json.sh`

- [ ] **Step 1: Write orchestrator**

```bash
#!/usr/bin/env bash
# migrate-md-to-json.sh — конвертирует один .md (v2 inline MCQ) в .json seed + чистит .md.
# Usage: bash scripts/migrate-md-to-json.sh cheatsheets/interview/<category>/<topic>.md

set -euo pipefail

MD_FILE="$1"
[[ -f "$MD_FILE" ]] || { echo "file not found: $MD_FILE"; exit 1; }

# Compute paths
REL=${MD_FILE#cheatsheets/interview/}
CATEGORY=$(dirname "$REL")
BASENAME=$(basename "$REL" .md)
JSON_DIR="modules/quiz-app/src/main/resources/seed/mcq/$CATEGORY"
JSON_FILE="$JSON_DIR/$BASENAME.json"

# Extract section name → JSON key
declare -A SEC_MAP=(
    [Развёрнутое объяснение]=explanation
    [Пример]=example
    [Когда применять]=when_to_apply
    [Подводные камни]=edge_cases
    [Связанные вопросы]=related
    [Что на самом деле]=what_actually
    [Откуда путаница]=source_of_confusion
    [Если бы это было правдой]=if_it_were_true
    [Как было бы правильно]=how_it_should_be
)

mkdir -p "$JSON_DIR"

# Run awk extractor → build JSON via jq
TSV=$(awk -f scripts/extract-mcq-blocks.awk "$MD_FILE")
if [[ -z "$TSV" ]]; then
    echo "no MCQ blocks found in $MD_FILE"
    exit 0
fi

# Build JSON with jq
# Group by q_number, then by block_idx, then by option label
JSON_BUILD=$(echo "$TSV" | python3 -c '
import sys, json, collections
sec_map = {
    "Развёрнутое объяснение":"explanation",
    "Пример":"example",
    "Когда применять":"when_to_apply",
    "Подводные камни":"edge_cases",
    "Связанные вопросы":"related",
    "Что на самом деле":"what_actually",
    "Откуда путаница":"source_of_confusion",
    "Если бы это было правдой":"if_it_were_true",
    "Как было бы правильно":"how_it_should_be",
}
data = collections.defaultdict(lambda: collections.defaultdict(lambda: collections.defaultdict(lambda: {"sections":{}})))
for line in sys.stdin:
    parts = line.rstrip("\n").split("\t")
    if len(parts) != 7: continue
    q, blk, label, correct, text, sec, content = parts
    key = sec_map.get(sec)
    if not key: continue
    opt = data[int(q)][int(blk)][label]
    opt["label"] = label
    opt["text"] = text
    opt["correct"] = (correct == "true")
    opt["sections"][key] = content
out = {"topic_slug": "'"$BASENAME"'", "questions": []}
for qn in sorted(data.keys()):
    blocks = []
    for bi in sorted(data[qn].keys()):
        options = []
        labels = sorted(data[qn][bi].keys())
        for order, lbl in enumerate(labels):
            o = data[qn][bi][lbl]
            o["order"] = order
            options.append(o)
        blocks.append({"block_idx": bi, "question_text": "", "options": options})
    out["questions"].append({"q_number": qn, "blocks": blocks})
print(json.dumps(out, ensure_ascii=False, indent=2))
')

echo "$JSON_BUILD" > "$JSON_FILE"

# Strip MCQ blocks from .md
awk '
    /^> \[!mcq\]/ { in_mcq = 1; next }
    in_mcq && /^>/ { next }
    in_mcq && /^[[:space:]]*$/ { in_mcq = 0; next }
    { print }
' "$MD_FILE" > "$MD_FILE.tmp" && mv "$MD_FILE.tmp" "$MD_FILE"

# Remove mcq_format_version from frontmatter
sed -i.bak '/^mcq_format_version:/d' "$MD_FILE" && rm "$MD_FILE.bak"

echo "Migrated: $MD_FILE → $JSON_FILE"
```

**Note:** uses `python3` for JSON building because awk-only JSON is unreliable. User constraint about "no python scripts" applies to MCQ generation, not one-off migration tool — confirm with user before running.

- [ ] **Step 2: Make executable**

```bash
chmod +x scripts/migrate-md-to-json.sh
```

- [ ] **Step 3: Commit**

```bash
git add scripts/migrate-md-to-json.sh
git commit -m "feat(scripts): add md-to-json migration tool"
```

---

### Task 15: Test migration on one file

**Files:**
- Modifies: `cheatsheets/interview/ai-ml/embeddings-interview.md`
- Creates: `modules/quiz-app/src/main/resources/seed/mcq/ai-ml/embeddings-interview.json`

- [ ] **Step 1: Pick a clean v2 file as test**

```bash
git stash  # safety: save any unrelated work
bash scripts/migrate-md-to-json.sh cheatsheets/interview/ai-ml/embeddings-interview.md
```

- [ ] **Step 2: Inspect result**

```bash
head -30 modules/quiz-app/src/main/resources/seed/mcq/ai-ml/embeddings-interview.json
grep -c '"q_number"' modules/quiz-app/src/main/resources/seed/mcq/ai-ml/embeddings-interview.json
```

Expected: 29 q_number entries (matches MCQ block count in original).

- [ ] **Step 3: Validate JSON against schema**

```bash
bash scripts/verify-mcq-json.sh \
    modules/quiz-app/src/main/resources/seed/mcq/ai-ml/embeddings-interview.json
```

Expected: `OK`. If FAIL — fix Python builder in migrate script to emit `question_text` (currently empty), populate from `> [!mcq] <text>` header line.

- [ ] **Step 4: Verify .md has no MCQ**

```bash
bash scripts/verify-md-no-mcq.sh cheatsheets/interview/ai-ml/embeddings-interview.md
```

Expected: `OK`.

- [ ] **Step 5: Run quiz-app integration test to confirm loader picks up the seed**

```bash
./gradlew :quiz-app:test --tests "*ImportService*"
```

Expected: PASS.

- [ ] **Step 6: If all green — commit; else rollback**

If anything failed:
```bash
git checkout cheatsheets/interview/ai-ml/embeddings-interview.md
rm -f modules/quiz-app/src/main/resources/seed/mcq/ai-ml/embeddings-interview.json
# fix migrate-md-to-json.sh and retry
```

If all OK:
```bash
git add cheatsheets/interview/ai-ml/embeddings-interview.md \
        modules/quiz-app/src/main/resources/seed/mcq/ai-ml/embeddings-interview.json
git commit -m "chore(mcq): migrate embeddings-interview to json seed (canary)"
```

---

### Task 16: Migrate remaining 16 v2 files

**Files:**
- Modify: all `.md` listed by `grep -l 'mcq_format_version: 2' cheatsheets/interview/ -r`
- Create: corresponding `.json` seeds

- [ ] **Step 1: List v2 files**

```bash
grep -l 'mcq_format_version:[[:space:]]*2' cheatsheets/interview/ -r > /tmp/v2-files.txt
wc -l /tmp/v2-files.txt
```

Expected: ~16 files.

- [ ] **Step 2: Run migration on each**

```bash
while IFS= read -r f; do
    echo "=== $f ==="
    bash scripts/migrate-md-to-json.sh "$f"
done < /tmp/v2-files.txt
```

- [ ] **Step 3: Validate all generated JSONs**

```bash
find modules/quiz-app/src/main/resources/seed/mcq/ -name '*.json' -print0 \
    | xargs -0 bash scripts/verify-mcq-json.sh
```

Expected: all `OK`.

- [ ] **Step 4: Validate .md cleanup**

```bash
find cheatsheets/interview/ -name '*.md' -print0 \
    | xargs -0 bash scripts/verify-md-no-mcq.sh 2>&1 | grep -c FAIL
```

Expected: 0 FAILs across migrated files (legacy v1 files will still FAIL — that's expected).

- [ ] **Step 5: Run quiz-app tests**

```bash
./gradlew :quiz-app:test
```

Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add cheatsheets/interview/ modules/quiz-app/src/main/resources/seed/mcq/
git commit -m "chore(mcq): migrate 16 v2 .md files to json seeds"
```

---

## Section G: SKILL update + batch v1 migration

### Task 17: Update SKILL `mcq-quality-fixer` for JSON format

**Files:**
- Modify: `~/.claude/skills/mcq-quality-fixer/SKILL.md`

- [ ] **Step 1: Rewrite SKILL**

Replace content with:

```markdown
---
name: mcq-quality-fixer
description: Создание / правка MCQ-сидеров (JSON) для тем в cheatsheets/interview/. MCQ хранятся в modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json, валидируются schema.

triggers: «улучшить MCQ», «починить варианты», «качество MCQ», «дидактика MCQ», «по плану mcq», «генерировать MCQ», «mcq seed».
---

# MCQ Quality Fixer v3 (JSON-based)

MCQ хранятся в JSON, не в markdown. Markdown теперь только для теории.

## Расположение

- Теория: `cheatsheets/interview/<category>/<topic>-interview.md` (без MCQ).
- MCQ: `modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>-interview.json` (зеркально).
- Schema: `modules/quiz-app/src/main/resources/seed/mcq-schema.json`.

## JSON-формат

См. `docs/superpowers/specs/2026-05-20-mcq-v3-design.md` раздел "JSON-формат" + Schema.

Структура:
- `topic_slug` = basename .md без расширения
- `questions[].q_number` = номер `## Q<N>` в .md
- `blocks[]` — позволяет несколько MCQ на один Q (концепция + production gotcha)
- `options[]` — ровно 4 (1 correct, 3 wrong)
- `sections` — типизированные ключи:
  - Correct (5): `explanation`, `example`, `when_to_apply`, `edge_cases`, `related`
  - Wrong (4): `what_actually`, `source_of_confusion`, `if_it_were_true`, `how_it_should_be`

## Правила содержания

(Переносим из старого SKILL — Single-Delta principle, distractor categories, варьирование позиции correct).

- 5 секций correct + 4 секции wrong (обязательно, schema enforced).
- Текст опции: 1-2 предложения, без bold.
- `text` — `<утверждение>` (без префикса буквы, она в `label` поле).
- `label` — `A`/`B`/`C`/`D`, `order` — 0-3.
- Ровно один `correct: true` на block.
- Single-Delta: 4 варианта отличаются ровно одним элементом.
- Варьирование позиции `[x]`: max/min позиция ≤ 2 в файле.

## Workflow

1. Прочитать .md теорию для контекста темы.
2. Сгенерировать .json по schema.
3. Запустить `bash scripts/verify-mcq-json.sh <file.json>` — должен exit 0.
4. Прогнать `./gradlew :quiz-app:test` (опционально).
5. Коммит.
```

- [ ] **Step 2: Commit**

```bash
git add ~/.claude/skills/mcq-quality-fixer/SKILL.md  # if SKILL is in this repo
# or note that SKILL is in user dotfiles, not project — manual sync
```

If SKILL is outside this repo (in `~/.claude/`), commit там же:
```bash
cd ~/.claude && git add skills/mcq-quality-fixer/SKILL.md && \
    git commit -m "skill(mcq): switch to json-based v3 format"
```

---

### Task 18: Update `interview-writer` SKILL to maintain JSON in sync

**Files:**
- Modify: `~/.claude/skills/interview-writer/SKILL.md`

**Цель:** когда `interview-writer` создаёт/изменяет/удаляет `## Q<N>` в `cheatsheets/interview/<topic>.md`, он должен синхронно обновлять соответствующий JSON-сидер в `modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json`.

- [ ] **Step 1: Read current `interview-writer` SKILL**

```bash
cat ~/.claude/skills/interview-writer/SKILL.md | head -100
```

- [ ] **Step 2: Append a new section "MCQ Sync (v3)" к SKILL**

Добавь в конец `~/.claude/skills/interview-writer/SKILL.md`:

```markdown
## MCQ Sync (v3) — обязательно при любом изменении Q

С 2026-05 MCQ хранятся отдельно от `.md` — в JSON-сидерах под `modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json`.
При любой работе с `cheatsheets/interview/<category>/<topic>.md` ты ОБЯЗАН поддерживать sync с JSON:

### Когда CREATE/ADD нового `## Q<N>`

1. Создать .md секцию `## Q<N>. <title>` с теорией (как обычно).
2. **Сгенерировать MCQ-блок** в `modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json`:
   - Если файла нет — создать с `{"topic_slug": "<topic>", "questions": []}`.
   - Добавить элемент `{"q_number": N, "blocks": [{"block_idx": 0, "question_text": "...", "options": [4 опции]}]}`.
   - Формат options см. `~/.claude/skills/mcq-quality-fixer/SKILL.md` (5 секций correct, 4 wrong, schema-valid).
3. Запустить `bash scripts/verify-mcq-json.sh <json>` — должен exit 0.

### Когда UPDATE существующего `## Q<N>` (изменил текст ответа)

1. Если изменения косметические (typos, форматирование) — JSON НЕ трогать.
2. Если изменения семантические (новая концепция, изменился ключевой факт):
   - Найти в JSON элемент с `q_number: N`.
   - Перегенерировать опции/секции под новый смысл вопроса.
   - Single-Delta principle, Distractor categories — см. mcq-quality-fixer SKILL.
3. Запустить `bash scripts/verify-mcq-json.sh <json>`.

### Когда DELETE `## Q<N>`

1. Удалить из .md.
2. Удалить из JSON элемент `q_number: N`.
3. Перенумеровать остальные Q? **НЕТ** — оставить gaps в нумерации. Перенумерация ломает внешние `[[file#Q5]]` ссылки.
4. Запустить `bash scripts/verify-mcq-json.sh`.

### Когда RENAME topic (basename .md)

1. Переименовать `cheatsheets/interview/<category>/<old>.md` → `<new>.md`.
2. Переименовать `modules/quiz-app/src/main/resources/seed/mcq/<category>/<old>.json` → `<new>.json`.
3. Обновить `topic_slug` в JSON на новое имя.
4. Найти все `[[<old>#Q...]]` ссылки в других .md и JSON, обновить.

### Связанные SKILL

- `mcq-quality-fixer` — детали написания качественных MCQ опций (Single-Delta, distractor categories, варьирование позиции correct, structural требования секций).
- `interview-options-writer` — депрекейтед, его логика поглощена `mcq-quality-fixer`.
```

- [ ] **Step 3: Также обновить SKILL `interview-options-writer` (deprecate)**

Edit `~/.claude/skills/interview-options-writer/SKILL.md`. В самом верху, после frontmatter, добавь:

```markdown
> **DEPRECATED (2026-05-20).** Этот skill заменён на `mcq-quality-fixer` (JSON-based). Не используй для нового контента.
> Для генерации MCQ опций см. `~/.claude/skills/mcq-quality-fixer/SKILL.md`.
> Для управления Q+MCQ совместно см. `~/.claude/skills/interview-writer/SKILL.md`.
```

- [ ] **Step 4: Commit**

```bash
cd ~/.claude
git add skills/interview-writer/SKILL.md skills/interview-options-writer/SKILL.md
git commit -m "skill(interview): require json mcq sync on q changes; deprecate interview-options-writer"
cd -
```

(Если SKILL живёт в этом репо как submodule/symlink — коммитить в его репозиторий; иначе в user dotfiles.)

---

### Task 19: Subagent-batch migration of 226 v1 legacy files

**Files:**
- Each `.md` in `cheatsheets/interview/` with legacy emoji-маркеры.
- Each corresponding `.json` in `modules/quiz-app/src/main/resources/seed/mcq/`.

This is an iterative task — split into batches of 5-8 parallel subagents over multiple cycles until all 226 migrated.

- [ ] **Step 1: Build queue**

```bash
grep -l '❌ ПОСЛЕДСТВИЕ\|✓ ПРИМЕНЯТЬ\|📋 ПРАВИЛО\|^> - \[[ x]\][^|]*\|' cheatsheets/interview/ -r \
    | sort > /tmp/v1-queue.txt
wc -l /tmp/v1-queue.txt
```

Expected: ~226 files.

- [ ] **Step 2: Per file — dispatch subagent (template below)**

For each file `cheatsheets/interview/<category>/<topic>.md` not yet migrated, dispatch a subagent with this brief:

```
**Файл:** cheatsheets/interview/<category>/<topic>.md
**Действие:**
1. Прочитай SKILL: ~/.claude/skills/mcq-quality-fixer/SKILL.md
2. Прочитай docs/superpowers/specs/2026-05-20-mcq-v3-design.md (JSON-формат)
3. Прочитай теорию в .md
4. Для каждой `## Q<N>` секции — сгенерируй MCQ блок согласно schema (4 опции, 1 correct, 5/4 секций)
5. Очисти .md от: `> [!mcq]` callout'ов, эмодзи-маркеров `❌ ✓ 📋 🔗`, single-line `> - [x] text | exp`, поля `mcq_format_version` из frontmatter
6. Запиши JSON в modules/quiz-app/src/main/resources/seed/mcq/<category>/<topic>.json
7. Запусти `bash scripts/verify-mcq-json.sh <json>` + `bash scripts/verify-md-no-mcq.sh <md>` — оба должны exit 0
8. Отчитайся: обработано N Q, exit code обоих скриптов.
```

- [ ] **Step 3: After each batch**

```bash
./gradlew :quiz-app:test --tests "*ImportService*"
git diff --stat
git add cheatsheets/interview/ modules/quiz-app/src/main/resources/seed/mcq/
git commit -m "chore(mcq): batch-migrate <N> v1 files to json seed"
```

- [ ] **Step 4: Periodic progress check**

```bash
# Files still legacy:
grep -l '❌ ПОСЛЕДСТВИЕ\|^> - \[[ x]\][^|]*\|' cheatsheets/interview/ -r | wc -l

# Files migrated to json:
find modules/quiz-app/src/main/resources/seed/mcq/ -name '*.json' ! -name 'mcq-schema.json' | wc -l
```

When `legacy = 0` and `json = total Q's count` — migration complete.

---

## Section H: Final validation

### Task 20: Full repo verification

- [ ] **Step 1: Verify all .md clean**

```bash
find cheatsheets/interview/ -name '*.md' -print0 \
    | xargs -0 bash scripts/verify-md-no-mcq.sh
```

Expected: all `OK`, exit 0.

- [ ] **Step 2: Verify all JSONs valid**

```bash
find modules/quiz-app/src/main/resources/seed/mcq/ -name '*.json' ! -name 'mcq-schema.json' -print0 \
    | xargs -0 bash scripts/verify-mcq-json.sh
```

Expected: all `OK`, exit 0.

- [ ] **Step 3: Full test suite**

```bash
./gradlew clean test
```

Expected: PASS.

- [ ] **Step 4: Manual quiz-app smoke test**

```bash
./gradlew :quiz-app:bootRun
```

Open http://localhost:8080, navigate through several questions — MCQ options должны отображаться.

- [ ] **Step 5: Final commit + cleanup**

```bash
git add -A
git commit -m "chore(mcq): complete v3 migration — all topics on json seed"
```

---

## Self-Review (done)

- **Spec coverage:** Каждый раздел spec мапится на задачу (Schema → T2, DTOs → T3, McqJsonLoader → T6+T7, Парсер удаление → T9, verify-mcq-json → T10, verify-md-no-mcq → T11, миграция 17 → T13-T16, mcq-quality-fixer SKILL → T17, interview-writer SKILL sync → T18, batch 226 → T19, final → T20). ✅
- **Placeholders:** Все шаги содержат конкретный код / команды. ✅
- **Type consistency:** `McqSeed`/`McqSeedQuestion`/`McqSeedBlock`/`McqSeedOption` имена согласованы в DTO задаче (T3), используются в loader (T7) и тестах (T6). `findIdByTopicAndQuestionNumber` имя согласовано T5↔T7. `McqLoadResult` поля `found/optionsInserted/questionsSkipped` согласованы T4↔T6↔T7. ✅

---

## Execution Handoff

Plan complete and saved to `docs/superpowers/plans/2026-05-20-mcq-json-migration.md`.
