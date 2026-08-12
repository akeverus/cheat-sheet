package com.cheatsheet.quiz.api.mapper.view;

import com.cheatsheet.quiz.common.util.ModeUtils;
import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.domain.InterviewFilter;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.QuestionIssueCategory;
import com.cheatsheet.quiz.domain.SessionSummary;
import com.cheatsheet.quiz.infrastructure.render.AnswerHtmlSplitter;
import com.cheatsheet.quiz.feature.interview.service.copy.AnswerCopy;
import com.cheatsheet.quiz.feature.interview.service.page.AnswerPageService;
import com.cheatsheet.quiz.feature.interview.service.page.BookmarksPageService;
import com.cheatsheet.quiz.feature.interview.service.page.FocusTrainingPageService;
import com.cheatsheet.quiz.feature.interview.service.page.StatsPageService;
import com.cheatsheet.quiz.feature.interview.service.topic.TopicCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Централизованный mapper заполнения MVC-модели атрибутами.
 *
 * <p>Убирает дублирование {@code model.addAttribute(...)} из контроллеров
 * и фиксирует единый контракт атрибутов для шаблонов.</p>
 */
@Component
@RequiredArgsConstructor
public class MvcModelAttributeMapper {

    private final AppProperties appProperties;
    private final ModeUtils modeUtils;
    private final TopicCatalogService topicCatalogService;

    /**
     * Кладёт список тем в модель дважды: плоским (совместимость с местами, где
     * нужен просто перечень) и разбитым на секции групп — из него рендерится
     * {@code <optgroup>} в селектах тем на /settings и /stats.
     *
     * <p>Секции считаются здесь, а не в page-сервисах: разбиение чисто
     * презентационное и одинаково для всех трёх экранов, а состояния страниц
     * несут доменный список.
     */
    private void applyTopics(Model model, java.util.List<String> topics) {
        model.addAttribute("topics", topics);
        model.addAttribute("topicSections", topicCatalogService.groupTopics(topics));
    }

    /**
     * Маппит общий surface-state (фильтры, топики, статистика) в модель.
     */
    public void applySurfaceState(Model model, FocusTrainingPageService.SurfaceState surfaceState) {
        model.addAttribute("stats", surfaceState.stats());
        model.addAttribute("metrics", surfaceState.metrics());
        model.addAttribute("filteredStats", surfaceState.filteredStats());
        applyTopics(model, surfaceState.topics());
        model.addAttribute("groups", surfaceState.groups());
        model.addAttribute("selectedGroup", surfaceState.selectedGroup());
        model.addAttribute("filter", surfaceState.filter());
        model.addAttribute("mode", surfaceState.mode());
        model.addAttribute("interviewSession", surfaceState.interviewSession());
        model.addAttribute("weakTopics", surfaceState.weakTopics());
        model.addAttribute("filterFavoritesCount", surfaceState.filterFavoritesCount());
        model.addAttribute("filterWrongCount", surfaceState.filterWrongCount());
        model.addAttribute("filterWeakTopicsCount", surfaceState.filterWeakTopicsCount());
        model.addAttribute("sessionModes", modeUtils.options(
                surfaceState.mode(),
                appProperties.getInterview().getDefaultExamCount(),
                appProperties.getInterview().getDefaultMarathonCount()));
    }

