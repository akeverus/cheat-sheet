package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.SessionRecord;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.feature.interview.service.flow.SessionHistoryService;
import com.cheatsheet.quiz.persistence.SessionHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Журнал занятий: что записывается, что не записывается и что переживает вкладку.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Сводка сессии жила ТОЛЬКО в {@code HttpSession}: экран итогов читал её один раз и терял
 * при следующем старте, перезагрузке или закрытии вкладки — проверено на живом приложении.
 * Значит вопрос «когда я занимался и как шло» продукт не отвечал нигде: график точности по
 * дням показывает ответы, но не сессии, их режимы и длительность.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>завершённая сессия попадает в историю;</li>
 *   <li>сессия без единого ответа — НЕ попадает: «0 из 0» выглядит как занятие, но им не было;</li>
 *   <li>отказ записи не роняет завершение сессии;</li>
 *   <li>журнал на странице показывает пустое состояние, а не пустую таблицу.</li>
 * </ol>
 */
class SessionHistoryContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-08-13T00:00:00Z"), ZoneOffset.UTC);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private static SessionSummary summary(int correct, int wrong, int unknown) {
        return SessionSummary.builder()
                .totalQuestions(correct + wrong + unknown)
                .correctCount(correct)
                .wrongCount(wrong)
                .unknownCount(unknown)
                .duration(Duration.ofMinutes(6))
                .completedAt(Instant.parse("2026-08-12T21:03:00Z"))
                .mode(InterviewMode.EXAM)
                .build();
    }

    @Test
    void finishedSessionLandsInHistoryButEmptyOneDoesNot() {
        SessionHistoryRepository repository = mock(SessionHistoryRepository.class);
        SessionHistoryService service = new SessionHistoryService(repository, CLOCK);

        service.record(summary(1, 2, 0));
        verify(repository).save(any(SessionRecord.class));

        service.record(summary(0, 0, 0));
        service.record(null);
        // Один вызов на три обращения: пустая сессия и null не пишутся.
        verify(repository).save(any(SessionRecord.class));
    }

    /**
     * Человек только что закончил работу. Потеря строки в журнале — меньшая беда, чем 500
     * вместо экрана итогов, поэтому ошибка записи гасится и логируется.
     */
    @Test
    void aFailingWriteDoesNotBreakFinishing() {
        SessionHistoryRepository repository = mock(SessionHistoryRepository.class);
        doThrow(new RuntimeException("db down")).when(repository).save(any());
        SessionHistoryService service = new SessionHistoryService(repository, CLOCK);

        service.record(summary(3, 1, 0));

        SessionHistoryRepository broken = mock(SessionHistoryRepository.class);
        doThrow(new RuntimeException("db down")).when(broken).findRecent(anyLimit());
        assertThat(new SessionHistoryService(broken, CLOCK).recent()).isEmpty();
        verify(broken, never()).save(any());
    }

    private static int anyLimit() {
        return SessionHistoryService.RECENT_LIMIT;
    }

    /** Точность считается по ОТВЕЧЕННЫМ, а не по запланированному размеру сессии. */
    @Test
    void accuracyCountsAnsweredNotPlanned() {
        SessionRecord halfDone = new SessionRecord(1L, Instant.now(CLOCK), InterviewMode.EXAM,
                20, 3, 1, 0, Duration.ofMinutes(4), 42);
        assertThat(halfDone.answeredCount()).isEqualTo(4);
        assertThat(halfDone.accuracy()).isEqualTo(75.0);
        assertThat(halfDone.hasAccuracy()).isTrue();
        assertThat(halfDone.compactDuration()).isEqualTo("4:00");

        SessionRecord nothing = new SessionRecord(2L, Instant.now(CLOCK), InterviewMode.TRAINING,
                5, 0, 0, 0, Duration.ZERO, 0);
        assertThat(nothing.hasAccuracy()).as("отвечать было не на что — процента нет").isFalse();
        assertThat(nothing.accuracy()).isNegative();

        SessionRecord longOne = new SessionRecord(3L, Instant.now(CLOCK), InterviewMode.MARATHON,
                200, 100, 20, 5, Duration.ofSeconds(3725), 900);
        assertThat(longOne.compactDuration()).as("часы показываются, а не сворачиваются в минуты")
                .isEqualTo("1:02:05");
    }

    /** У журнала есть пустое состояние: пустая таблица — это не ответ, а её отсутствие. */
    @Test
    void theJournalHasAnEmptyStateAndLivesInDynamics() throws IOException {
        String markup = COMMENT.matcher(read("templates/stats.html")).replaceAll("\n");

        assertThat(markup).as("журнал занятий исчез со страницы аналитики")
                .contains("id=\"session-history-title\"");
        assertThat(markup).as("у журнала нет пустого состояния — на чистой базе будет пустая таблица")
                .contains("Журнал пуст");

        List<String> panels = new ArrayList<>();
        java.util.regex.Matcher panel =
                Pattern.compile("id=\"stats-panel-([a-z-]+)\"").matcher(markup);
        while (panel.find()) {
            panels.add(panel.group(1));
        }
        assertThat(panels).as("вкладок аналитики стало больше: журнал обязан жить в «Динамике», "
                        + "а не отдельной вкладкой ради одной таблицы")
                .containsExactly("topics", "dynamics", "errors");

        int dynamics = markup.indexOf("id=\"stats-panel-dynamics\"");
        int errors = markup.indexOf("id=\"stats-panel-errors\"");
        int journal = markup.indexOf("id=\"session-history-title\"");
        assertThat(journal).as("журнал уехал из панели «Динамика»").isBetween(dynamics, errors);
    }
}
