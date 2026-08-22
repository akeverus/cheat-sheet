package com.cheatsheet.quiz.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cheatsheet.quiz.domain.InterviewSession;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class InterviewControllerTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        com.cheatsheet.quiz.TestInterviewPath.register(registry);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    com.cheatsheet.quiz.persistence.QuestionRepository questionRepository;

    @Autowired
    com.cheatsheet.quiz.persistence.AnswerOptionRepository answerOptionRepository;

    /**
     * Вопрос-якорь этого класса. Без него `GET /` отдавал случайный вопрос из общей
     * тестовой базы: попадётся вопрос без seed-вариантов — MVC форсит флешкарту, и
     * половина разметки (форма, submit, прогресс) законно исчезает. Из-за этого
     * проверки писались условно и молча зеленели при сорванном рендере.
     *
     * <p>Фикстура создаётся один раз на JVM и адресуется через {@code ?questionId=},
     * который рендерит именно её (см. {@code FocusTrainingPageService#pinnedQuestionId}).
     * Общая база при этом не чистится — соседние классы своих данных не теряют.</p>
     */
    private static Long fixtureQuestionId;

    private static final String MISSING_TOPIC = "no-such-topic-for-empty-state";

    @org.junit.jupiter.api.BeforeEach
    void ensureFixtureQuestion() {
        if (fixtureQuestionId != null) {
            return;
        }
        long id = questionRepository.insert(com.cheatsheet.quiz.TestQuestionBuilder.aQuestion()
                .withSlug("interview-controller-fixture")
                .withTopic("interview-controller-fixture")
                .withFilePath("interview-controller-fixture.md")
                .withQuestionText("Что делает фикстура этого теста?")
                .withAnswerMarkdown("Даёт детерминированный вопрос с вариантами ответа.")
                .withSourceHash("interview-controller-fixture")
                .build());
        answerOptionRepository.insertAll(id, java.util.List.of(
                new com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate(
                        "Фиксирует вопрос, который отдаёт `GET /`", true, 0, "SEED", "Верно."),
                new com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate(
                        "Очищает базу перед каждым тестом", false, 1, "SEED", "Нет, база общая."),
                new com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate(
                        "Отключает импорт вопросов из шпаргалок", false, 2, "SEED", "Импорт не трогаем."),
                new com.cheatsheet.quiz.persistence.AnswerOptionRepository.AnswerOptionCreate(
                        "Повторяет запрос до зелёного результата", false, 3, "SEED", "Ретраев здесь нет.")
        ));
        fixtureQuestionId = id;
    }

    /** Экран тренировки с закреплённым вопросом-фикстурой: разметка вариантов гарантирована. */
    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder focusRequest() {
        return get("/").param("questionId", String.valueOf(fixtureQuestionId));
    }

    private String focusBody() throws Exception {
        return mockMvc.perform(focusRequest())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * Экран фокуса с закреплённой фикстурой на произвольном маршруте и в произвольном режиме.
     *
     * <p>Закрепление работает и на {@code /review}: пин разрешается раньше, чем применяется
     * review-фильтр ({@code FocusTrainingPageService#buildFocusPageState} берёт
     * {@code questionById} до {@code nextQuestion}), поэтому режимная обвязка — чип зоны и
     * подсказка — рендерится независимо от того, накопились ли в общей базе ошибочные
     * ответы. Без пина ветка «есть вопрос» была недостижима детерминированно, и проверка
     * писалась условно.</p>
     */
    private String focusBody(String path, String mode) throws Exception {
        var request = get(path).param("questionId", String.valueOf(fixtureQuestionId));
        if (mode != null) {
            request = request.param("mode", mode);
        }
        return mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    /** Экран тренировки, гарантированно упирающийся в пустое состояние: темы не существует. */
    private String emptyFocusBody(String path) throws Exception {
        return emptyFocusBody(path, null);
    }

    private String emptyFocusBody(String path, String mode) throws Exception {
        var request = get(path).param("topic", MISSING_TOPIC);
        if (mode != null) {
            request = request.param("mode", mode);
        }
        return mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * Экран тренировки с живой сессией: прогресс-рельс гейтится
     * {@code th:if="${interviewSession != null}"}, поэтому на голом {@code GET /} его нет
     * вовсе — проверка рельса без старта сессии не может сработать в принципе.
     */
    private String sessionFocusBody(MockHttpSession session, int count) throws Exception {
        mockMvc.perform(post("/start")
                        .with(csrf())
                        .session(session)
                        .param("mode", "EXAM")
                        .param("count", String.valueOf(count)))
                .andExpect(status().is3xxRedirection());
        return mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    /**
     * Страница результата (no-JS путь {@code POST /answer}) по вопросу-фикстуре.
     *
     * <p>Нужна там, где проверяемая разметка живёт в {@code result.html}, а не на экране
     * вопроса. Кнопка избранного — ровно такой случай: на фокусе её нет вовсе, и проверка
     * через {@code GET /} зеленела бы всегда, ничего не проверив.</p>
     */
    private String resultBody() throws Exception {
        long optionId = extractLong(focusBody(), "name=\"optionId\"\\s+value=\"(\\d+)\"");
        return mockMvc.perform(post("/answer")
                        .with(csrf())
                        .param("questionId", String.valueOf(fixtureQuestionId))
                        .param("optionId", String.valueOf(optionId))
                        .param("ordered", "true"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
    }

    private long extractLong(String body, String regex) {
        var matcher = java.util.regex.Pattern.compile(regex).matcher(body);
        assertThat(matcher.find())
                .as("в разметке нет совпадения с %s — фикстура отрендерилась не так, как ожидалось", regex)
                .isTrue();
        return Long.parseLong(matcher.group(1));
    }

    private int countOccurrences(String text, String fragment) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(fragment, index)) != -1) {
            count++;
            index += fragment.length();
        }
        return count;
    }

    /** Экран фокуса обязан показать либо вопрос, либо явное пустое состояние. */
    private void assertFocusPageRendered(String body) {
        assertThat(body).contains("<body class=\"focus-page\"");
        assertThat(body).containsAnyOf("class=\"focus-question\"", "class=\"empty-state-title\"");
    }

    private void assertNoInlineUiAttributes(String body) {
        assertThat(body).doesNotContain(" style=\"");
        assertThat(body).doesNotContain(" onclick=\"");
        assertThat(body).doesNotContain(" onchange=\"");
        assertThat(body).doesNotContain(" oninput=\"");
        assertThat(body).doesNotContain(" onsubmit=\"");
        assertThat(body).doesNotContain(" onkeydown=\"");
        assertThat(body).doesNotContain(" onkeyup=\"");
    }

    @Test
    void startSessionWithValidCountRedirects() throws Exception {
        mockMvc.perform(post("/start")
                        .with(csrf())
                        .param("mode", "EXAM")
                        .param("count", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void startSessionWithExcessiveCountIsCapped() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/start")
                        .with(csrf())
                        .session(session)
                        .param("mode", "EXAM")
                        .param("count", "99999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        InterviewSession interviewSession = (InterviewSession) session.getAttribute("interviewSession");
        assertThat(interviewSession).isNotNull();
        assertThat(interviewSession.getQuestionIds().size()).isLessThanOrEqualTo(200);
    }

    @Test
    void startSessionWithNegativeCountDoesNotCreateSession() throws Exception {
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/start")
                        .with(csrf())
                        .session(session)
                        .param("mode", "EXAM")
                        .param("count", "-5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andReturn();

        InterviewSession interviewSession = (InterviewSession) session.getAttribute("interviewSession");
        assertThat(interviewSession).isNull();
    }

    @Test
    void studyModeLearnPhaseRendersConfirmButtonThenQuizShowsOptions() throws Exception {
        // RIA-2: раньше фаза LEARN режима STUDY не имела UI — показывалась форма
        // вариантов (как TRAINING), а POST /study-confirm был недостижим. Тест
        // фиксирует контракт: LEARN = учебная карточка «ответ + Проверить себя»
        // (форма вариантов скрыта), /study-confirm → QUIZ = форма вариантов.
        MockHttpSession session = new MockHttpSession();
        // ordered=true → первый вопрос детерминирован и имеет seed-варианты.
        mockMvc.perform(post("/start")
                        .with(csrf())
                        .session(session)
                        .param("mode", "STUDY")
                        .param("count", "5")
                        .param("ordered", "true"))
                .andExpect(status().is3xxRedirection());

        InterviewSession interviewSession = (InterviewSession) session.getAttribute("interviewSession");
        org.junit.jupiter.api.Assumptions.assumeTrue(
                interviewSession != null, "STUDY-сессия не создана — нет вопросов в тестовой БД");

        String learn = mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        // Вырожденный случай: вопрос без вариантов → флешкарта, отдельной карточки
        // изучения нет (её показывает flashcard-browse) — пропускаем.
        org.junit.jupiter.api.Assumptions.assumeTrue(
                learn.contains("study-learn-phase"),
                "Первый STUDY-вопрос без вариантов — фаза изучения вырождена во флешкарту");

        // LEARN: карточка ответа + кнопка перехода к проверке; формы вариантов НЕТ.
        assertThat(learn).contains("action=\"/study-confirm\"");
        assertThat(learn).contains("Проверить себя");
        assertThat(learn).contains("class=\"answer markdown-content\"");
        assertThat(learn).doesNotContain("id=\"interview-form\"");

        // Переход LEARN → QUIZ.
        mockMvc.perform(post("/study-confirm").with(csrf()).session(session))
                .andExpect(status().is3xxRedirection());

        String quiz = mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        // QUIZ: форма вариантов появилась, карточки изучения больше нет.
        assertThat(quiz).contains("id=\"interview-form\"");
        assertThat(quiz).doesNotContain("study-learn-phase");
    }

    @Test
    void statsSearchResultsPreserveFilterParamsInTopicLinks() throws Exception {
        String body = mockMvc.perform(get("/stats")
                        .param("topic", "sample")
                        .param("important", "true")
                        .param("onlyWrong", "true")
                        .param("q", "Пример"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(body).contains("important=true");
        assertThat(body).contains("onlyWrong=true");
    }

    @Test
    void indexContainsClearStepByStepActions() throws Exception {
        String body = focusBody();
        assertFocusPageRendered(body);

        assertThat(body).contains("Подготовка к собеседованию");
        assertThat(body).doesNotContain("id=\"left-sidebar-card\"");
        assertThat(body).contains("aria-label=\"Основная навигация\"");
        assertThat(body).contains(">Тренировка<");
        assertThat(body).contains(">Аналитика<");
        assertThat(body).contains(">Настройки<");
        assertThat(body).doesNotContain("tabindex=\"-1\">Следующий вопрос");
    }

    @Test
    void indexFallbackStateKeepsDegradedUxContract() throws Exception {
        String body = emptyFocusBody("/");
        assertFocusPageRendered(body);
        assertThat(body).contains("Нет доступных вопросов по выбранным фильтрам.");
        assertThat(body).contains("empty-action-settings");
        assertThat(body).contains("empty-action-retry");
        assertThat(body).doesNotContain("id=\"interview-form\"");
        assertThat(body).doesNotContain("id=\"interview-options\"");
        assertThat(body).doesNotContain("id=\"interview-submit\"");
    }

    /**
     * UXD-001: конструктор сессии живёт на собственной поверхности /train
     * («Собрать сессию»), а /settings — честные настройки без него.
     */
    @Test
    void trainContainsSessionConfigurationBlocks() throws Exception {
        var result = mockMvc.perform(get("/train"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isEqualTo(200);
        String body = result.getContentAsString();
        assertThat(body).contains("<h1>Собрать сессию</h1>");
        assertThat(body).contains("id=\"filters-form\"");
        assertThat(body).contains("id=\"session-form\"");
        assertThat(body).contains("Применить");
        assertThat(body).contains("Начать тренировку");

        String settings = mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(settings).as("конструктор с /settings снят — иначе CTA «Собрать сессию» снова ведут в «Настройки»")
                .doesNotContain("id=\"session-form\"");
    }

    @Test
    void settingsTabStructureKeepsAccessibilityContract() throws Exception {
        String body = mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(body).contains("type=\"button\"");
        assertThat(body).contains("id=\"main-content\"");
        // UXD-001: вкладок ДВЕ (Оформление — дефолтная активная + Данные; в этом
        // профиле admin-token задан, поэтому «Данные» рендерится и полоса нужна).
        // В single-user без admin-гейта осталась бы одна панель — и tablist из
        // одной кнопки не рендерился бы вовсе (th:if на полосе).
        assertThat(body).contains("role=\"tablist\"");
        assertThat(body).contains("role=\"tab\"");
        assertThat(body).contains("role=\"tabpanel\"");
        assertThat(body).doesNotContain("aria-controls=\"panel-session\"");
        assertThat(body).containsSubsequence("aria-controls=\"panel-appearance\"", "aria-selected=\"true\"");
        assertThat(body).contains("settings-panel is-active");
        assertThat(body).contains("id=\"panel-appearance\"");
        assertThat(body).contains("id=\"panel-data\"");
        // Старый sidebar-каркас и мёртвый тоггл сворачивания убраны редизайном.
        assertThat(body).doesNotContain("id=\"left-sidebar-card\"");
        assertThat(body).doesNotContain("id=\"sidebar-collapse-toggle\"");
    }

    /**
     * UXD-001: builder-параметры на /settings — легаси deep-link'и конструктора.
     * Сохранённые ссылки не ломаются: 302 на /train с переносом параметров, и уже
     * /train применяет их к форме (прежний контракт префилла — без изменений).
     */
    @Test
    void legacySettingsBuilderParamsRedirectToTrainWhichAppliesThem() throws Exception {
        mockMvc.perform(get("/settings")
                        .param("weakTopics", "true")
                        .param("mode", "FLASHCARD"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/train?weakTopics=true&mode=FLASHCARD"));

        var result = mockMvc.perform(get("/train")
                        .param("weakTopics", "true")
                        .param("mode", "FLASHCARD"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isEqualTo(200);
        String body = result.getContentAsString();

        assertThat(body).contains("name=\"weakTopics\"");
        // Порядок, а не соседство: Thymeleaf печатает th:checked ПОСЛЕ th:attr, поэтому
        // между value и checked стоят data-*. Требовать их склейки значило бы закреплять
        // порядок вывода движка, а контракт — «запрошенный режим и есть отмеченный».
        assertThat(body).containsSubsequence("value=\"FLASHCARD\"", "checked=\"checked\"");
    }

    @Test
    void statsEchoesSearchQueryInSearchInput() throws Exception {
        String query = "hashmap";
        var result = mockMvc.perform(get("/stats")
                        .param("q", query))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isEqualTo(200);
        String body = result.getContentAsString();

        assertThat(body).contains("name=\"q\"");
        assertThat(body).contains("value=\"" + query + "\"");
    }

    @Test
    void keyPagesExposeExpectedBodyPageNamespaces() throws Exception {
        String statsBody = mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(statsBody).contains("<body class=\"stats-page\">");

        String settingsBody = mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(settingsBody).contains("<body class=\"settings-page\">");
    }

    @Test
    void exportActionsRenderOnlyOnSettingsRoute() throws Exception {
        // Экспорт прогресса (JSON/CSV) живёт на /settings → «Управление данными».
        // Раньше дублировался ссылками nav-export в шапке; перенесён в единую точку,
        // шапка очищена (header.html). Маркер теперь — data-export-format на кнопках.
        String settingsBody = mockMvc.perform(get("/settings"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(settingsBody).contains("data-export-format=\"json\"");
        assertThat(settingsBody).contains("data-export-format=\"csv\"");
        assertThat(settingsBody).contains("Экспорт JSON");
        assertThat(settingsBody).contains("Экспорт CSV");

        String statsBody = mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(statsBody).doesNotContain("data-export-format=");

        var indexResult = mockMvc.perform(get("/")).andReturn().getResponse();
        assertThat(indexResult.getStatus()).isEqualTo(200);
        assertThat(indexResult.getContentAsString()).doesNotContain("data-export-format=");
    }

    @Test
    void statsRouteExposesChartFallbackAccessibilityContract() throws Exception {
        String body = mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(body).contains("id=\"topicProgressChart\"");
        assertThat(body).contains("id=\"topicAccuracyChart\"");
        assertThat(body).contains("id=\"topicProgressChartFallback\"");
        assertThat(body).contains("id=\"topicAccuracyChartFallback\"");
        assertThat(body).contains("class=\"chart-fallback hidden\"");
        assertThat(body).contains("role=\"status\"");
        assertThat(body).contains("aria-live=\"polite\"");
    }

    @Test
    void statsFilterBandIsSingleFormOverSingleEndpoint() throws Exception {
        // iter-72. До этого над одним `GET /stats` стояли ДВЕ формы, и каждая
        // тащила поля соседки скрытыми копиями со значениями с сервера —
        // несохранённый ввод соседней формы при отправке молча пропадал.
        // Гейт держит инвариант: одна форма, ни одного скрытого дубля видимого
        // поля, каждый параметр отбора встречается в разметке ровно один раз.
        String body = mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(countOccurrences(body, "class=\"filter-bar-form\"")).isEqualTo(1);
        for (String param : new String[] {"q", "group", "topic", "important", "onlyWrong"}) {
            assertThat(countOccurrences(body, "name=\"" + param + "\""))
                    .as("параметр отбора %s не должен дублироваться скрытой копией", param)
                    .isEqualTo(1);
        }

        // Одна система полей и одна система кнопок (§24, продолжение FRD-022):
        // отбор рисуется общими `.field`/`.field-label`/`.toggle-item`/`.btn`.
        assertThat(body).contains("class=\"field filter-bar-field");
        assertThat(body).contains("class=\"field-label\"");
        assertThat(body).contains("class=\"toggle-item\"");
        // Якорь «единой системы кнопок» — submit самой полосы отбора: он рендерится
        // при ЛЮБЫХ данных. Прежний якорь `btn next-btn` живёт только в empty-state
        // ветках (noTopics…) и молча зависел от порядка методов: первый же тест,
        // записавший попытку, наполнял user_topic_stats — и ассерт падал.
        assertThat(body).contains("class=\"btn secondary-btn\"");
        for (String retired : new String[] {
            "stats-filters-form", "stats-search-form", "stats-action-btn",
            "stats-apply-action", "stats-search-action", "stats-filter-pane",
            "stats-search-pane", "stats-pane-label", "stats-filter-row",
            "stats-filter-separated", "filter-row", "search-form",
            // iter-74: полоса отбора стала общим компонентом `.filter-bar-*`
            // (/stats + /bookmarks). Страничные имена сняты целиком — вернуться
            // они могут только вместе со второй копией правил, которую §24
            // запрещает; отсюда гейт на само имя, а не на его CSS.
            "stats-filter-card", "stats-filter-form", "stats-filter-field",
            "stats-filter-state", "stats-filter-chip", "stats-filter-reset"
        }) {
            assertThat(body).as("снятое имя %s не должно возвращаться", retired).doesNotContain(retired);
        }
    }

    @Test
    void statsFilterBandStatesActiveSelectionInWords() throws Exception {
        // Без отбора строки состояния и «Сбросить» нет — пустой чип не сообщение.
        String plain = mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(plain).doesNotContain("filter-bar-state");

        // С отбором — выборка сказана словами, а не только положением контролов.
        String filtered = mockMvc.perform(get("/stats").param("q", "hikaricp").param("onlyWrong", "true"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(filtered).contains("filter-bar-state");
        assertThat(filtered).contains("hikaricp");
        assertThat(filtered).contains("только ошибки");
        assertThat(filtered).contains("Сбросить");
    }

    @Test
    void focusRoutesExposeFocusPageNamespaceWhenRendered() throws Exception {
        var indexResult = mockMvc.perform(get("/")).andReturn().getResponse();
        assertThat(indexResult.getStatus()).isEqualTo(200);
        assertThat(indexResult.getContentAsString()).contains("<body class=\"focus-page\"");
    }

    @Test
    void legacyTrainingRouteRedirectsToCanonicalRootKeepingFilters() throws Exception {
        // Один экран — один адрес. Старые ссылки не ломаем, но параметры выборки
        // переносим, иначе редирект молча сбрасывал бы фильтр пользователя.
        mockMvc.perform(get("/training"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        mockMvc.perform(get("/training").param("topic", "java").param("mode", "STUDY"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/?topic=java&mode=STUDY"));

        // Чужие параметры не тащим — редирект работает по whitelist.
        mockMvc.perform(get("/training").param("utm_source", "spam"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void focusRoutesKeepKeyboardHintAndTimerLiveRegionContracts() throws Exception {
        String body = focusBody();
        assertFocusPageRendered(body);
        assertThat(body).contains("id=\"interview-form\"");
        assertThat(body).contains("class=\"keyboard-hint\"");
        assertThat(body).contains("id=\"question-timer\"");
        assertThat(body).contains("aria-live=\"polite\"");
    }

    @Test
    void trainingProgressTrackKeepsBoundedAriaValuesContract() throws Exception {
        // Рельс существует только внутри сессии, поэтому её надо стартовать: без старта
        // условие «есть progressbar» не выполнялось никогда, и проверка была выключателем.
        String body = sessionFocusBody(new MockHttpSession(), 5);
        assertFocusPageRendered(body);
        assertThat(body).contains("aria-label=\"Прогресс сессии\"");
        assertThat(body).contains("aria-valuemin=\"0\"");
        // Границы — реальный размер сессии, а не проценты: скринридер обязан сказать
        // «1 из 5», а не «1 из 100».
        assertThat(body).contains("aria-valuemax=\"5\"");
        assertThat(body).contains("aria-valuenow=\"1\"");
        assertThat(body).doesNotContain("aria-valuemax=\"100\"");
    }

    @Test
    void reviewModeRendersOnlyWrongContextAndHint() throws Exception {
        String body = focusBody("/review", null);
        assertFocusPageRendered(body);
        assertThat(body).contains("class=\"zone-chip\"");
        assertThat(body).contains("Повтор ошибок");
        assertThat(body).contains("Повтор ошибок: отвечай на вопросы, где раньше были ошибки.");
    }

    @Test
    void reviewPageAvoidsInlineStylesAndInlineEventsWhenRendered() throws Exception {
        var result = mockMvc.perform(get("/review"))
                .andReturn()
                .getResponse();
        assertThat(result.getStatus()).isEqualTo(200);
        assertNoInlineUiAttributes(result.getContentAsString());
    }

    @Test
    void statsTopicLinksKeepAllActiveFilters() throws Exception {
        String body = mockMvc.perform(get("/stats")
                        .param("group", "backend")
                        .param("ordered", "false")
                        .param("important", "true")
                        .param("onlyWrong", "true")
                        .param("q", "cache"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(body).contains("name=\"ordered\"");
        assertThat(body).contains("value=\"false\"");
        assertThat(body).contains("name=\"important\"");
        assertThat(body).contains("name=\"onlyWrong\"");
        assertThat(body).contains("value=\"cache\"");
    }

    /**
     * Кнопка избранного живёт в {@code result.html}, а не на экране вопроса. Проверка
     * стояла на {@code GET /}, где её нет ни в одном состоянии, — гейт a11y-контракта
     * (aria-pressed + осмысленное имя) фактически отсутствовал.
     */
    @Test
    void resultFavoriteButtonExposesPressedStateForAssistiveTech() throws Exception {
        String body = resultBody();
        assertThat(body).contains("id=\"btn-favorite\"");
        // Свежая фикстура не в избранном → состояние и имя должны говорить именно это,
        // а не «одно из двух»: иначе тест переживёт залипание кнопки в обратном состоянии.
        assertThat(body).contains("aria-pressed=\"false\"");
        assertThat(body).contains("aria-label=\"Добавить в избранное\"");
    }

    @Test
    void bookmarksPageRendersEmptyStateAndNavLink() throws Exception {
        String body = mockMvc.perform(get("/bookmarks"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(body).contains("bookmarks-page");
        assertThat(body).contains("Закладки");
        assertThat(body).contains("href=\"/bookmarks\"");
        assertThat(body).containsAnyOf("Пока нет закладок", "Список");
        assertThat(body).doesNotContain("href=\"/stats?important=true\"");
    }

    /**
     * Сортировка таблицы тем приходит готовой в первом кадре (iter-81, FRD-048).
     *
     * <p>Раньше сервер отдавал «скелет» ({@code class="sortable"} +
     * {@code data-sort-label}), а состояние навешивал {@code table-sort.js} —
     * то есть в серверном HTML заголовок был БЕЗ {@code aria-sort}, и до
     * инициализации скрипта скринридер читал отсортированную таблицу как
     * несортированную. Сейчас порядок задаёт адрес страницы, поэтому
     * {@code aria-sort} и целевые ссылки обязаны быть прямо в ответе.</p>
     */
    @Test
    void statsTableHeadersCarrySortStateInServerHtml() throws Exception {
        String body = mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(body).contains("aria-sort=");
        assertThat(countOccurrences(body, "class=\"col-sort\"")).isGreaterThanOrEqualTo(4);
        assertThat(body).contains("sort=accuracy");
        assertThat(body).contains("#topic-table-section");
        // Ровно один заголовок отсортирован — иначе порядок неоднозначен.
        assertThat(countOccurrences(body, "aria-sort=\"ascending\"")
                + countOccurrences(body, "aria-sort=\"descending\"")).isEqualTo(1);
        assertThat(body).doesNotContain("data-sort-label=");
        assertThat(body).doesNotContain("table-sort-status");
    }

    /**
     * {@code ?sort=} и {@code ?rows=} меняют реальную выдачу, а неизвестное
     * значение молча падает в дефолт (идиома FRD-024 с {@code /bookmarks}).
     */
    @Test
    void statsTableOrderAndVolumeFollowUrlState() throws Exception {
        String byAccuracy = mockMvc.perform(get("/stats").param("sort", "accuracy"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(byAccuracy).contains("aria-sort=\"ascending\"");

        String garbage = mockMvc.perform(get("/stats").param("sort", "../etc").param("dir", "sideways"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(garbage).contains("sort=name");
        assertThat(garbage).doesNotContain("sideways");

        String all = mockMvc.perform(get("/stats").param("rows", "all"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        assertThat(all).contains("Свернуть таблицу");
        assertThat(all).doesNotContain("Показать все темы");
    }

    @Test
    void indexActionFooterExposesKeyboardShortcuts() throws Exception {
        String body = focusBody();
        assertFocusPageRendered(body);
        assertThat(body).contains("id=\"interview-submit\"");
        assertThat(body).contains("id=\"next-question\"");
        // next-question — это <a href>: нативно активируется Enter, не Space
        // (quiz/interview.js на Space для тегов A обработчик не вешает). aria-keyshortcuts
        // честно обещает только Enter — паритет с submit-кнопкой. Обещание должно стоять
        // у обоих контролов, поэтому считаем вхождения, а не факт наличия.
        assertThat(countOccurrences(body, "aria-keyshortcuts=\"Enter\"")).isGreaterThanOrEqualTo(2);
    }

    @Test
    void trainingPageAvoidsInlineOnchangeHandlers() throws Exception {
        assertThat(focusBody()).doesNotContain("onchange=\"enableSubmit()\"");
    }

    @Test
    void trainingPageUsesUnifiedKeyboardHintClass() throws Exception {
        String body = focusBody();
        assertFocusPageRendered(body);
        assertThat(body).contains("id=\"interview-form\"");
        assertThat(body).contains("class=\"keyboard-hint\"");
        assertThat(body).doesNotContain("class=\"kbd-hint\"");
    }

    @Test
    void trainingPageUsesUnifiedSubmitIdAndHotkeyHintContract() throws Exception {
        String body = focusBody();
        assertFocusPageRendered(body);
        assertThat(body).contains("id=\"interview-submit\"");
        assertThat(body).contains("aria-keyshortcuts=\"Enter\"");
        assertThat(body).containsAnyOf("Клавиши 1-", "Клавиши 1–");
        assertThat(body).doesNotContain("id=\"submitBtn\"");
    }

    @Test
    void trainingPageAvoidsRedundantLabelRadioRolesAndTabStops() throws Exception {
        String body = focusBody();
        assertFocusPageRendered(body);
        assertThat(body).contains("id=\"interview-options\"");
        assertThat(body).contains("aria-describedby=\"options-flow-hint\"");
        // Нативные radio уже дают роль и управление стрелками: явные role/tabindex
        // ломают roving-tabindex группы.
        assertThat(body).doesNotContain("role=\"radio\"");
        assertThat(body).doesNotContain("tabindex=\"0\"");
    }

    /**
     * Третья ветка пустого состояния — «Сессия запущена, но вопрос пока недоступен» —
     * снаружи недостижима: сессия без вопросов создаётся сразу завершённой, а живая
     * сессия всегда отдаёт вопрос. Отдельного теста у неё нет намеренно; условный
     * assert только маскировал бы это под покрытие.
     */
    @Test
    void trainingFallbackStateExposesRecoveryActionsWhenNoQuestion() throws Exception {
        String body = emptyFocusBody("/");
        assertFocusPageRendered(body);
        assertThat(body).contains("Нет доступных вопросов");
        assertThat(body).contains("empty-action-settings");
        assertThat(body).contains("empty-action-retry");
        assertThat(body).contains("Обновить тренировку");
        // Кнопка «повторить» ведёт на канонический адрес тренировки, а не
        // на легаси-редирект /training (лишний хоп и потеря query).
        assertThat(body).doesNotContain("empty-action-retry\" href=\"/training");
    }

    /**
     * Пустой разбор (iter-143): экран называет СОБЫТИЕ и ведёт дальше.
     *
     * <p>Здесь стояло обратное: тест закреплял общий текст «Нет вопросов с ошибками
     * для повтора.» и кнопку «Обновить повтор ошибок» — перезагрузку того же адреса,
     * на котором вопросов заведомо нет. Кнопка-петля снята, поэтому проверка
     * переписана под новое поведение, а не ослаблена: список утверждений вырос.</p>
     */
    @Test
    void reviewFallbackStateNamesTheEventAndOffersAWayForward() throws Exception {
        String body = emptyFocusBody("/review");
        assertFocusPageRendered(body);
        // Фикстура зовёт `/review?topic=<несуществующая тема>`, то есть ОТФИЛЬТРОВАННЫЙ
        // пустой разбор: экран обязан сказать, что пусто именно в этой выборке, и дать
        // выход к остальным ошибкам. Ветку без отбора закрепляет разметочный гейт
        // ReviewEmptyStateContractTest.
        assertThat(body).contains("В этой выборке ошибок нет");
        assertThat(body).contains("empty-action-review-all");
        assertThat(body).contains("empty-action-train");
        assertThat(body).as("перезагрузка того же пустого адреса — петля")
                .doesNotContain("Обновить повтор ошибок");
    }

    @Test
    void flashcardModeKeepsContextualCopyAndRecoveryContract() throws Exception {
        String body = focusBody("/", "FLASHCARD");
        assertFocusPageRendered(body);
        assertThat(body).contains("class=\"zone-chip\"");
        assertThat(body).contains("Флешкарты");
        assertThat(body).contains("Флешкарты: сначала вспомни ответ, затем раскрой и оцени себя.");
        assertThat(body).doesNotContain("Выбери один вариант. Проверка и разбор идут по шагам.");

        String empty = emptyFocusBody("/", "FLASHCARD");
        assertThat(empty).contains("Нет доступных вопросов");
        assertThat(empty).contains("empty-action-retry");
        assertThat(empty).contains("href=\"/?mode=FLASHCARD\"");
        assertThat(empty).contains("Обновить флешкарты");
    }

    /**
     * UX-01 (refoundation, Phase A): вопрос без MCQ-вариантов внутри EXAM-сессии
     * не должен быть тупиком. Форма {@code /answer} (вместе со своим «Не знаю
     * ответа») гейтится {@code !flashcardMode}, а optionless-вопрос форсит
     * {@code flashcardMode=true} — значит session-продвигающий контрол обязана
     * нести сама флешкарт-ветка: POST {@code /skip} (честный UNKNOWN, та же
     * идемпотентность и штрафной пул экзамена, что у MCQ-ветки).
     *
     * <p>GET-ссылка browse-режима внутри активной сессии запрещена: ветка
     * {@code interviewSession} в {@code FocusTrainingPageService} приоритетна,
     * и GET с {@code excludeQuestionId} всё равно рендерит тот же
     * {@code session.currentQuestionId()} — ссылка выглядит рабочей, но
     * возвращает тот же вопрос (живой репро: «Вопрос 1 из 5» не менялся).</p>
     */
    @Test
    void optionlessQuestionInsideExamSessionOffersSkipAndAdvances() throws Exception {
        String topic = "optionless-session-topic";
        long questionId = questionRepository.insert(com.cheatsheet.quiz.TestQuestionBuilder.aQuestion()
                .withSlug("optionless-session-fixture")
                .withTopic(topic)
                .withQuestionText("Optionless session contract question")
                .build());
        MockHttpSession session = new MockHttpSession();
        mockMvc.perform(post("/start")
                        .with(csrf())
                        .session(session)
                        .param("mode", "EXAM")
                        .param("count", "1")
                        .param("topic", topic))
                .andExpect(status().is3xxRedirection());

        String body = mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(body).contains("Optionless session contract question");
        assertThat(body).as("флешкарта внутри сессии обязана иметь POST-продвижение /skip")
                .contains("action=\"/skip\"");
        assertThat(body).as("GET-ссылка browse внутри сессии рендерит тот же вопрос — запрещена")
                .doesNotContain("flashcard-browse-next");

        mockMvc.perform(post("/skip")
                        .with(csrf())
                        .session(session)
                        .param("questionId", String.valueOf(questionId)))
                .andExpect(status().is3xxRedirection());

        String after = mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(after).as("после skip односложная сессия завершена — тот же вопрос не повторяется")
                .doesNotContain("Optionless session contract question");
    }

    @Test
    void studyModeKeepsContextualCopyAndRecoveryContract() throws Exception {
        String body = focusBody("/", "STUDY");
        assertFocusPageRendered(body);
        assertThat(body).contains("class=\"zone-chip\"");
        // У STUDY две под-фазы, и чип с подсказкой обязаны говорить об одной и той же.
        // Фаза LEARN существует только внутри сессии (StudyPhase живёт в InterviewSession),
        // поэтому вне сессии режим детерминированно показывает QUIZ — проверяем именно её,
        // а не «одну из двух»: развилка скрывала бы рассинхрон чипа и подсказки.
        assertThat(body).contains("Проверь себя: выбери верный вариант — разбор появится после ответа.");
        assertThat(body).contains(">Проверка<");
        assertThat(body).doesNotContain("Изучение: разберись с материалом, затем переходи к проверке.");
        // Общая подсказка тренировки не должна протекать в STUDY.
        assertThat(body).doesNotContain("Выбери один вариант. Проверка и разбор идут по шагам.");

        String empty = emptyFocusBody("/", "STUDY");
        assertThat(empty).contains("Нет доступных вопросов");
        assertThat(empty).contains("empty-action-retry");
        assertThat(empty).contains("href=\"/?mode=STUDY\"");
        assertThat(empty).contains("Обновить изучение");
    }

    @Test
    void indexKeepsSharedAnswerFlowHintContract() throws Exception {
        String body = focusBody();
        assertFocusPageRendered(body);
        assertThat(body).contains("id=\"interview-form\"");
        assertThat(body).contains("id=\"answer-flow-hint\"");
        assertThat(body).contains("result-zone-head hidden");
    }

    @Test
    void indexKeepsSharedPostAnswerControlsContract() throws Exception {
        String body = focusBody();
        assertFocusPageRendered(body);
        assertThat(body).contains("id=\"interview-form\"");
        assertThat(body).contains("id=\"interview-alert\"");
        assertThat(body).contains("id=\"result-feedback\"");
        assertThat(body).contains("aria-atomic=\"true\"");
        // iter-127: кнопка «Похожие вопросы» и зона #extra-analysis-content УДАЛЕНЫ —
        // похожие стали третьей вкладкой разбора. Проверяем именно отсутствие: пока
        // маркеры живы, легко вернуть вторую зону под разбором, а она и была обходом
        // длины (замер: разбор 1955px, похожие дописывались ПОД ним).
        assertThat(body).doesNotContain("id=\"extra-analysis-toggle\"");
        assertThat(body).doesNotContain("id=\"extra-analysis-content\"");
    }

    @Test
    void indexUsesUnifiedPostAnswerCopyContract() throws Exception {
        String body = focusBody();
        assertFocusPageRendered(body);
        assertThat(body).contains("id=\"interview-form\"");
        assertThat(body).contains("Пост-разбор");
        assertThat(body).contains("Сначала разбор ответа, затем итог и дополнительные блоки");
        // Разбор со вкладками строится ПОСЛЕ ответа: до него на странице не должно быть
        // ни панелей, ни переключателя — иначе пустые вкладки видны в первом кадре.
        assertThat(body).doesNotContain("id=\"feedback-tablist\"");
        assertThat(body).doesNotContain("class=\"feedback-panel");
        // Раскрывашка «Подробнее» — часть страницы результата, до ответа её
        // быть не должно. Проверяем сам контрол, а не слово: «Подробнее»
        // легально встречается в комментариях подключённых фрагментов.
        assertThat(body).doesNotContain("<summary class=\"explanation-eyebrow\">Подробнее</summary>");
    }

    @Test
    void indexExposesSharedFragmentMarkers() throws Exception {
        String body = focusBody();
        assertFocusPageRendered(body);
        assertThat(body).contains("id=\"interview-form\"");
        assertThat(body).contains("data-ui-fragment=\"training-actions\"");
        assertThat(body).contains("data-ui-fragment=\"result-zone-head\"");
        assertThat(body).contains("data-ui-fragment=\"post-answer-controls\"");
        assertThat(body).contains("data-ui-fragment=\"notice-slot\"");
    }

    @Test
    void focusPagesExposeSingleUnifiedPrimaryNavigation() throws Exception {
        // Editorial IA: дубль-навигация устранена — единая «Основная навигация» в
        // шапке на всех страницах; surface-tabs («Навигация по режимам фокуса») удалена.
        var index = mockMvc.perform(get("/")).andReturn().getResponse();
        assertThat(index.getStatus()).isEqualTo(200);
        String body = index.getContentAsString();
        assertThat(body).doesNotContain("data-ui-fragment=\"focus-surface-tabs\"");
        assertThat(countOccurrences(body, "aria-label=\"Основная навигация\"")).isEqualTo(1);
        assertThat(countOccurrences(body, "aria-label=\"Навигация по режимам фокуса\"")).isEqualTo(0);
    }

    @Test
    void keyPagesAvoidInlineStylesAndInlineEvents() throws Exception {
        var index = mockMvc.perform(get("/")).andReturn().getResponse();
        assertThat(index.getStatus()).isEqualTo(200);
        assertNoInlineUiAttributes(index.getContentAsString());

        var stats = mockMvc.perform(get("/stats")).andReturn().getResponse();
        assertThat(stats.getStatus()).isEqualTo(200);
        assertNoInlineUiAttributes(stats.getContentAsString());

        var settings = mockMvc.perform(get("/settings")).andReturn().getResponse();
        assertThat(settings.getStatus()).isEqualTo(200);
        assertNoInlineUiAttributes(settings.getContentAsString());
    }
}