    /**
     * Маппит state главной страницы фокус-тренировки.
     */
    public void applyFocusPageState(Model model, FocusTrainingPageService.FocusPageState pageState, boolean reviewMode) {
        applySurfaceState(model, pageState.surface());
        model.addAttribute("current", pageState.current().orElse(null));
        model.addAttribute("studyLearnPhase", pageState.studyLearnPhase());
        model.addAttribute("flashcardMode", pageState.flashcardMode());
        model.addAttribute("flashcardRevealed", pageState.flashcardRevealed());
        model.addAttribute("sessionRecallMode", pageState.sessionRecallMode());
        model.addAttribute("typedInputMode", pageState.typedInputMode());
        model.addAttribute("codeChallengeMode", pageState.codeChallengeMode());
        if (pageState.diagram() != null) {
            model.addAttribute("diagram", pageState.diagram());
        }
        if (pageState.studyAnswerHtml() != null) {
            model.addAttribute("studyAnswerHtml", pageState.studyAnswerHtml());
        }
        if (pageState.difficulty() != null) {
            model.addAttribute("difficulty", pageState.difficulty());
        }
        if (pageState.reviewReason() != null) {
            model.addAttribute("reviewReason", pageState.reviewReason());
        }
        model.addAttribute("contentBlocks", pageState.contentBlocks() != null
                ? pageState.contentBlocks() : java.util.List.of());
        // Вкладки «Пример кода / Схема» рендерятся из этих двух блоков. Шаблон
        // раньше вычислял их сам из codeSnippet/diagramMermaid — и не знал, что в
        // codeSnippet может лежать нарисованная схема, а не код.
        model.addAttribute("codeBlock", pageState.codeBlock());
        model.addAttribute("diagramBlock", pageState.diagramBlock());
        model.addAttribute("questionRevisionId", pageState.questionRevisionId());
        model.addAttribute("progressPercent", pageState.progressPercent());
        model.addAttribute("reviewMode", reviewMode);
        // Lazy Mermaid: load vendor only when the current question has a diagram.
        boolean needsMermaid = pageState.diagram() != null && !pageState.diagram().isBlank();
        model.addAttribute("needsMermaid", needsMermaid);
        InterviewMode mode = pageState.surface().mode();
        // flashcardMode — фактическое состояние UI: вопрос без вариантов форсит
        // флешкарту при любом session-mode. Чип/подсказка должны отражать именно
        // его, иначе над флешкардой висело «Выбери один вариант» (MCQ-копирайт).
        boolean flashcard = pageState.flashcardMode();
        // STUDY проходит две под-фазы: LEARN (читаем эталон) и QUIZ (отвечаем на
        // MCQ после /study-confirm). Чип/подсказка обязаны отражать под-фазу —
        // иначе на этапе проверки над вариантами висит «Изучение: разберись…»,
        // инструктируя сделать уже сделанное (та же MCQ-копирайт-рассинхронизация,
        // что чинилась для флешкарты выше).
        boolean studyLearnPhase = pageState.studyLearnPhase();
        model.addAttribute("focusModeChipText", resolveFocusModeChipText(reviewMode, mode, flashcard, studyLearnPhase));
        model.addAttribute("focusModeHintText", resolveFocusModeHintText(reviewMode, mode, flashcard, studyLearnPhase));
        model.addAttribute("recallBadgeText", resolveRecallBadgeText(mode, pageState.typedInputMode(), pageState.codeChallengeMode()));
        model.addAttribute("typedAnswerLabel", resolveTypedAnswerLabel(mode, pageState.codeChallengeMode()));
        // Опора рабочего места — свойство режима (ModeUtils#workspaceChecklist), а не
        // текст в шаблоне: три печатных режима отличаются именно методом работы.
        model.addAttribute("workspaceChecklist", modeUtils.workspaceChecklist(mode));
        // Категории жалобы рисует <template> в разметке, а не таблица в modals.js:
        // подпись — свойство константы, и вторая её копия в JS расходилась бы молча
        // (fromString снисходителен, неизвестное имя уезжает в OTHER).
        model.addAttribute("reportCategories", QuestionIssueCategory.values());
        model.addAttribute("focusEmptyRetryHref", resolveFocusEmptyRetryHref(reviewMode, mode));
        model.addAttribute("focusEmptyRetryText", resolveFocusEmptyRetryText(reviewMode, mode));
        model.addAttribute("focusBrowseNextHref", resolveBrowseNextHref(pageState, reviewMode));
    }

