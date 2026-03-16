package com.cheatsheet.quiz.service.strategy;

import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Стратегия случайного выбора вопросов.
 *
 * <p>Извлечена из {@code InterviewService.nextQuestion()} (ветка shuffle).</p>
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ShuffleSelectionStrategy implements QuestionSelectionStrategy {

    QuestionRepository questionRepository;

    @Override
    public Optional<Long> selectNextQuestionId(InterviewFilter filter, long nowEpoch) {
        List<Long> ids = questionRepository.findShuffledQuestionIds(
                filter.importantOnly(), filter.onlyWrong(), 1);
        return ids.isEmpty() ? Optional.empty() : Optional.of(ids.get(0));
    }
}
