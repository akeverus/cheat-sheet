package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.TopicStats;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import com.cheatsheet.quiz.feature.interview.service.page.StatsPageService;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.infrastructure.search.SearchService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.persistence.QuestionStatsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsPageServiceTest {

    @Mock
    InterviewFacade facade;
    @Mock
    QuestionRepository questionRepository;
    @Mock
    TopicCatalogService topicCatalogService;
    @Mock
    SearchService searchService;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    QuestionStatsRepository questionStatsRepository;

    StatsPageService service;

    @BeforeEach
    void setUp() {
        service = new StatsPageService(facade, questionRepository, topicCatalogService,
                searchService, objectMapper, questionStatsRepository);
    }

    @Test
    void fallsBackToEmptyJsonWhenSerializationFails() throws Exception {
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        List<TopicStats> topicStats = List.of(new TopicStats("java", 10, 4, 3, 6, 1, 2));
        when(facade.getStats(filter)).thenReturn(new InterviewStats(10, 5, 2, 7, 3));
        when(questionRepository.findTopics()).thenReturn(List.of("java"));
        when(topicCatalogService.normalizeGroup("backend")).thenReturn("backend");
        when(topicCatalogService.filterAndSortTopics(List.of("java"), "backend", true)).thenReturn(List.of("java"));
        when(topicCatalogService.groupOptions(List.of("java"))).thenReturn(List.of());
        when(facade.getTopicStats()).thenReturn(topicStats);
        when(searchService.search(eq("hashmap"), anyInt())).thenReturn(List.of());
        when(objectMapper.writeValueAsString(topicStats)).thenThrow(new JsonProcessingException("bad json") {});

        StatsPageService.StatsPageState state = service.build(filter, "hashmap", 20);

        assertThat(state.topicStatsJson()).isEqualTo("[]");
        assertThat(state.searchQuery()).isEqualTo("hashmap");
        assertThat(state.stats().correct()).isEqualTo(7);
    }
}