    /**
     * Адрес «следующего вопроса» для browse-флешкарты (флешкарта вне recall-сессии).
     *
     * <p>В этой ветке шаблона нет ни формы ответа, ни кнопок само-оценки: ответ
     * раскрывается нативным {@code <details>}, и после раскрытия экран не давал
     * НИ ОДНОГО пути дальше — тупик, из которого можно было выйти только через
     * сайдбар. MCQ-ветка такой путь имеет (interview.js рисует «Следующий вопрос»
     * после разбора), но она JS-овая и к флешкарте отношения не имеет.</p>
     *
     * <p>Ссылка серверная (работает без JS) и повторяет контракт
     * {@code buildNextQuestionHref} из interview.js: та же поверхность, тот же
     * фильтр, тот же {@code excludeQuestionId} — чтобы не выдать тот же вопрос
     * снова.</p>
     */
    private String resolveBrowseNextHref(FocusTrainingPageService.FocusPageState pageState, boolean reviewMode) {
        FocusTrainingPageService.SurfaceState surface = pageState.surface();
        UriComponentsBuilder uri = UriComponentsBuilder.fromPath(reviewMode ? "/review" : "/");
        InterviewFilter filter = surface.filter();
        if (filter != null) {
            if (filter.topic() != null) {
                uri.queryParam("topic", filter.topic());
            }
            if (filter.group() != null) {
                uri.queryParam("group", filter.group());
            }
            if (Boolean.TRUE.equals(filter.importantOnly())) {
                uri.queryParam("important", "true");
            }
            if (Boolean.TRUE.equals(filter.onlyWrong())) {
                uri.queryParam("onlyWrong", "true");
            }
            if (filter.isShuffled()) {
                uri.queryParam("shuffle", "true");
            }
            if (!filter.isOrdered()) {
                uri.queryParam("ordered", "false");
            }
        }
        if (surface.weakTopics()) {
            uri.queryParam("weakTopics", "true");
        }
        if (surface.mode() != null) {
            uri.queryParam("mode", surface.mode().name());
        }
        pageState.current().ifPresent(current -> uri.queryParam("excludeQuestionId", current.question().id()));
        return uri.build().encode().toUriString();
    }

    /**
     * Подпись над формой recall-фазы («Флешкарта — вспомни ответ до раскрытия»).
     *
     * <p>Раньше это были четыре {@code <div th:if>} в {@code focus-training.html},
     * два из них — со сравнением {@code mode.name() == 'INTERVIEW'}/{@code 'TYPED_RECALL'}.
     * Переименование константы не ломало сборку: ветка просто переставала выбираться,
     * и над формой развёрнутого ответа не оставалось ни одной подписи — из четырёх
     * взаимоисключающих условий не срабатывало ни одно.
     */
    private String resolveRecallBadgeText(InterviewMode mode, boolean typedInput, boolean codeChallenge) {
        if (!typedInput) {
            return "Флешкарта — вспомни ответ до раскрытия";
        }
        if (codeChallenge) {
            return "Код-челлендж — напиши решение, затем сверь с эталоном";
        }
        if (mode == InterviewMode.INTERVIEW) {
            return "Интервью — устный/развёрнутый ответ (можно записать заметки)";
        }
        return "Печатный ответ — напиши ответ, затем проверь себя";
    }

    /**
     * Подпись поля ввода в recall-фазе: код, заметки или обычный ответ.
     */
    private String resolveTypedAnswerLabel(InterviewMode mode, boolean codeChallenge) {
        if (codeChallenge) {
            return "Твой код";
        }
        return mode == InterviewMode.INTERVIEW ? "Заметки к ответу" : "Твой ответ";
    }

