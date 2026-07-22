package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.common.util.TopicUtils;
import com.cheatsheet.quiz.domain.Bookmark;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.feature.interview.service.page.BookmarksPageService;
import com.cheatsheet.quiz.feature.interview.service.progress.BookmarkService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookmarksPageServiceTest {

    @Mock
    private BookmarkService bookmarkService;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private TopicUtils topicUtils;

    private BookmarksPageService service;

    @BeforeEach
    void setUp() {
        service = new BookmarksPageService(bookmarkService, questionRepository, topicUtils);
    }

    @Test
    void buildReturnsEnrichedItemsWithoutSeedImportantSync() {
        when(bookmarkService.list()).thenReturn(List.of(
                new Bookmark(1L, 5L, "повтор hash", 10L, 20L)
        ));
        when(questionRepository.findByIds(List.of(5L))).thenReturn(List.of(question(5L, "java-collections")));
        when(topicUtils.displayName("java-collections")).thenReturn("Java Collections");

        BookmarksPageService.BookmarksPageState state = service.build();

        assertThat(state.total()).isEqualTo(1L);
        assertThat(state.items()).hasSize(1);
        BookmarksPageService.BookmarkItem item = state.items().get(0);
        assertThat(item.questionId()).isEqualTo(5L);
        assertThat(item.topic()).isEqualTo("java-collections");
        assertThat(item.topicDisplayName()).isEqualTo("Java Collections");
        assertThat(item.note()).isEqualTo("повтор hash");
        assertThat(item.questionPreview()).contains("Question 5");
    }

    @Test
    void buildReturnsEmptyWhenNoBookmarks() {
        when(bookmarkService.list()).thenReturn(List.of());

        BookmarksPageService.BookmarksPageState state = service.build();

        assertThat(state.items()).isEmpty();
        assertThat(state.total()).isZero();
    }

    private static Question question(long id, String topic) {
        return new Question(
                id,
                topic + ".md#Q" + id,
                topic + ".md#Q" + id,
                topic + ".md",
                topic,
                "Question " + id + " about maps?",
                "Answer " + id,
                true,
                "hash-" + id,
                QuestionType.TEXT,
                null,
                null,
                0,
                null
        );
    }
}
