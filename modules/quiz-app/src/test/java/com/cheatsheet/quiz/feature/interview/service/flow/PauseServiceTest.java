package com.cheatsheet.quiz.feature.interview.service.flow;

import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.domain.PausedSessionInfo;
import com.cheatsheet.quiz.persistence.PausedSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PauseServiceTest {

    private static final Instant NOW = Instant.parse("2026-07-15T10:15:30Z");

    @Mock
    private PausedSessionRepository repository;

    private PauseService service;

    @BeforeEach
    void setUp() {
        service = new PauseService(repository, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    private InterviewSession sessionWithOneAnswered() {
        InterviewSession session = InterviewSession.builder()
                .mode(InterviewMode.EXAM)
                .questionIds(List.of(1L, 2L, 3L))
                .topic("java")
                .ordered(true)
                .build();
        session.registerAnswer(true, "java"); // index → 1, correct → 1
        return session;
    }

    @Test
    void pauseSerializesSessionAndSavesMetadata() {
        InterviewSession session = sessionWithOneAnswered();

        service.pause(session);

        ArgumentCaptor<byte[]> blobCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(repository).save(blobCaptor.capture(), eq(InterviewMode.EXAM), eq("java"),
                eq(3), eq(1), eq(NOW));
        assertThat(blobCaptor.getValue()).isNotEmpty();
    }

    @Test
    void pauseIgnoresNullSession() {
        service.pause(null);
        verify(repository, never()).save(any(), any(), any(), anyInt(), anyInt(), any());
    }

    @Test
    void pauseIgnoresFinishedSession() {
        InterviewSession session = InterviewSession.builder()
                .mode(InterviewMode.EXAM)
                .questionIds(List.of(1L))
                .topic("java")
                .ordered(true)
                .build();
        session.registerAnswer(true, "java"); // index → 1 == size → finished

        service.pause(session);

        verify(repository, never()).save(any(), any(), any(), anyInt(), anyInt(), any());
    }

    @Test
    void resumeDeserializesRoundTripsStateAndClears() {
        // Сериализуем реальную сессию через pause и перехватываем блоб.
        InterviewSession original = sessionWithOneAnswered();
        service.pause(original);
        ArgumentCaptor<byte[]> blobCaptor = ArgumentCaptor.forClass(byte[].class);
        verify(repository).save(blobCaptor.capture(), any(), any(), anyInt(), anyInt(), any());
        byte[] blob = blobCaptor.getValue();

        when(repository.findBlob()).thenReturn(Optional.of(blob));

        Optional<InterviewSession> resumed = service.resume();

        assertThat(resumed).isPresent();
        InterviewSession session = resumed.get();
        assertThat(session.getMode()).isEqualTo(InterviewMode.EXAM);
        assertThat(session.getTopic()).isEqualTo("java");
        assertThat(session.getTotal()).isEqualTo(3);
        assertThat(session.getIndex()).isEqualTo(1);
        assertThat(session.getCorrect()).isEqualTo(1);
        assertThat(session.getAnswerHistory()).hasSize(1);
        verify(repository).clear(); // одноразовое потребление
    }

    @Test
    void resumeReturnsEmptyAndDoesNotClearWhenNoPause() {
        when(repository.findBlob()).thenReturn(Optional.empty());

        assertThat(service.resume()).isEmpty();

        verify(repository, never()).clear();
    }

    @Test
    void resumeDiscardsCorruptBlob() {
        when(repository.findBlob()).thenReturn(Optional.of(new byte[]{1, 2, 3, 4})); // не Java-объект

        assertThat(service.resume()).isEmpty();

        verify(repository).clear(); // битый блоб отбрасывается
    }

    @Test
    void pausedInfoDelegatesToRepository() {
        PausedSessionInfo info = new PausedSessionInfo(InterviewMode.STUDY, "kafka", 20, 7, NOW);
        when(repository.findInfo()).thenReturn(Optional.of(info));

        assertThat(service.pausedInfo()).contains(info);
    }

    @Test
    void discardDelegatesToClear() {
        service.discard();
        verify(repository).clear();
    }
}