    /**
     * Текст eyebrow-чипа фокус-страницы. Раньше возвращал англицизмы
     * («Focus mode»/«Study mode»/…) в полностью русском UI и не различал режимы:
     * EXAM/MARATHON/TRAINING все показывали «Focus mode» (критика round-01
     * C-live-3). Теперь — русское имя реального режима из {@link ModeUtils}
     * (единый словарь, как на /settings и в итогах), с приоритетом
     * review/flashcard над session-mode (flashcard форсится при вопросе без
     * вариантов независимо от режима сессии).
     */
    private String resolveFocusModeChipText(boolean reviewMode, InterviewMode mode, boolean flashcard, boolean studyLearnPhase) {
        if (reviewMode) {
            return "Повтор ошибок";
        }
        if (mode != null && mode.usesTypedInput()) {
            return modeUtils.displayName(mode);
        }
        if (flashcard || mode == InterviewMode.FLASHCARD) {
            return modeUtils.displayName(InterviewMode.FLASHCARD);
        }
        if (mode == null) {
            return modeUtils.displayName(InterviewMode.TRAINING);
        }
        // STUDY в QUIZ-под-фазе (эталон изучен, идёт проверка вариантами) — чип
        // отражает шаг проверки, а не «Изучение» (mode-display), которое течёт из
        // общего fallthrough и лжёт про текущую стадию.
        if (mode == InterviewMode.STUDY && !studyLearnPhase) {
            return "Проверка";
        }
        return modeUtils.displayName(mode);
    }

    private String resolveFocusModeHintText(boolean reviewMode, InterviewMode mode, boolean flashcard, boolean studyLearnPhase) {
        if (reviewMode) {
            return "Повтор ошибок: отвечай на вопросы, где раньше были ошибки.";
        }
        if (mode == InterviewMode.TYPED_RECALL) {
            return "Печатный ответ: напиши ответ, затем раскрой эталон и оцени себя.";
        }
        if (mode == InterviewMode.INTERVIEW) {
            return "Интервью: сформулируй развёрнутый ответ (можно записать заметки), затем сверь с эталоном.";
        }
        if (mode == InterviewMode.CODE_CHALLENGE) {
            return "Код-челлендж: напиши решение, затем раскрой эталон и оцени себя.";
        }
        if (flashcard || mode == InterviewMode.FLASHCARD) {
            return "Флешкарты: сначала вспомни ответ, затем раскрой и оцени себя.";
        }
        if (mode == InterviewMode.STUDY) {
            // Под-фаза: LEARN направляет к изучению, QUIZ — к выбору варианта.
            return studyLearnPhase
                    ? "Изучение: разберись с материалом, затем переходи к проверке."
                    : "Проверь себя: выбери верный вариант — разбор появится после ответа.";
        }
        return "Выбери один вариант. Проверка и разбор идут по шагам.";
    }

    /** Канонический адрес тренировки — «/»; {@code /training} оставлен только как редирект. */
    private String resolveFocusEmptyRetryHref(boolean reviewMode, InterviewMode mode) {
        if (reviewMode) {
            return "/review";
        }
        if (mode != null && mode.isRecallMode()) {
            return "/?mode=" + mode.name();
        }
        if (mode == InterviewMode.STUDY) {
            return "/?mode=STUDY";
        }
        return "/";
    }

    private String resolveFocusEmptyRetryText(boolean reviewMode, InterviewMode mode) {
        if (reviewMode) {
            return "Обновить повтор ошибок";
        }
        if (mode == InterviewMode.FLASHCARD) {
            return "Обновить флешкарты";
        }
        if (mode == InterviewMode.TYPED_RECALL) {
            return "Обновить печатный ответ";
        }
        if (mode == InterviewMode.INTERVIEW) {
            return "Обновить интервью";
        }
        if (mode == InterviewMode.CODE_CHALLENGE) {
            return "Обновить код-челлендж";
        }
        if (mode == InterviewMode.STUDY) {
            return "Обновить изучение";
        }
        return "Обновить тренировку";
    }

