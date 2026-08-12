package com.cheatsheet.quiz.api.mapper.view;

import com.cheatsheet.quiz.common.util.ModeUtils;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.InterviewQuestion;
import com.cheatsheet.quiz.domain.InterviewStats;
import com.cheatsheet.quiz.domain.ReviewReason;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.feature.interview.service.page.AnswerPageService;
import com.cheatsheet.quiz.feature.interview.service.page.FocusTrainingPageService;
import com.cheatsheet.quiz.feature.interview.service.page.StatsPageService;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.cheatsheet.quiz.TestQuestionBuilder.aQuestion;
import static org.assertj.core.api.Assertions.assertThat;

class MvcModelAttributeMapperTest {

    // Настройки настоящие, а не мок: из них берутся значения по умолчанию для
    // числа вопросов в списке режимов, и мок молча отдавал бы null там, где
    // продакшен-код рассчитывает на дефолты из application.yml.
    private final AppProperties appProperties = new AppProperties();
    // Каталог настоящий, а не мок: секции тем считаются по реальному
    // topic-groups.yaml, и мок скрыл бы расхождение модели с конфигом.
    private final MvcModelAttributeMapper mapper = new MvcModelAttributeMapper(
            appProperties, new ModeUtils(), new TopicCatalogService(appProperties));

    /**
     * Обвязка для тестов, которые проверяют производные атрибуты режима: сама
     * статистика им безразлична, важен только mode. Глобальная и отфильтрованная
     * статистики намеренно одна и та же пустая — расхождение знаменателей
     * проверяется отдельно, во фрагментных тестах виджета «Сегодня».
     */
    private static FocusTrainingPageService.SurfaceState emptySurface(InterviewMode mode) {
        InterviewStats empty = new InterviewStats(0, 0, 0, 0, 0, 0);
        return FocusTrainingPageService.SurfaceState.builder()
                .stats(empty)
                .filteredStats(empty)
                .topics(List.of())
                .groups(List.of())
                .filter(new InterviewFilter(null, null, false, false, false, true))
                .mode(mode)
                .build();
    }

    @Test
    void appliesFocusPageStateAndReviewFlag() {
        ExtendedModelMap model = new ExtendedModelMap();
        InterviewFilter filter = new InterviewFilter("java", "backend", false, false, false, true);
        InterviewStats stats = new InterviewStats(10, 5, 2, 2, 7, 3);
        FocusTrainingPageService.SurfaceState surfaceState = FocusTrainingPageService.SurfaceState.builder()
                .stats(stats)
                .filteredStats(stats)
                .topics(List.of("java"))
                .groups(List.of())
                .selectedGroup("backend")
                .filter(filter)
                .mode(InterviewMode.TRAINING)
                .build();
        ReviewReason reviewReason = new ReviewReason(ReviewReason.Type.OVERDUE, "Повторение просрочено на 3 дня");
        FocusTrainingPageService.FocusPageState pageState = new FocusTrainingPageService.FocusPageState(
                Optional.empty(),
                false,
                false,
                false,
                null,
                null,
                null,
                33.3,
                surfaceState,
                reviewReason
        );

        mapper.applyFocusPageState(model, pageState, true);

        assertThat(model.getAttribute("reviewMode")).isEqualTo(true);
        assertThat(model.getAttribute("progressPercent")).isEqualTo(33.3);
        assertThat(model.getAttribute("stats")).isEqualTo(surfaceState.stats());
        assertThat(model.getAttribute("reviewReason")).isEqualTo(reviewReason);
        assertThat(model.getAttribute("focusModeChipText")).isEqualTo("Повтор ошибок");
        assertThat(model.getAttribute("focusModeHintText")).isEqualTo("Повтор ошибок: отвечай на вопросы, где раньше были ошибки.");
        assertThat(model.getAttribute("focusEmptyRetryHref")).isEqualTo("/review");
        assertThat(model.getAttribute("focusEmptyRetryText")).isEqualTo("Обновить повтор ошибок");
    }

