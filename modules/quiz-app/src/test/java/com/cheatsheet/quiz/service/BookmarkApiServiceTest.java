package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.Bookmark;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.feature.interview.dto.response.progress.BookmarkResponse;
import com.cheatsheet.quiz.feature.interview.service.progress.BookmarkService;
import com.cheatsheet.quiz.feature.interview.service.progress.FavoriteService;
import com.cheatsheet.quiz.feature.interview.usecase.BookmarkApiService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookmarkApiServiceTest {

    @Mock
    private BookmarkService bookmarkService;
    @Mock
    private FavoriteService favoriteService;
    @Mock
    private QuestionRepository questionRepository;

    private BookmarkApiService service;

    @BeforeEach
    void setUp() {
        service = new BookmarkApiService(bookmarkService, favoriteService, questionRepository);
    }

    @Test
    void toggleDelegatesToFavoriteAndReturnsNote() {
        when(questionRepository.findById(7L)).thenReturn(Optional.of(question(7L, false)));
        when(favoriteService.toggleFavorite(7L)).thenReturn(new FavoriteService.FavoriteResult(7L, true));
        when(bookmarkService.find(7L)).thenReturn(Optional.of(new Bookmark(1L, 7L, "hint", 1L, 2L)));

        BookmarkResponse response = service.toggle(7L);

        assertThat(response.bookmarked()).isTrue();
        assertThat(response.note()).isEqualTo("hint");
        verify(favoriteService).toggleFavorite(7L);
    }

    @Test
    void saveNoteMarksImportantWhenNeeded() {
        when(questionRepository.findById(8L)).thenReturn(Optional.of(question(8L, false)));
        when(bookmarkService.find(8L)).thenReturn(Optional.of(new Bookmark(2L, 8L, "n", 1L, 2L)));

        BookmarkResponse response = service.saveNote(8L, "n");

        assertThat(response.bookmarked()).isTrue();
        assertThat(response.note()).isEqualTo("n");
        verify(bookmarkService).saveNote(8L, "n");
        verify(questionRepository).updateImportant(8L, true);
    }

    @Test
    void removeClearsBookmarkAndImportant() {
        when(questionRepository.findById(9L)).thenReturn(Optional.of(question(9L, true)));

        BookmarkResponse response = service.remove(9L);

        assertThat(response.bookmarked()).isFalse();
        assertThat(response.note()).isNull();
        verify(bookmarkService).remove(9L);
        verify(questionRepository).updateImportant(9L, false);
    }

    private static Question question(long id, boolean important) {
        return Question.builder()
                .id(id)
                .slug("t.md#Q" + id)
                .sourceSlug("t.md#Q" + id)
                .filePath("t.md")
                .topic("t")
                .questionText("Q" + id)
                .answerMarkdown("A" + id)
                .important(important)
                .sourceHash("h" + id)
                .questionType(QuestionType.TEXT)
                .codeSnippet(null)
                .diagramMermaid(null)
                .takeaway(null)
                .build();
    }
}