    /**
     * Маппит state страницы результата ответа.
     */
    public void applyAnswerPageState(Model model, AnswerPageService.AnswerPageState state) {
        model.addAttribute("result", state.result());
        model.addAttribute("answerHtml", state.answerHtml());
        // UX-13: краткий лид (первый абзац разбора) видим под вердиктом, остаток —
        // под «Подробнее». Деривация та же, что в API-пути (AnswerApiService) —
        // общий AnswerHtmlSplitter, чтобы JS- и no-JS-разбор совпадали. В проде
        // result на странице ответа всегда есть; null-guard — на случай контрактных
        // юнит-тестов маппера с «пустым» state.
        if (state.result() != null) {
            AnswerHtmlSplitter.Split answerSplit = AnswerHtmlSplitter.split(state.answerHtml());
            // Хендофф-3, Этап 4: distractor-панель («Почему другие варианты не подходят»)
            // показываем, только если хотя бы у одного НЕверного варианта есть пояснение.
            // Флаг из Java, а не Thymeleaf-проекции (ссылка на correct.id внутри ?[] —
            // хрупкий скоуп элемента).
            long correctOptionId = state.result().correct() != null ? state.result().correct().id() : -1L;
            boolean anyDistractorExplanation = state.result().options().stream()
                    .anyMatch(o -> o.id() != correctOptionId && o.explanation() != null && !o.explanation().isBlank());
            model.addAttribute("answerLeadHtml", answerSplit.leadHtml());
            model.addAttribute("answerRestHtml", answerSplit.restHtml());
            model.addAttribute("hasDistractorAnalysis", anyDistractorExplanation);
            // Готовая строка вердикта, а не тройной тернарник в th:text: формулировка
            // — это копирайт, у неё есть автор и её читают вслух скринридером;
            // собирать её в шаблоне значит прятать текст продукта в разметку.
            // Слово в слово то же, что печатает JS-путь: строку собирает общий
            // AnswerCopy, а не два разных места.
            model.addAttribute("verdictText", AnswerCopy.verdict(state.result()));
        }
        model.addAttribute("answerDisplayMode", state.answerDisplayMode());
        model.addAttribute("stats", state.stats());
        applyTopics(model, state.topics());
        model.addAttribute("groups", state.groups());
        model.addAttribute("selectedGroup", state.selectedGroup());
        model.addAttribute("filter", state.filter());
        model.addAttribute("mode", state.mode());
        model.addAttribute("interviewSession", state.interviewSession());
        if (state.diagram() != null) {
            model.addAttribute("diagram", state.diagram());
        }
        boolean needsMermaid = state.diagram() != null && !state.diagram().isBlank();
        if (!needsMermaid && state.answerHtml() != null) {
            needsMermaid = state.answerHtml().contains("class=\"mermaid\"");
        }
        model.addAttribute("needsMermaid", needsMermaid);
        model.addAttribute("relatedQuestions", state.relatedQuestions());
        model.addAttribute("aiEnabled", false);
        model.addAttribute("reviewState", state.reviewState());
        model.addAttribute("xpAwarded", state.xpAwarded());
        model.addAttribute("nextReviewText",
                AnswerCopy.nextReview(state.reviewState() != null ? state.reviewState().intervalDays() : 0));
    }