    /**
     * Browse-флешкарта — единственная ветка шаблона без формы ответа и без кнопок
     * само-оценки: без этой ссылки экран после раскрытия ответа становится тупиком.
     * Ссылка обязана нести весь текущий фильтр (иначе «дальше» уводит из выборки)
     * и excludeQuestionId (иначе следующий вопрос — тот же самый).
     */
    @Test
    void buildsBrowseNextHrefFromFilterAndCurrentQuestion() {
        ExtendedModelMap model = new ExtendedModelMap();
        InterviewFilter filter = new InterviewFilter("java/core", "backend", true, false, false, false);
        InterviewStats browseStats = new InterviewStats(10, 5, 2, 2, 7, 3);
        FocusTrainingPageService.SurfaceState surfaceState = FocusTrainingPageService.SurfaceState.builder()
                .stats(browseStats)
                .filteredStats(browseStats)
                .topics(List.of("java/core"))
                .groups(List.of())
                .selectedGroup("backend")
                .filter(filter)
                .mode(InterviewMode.FLASHCARD)
                .weakTopics(true)
                .build();
        InterviewQuestion current = new InterviewQuestion(
                aQuestion().withId(42).withTopic("java/core").build(), List.of(), null);
        FocusTrainingPageService.FocusPageState pageState = new FocusTrainingPageService.FocusPageState(
                Optional.of(current), false, true, false, null, null, null, 0.0, surfaceState, null);

        mapper.applyFocusPageState(model, pageState, false);

        assertThat((String) model.getAttribute("focusBrowseNextHref"))
                .startsWith("/?")
                .contains("topic=java/core")
                .contains("group=backend")
                .contains("important=true")
                .contains("ordered=false")
                .contains("weakTopics=true")
                .contains("mode=FLASHCARD")
                .contains("excludeQuestionId=42")
                .doesNotContain("onlyWrong")
                .doesNotContain("shuffle");
    }

    @Test
    void omitsReviewReasonAttributeWhenNull() {
        ExtendedModelMap model = new ExtendedModelMap();
        FocusTrainingPageService.FocusPageState pageState = new FocusTrainingPageService.FocusPageState(
                Optional.empty(),
                false,
                false,
                false,
                null,
                null,
                null,
                0.0,
                emptySurface(InterviewMode.TRAINING),
                null
        );

        mapper.applyFocusPageState(model, pageState, false);

        assertThat(model.getAttribute("reviewReason")).isNull();
    }

    @Test
    void appliesFocusModeDerivedAttributesForFlashcardAndStudy() {
        FocusTrainingPageService.FocusPageState flashcardState = new FocusTrainingPageService.FocusPageState(
                Optional.empty(),
                false,
                true,
                false,
                null,
                null,
                null,
                0.0,
                emptySurface(InterviewMode.FLASHCARD),
                null
        );
        FocusTrainingPageService.FocusPageState studyState = flashcardState.toBuilder()
                .surface(flashcardState.surface().toBuilder().mode(InterviewMode.STUDY).build())
                .flashcardMode(false)
                .studyLearnPhase(true)
                .build();

        ExtendedModelMap flashcardModel = new ExtendedModelMap();
        mapper.applyFocusPageState(flashcardModel, flashcardState, false);
        assertThat(flashcardModel.getAttribute("focusModeChipText")).isEqualTo("Флешкарты");
        assertThat(flashcardModel.getAttribute("focusModeHintText"))
                .isEqualTo("Флешкарты: сначала вспомни ответ, затем раскрой и оцени себя.");
        assertThat(flashcardModel.getAttribute("focusEmptyRetryHref")).isEqualTo("/?mode=FLASHCARD");
        assertThat(flashcardModel.getAttribute("focusEmptyRetryText")).isEqualTo("Обновить флешкарты");
        // Подпись над формой раскрытия считает сервер: в шаблоне она была
        // четырьмя взаимоисключающими th:if по имени константы, и переименование
        // режима оставило бы карточку вообще без подписи.
        assertThat(flashcardModel.getAttribute("recallBadgeText"))
                .isEqualTo("Флешкарта — вспомни ответ до раскрытия");

        ExtendedModelMap studyModel = new ExtendedModelMap();
        mapper.applyFocusPageState(studyModel, studyState, false);
        assertThat(studyModel.getAttribute("focusModeChipText")).isEqualTo("Изучение");
        assertThat(studyModel.getAttribute("focusModeHintText"))
                .isEqualTo("Изучение: разберись с материалом, затем переходи к проверке.");
        assertThat(studyModel.getAttribute("focusEmptyRetryHref")).isEqualTo("/?mode=STUDY");
        assertThat(studyModel.getAttribute("focusEmptyRetryText")).isEqualTo("Обновить изучение");

        // STUDY QUIZ-под-фаза (после /study-confirm): studyLearnPhase=false → чип и
        // подсказка отражают проверку, а не изучение (иначе над MCQ висит инструкция
        // «разберись с материалом», предлагая сделать уже сделанное).
        FocusTrainingPageService.FocusPageState studyQuizState = studyState.toBuilder()
                .studyLearnPhase(false)
                .build();
        ExtendedModelMap studyQuizModel = new ExtendedModelMap();
        mapper.applyFocusPageState(studyQuizModel, studyQuizState, false);
        assertThat(studyQuizModel.getAttribute("focusModeChipText")).isEqualTo("Проверка");
        assertThat(studyQuizModel.getAttribute("focusModeHintText"))
                .isEqualTo("Проверь себя: выбери верный вариант — разбор появится после ответа.");
    }

