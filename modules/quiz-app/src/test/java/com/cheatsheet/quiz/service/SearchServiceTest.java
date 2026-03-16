package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.infrastructure.render.MarkdownRenderService;
import com.cheatsheet.quiz.infrastructure.search.SearchService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.persistence.FullTextSearchRepository;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    FullTextSearchRepository fullTextSearchRepository;

    @Mock
    QuestionRepository questionRepository;

    @Mock
    AppProperties appProperties;

    @Mock
    MarkdownRenderService markdownRenderService;

    @InjectMocks
    SearchService searchService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        var search = new AppProperties.Search();
        search.setMaxLimit(100);
        lenient().when(appProperties.getSearch()).thenReturn(search);
        lenient().when(markdownRenderService.toPlainTextPreview(anyString())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void returnsEmptyForNullQuery() {
        assertThat(searchService.search(null, 10)).isEmpty();
        verify(fullTextSearchRepository, never()).search(anyString(), anyInt());
    }

    @Test
    void returnsEmptyForBlankQuery() {
        assertThat(searchService.search("  ", 10)).isEmpty();
    }

    @Test
    void capsLimitAtMaxLimit() {
        when(fullTextSearchRepository.search(anyString(), anyInt())).thenReturn(List.of());
        searchService.search("test", 999);
        verify(fullTextSearchRepository).search(eq("test"), eq(100));
    }

    @Test
    void usesBatchQueryInsteadOfNPlusOne() {
        var ftsResults = List.of(
                new FullTextSearchRepository.SearchResult(1L, "snippet1"),
                new FullTextSearchRepository.SearchResult(2L, "snippet2")
        );
        when(fullTextSearchRepository.search("test", 10)).thenReturn(ftsResults);
        when(questionRepository.findByIds(List.of(1L, 2L))).thenReturn(List.of(
                new Question(1L, "slug1", "slug1", "file1", "topic", "Q1?", "A1", false, "h1", QuestionType.TEXT, null, null, 0, null),
                new Question(2L, "slug2", "slug2", "file2", "topic", "Q2?", "A2", false, "h2", QuestionType.TEXT, null, null, 0, null)
        ));

        var results = searchService.search("test", 10);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).questionId()).isEqualTo(1L);
        assertThat(results.get(0).questionText()).isEqualTo("Q1?");
        assertThat(results.get(1).questionId()).isEqualTo(2L);
        assertThat(results.get(1).snippet()).isEqualTo("snippet2");
        assertThat(results.get(1).answerPreview()).isEqualTo("A2");
        // Verify findByIds was used (batch) instead of N individual findById calls
        verify(questionRepository).findByIds(anyList());
    }
}
