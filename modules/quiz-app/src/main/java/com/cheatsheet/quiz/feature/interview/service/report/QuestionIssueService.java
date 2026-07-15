package com.cheatsheet.quiz.feature.interview.service.report;

import com.cheatsheet.quiz.domain.QuestionIssue;
import com.cheatsheet.quiz.domain.QuestionIssueCategory;
import com.cheatsheet.quiz.domain.QuestionIssueStatus;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.persistence.QuestionIssueRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

/**
 * Приём жалоб пользователя на вопросы (FLOW-REPORT / UX-21).
 *
 * <p>Валидирует существование вопроса (иначе {@link QuestionNotFoundException}),
 * сохраняет жалобу со статусом {@link QuestionIssueStatus#OPEN} и возвращает
 * созданную запись.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuestionIssueService {

    QuestionIssueRepository questionIssueRepository;
    QuestionRepository questionRepository;
    Clock clock;

    /**
     * Регистрирует жалобу на вопрос.
     *
     * @param questionId вопрос, на который жалуются
     * @param category   категория проблемы
     * @param comment    опциональный комментарий (может быть {@code null}/пустым)
     * @return созданная жалоба (статус OPEN)
     * @throws QuestionNotFoundException если вопрос не найден
     */
    @Transactional
    public QuestionIssue report(long questionId, QuestionIssueCategory category, String comment) {
        questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Вопрос не найден: id=" + questionId));
        String normalizedComment = (comment == null || comment.isBlank()) ? null : comment.strip();
        Instant now = clock.instant();
        long id = questionIssueRepository.save(questionId, category, normalizedComment, now);
        log.info("Зарегистрирована жалоба на вопрос id={}, категория={}, issueId={}", questionId, category, id);
        return new QuestionIssue(id, questionId, category, normalizedComment, QuestionIssueStatus.OPEN, now);
    }
}
