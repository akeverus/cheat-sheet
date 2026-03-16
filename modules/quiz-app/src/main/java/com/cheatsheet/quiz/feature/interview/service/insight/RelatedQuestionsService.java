package com.cheatsheet.quiz.feature.interview.service.insight;

import com.cheatsheet.quiz.domain.RelatedQuestion;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Сервис поиска связанных вопросов для блока "Похожие вопросы для закрепления".
 *
 * <p>Стратегия выбора:</p>
 * <ol>
 *   <li>Вопросы из той же темы с ошибками (приоритет);</li>
 *   <li>Остальные вопросы из той же темы (случайный порядок).</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RelatedQuestionsService {

    private static final int DEFAULT_LIMIT = 3;

    QuestionRepository questionRepository;

    /**
     * Находит связанные вопросы для закрепления.
     *
     * @param questionId текущий вопрос (исключается)
     * @param topic      тема для поиска
     * @param limit      максимум результатов
     * @return список связанных вопросов
     */
    public List<RelatedQuestion> findRelated(long questionId, String topic, int limit) {
        if (topic == null || topic.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return questionRepository.findRelatedByTopic(questionId, topic, Math.max(1, limit));
        } catch (Exception e) {
            log.warn("Ошибка поиска связанных вопросов для id={}: {}", questionId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<RelatedQuestion> findRelated(long questionId, String topic) {
        return findRelated(questionId, topic, DEFAULT_LIMIT);
    }
}
