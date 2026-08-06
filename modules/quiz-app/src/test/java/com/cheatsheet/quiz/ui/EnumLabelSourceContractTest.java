package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.common.util.ModeUtils;
import com.cheatsheet.quiz.domain.Difficulty;
import com.cheatsheet.quiz.domain.InterviewMode;
import com.cheatsheet.quiz.domain.QuestionIssueCategory;
import com.cheatsheet.quiz.domain.ReviewResult;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Имя enum-константы, попавшее в разметку или JS строковым литералом.
 *
 * <p>Класс дефекта — тот же, что у {@link DifficultyBadgeContractTest} и
 * {@link ReviewReasonPaletteTest}, но с другой стороны: там подпись собиралась в
 * шаблоне и новая константа печаталась как старая, здесь — константа сравнивается со
 * строкой, и её переименование <b>не ломает сборку</b>. Компилятор литерал не видит:
 * ветка просто перестаёт выбираться. Замер до правки — 15 вхождений
 * {@code X.name() == 'ЛИТЕРАЛ'} в шаблонах ({@link InterviewMode} — 11,
 * {@code BlockType}/{@code 'MERMAID'} — 4) плюс карта {@code MODE_CTA} в
 * {@code settings/session-form.js}, где именами констант были ключи объекта.
 *
 * <p>Проявления были бы разными и все — тихими: из выпадающего списка пропадает режим,
 * над формой развёрнутого ответа не остаётся ни одной подписи (из четырёх
 * взаимоисключающих {@code th:if} не срабатывает ни один), схема остаётся на экране, но
 * рендерится псевдографикой вместо mermaid, кнопка старта откатывается к нейтральному
 * «Начать сессию».
 *
 * <h2>Что именно проверяется</h2>
 * <ol>
 *   <li><b>Литералов нет.</b> Из всех шаблонов вынимаются сравнения
 *       {@code <выражение>.name() == '<Л>'} — их не должно остаться: ветвление по
 *       режиму выражается предикатом ({@code isUnlimited}, {@code usesTypedInput},
 *       {@code rendersAsMermaid}), который переименование ловит компилятором.</li>
 *   <li><b>Подпись — в одном месте.</b> Ни одна русская подпись режима не встречается
 *       в разметке и JS: и {@code <option>}, и подпись кнопки старта приходят из
 *       {@link ModeUtils}. Именно расхождение этих копий однажды и доехало до
 *       пользователя — «Интенсив» на итогах показывался как «MARATHON» (round-01 C22),
 *       а «единый источник правды» был утверждением в javadoc.</li>
 *   <li><b>Список полон.</b> Порядок показа в {@link ModeUtils#options} перечисляет все
 *       константы {@link InterviewMode} ровно по разу: забытый режим недостижим из
 *       интерфейса, и это не видно ни по одному падению.</li>
 *   <li><b>{@link ReviewResult} умеет назвать себя по-русски</b> — фоллбэк
 *       {@code th:case="*"} на экране разбора печатал сырое английское имя константы.</li>
 * </ol>
 */
class EnumLabelSourceContractTest {

    private static final Path TEMPLATES = Path.of("src/main/resources/templates");
    private static final Path JS = Path.of("src/main/resources/static/js");

    /** {@code что-то.name() == 'ЛИТЕРАЛ'} — сравнение константы со строкой в разметке. */
    private static final Pattern NAME_LITERAL = Pattern.compile(
            "([\\w.()]+)\\.name\\(\\)\\s*==\\s*'([A-Z_]+)'");

    private static List<Path> filesUnder(Path root, String suffix) {
        try (Stream<Path> paths = Files.walk(root)) {
            return paths.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(suffix))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void templatesDoNotBranchOnEnumConstantNamesAsStringLiterals() {
        Map<String, String> offenders = new LinkedHashMap<>();
        for (Path template : filesUnder(TEMPLATES, ".html")) {
            Matcher matcher = NAME_LITERAL.matcher(read(template));
            while (matcher.find()) {
                offenders.put(
                        TEMPLATES.relativize(template) + " → " + matcher.group(1) + " == '" + matcher.group(2) + "'",
                        matcher.group(2));
            }
        }
        assertThat(offenders)
                .describedAs("""
                        Шаблон сравнивает имя enum-константы со строковым литералом. \
                        Переименование константы не сломает сборку — ветка молча перестанет \
                        выбираться. Выразить условие предикатом на самом перечислении \
                        (см. InterviewMode.isUnlimited / BlockType.rendersAsMermaid) \
                        или отдать готовую строку из модели.""")
                .isEmpty();
    }

    @Test
    void modeDisplayOrderCoversEveryMode() {
        List<String> shown = new ModeUtils()
                .options(null, 20, 50).stream()
                .map(ModeUtils.ModeOption::name)
                .toList();

        assertThat(shown)
                .describedAs("Режим, отсутствующий в порядке показа ModeUtils.options, "
                        + "недостижим из интерфейса — и это ничего не ломает")
                .containsExactlyInAnyOrderElementsOf(
                        Arrays.stream(InterviewMode.values()).map(Enum::name).toList());
        assertThat(shown).doesNotHaveDuplicates();
    }

    @Test
    void everyModeHasDistinctRussianLabelAndStartAction() {
        ModeUtils modeUtils = new ModeUtils();
        for (InterviewMode mode : InterviewMode.values()) {
            assertThat(modeUtils.displayName(mode))
                    .describedAs("подпись режима " + mode)
                    .isNotBlank()
                    .doesNotContain(mode.name());
            assertThat(modeUtils.startAction(mode))
                    .describedAs("подпись кнопки старта для " + mode)
                    .startsWith("Начать ")
                    .doesNotContain(mode.name());
        }
        assertThat(Arrays.stream(InterviewMode.values()).map(modeUtils::displayName).toList())
                .describedAs("две одинаковые подписи — два неразличимых пункта в списке режимов")
                .doesNotHaveDuplicates();
    }

    /**
     * Порог, а не «ни одного вхождения»: подписи режимов — обычные русские слова, и
     * поодиночке они законно встречаются в другом смысле. «Тренировка» — имя пункта
     * навигации, «Интенсив» живёт внутри «Интенсивности arcade-эффектов», «Изучение»
     * стоит в подписи учебной фазы. Гейт с растущим списком исключений перестаёт быть
     * гейтом, поэтому ловится то, что действительно опасно: <b>таблица</b> подписей,
     * то есть вторая копия перечисления. Прозе не нужны четыре режима из восьми.
     */
    private static final int LABEL_TABLE_THRESHOLD = 4;

    @Test
    void noFileOutsideModeUtilsEnumeratesTheModes() {
        ModeUtils modeUtils = new ModeUtils();
        Set<String> labels = Arrays.stream(InterviewMode.values())
                .map(modeUtils::displayName)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        Map<String, List<String>> copies = new LinkedHashMap<>();
        Stream.concat(filesUnder(TEMPLATES, ".html").stream(), filesUnder(JS, ".js").stream())
                .forEach(file -> {
                    String body = read(file);
                    List<String> found = labels.stream().filter(body::contains).toList();
                    if (found.size() >= LABEL_TABLE_THRESHOLD) {
                        copies.put(file.toString(), found);
                    }
                });

        assertThat(copies)
                .describedAs("""
                        Файл перечисляет режимы своими словами — это вторая копия таблицы \
                        подписей. Именно расхождение таких копий однажды показало пользователю \
                        «MARATHON» вместо «Интенсив» (round-01 C22): компилятор их не сверяет, \
                        а глаз сверяет не каждый раз. Читать подписи из модели (sessionModes).""")
                .isEmpty();
    }

    /** Блочные и строчные комментарии; {@code //} после {@code :} не трогаем — это URL. */
    private static final Pattern JS_COMMENT = Pattern.compile(
            "/\\*[\\s\\S]*?\\*/|(?<!:)//[^\\n]*");

    private static String stripComments(String js) {
        return JS_COMMENT.matcher(js).replaceAll("");
    }

    /**
     * Гейт того же класса, что {@link #templatesDoNotBranchOnEnumConstantNamesAsStringLiterals},
     * но по другой стороне: шаблонный гейт по определению не видит копий, живущих в
     * {@code static/js/}. Именно там пряталась карта {@code MODE_CTA} с именами режимов в
     * ключах, а рядом — таблица категорий жалобы и таблица цветов сложности.
     *
     * <p>Проверяется КОД, а не текст: комментарии вырезаются, потому что объяснение
     * «почему тут нет своей таблицы» обязано называть константы по именам — иначе гейт
     * запрещал бы документировать сам себя.
     *
     * <p>Гейт широкий намеренно: адресный разбор одного случая уже есть
     * ({@link ReportCategorySourceContractTest} — там же и объяснение, почему копия
     * категорий жалобы самая тихая из всех: {@code fromString} молча кладёт незнакомое
     * имя в {@code OTHER}). Здесь ловится следующая такая копия, которую ещё никто не
     * разобрал поимённо.
     */
    @Test
    void noJsFileHardcodesServerEnumConstantNames() {
        Map<String, List<String>> offenders = new LinkedHashMap<>();
        List<String> names = Stream.of(
                        Arrays.stream(InterviewMode.values()).map(Enum::name),
                        Arrays.stream(QuestionIssueCategory.values()).map(Enum::name),
                        Arrays.stream(Difficulty.values()).map(Enum::name))
                .flatMap(s -> s)
                .toList();

        for (Path file : filesUnder(JS, ".js")) {
            String code = stripComments(read(file));
            List<String> found = names.stream()
                    .filter(n -> code.contains("'" + n + "'") || code.contains("\"" + n + "\"")
                            || code.contains(n + ":"))
                    .distinct()
                    .toList();
            if (!found.isEmpty()) {
                offenders.put(file.toString(), found);
            }
        }

        assertThat(offenders)
                .describedAs("""
                        JS называет серверную константу по имени — это копия перечисления, \
                        невидимая компилятору. Данные должен отдавать сервер: список режимов \
                        (${sessionModes}), категории жалобы (<template id="report-category-options">), \
                        цвета сложности — с уже отрисованной легенды.""")
                .isEmpty();
    }

    @Test
    void reviewResultNamesItselfInRussian() {
        for (ReviewResult result : ReviewResult.values()) {
            assertThat(result.label())
                    .describedAs("подпись результата " + result)
                    .isNotBlank()
                    .doesNotContain(result.name());
        }
        assertThat(Arrays.stream(ReviewResult.values()).map(ReviewResult::label).toList())
                .doesNotHaveDuplicates();
    }

    @Test
    void sessionModeSelectIsRenderedFromTheServerList() {
        String settings = read(TEMPLATES.resolve("settings.html"));
        assertThat(settings)
                .describedAs("селект режима должен рендериться из модели sessionModes, "
                        + "иначе подписи и порядок снова разъедутся с Java")
                .contains("th:each=\"m : ${sessionModes}\"")
                .contains("data-cta=${m.startAction}")
                .contains("data-unlimited=${m.unlimited}");

        String sessionForm = read(JS.resolve("settings/session-form.js"));
        assertThat(sessionForm)
                .describedAs("JS обязан читать подпись кнопки и признак «без счётчика» "
                        + "с выбранного <option>, а не держать свою копию")
                .doesNotContain("MODE_CTA")
                .doesNotContain("'TRAINING'");
    }
}