    @Test
    void appliesFocusModeDerivedAttributesForTypedRecallModes() {
        FocusTrainingPageService.FocusPageState typedRecallState = new FocusTrainingPageService.FocusPageState(
                Optional.empty(),
                false,
                true,
                false,
                true,
                true,
                false,
                null,
                null,
                null,
                0.0,
                emptySurface(InterviewMode.TYPED_RECALL),
                null,
                List.of(),
                null
        ,
                com.cheatsheet.quiz.domain.TopicMap.EMPTY);

        ExtendedModelMap model = new ExtendedModelMap();
        mapper.applyFocusPageState(model, typedRecallState, false);

        assertThat(model.getAttribute("sessionRecallMode")).isEqualTo(true);
        assertThat(model.getAttribute("typedInputMode")).isEqualTo(true);
        assertThat(model.getAttribute("codeChallengeMode")).isEqualTo(false);
        assertThat(model.getAttribute("focusModeChipText")).isEqualTo("Печатный ответ");
        assertThat(model.getAttribute("focusModeHintText"))
                .isEqualTo("Печатный ответ: напиши ответ, затем раскрой эталон и оцени себя.");
        assertThat(model.getAttribute("focusEmptyRetryHref")).isEqualTo("/?mode=TYPED_RECALL");
        assertThat(model.getAttribute("focusEmptyRetryText")).isEqualTo("Обновить печатный ответ");
        assertThat(model.getAttribute("recallBadgeText"))
                .isEqualTo("Печатный ответ — напиши ответ, затем проверь себя");
        assertThat(model.getAttribute("typedAnswerLabel")).isEqualTo("Твой ответ");

        // INTERVIEW отличается от TYPED_RECALL только копирайтом: и подпись формы,
        // и бейдж обязаны это различать, иначе развёрнутый устный ответ выглядит
        // как поле для короткого печатного.
        ExtendedModelMap interviewModel = new ExtendedModelMap();
        mapper.applyFocusPageState(interviewModel, typedRecallState.toBuilder()
                .surface(typedRecallState.surface().toBuilder().mode(InterviewMode.INTERVIEW).build())
                .build(), false);
        assertThat(interviewModel.getAttribute("typedAnswerLabel")).isEqualTo("Заметки к ответу");
        assertThat(interviewModel.getAttribute("recallBadgeText"))
                .isEqualTo("Интервью — устный/развёрнутый ответ (можно записать заметки)");

        // CODE_CHALLENGE — третья ветка той же подписи.
        ExtendedModelMap codeModel = new ExtendedModelMap();
        mapper.applyFocusPageState(codeModel, typedRecallState.toBuilder()
                .surface(typedRecallState.surface().toBuilder().mode(InterviewMode.CODE_CHALLENGE).build())
                .codeChallengeMode(true)
                .build(), false);
        assertThat(codeModel.getAttribute("typedAnswerLabel")).isEqualTo("Твой код");
        assertThat(codeModel.getAttribute("recallBadgeText"))
                .isEqualTo("Код-челлендж — напиши решение, затем сверь с эталоном");
    }

    /**
     * Список режимов приходит из модели готовым: подписи, кнопка старта и число
     * вопросов по умолчанию считаются в {@link ModeUtils}, а не переписываются
     * восемью {@code <option>} в разметке — именно расхождение таких копий однажды
     * показало пользователю «MARATHON» вместо «Интенсив».
     */
    @Test
    void surfaceStateCarriesTheWholeModeListForTheSelect() {
        ExtendedModelMap model = new ExtendedModelMap();
        mapper.applySurfaceState(model, emptySurface(InterviewMode.EXAM));

        @SuppressWarnings("unchecked")
        List<ModeUtils.ModeOption> modes = (List<ModeUtils.ModeOption>) model.getAttribute("sessionModes");
        assertThat(modes).hasSize(InterviewMode.values().length);
        assertThat(modes).filteredOn(ModeUtils.ModeOption::selected)
                .describedAs("выбранным помечен ровно один режим — тот, что в состоянии")
                .extracting(ModeUtils.ModeOption::name)
                .containsExactly("EXAM");
        assertThat(modes).filteredOn(m -> "TRAINING".equals(m.name()))
                .describedAs("бесконечный режим прячет поле «Вопросов» — признак приходит с сервера")
                .extracting(ModeUtils.ModeOption::unlimited)
                .containsExactly(true);
        assertThat(modes).extracting(ModeUtils.ModeOption::startAction).doesNotContainNull();
    }

