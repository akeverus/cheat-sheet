package com.cheatsheet.quiz.api.mapper.view;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.feature.interview.service.page.AnswerPageService;
import com.cheatsheet.quiz.feature.interview.service.page.FocusTrainingPageService;
import com.cheatsheet.quiz.feature.interview.service.page.StatsPageService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MvcModelAttributeMapperTest {

    private final AppProperties appProperties = mock(AppProperties.class);
    private final MvcModelAttributeMapper mapper = new MvcModelAttributeMapper(appProperties);

    @Test
    void appliesFocusPageStateAndReviewFlag() {
        ExtendedModelMap model = new ExtendedModelMap();
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        FocusTrainingPageService.SurfaceState surfaceState = new FocusTrainingPageService.SurfaceState(
                new InterviewStats(10, 5, 2, 7, 3),
                List.of("java"),
                List.of(),
                "backend",
                filter,
                InterviewMode.TRAINING,
                null,
                false
        );
        FocusTrainingPageService.FocusPageState pageState = new FocusTrainingPageService.FocusPageState(
                Optional.empty(),
                false,
                false,
                false,
                false,
                null,
                null,
                null,
                33.3,
                surfaceState
        );

        mapper.applyFocusPageState(model, pageState, true);

        assertThat(model.getAttribute("reviewMode")).isEqualTo(true);
        assertThat(model.getAttribute("progressPercent")).isEqualTo(33.3);
        assertThat(model.getAttribute("stats")).isEqualTo(surfaceState.stats());
        assertThat(model.getAttribute("focusModeChipText")).isEqualTo("Review mode");
        assertThat(model.getAttribute("focusModeHintText")).isEqualTo("Режим review: отвечай на вопросы с ошибками.");
        assertThat(model.getAttribute("focusEmptyRetryHref")).isEqualTo("/review");
        assertThat(model.getAttribute("focusEmptyRetryText")).isEqualTo("Обновить review");
    }

    @Test
    void appliesFocusModeDerivedAttributesForFlashcardAndStudy() {
        FocusTrainingPageService.FocusPageState flashcardState = new FocusTrainingPageService.FocusPageState(
                Optional.empty(),
                false,
                false,
                true,
                false,
                null,
                null,
                null,
                0.0,
                new FocusTrainingPageService.SurfaceState(
                        new InterviewStats(0, 0, 0, 0, 0),
                        List.of(),
                        List.of(),
                        null,
                        new InterviewFilter(null, null, false, false, false, true),
                        InterviewMode.FLASHCARD,
                        null,
                        false
                )
        );
        FocusTrainingPageService.FocusPageState studyState = flashcardState.toBuilder()
                .surface(flashcardState.surface().toBuilder().mode(InterviewMode.STUDY).build())
                .flashcardMode(false)
                .studyLearnPhase(true)
                .build();

        ExtendedModelMap flashcardModel = new ExtendedModelMap();
        mapper.applyFocusPageState(flashcardModel, flashcardState, false);
        assertThat(flashcardModel.getAttribute("focusModeChipText")).isEqualTo("Flashcard mode");
        assertThat(flashcardModel.getAttribute("focusModeHintText"))
                .isEqualTo("Флешкарты: сначала вспомни ответ, затем раскрой и оцени себя.");
        assertThat(flashcardModel.getAttribute("focusEmptyRetryHref")).isEqualTo("/training?mode=FLASHCARD");
        assertThat(flashcardModel.getAttribute("focusEmptyRetryText")).isEqualTo("Обновить флешкарты");

        ExtendedModelMap studyModel = new ExtendedModelMap();
        mapper.applyFocusPageState(studyModel, studyState, false);
        assertThat(studyModel.getAttribute("focusModeChipText")).isEqualTo("Study mode");
        assertThat(studyModel.getAttribute("focusModeHintText"))
                .isEqualTo("Изучение: разберись с материалом, затем переходи к проверке.");
        assertThat(studyModel.getAttribute("focusEmptyRetryHref")).isEqualTo("/training?mode=STUDY");
        assertThat(studyModel.getAttribute("focusEmptyRetryText")).isEqualTo("Обновить изучение");
    }

    @Test
    void appliesAnswerAndStatsAndSummaryStates() {
        ExtendedModelMap model = new ExtendedModelMap();
        AnswerPageService.AnswerPageState answerState = new AnswerPageService.AnswerPageState(
                null, "<p>ok</p>", "FULL", new InterviewStats(1, 1, 0, 1, 0),
                List.of("java"), List.of(), "backend", new InterviewFilter("java", "backend", false, false, false, true),
                InterviewMode.TRAINING, null, null, List.of()
        );
        StatsPageService.StatsPageState statsState = new StatsPageService.StatsPageState(
                new InterviewStats(2, 1, 1, 1, 1), List.of("java"), List.of(), "backend",
                new InterviewFilter("java", "backend", false, false, false, true),
                "hashmap", List.of(), List.of(), "[]"
        );
        SessionSummary summary = SessionSummary.builder()
                .mode(InterviewMode.EXAM)
                .duration(Duration.ofMinutes(1))
                .totalQuestions(2)
                .correctCount(1)
                .wrongCount(1)
                .build();

        mapper.applyAnswerPageState(model, answerState);
        mapper.applyStatsPageState(model, statsState);
        mapper.applySessionSummary(model, summary);

        assertThat(model.getAttribute("answerHtml")).isEqualTo("<p>ok</p>");
        assertThat(model.getAttribute("topicStatsJson")).isEqualTo("[]");
        assertThat(model.getAttribute("summary")).isEqualTo(summary);
    }
}
