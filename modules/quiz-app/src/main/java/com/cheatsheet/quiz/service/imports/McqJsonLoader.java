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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Загружает JSON-сиды MCQ из classpath ({@code seed/mcq/<category>/<topic>.json}) и
 * вставляет варианты ответа в БД. Используется на старте приложения сервисом импорта.
 *
 * <p>Валидирует JSON по схеме {@code seed/mcq-schema.json}. При несоответствии бросает
 * {@link InvalidMcqSeedException}. Если файла нет — возвращает {@link McqLoadResult#notFound()}.
 *
 * <p>Идемпотентен: перед вставкой удаляет существующие варианты по {@code question_id}.
 */
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

    /**
     * Загружает JSON-сид для конкретной темы и вставляет варианты.
     *
     * @param categoryPath относительный путь категории (например {@code "test"} или {@code "interview/ai-ml"})
     * @param topicSlug    slug темы (имя файла без {@code .json})
     * @return результат с количеством вставленных вариантов и пропущенных вопросов
     */
    public McqLoadResult loadForTopic(String categoryPath, String topicSlug) {
        String resourcePath = "seed/mcq/" + categoryPath + "/" + topicSlug + ".json";
        Path resourcePathForError = Paths.get(resourcePath);
        Resource resource = new ClassPathResource(resourcePath);
        if (!resource.exists()) {
            return McqLoadResult.notFound();
        }
        try (InputStream in = resource.getInputStream()) {
            JsonNode tree = objectMapper.readTree(in);
            Set<ValidationMessage> errors = schema.validate(tree);
            if (!errors.isEmpty()) {
                throw new InvalidMcqSeedException(resourcePathForError, errors);
            }
            McqSeed seed = objectMapper.treeToValue(tree, McqSeed.class);
            if (!seed.topicSlug().equals(topicSlug)) {
                throw new InvalidMcqSeedException(resourcePathForError,
                        "topic_slug mismatch: expected " + topicSlug + " got " + seed.topicSlug());
            }
            return upsertOptions(categoryPath, seed);
        } catch (IOException e) {
            throw new InvalidMcqSeedException(resourcePathForError, e.getMessage());
        }
    }

    private McqLoadResult upsertOptions(String categoryPath, McqSeed seed) {
        // Slug в БД хранится как <category-path>/<topic-slug>, поэтому собираем здесь.
        String topic = categoryPath + "/" + seed.topicSlug();
        int totalInserted = 0;
        int skipped = 0;
        for (McqSeedQuestion question : seed.questions()) {
            Optional<Long> questionId = questionRepository.findIdByTopicAndQuestionNumber(
                    topic, question.qNumber());
            if (questionId.isEmpty()) {
                log.warn("No question found for topic={}, q_number={}", topic, question.qNumber());
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
            String content = opt.sections() == null ? null : opt.sections().get(key);
            if (content == null) continue;
            if (sb.length() > 0) sb.append("\n\n");
            sb.append("**").append(labels.get(key)).append(".** ").append(content);
        }
        return sb.toString();
    }
}