    /**
     * Маппит state страницы статистики.
     */
    public void applyStatsPageState(Model model, StatsPageService.StatsPageState state) {
        model.addAttribute("stats", state.stats());
        applyTopics(model, state.topics());
        model.addAttribute("groups", state.groups());
        model.addAttribute("selectedGroup", state.selectedGroup());
        model.addAttribute("filter", state.filter());
        model.addAttribute("searchQuery", state.searchQuery());
        model.addAttribute("searchResults", state.searchResults());
        model.addAttribute("topicStats", state.topicStats());
        // Таблица тем — отдельный атрибут, а не срез `topicStats`: по полному списку
        // считаются графики и сводки над таблицей, и урезав его до 12 строк, страница
        // объявила бы 12 тем вместо 319 всюду, где ссылается на размер выборки.
        model.addAttribute("topicTable", state.topicTable());
        model.addAttribute("topicStatsJson", state.topicStatsJson());
        model.addAttribute("coverageGaps", state.coverageGaps());
        model.addAttribute("reviewForecast", state.reviewForecast());
        model.addAttribute("metrics", state.metrics());
        model.addAttribute("difficultyDistribution", state.difficultyDistribution());
        model.addAttribute("difficultyJson", state.difficultyJson());
        model.addAttribute("metricDefinitions", state.metricDefinitions());
        model.addAttribute("weakestTopic", state.weakestTopic());
        model.addAttribute("weakTopicCount", state.weakTopicCount());
        model.addAttribute("recentWrongQuestions", state.recentWrongQuestions());
        model.addAttribute("accuracyTrend", state.accuracyTrend());
        model.addAttribute("accuracyTrendJson", state.accuracyTrendJson());
        model.addAttribute("groupCatalog", state.groupCatalog());
        model.addAttribute("progression", state.progression());
        // Передаём max(count) уже вычисленный — Thymeleaf 3.1 запрещает
        // T(java.util.Collections) в SpEL, считать в шаблоне нельзя.
        long maxForecast = state.reviewForecast() == null ? 1L
                : state.reviewForecast().stream()
                        .mapToLong(com.cheatsheet.quiz.persistence.QuestionStatsRepository.ForecastDay::count)
                        .max().orElse(1L);
        model.addAttribute("reviewForecastMax", Math.max(maxForecast, 1L));
        int maxTrendAnswered = state.accuracyTrend() == null ? 1
                : state.accuracyTrend().stream()
                        .mapToInt(com.cheatsheet.quiz.feature.interview.service.page.StatsPageService.AccuracyTrendDay::answered)
                        .max().orElse(1);
        model.addAttribute("accuracyTrendMaxAnswered", Math.max(maxTrendAnswered, 1));
    }

    /**
     * Маппит state страницы закладок (UI-014).
     */
    public void applyBookmarksPageState(Model model, BookmarksPageService.BookmarksPageState state) {
        model.addAttribute("bookmarks", state.items());
        model.addAttribute("bookmarksTotal", state.total());
        // `bookmarksShown` отдельно от `bookmarksTotal`: без отбора числа равны, а
        // при отборе шапка обязана называть оба — «14 из 28». Одно число здесь
        // было бы неправдой в обе стороны: total выдал бы за показанное то, чего
        // на экране нет, shown — скрыл бы, что коллекция больше выборки.
        model.addAttribute("bookmarksShown", state.shown());
        model.addAttribute("bookmarkGroups", state.groups());
        model.addAttribute("bookmarksFilterActive", state.filtered());
        model.addAttribute("bookmarksQuery", state.query().q());
        model.addAttribute("bookmarksGroup", state.query().group());
        model.addAttribute("bookmarksNote", state.query().note());
        model.addAttribute("bookmarksSort", state.query().sort());
    }

    /**
     * Маппит summary завершенной сессии.
     *
     * <p>Рядом кладётся {@code progression} — то же «место в пути», что показывает
     * аналитика. Собирается из уже посчитанной геймификации сессии, а не запросом:
     * уровень, опыт внутри уровня и цена уровня в {@code Gamification} уже есть, и
     * второй источник тех же чисел развёл бы два экрана по разным значениям.</p>
     */
    public void applySessionSummary(Model model, SessionSummary summary) {
        model.addAttribute("summary", summary);
        model.addAttribute("progression", progressionOf(summary));
    }

    private static com.cheatsheet.quiz.domain.Progression progressionOf(SessionSummary summary) {
        SessionSummary.Gamification g = summary == null ? null : summary.getGamification();
        if (g == null || g.xpToNextLevel() <= 0) {
            return null;
        }
        return new com.cheatsheet.quiz.domain.Progression(
                g.rank(), g.level(), g.xpIntoLevel(), g.xpToNextLevel(),
                g.totalXp(), g.currentStreak(), g.bestStreak());
    }
}