    @Test
    void modeUtilsDisplayNamesForStage6Modes() {
        ModeUtils modeUtils = new ModeUtils();
        assertThat(modeUtils.displayName(InterviewMode.TYPED_RECALL)).isEqualTo("Печатный ответ");
        assertThat(modeUtils.displayName(InterviewMode.INTERVIEW)).isEqualTo("Интервью");
        assertThat(modeUtils.displayName(InterviewMode.CODE_CHALLENGE)).isEqualTo("Код-челлендж");
    }

    @Test
    void flashcardChipAndHintWhenQuestionHasNoOptionsInTrainingMode() {
        // Вопрос без вариантов форсит флешкарту даже при session-mode=TRAINING.
        // Чип/подсказка обязаны отражать флешкарту, а не «Выбери один вариант».
        FocusTrainingPageService.FocusPageState trainingFlashcard = new FocusTrainingPageService.FocusPageState(
                Optional.empty(),
                false,
                true,
                false,
                null,
                null,
                null,
                0.0,
                emptySurface(InterviewMode.TRAINING),
                null
        );

        ExtendedModelMap model = new ExtendedModelMap();
        mapper.applyFocusPageState(model, trainingFlashcard, false);

        assertThat(model.getAttribute("focusModeChipText")).isEqualTo("Флешкарты");
        assertThat(model.getAttribute("focusModeHintText"))
                .isEqualTo("Флешкарты: сначала вспомни ответ, затем раскрой и оцени себя.");
    }

    @Test
    void appliesAnswerAndStatsAndSummaryStates() {
        ExtendedModelMap model = new ExtendedModelMap();
        AnswerPageService.AnswerPageState answerState = new AnswerPageService.AnswerPageState(
                null, "<p>ok</p>", "FULL", new InterviewStats(1, 1, 0, 0, 1, 0),
                List.of("java"), List.of(), "backend", new InterviewFilter("java", "backend", false, false, false, true),
                InterviewMode.TRAINING, null, null, List.of(), null, 10L
        );
        StatsPageService.StatsPageState statsState = new StatsPageService.StatsPageState(
                new InterviewStats(2, 1, 0, 1, 1, 1), List.of("java"), List.of(), "backend",
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
        assertThat(model.getAttribute("xpAwarded")).isEqualTo(10L);
        assertThat(model.getAttribute("topicStatsJson")).isEqualTo("[]");
        assertThat(model.getAttribute("summary")).isEqualTo(summary);
    }

    /**
     * Селекты тем на /settings и /stats рендерят {@code <optgroup>} из
     * {@code topicSections}, а плоский {@code topics} остаётся для мест, где
     * нужен просто перечень. Две модели одного списка обязаны совпадать по
     * составу: расхождение означало бы, что тема есть в фильтре, но её нельзя
     * выбрать (или наоборот). Поэтому проверяется не наличие атрибута, а
     * равенство множеств и каталожный порядок секций.
     */
    @Test
    void publishesTopicSectionsCoveringEveryTopicInCatalogOrder() {
        ExtendedModelMap model = new ExtendedModelMap();
        // Порядок входа намеренно перемешан относительно каталога, а последняя
        // тема не подпадает ни под одно правило — она обязана уехать в «Прочее»,
        // а не потеряться.
        List<String> topics = List.of(
                "databases/postgres",
                "algorithms/sorting",
                "frameworks/spring/spring-boot",
                "zzz-unknown/topic"
        );
        StatsPageService.StatsPageState statsState = new StatsPageService.StatsPageState(
                new InterviewStats(2, 1, 0, 1, 1, 1), topics, List.of(), null,
                new InterviewFilter(null, null, false, false, false, true),
                null, List.of(), List.of(), "[]"
        );

        mapper.applyStatsPageState(model, statsState);

        @SuppressWarnings("unchecked")
        List<TopicCatalogService.TopicSection> sections =
                (List<TopicCatalogService.TopicSection>) model.getAttribute("topicSections");
        assertThat(sections).isNotNull();
        assertThat(sections.stream().flatMap(section -> section.topics().stream()))
                .containsExactlyInAnyOrderElementsOf(topics);
        assertThat(sections).extracting(TopicCatalogService.TopicSection::title)
                .containsExactly("Основы", "Spring-стек", "Базы данных и хранилища", "Прочее");
    }
}
