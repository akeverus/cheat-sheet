package com.cheatsheet.quiz.feature.interview.service.progress;

import com.cheatsheet.quiz.domain.Bookmark;
import com.cheatsheet.quiz.persistence.BookmarkRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    /** Максимальная длина заметки (символы после strip). */
    public static final int MAX_NOTE_LENGTH = 2000;

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
     * Гарантирует наличие закладки без перезаписи заметки.
     * Используется при синхронизации со звездой «избранное».
     */
    @Transactional
    public void ensure(long questionId) {
        if (bookmarkRepository.findByQuestionId(questionId).isPresent()) {
            return;
        }
        bookmarkRepository.upsert(questionId, null, clock.instant().getEpochSecond());
    }

    /**
     * Создаёт отсутствующие закладки для уже помеченных избранным вопросов
     * (миграция UI-014: избранное жило в {@code is_important}, заметки — в {@code bookmark}).
     *
     * @return сколько новых закладок создано
     */
    @Transactional
    public int ensureMissing(List<Long> questionIds) {
        if (questionIds == null || questionIds.isEmpty()) {
            return 0;
        }
        Set<Long> existing = new HashSet<>();
        for (Bookmark bookmark : bookmarkRepository.findAll()) {
            existing.add(bookmark.questionId());
        }
        int created = 0;
        long now = clock.instant().getEpochSecond();
        for (Long questionId : questionIds) {
            if (questionId == null || existing.contains(questionId)) {
                continue;
            }
            bookmarkRepository.upsert(questionId, null, now);
            existing.add(questionId);
            created++;
        }
        return created;
    }

    /**
     * Сохраняет/обновляет заметку. Создаёт закладку, если её ещё нет (сохранение
     * заметки подразумевает интерес к вопросу).
     */
    @Transactional
    public void saveNote(long questionId, String note) {
        String normalized = normalizeNote(note);
        bookmarkRepository.upsert(questionId, normalized, clock.instant().getEpochSecond());
    }

    /** Убирает закладку вопроса. */
    @Transactional
    public void remove(long questionId) {
        bookmarkRepository.delete(questionId);
    }

    static String normalizeNote(String note) {
        if (note == null || note.isBlank()) {
            return null;
        }
        String stripped = note.strip();
        if (stripped.length() > MAX_NOTE_LENGTH) {
            return stripped.substring(0, MAX_NOTE_LENGTH);
        }
        return stripped;
    }
}
