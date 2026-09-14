package com.cheatsheet.quiz.feature.interview.service.flow;

import com.cheatsheet.quiz.TestQuestionBuilder;
import com.cheatsheet.quiz.api.mapper.view.SessionLog;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.domain.InterviewSession;
import com.cheatsheet.quiz.feature.interview.service.facade.InterviewFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

/**
 * Самооценка режимов припоминания попадает В СЕССИЮ, а не только в планировщик.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер на живом приложении (Chromium, код-челлендж, три отвеченных вопроса с оценками
 * «отлично» и «не помню»): рельс показывал «4/20», а экран итогов —
 *
 * <pre>
 *   «Сессия завершена без ответов»
 *   «Засчитывать нечего: ни опыта, ни ошибок для разбора, ни данных по темам»
 * </pre>
 *
 * Причина была одна строкой: оценку применял {@code advanceFlashcard()}, который двигал
 * индекс и НЕ трогал ни {@code correct}/{@code wrong}, ни историю ответов. Под это
 * попадали четыре режима из восьми — флешкарта, печатный пересказ, интервью,
 * код-челлендж, то есть всё семейство {@code isRecallMode()}.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>оценка увеличивает счётчики сессии и пишет запись в историю;</li>
 *   <li>верность берётся из той же таблицы, что и SM-2-оценка;</li>
 *   <li>тема попадает в запись — на ней держится разбор по темам в итогах;</li>
 *   <li>пропавший вопрос не отменяет запись об ответе.</li>
 * </ol>
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RecallSessionRecordingTest {

    @Mock
    private InterviewFacade facade;

    @Mock
    private SessionSummaryService sessionSummaryService;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private SessionFlowService flow;

    private InterviewSession recallSession(InterviewMode mode) {
        return new InterviewSession(mode, List.of(10L, 11L, 12L), null, null, null, null, Instant.now());
    }

    private void questionExists(long id, String topic) {
        lenient().when(facade.questionById(id)).thenReturn(Optional.of(InterviewQuestion.builder()
                .question(TestQuestionBuilder.aQuestion().withId(id).withTopic(topic).build())
                .options(List.of())
                .build()));
    }

    @Test
    void gradingARecallAnswerCountsItInTheSession() {
        for (InterviewMode mode : InterviewMode.values()) {
            if (!mode.isRecallMode()) {
                continue;
            }
            InterviewSession session = recallSession(mode);
            questionExists(10L, "java/collections-interview.md");
            questionExists(11L, "spring/di-interview.md");

            assertThat(flow.applyFlashcardReveal(session)).isTrue();
            assertThat(flow.applyFlashcardGrade(session, 10L, 4)).isTrue();
            assertThat(flow.applyFlashcardReveal(session)).isTrue();
            assertThat(flow.applyFlashcardGrade(session, 11L, 1)).isTrue();

            assertThat(session.getIndex()).as("сессия продвигается, как и раньше: " + mode).isEqualTo(2);
            assertThat(session.getCorrect()).as("оценка «отлично» — верный ответ: " + mode).isEqualTo(1);
            assertThat(session.getWrong()).as("оценка «не помню» — ошибка: " + mode).isEqualTo(1);
            assertThat(session.getAnswerHistory())
                    .as("без истории экран итогов пуст при любом числе вопросов: " + mode)
                    .hasSize(2);
            assertThat(session.getAnswerHistory().get(0).topic())
                    .as("тема нужна разбору по темам в итогах: " + mode)
                    .isEqualTo("java/collections-interview.md");
            assertThat(session.getAnswerHistory().get(1).correct()).isFalse();
        }
    }

    @Test
    void aMissingQuestionDoesNotCancelTheAnswer() {
        InterviewSession session = recallSession(InterviewMode.INTERVIEW);
        lenient().when(facade.questionById(10L)).thenReturn(Optional.empty());

        assertThat(flow.applyFlashcardReveal(session)).isTrue();
        assertThat(flow.applyFlashcardGrade(session, 10L, 3)).isTrue();

        assertThat(session.getAnswerHistory()).hasSize(1);
        assertThat(session.getAnswerHistory().get(0).topic())
                .as("темы нет — запись всё равно есть: счётчики и журнал важнее разбора по темам")
                .isNull();
        assertThat(session.getCorrect()).isEqualTo(1);
    }

    /**
     * Журнал хода сессии — представление той же истории: свежие первыми, длинный
     * хвост свёрнут в две цифры. Предел показа тут не «красивое число», а высота
     * пустого хвоста колонки задания (замер 2560×1440: 492px при строке 28px).
     */
    @Test
    void theLogShowsFreshEntriesFirstAndFoldsTheTail() {
        InterviewSession session = new InterviewSession(
                InterviewMode.EXAM, List.of(1L, 2L, 3L, 4L, 5L), null, null, null, null, Instant.now());
        session.registerAnswer(true, "java/a-interview.md");
        session.registerAnswer(false, "java/b-interview.md");
        session.registerAnswer(true, "java/c-interview.md");

        SessionLog log = SessionLog.of(session.getAnswerHistory(), 2);

        assertThat(log.present()).isTrue();
        assertThat(log.entries()).hasSize(2);
        assertThat(log.entries().get(0).number()).as("свежая запись — первая").isEqualTo(3);
        assertThat(log.entries().get(0).topic()).isEqualTo("java/c-interview.md");
        assertThat(log.entries().get(1).number()).isEqualTo(2);
        assertThat(log.truncated()).isTrue();
        assertThat(log.hiddenCorrect()).isEqualTo(1);
        assertThat(log.hiddenWrong()).isZero();

        SessionLog whole = SessionLog.of(session.getAnswerHistory(), 10);
        assertThat(whole.truncated()).as("влезло всё — хвоста нет").isFalse();
        assertThat(whole.entries()).hasSize(3);

        assertThat(SessionLog.of(List.of(), 10).present())
                .as("пустая история — блока нет вовсе, а не пустая рамка")
                .isFalse();
        assertThat(SessionLog.of(null, 10)).isSameAs(SessionLog.EMPTY);
    }
}
