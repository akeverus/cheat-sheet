package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Bookmark;
import com.cheatsheet.quiz.feature.interview.service.progress.BookmarkService;
import com.cheatsheet.quiz.persistence.BookmarkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookmarkServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.ofEpochSecond(1_700_000_000L), ZoneOffset.UTC);

    @Mock
    private BookmarkRepository bookmarkRepository;

    private BookmarkService service;

    @BeforeEach
    void setUp() {
        service = new BookmarkService(bookmarkRepository, CLOCK);
    }

    @Test
    void toggleCreatesBookmarkWhenMissing() {
        when(bookmarkRepository.findByQuestionId(10L)).thenReturn(Optional.empty());

        assertThat(service.toggle(10L)).isTrue();
        verify(bookmarkRepository).upsert(eq(10L), isNull(), eq(1_700_000_000L));
    }

    @Test
    void toggleRemovesExistingBookmark() {
        when(bookmarkRepository.findByQuestionId(11L))
                .thenReturn(Optional.of(new Bookmark(1L, 11L, "note", 1L, 1L)));

        assertThat(service.toggle(11L)).isFalse();
        verify(bookmarkRepository).delete(11L);
    }

    @Test
    void saveNoteTruncatesLongNotes() {
        String longNote = "x".repeat(BookmarkService.MAX_NOTE_LENGTH + 50);
        service.saveNote(12L, longNote);
        verify(bookmarkRepository).upsert(
                eq(12L),
                eq("x".repeat(BookmarkService.MAX_NOTE_LENGTH)),
                eq(1_700_000_000L));
    }

    @Test
    void ensureMissingCreatesOnlyAbsentRows() {
        when(bookmarkRepository.findAll()).thenReturn(List.of(new Bookmark(1L, 1L, null, 1L, 1L)));

        int created = service.ensureMissing(List.of(1L, 2L, 3L));

        assertThat(created).isEqualTo(2);
        verify(bookmarkRepository).upsert(eq(2L), isNull(), eq(1_700_000_000L));
        verify(bookmarkRepository).upsert(eq(3L), isNull(), eq(1_700_000_000L));
    }
}
