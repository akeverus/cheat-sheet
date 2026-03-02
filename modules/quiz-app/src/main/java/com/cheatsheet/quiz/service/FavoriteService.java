package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.imports.MarkdownFavoriteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис переключения состояния «избранное» для вопроса.
 */
@Service
public class FavoriteService {

    private final QuestionRepository questionRepository;
    private final MarkdownFavoriteService markdownFavoriteService;

    public FavoriteService(QuestionRepository questionRepository, MarkdownFavoriteService markdownFavoriteService) {
        this.questionRepository = questionRepository;
        this.markdownFavoriteService = markdownFavoriteService;
    }

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
    public record FavoriteResult(long questionId, boolean favorite, boolean synced) {
    }
}
