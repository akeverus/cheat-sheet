package com.cheatsheet.quiz.feature.interview.service.progress;

import com.cheatsheet.quiz.domain.Bookmark;
import com.cheatsheet.quiz.persistence.BookmarkRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

/**
 * Закладки и заметки к вопросам (UI-014, хендофф-3).
 *
 * <p>Тонкая обёртка над {@link BookmarkRepository}: переключение закладки,
 * сохранение заметки, список. Одна закладка на вопрос.</p>
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookmarkService {

    BookmarkRepository bookmarkRepository;
    Clock clock;

    /** Все закладки, новые первыми. */
    public List<Bookmark> list() {
        return bookmarkRepository.findAll();
    }

    /** Закладка вопроса, если есть. */
    public Optional<Bookmark> find(long questionId) {
        return bookmarkRepository.findByQuestionId(questionId);
    }

    /** Есть ли закладка на вопрос. */
    public boolean isBookmarked(long questionId) {
        return bookmarkRepository.findByQuestionId(questionId).isPresent();
    }

    /** Число закладок (для бейджей навигации). */
    public long count() {
        return bookmarkRepository.count();
    }

    /**
     * Переключает закладку: если её нет — создаёт, если есть — удаляет.
     * @return {@code true} если вопрос теперь в закладках, {@code false} если убран
     */
    @Transactional
    public boolean toggle(long questionId) {
        if (bookmarkRepository.findByQuestionId(questionId).isPresent()) {
            bookmarkRepository.delete(questionId);
            return false;
        }
        bookmarkRepository.upsert(questionId, null, clock.instant().getEpochSecond());
        return true;
    }

    /**
     * Сохраняет/обновляет заметку. Создаёт закладку, если её ещё нет (сохранение
     * заметки подразумевает интерес к вопросу).
     */
    @Transactional
    public void saveNote(long questionId, String note) {
        String normalized = note == null || note.isBlank() ? null : note.strip();
        bookmarkRepository.upsert(questionId, normalized, clock.instant().getEpochSecond());
    }

    /** Убирает закладку вопроса. */
    @Transactional
    public void remove(long questionId) {
        bookmarkRepository.delete(questionId);
    }
}
