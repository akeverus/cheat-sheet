package com.cheatsheet.quiz.service;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.Question;
import com.cheatsheet.quiz.domain.QuestionType;
import com.cheatsheet.quiz.feature.interview.service.core.PreloadService;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import com.cheatsheet.quiz.infrastructure.diagram.DiagramService;
import com.cheatsheet.quiz.persistence.QuestionRepository;
import com.cheatsheet.quiz.service.ai.AiGenerationException;
import com.cheatsheet.quiz.service.ai.option.AIQuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PreloadServiceTest {

    @Mock QuestionRepository questionRepository;
    @Mock AIQuestionService optionGenerationService;
    @Mock DiagramService diagramService;
    @Mock TopicCatalogService topicCatalogService;

    private PreloadService preloadService;

    @BeforeEach
    void setUp() {
        AppProperties props = new AppProperties();
        props.getPreload().setBatchSize(2);
        props.getPreload().setSleepMs(0);
        props.getPreload().setMaxQueueSize(10);
        props.getPreload().setMaxQueues(5);
        // Включаем AI-режим, чтобы preloadNext/warmupAll не делали early return
        AppProperties.OpenAi openAi = new AppProperties.OpenAi();
        openAi.setApiKey("test-key");
        props.setOpenai(openAi);
        TaskExecutor directExecutor = Runnable::run;
        preloadService = new PreloadService(
                props,
                questionRepository,
                optionGenerationService,
                diagramService,
                topicCatalogService,
                directExecutor,
                directExecutor
        );
        lenient().when(optionGenerationService.getOrCreateOptions(any())).thenReturn(List.of());
    }

    @Test
    void preloadQuestionsUsesBatchLookupAndPreservesInputOrder() {
        List<Long> ids = List.of(3L, 1L);
        Question q1 = sampleQuestion(1L);
        Question q3 = sampleQuestion(3L);

        // Репозиторий может вернуть в любом порядке, сервис должен восстановить порядок ids.
        when(questionRepository.findByIds(ids)).thenReturn(List.of(q1, q3));

        preloadService.preloadQuestions(ids);

        verify(questionRepository).findByIds(ids);
        verify(optionGenerationService).getOrCreateOptions(q3);
        verify(optionGenerationService).getOrCreateOptions(q1);
        verify(questionRepository, never()).findById(anyLong());
    }

    @Test
    void preloadNextQueuesQuestionsAndAllowsPollingInOrder() {
        InterviewFilter filter = new InterviewFilter("topic", false, false, false);
        List<Long> ids = List.of(10L, 20L);
        Question q10 = sampleQuestion(10L);
        Question q20 = sampleQuestion(20L);

        when(questionRepository.findQuestionIdsForPreload("topic", false, false, 2))
                .thenReturn(ids);
        when(questionRepository.findByIds(ids)).thenReturn(List.of(q20, q10));

        preloadService.preloadNext(filter);

        Optional<Long> first = preloadService.pollPreloaded(filter);
        Optional<Long> second = preloadService.pollPreloaded(filter);

        assertThat(first).contains(10L);
        assertThat(second).contains(20L);
        verify(questionRepository).findByIds(ids);
        verify(optionGenerationService).getOrCreateOptions(q10);
        verify(optionGenerationService).getOrCreateOptions(q20);
        verify(questionRepository, never()).findById(anyLong());
    }

    @Test
    void preloadNextUsesTopicCatalogWhenGroupSelected() {
        InterviewFilter filter = new InterviewFilter(null, "languages", false, false, false, true);
        List<String> availableTopics = List.of("programming-languages/java/java-core-interview");
        List<String> selectedTopics = List.of("programming-languages/java/java-core-interview");
        List<Long> ids = List.of(55L);
        Question q55 = sampleQuestion(55L);

        when(questionRepository.findQuestionIdsForPreload(null, false, false, 2))
                .thenReturn(List.of());
        when(questionRepository.findTopics()).thenReturn(availableTopics);
        when(topicCatalogService.topicsForFilter(filter, availableTopics)).thenReturn(selectedTopics);
        when(questionRepository.findQuestionIdsForPreloadByTopics(selectedTopics, false, false, 2))
                .thenReturn(ids);
        when(questionRepository.findByIds(ids)).thenReturn(List.of(q55));

        preloadService.preloadNext(filter);

        assertThat(preloadService.pollPreloaded(filter)).contains(55L);
        verify(questionRepository).findQuestionIdsForPreloadByTopics(selectedTopics, false, false, 2);
    }

    @Test
    void preloadNextDoesNotRetryFailedQuestionAndContinuesBatch() {
        InterviewFilter filter = new InterviewFilter("topic", false, false, false);
        List<Long> ids = List.of(10L, 20L);
        Question q10 = sampleQuestion(10L);
        Question q20 = sampleQuestion(20L);

        when(questionRepository.findQuestionIdsForPreload("topic", false, false, 2))
                .thenReturn(ids);
        when(questionRepository.findByIds(ids)).thenReturn(List.of(q10, q20));

        org.mockito.Mockito.doThrow(new AiGenerationException("fail-once"))
                .when(optionGenerationService).getOrCreateOptions(q10);

        preloadService.preloadNext(filter);

        verify(optionGenerationService, times(1)).getOrCreateOptions(q10);
        verify(optionGenerationService, times(1)).getOrCreateOptions(q20);
        assertThat(preloadService.pollPreloaded(filter)).contains(20L);
        assertThat(preloadService.pollPreloaded(filter)).isEmpty();
    }

    @Test
    void warmupAllKeepsGoingWhenOneQuestionFails() {
        List<Long> ids = List.of(1L, 2L);
        Question q1 = sampleQuestion(1L);
        Question q2 = sampleQuestion(2L);

        when(questionRepository.findAllQuestionIdsWithoutOptions()).thenReturn(ids);
        when(questionRepository.findByIds(ids)).thenReturn(List.of(q1, q2));

        org.mockito.Mockito.doThrow(new AiGenerationException("always-fail"))
                .when(optionGenerationService).getOrCreateOptions(q1);

        preloadService.warmupAll();

        verify(optionGenerationService, times(1)).getOrCreateOptions(q1);
        verify(optionGenerationService, times(1)).getOrCreateOptions(q2);
        verify(diagramService, times(1)).getOrGenerateDiagram(q2);
        verify(diagramService, never()).getOrGenerateDiagram(q1);
    }

    @Test
    void warmupAllGeneratesDiagramsForSuccessfulQuestions() {
        List<Long> ids = List.of(1L, 2L);
        Question q1 = sampleQuestion(1L);
        Question q2 = sampleQuestion(2L);

        when(questionRepository.findAllQuestionIdsWithoutOptions()).thenReturn(ids);
        when(questionRepository.findByIds(ids)).thenReturn(List.of(q1, q2));
        org.mockito.Mockito.when(optionGenerationService.getOrCreateOptions(q1)).thenReturn(List.of());
        org.mockito.Mockito.when(optionGenerationService.getOrCreateOptions(q2)).thenReturn(List.of());

        preloadService.warmupAll();

        verify(optionGenerationService, times(1)).getOrCreateOptions(q1);
        verify(optionGenerationService, times(1)).getOrCreateOptions(q2);
        verify(diagramService, times(1)).getOrGenerateDiagram(q1);
        verify(diagramService, times(1)).getOrGenerateDiagram(q2);
    }

    @Test
    void warmupAllRespectsWarmupLimitInTestMode() {
        List<Long> allIds = List.of(1L, 2L, 3L);
        Question q1 = sampleQuestion(1L);
        Question q2 = sampleQuestion(2L);
        Question q3 = sampleQuestion(3L);
        long seed = 7L;
        List<Long> expectedSubset = shuffledSubset(allIds, 2, seed);

        AppProperties props = propsWithAiEnabled();
        props.getPreload().setBatchSize(2);
        props.getPreload().setSleepMs(0);
        props.getPreload().setMaxQueueSize(10);
        props.getPreload().setMaxQueues(5);
        props.getPreload().setWarmupLimit(2);
        props.getPreload().setWarmupRandomSeed(seed);
        TaskExecutor directExecutor = Runnable::run;
        preloadService = new PreloadService(
                props,
                questionRepository,
                optionGenerationService,
                diagramService,
                topicCatalogService,
                directExecutor,
                directExecutor
        );

        when(questionRepository.findAllQuestionIdsWithoutOptions()).thenReturn(allIds);
        when(questionRepository.findByIds(expectedSubset)).thenReturn(List.of(q1, q2, q3));

        preloadService.warmupAll();

        verify(questionRepository, times(1)).findByIds(expectedSubset);
        for (Long questionId : expectedSubset) {
            verify(optionGenerationService, times(1)).getOrCreateOptions(sampleQuestion(questionId));
        }
    }

    @Test
    void warmupAllUsesDefaultLimitWhenTestModeEnabledAndWarmupLimitNotSet() {
        List<Long> allIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L);
        long seed = 11L;
        List<Long> expectedSubset = shuffledSubset(allIds, 10, seed);
        List<Long> prefixSubset = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

        AppProperties props = propsWithAiEnabled();
        props.getPreload().setBatchSize(2);
        props.getPreload().setSleepMs(0);
        props.getPreload().setMaxQueueSize(10);
        props.getPreload().setMaxQueues(5);
        props.getPreload().setWarmupLimit(0);
        props.getPreload().setTestMode(true);
        props.getPreload().setWarmupRandomSeed(seed);
        TaskExecutor directExecutor = Runnable::run;
        preloadService = new PreloadService(
                props,
                questionRepository,
                optionGenerationService,
                diagramService,
                topicCatalogService,
                directExecutor,
                directExecutor
        );

        when(questionRepository.findAllQuestionIdsWithoutOptions()).thenReturn(allIds);
        when(questionRepository.findByIds(expectedSubset))
                .thenReturn(expectedSubset.stream().map(this::sampleQuestion).toList());

        preloadService.warmupAll();

        assertThat(expectedSubset).isNotEqualTo(prefixSubset);
        verify(questionRepository, times(1)).findByIds(expectedSubset);
        verify(questionRepository, never()).findByIds(allIds);
    }

    private static AppProperties propsWithAiEnabled() {
        AppProperties props = new AppProperties();
        AppProperties.OpenAi openAi = new AppProperties.OpenAi();
        openAi.setApiKey("test-key");
        props.setOpenai(openAi);
        return props;
    }

    private List<Long> shuffledSubset(List<Long> source, int limit, long seed) {
        List<Long> copy = new ArrayList<>(source);
        Collections.shuffle(copy, new Random(seed));
        return List.copyOf(copy.subList(0, limit));
    }

    private Question sampleQuestion(long id) {
        return new Question(
                id,
                "slug-" + id,
                "source-" + id,
                "topic.md",
                "topic",
                "Q" + id,
                "A" + id,
                false,
                "hash",
                QuestionType.TEXT,
                null,
                null,
                0,
                null
        );
    }
}
