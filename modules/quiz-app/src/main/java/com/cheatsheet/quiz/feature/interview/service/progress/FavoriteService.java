package com.cheatsheet.quiz.feature.interview.service.progress;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.imports.MarkdownFavoriteService;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.Builder;

/**
 * Сервис переключения состояния «избранное» для вопроса.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteService {
    QuestionRepository questionRepository;
    MarkdownFavoriteService markdownFavoriteService;

    /**
     * Переключает флаг избранного для вопроса и синхронизирует markdown-файл.
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
        boolean synced = markdownFavoriteService.toggleInFile(question, newFavorite);
        return new FavoriteResult(questionId, newFavorite, synced);
    }

    /**
     * Результат переключения избранного.
     *
     * @param questionId идентификатор вопроса
     * @param favorite новое состояние «избранное»
     * @param synced признак успешной синхронизации markdown
     */
    @Builder(toBuilder = true)
    public record FavoriteResult(long questionId, boolean favorite, boolean synced) {
    }
}
