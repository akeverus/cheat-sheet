package com.cheatsheet.quiz.feature.interview.service.progress;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис переключения состояния «избранное» для вопроса.
 * Флаг {@code important} хранится только в БД — markdown-файлы с вопросами read-only.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteService {
    QuestionRepository questionRepository;

    /**
     * Переключает флаг избранного для вопроса в БД.
     *
     * @param questionId идентификатор вопроса
     * @return результат операции
     */
    @Transactional
    public FavoriteResult toggleFavorite(long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Вопрос не найден: id=" + questionId));
        boolean newFavorite = !question.important();
        questionRepository.updateImportant(questionId, newFavorite);
        return new FavoriteResult(questionId, newFavorite);
    }

    /**
     * Результат переключения избранного.
     *
     * @param questionId идентификатор вопроса
     * @param favorite новое состояние «избранное»
     */
    @Builder(toBuilder = true)
    public record FavoriteResult(long questionId, boolean favorite) {
    }
}
