package com.cheatsheet.quiz.feature.interview.usecase;

import com.cheatsheet.quiz.domain.Bookmark;
import com.cheatsheet.quiz.domain.exception.QuestionNotFoundException;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.BookmarkResponse;
import com.cheatsheet.quiz.feature.interview.service.progress.BookmarkService;
import com.cheatsheet.quiz.feature.interview.service.progress.FavoriteService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use-case orchestration для API закладок (UI-014).
 *
 * <p>Переключение закладки синхронизирует флаг избранного ({@code is_important}),
 * чтобы фильтр «Избранные» и страница `/bookmarks` не расходились.</p>
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookmarkApiService {

    BookmarkService bookmarkService;
    FavoriteService favoriteService;
    QuestionRepository questionRepository;

    @Transactional
    public BookmarkResponse toggle(long questionId) {
        ensureQuestion(questionId);
        // FavoriteService уже создаёт/удаляет bookmark — единый путь.
        FavoriteService.FavoriteResult result = favoriteService.toggleFavorite(questionId);
        String note = bookmarkService.find(questionId).map(Bookmark::note).orElse(null);
        return new BookmarkResponse(questionId, result.favorite(), note);
    }

    @Transactional
    public BookmarkResponse saveNote(long questionId, String note) {
        ensureQuestion(questionId);
        bookmarkService.saveNote(questionId, note);
        if (!questionRepository.findById(questionId).map(q -> q.important()).orElse(false)) {
            questionRepository.updateImportant(questionId, true);
        }
        String saved = bookmarkService.find(questionId).map(Bookmark::note).orElse(null);
        return new BookmarkResponse(questionId, true, saved);
    }

    @Transactional
    public BookmarkResponse remove(long questionId) {
        ensureQuestion(questionId);
        bookmarkService.remove(questionId);
        questionRepository.updateImportant(questionId, false);
        return new BookmarkResponse(questionId, false, null);
    }

    public ResponseEntity<BookmarkResponse> toToggleHttpResponse(long questionId) {
        return ResponseEntity.ok(toggle(questionId));
    }

    public ResponseEntity<BookmarkResponse> toSaveNoteHttpResponse(long questionId, String note) {
        return ResponseEntity.ok(saveNote(questionId, note));
    }

    public ResponseEntity<BookmarkResponse> toRemoveHttpResponse(long questionId) {
        return ResponseEntity.ok(remove(questionId));
    }

    private void ensureQuestion(long questionId) {
        questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Вопрос не найден: id=" + questionId));
    }
}
