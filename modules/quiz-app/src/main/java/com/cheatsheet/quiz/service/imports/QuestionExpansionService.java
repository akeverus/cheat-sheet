package com.cheatsheet.quiz.service.imports;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.persistence.FullTextSearchRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.ReviewStateRepository;
import com.cheatsheet.quiz.service.ai.OptionGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

/**
 * Сервис расширения одного вопроса в несколько альтернативных формулировок.
 *
 * <p>Вызывает LLM для генерации 2–5 альтернативных вопросов, проверяющих то же знание,
 * и создаёт соответствующие записи в БД. Управляется параметром {@code app.interview.expand-per-source}.</p>
 */
@Service
@Slf4j
public class QuestionExpansionService {

    private final QuestionRepository questionRepository;
    private final ReviewStateRepository reviewStateRepository;
    private final FullTextSearchRepository fullTextSearchRepository;
    private final OptionGenerator optionGenerator;
    private final AppProperties appProperties;
    private final Clock clock;

    public QuestionExpansionService(
            QuestionRepository questionRepository,
            ReviewStateRepository reviewStateRepository,
            FullTextSearchRepository fullTextSearchRepository,
            OptionGenerator optionGenerator,
            AppProperties appProperties,
            Clock clock
    ) {
        this.questionRepository = questionRepository;
        this.reviewStateRepository = reviewStateRepository;
        this.fullTextSearchRepository = fullTextSearchRepository;
        this.optionGenerator = optionGenerator;
        this.appProperties = appProperties;
        this.clock = clock;
    }

    /**
     * Создаёт дополнительные варианты вопроса на основе LLM-генерации.
     *
     * <p>Выполняется в отдельной транзакции ({@code REQUIRES_NEW}), чтобы не держать
     * соединение основной транзакции импорта во время долгих HTTP-вызовов к LLM.</p>
     *
     * @param baseQuestion базовый вопрос (уже вставленный в БД)
     * @param topic        тема
     * @param relativePath относительный путь к файлу
     * @return количество созданных дополнительных вопросов
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int expandFromBase(Question baseQuestion, String topic, String relativePath) {
        int expandCount = appProperties.getInterview().getExpandPerSource();
        if (expandCount < 1) {
            return 0;
        }

        Optional<List<String>> alternativeQuestions = optionGenerator.generateAlternativeQuestions(
                baseQuestion.questionText(),
                baseQuestion.answerMarkdown(),
                expandCount
        );

        List<String> alternatives = alternativeQuestions.orElse(List.of());
        if (alternatives.isEmpty()) {
            return 0;
        }

        String baseSlug = baseQuestion.slug();
        String sourceSlug = baseQuestion.sourceSlug();
        int created = 0;
        for (int i = 0; i < alternatives.size(); i++) {
            String altQuestionText = alternatives.get(i);
            String variantSlug = baseSlug + "-v" + (i + 1);
            if (questionRepository.findBySlug(variantSlug).isPresent()) {
                continue;
            }
            Question variant = new Question(
                    0L, variantSlug, sourceSlug, relativePath, topic,
                    altQuestionText, baseQuestion.answerMarkdown(), baseQuestion.important(),
                    baseQuestion.sourceHash(), baseQuestion.questionType(), baseQuestion.codeSnippet(), null, 0, null);
            long id = questionRepository.insert(variant);
            reviewStateRepository.insertIfAbsent(id, clock.instant().getEpochSecond());
            fullTextSearchRepository.upsert(id, variant.questionText(), variant.answerMarkdown());
            created++;
        }
        if (created > 0) {
            log.debug("Создано {} вариантов вопроса из источника {}", created, baseSlug);
        }
        return created;
    }
}
