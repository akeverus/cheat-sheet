package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.OptionGenerator;
import com.cheatsheet.quiz.service.diagram.MermaidSanitizer;
import com.cheatsheet.quiz.service.diagram.MermaidValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис генерации и кэширования Mermaid-диаграмм для вопросов.
 *
 * <p>Если диаграмма уже сохранена в БД — возвращает её.
 * Иначе — запрашивает у AI и сохраняет результат.
 * Если AI решит, что диаграмма не нужна — возвращает пустой Optional.</p>
 */
@Service
@Slf4j
public class DiagramService {

    private final QuestionRepository questionRepository;
    private final OptionGenerator optionGenerator;
    private final MermaidValidator mermaidValidator;
    private final MermaidSanitizer mermaidSanitizer;

    public DiagramService(
            QuestionRepository questionRepository,
            OptionGenerator optionGenerator,
            MermaidValidator mermaidValidator,
            MermaidSanitizer mermaidSanitizer
    ) {
        this.questionRepository = questionRepository;
        this.optionGenerator = optionGenerator;
        this.mermaidValidator = mermaidValidator;
        this.mermaidSanitizer = mermaidSanitizer;
    }

    /**
     * Возвращает Mermaid-диаграмму для вопроса (из БД или через AI-генерацию).
     * Включает валидацию и retry при невалидном синтаксисе.
     *
     * @param question вопрос
     * @return Mermaid-код или пустой Optional, если диаграмма не нужна/не удалась
     */
    public Optional<String> getOrGenerateDiagram(Question question) {
        // Уже есть в БД
        if (question.diagramMermaid() != null && !question.diagramMermaid().isBlank()) {
            return Optional.of(question.diagramMermaid());
        }

        try {
            Optional<String> mermaid = generateAndValidateDiagram(question);
            if (mermaid.isPresent()) {
                questionRepository.updateDiagram(question.id(), mermaid.get());
                log.info("Диаграмма сохранена для вопроса {} ({})", question.id(), question.slug());
            }
            return mermaid;
        } catch (Exception e) {
            log.error("Ошибка генерации диаграммы для вопроса {}: {}", question.id(), e.getMessage(), e);
            return Optional.empty();
        }
    }

    /**
     * Генерирует диаграмму с валидацией и одной повторной попыткой.
     */
    private Optional<String> generateAndValidateDiagram(Question question) {
        for (int attempt = 1; attempt <= 2; attempt++) {
            Optional<String> raw = optionGenerator.generateDiagram(
                    question.questionText(), question.answerMarkdown(), question.topic());
            if (raw.isEmpty()) {
                return raw;
            }
            String sanitized = mermaidSanitizer.sanitizeMermaid(raw.get());
            if (mermaidValidator.isRelevantMermaid(sanitized, question.questionText(), question.answerMarkdown())) {
                return Optional.of(sanitized);
            }
            if (attempt == 1) {
                log.warn("Mermaid-диаграмма не прошла проверку качества для вопроса {}, retry", question.id());
            } else {
                log.warn("Повторная генерация Mermaid для вопроса {} нерелевантна/невалидна, пропускаем", question.id());
            }
        }
        return Optional.empty();
    }
}
