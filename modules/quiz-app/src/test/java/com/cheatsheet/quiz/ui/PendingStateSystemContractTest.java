package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Гейт единственности системы ожидания (iter-84, §24).
 *
 * <p>До iter-84 «идёт работа» приезжало из ЧЕТЫРЁХ независимых мест, и замер
 * WebKit 1440×900 дал на одно понятие четыре вида:</p>
 *
 * <pre>
 *   A  [aria-busy]                                    components-controls.css   opacity 0.82
 *   B  .settings-page .data-export-actions [aria-busy] pages-settings.css       opacity 0.70
 *   C  #interview-submit.is-submitting                 pages-focus + pixel      opacity 0.88
 *   D  [data-diagram-loading]                          shell.css                своя подпись
 * </pre>
 *
 * <p>Ставили это ПЯТЬ модулей пятью разными наборами ключей, а компонентный
 * файл нёс пять копий исключения {@code :not(#interview-submit)} — то есть знал
 * про идентификатор одной кнопки одной страницы, чтобы не мешать соседней
 * системе. Здесь проверяется не внешний вид (его держит замер в браузере), а
 * то, что система осталась ОДНА: такой дефект возвращается тихо — достаточно
 * одного нового экрана, которому «проще поставить свой класс».</p>
 */
class PendingStateSystemContractTest {

    private static final Pattern CSS_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private static final Pattern TEMPLATE_COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);
    /**
     * Блочные и строчные комментарии JS.
     *
     * <p>Хвост строчного комментария описан через {@code [^\n]}, а НЕ через
     * {@code .}: флаг DOTALL нужен блочной альтернативе и распространяется на
     * весь шаблон, из-за чего {@code //.*$} съедал файл целиком от первого
     * комментария до конца — и все проверки тела «проходили» на пустоте.
     * Ретроспектива {@code (?<!:)} отделяет комментарий от {@code https://}.</p>
     */
    private static final Pattern JS_COMMENT =
            Pattern.compile("/\\*.*?\\*/|(?<!:)//[^\\n]*", Pattern.DOTALL);

    /** Ключи вытесненных систем C и D. Любой из них — след неудалённой реализации. */
    private static final List<String> RETIRED = List.of("is-submitting", "data-diagram-loading");

    /** Единственный владелец ARIA ожидания. */
    private static final String OWNER = "static/js/ui/pending.js";

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private List<String> manifestEntries() throws IOException {
        List<String> entries = new ArrayList<>();
        for (String line : read("css-bundle-manifest.txt").split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                entries.add(trimmed.replaceAll("\\s+", " "));
            }
        }
        return entries;
    }

    /** Все CSS каскада — в порядке манифеста, без комментариев. */
    private String structuralCss() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String entry : manifestEntries()) {
            String[] parts = entry.split(" ");
            if (parts.length == 2 && parts[1].startsWith("/css/")) {
                sb.append(CSS_COMMENT.matcher(read("static" + parts[1])).replaceAll("\n")).append('\n');
            }
        }
        return sb.toString();
    }

    /** Все модули приложения — путь относительно static/js, тело без комментариев. */
    private List<Path> jsModules() throws IOException {
        Path root = new ClassPathResource("static/js").getFile().toPath();
        try (Stream<Path> walk = Files.walk(root)) {
            return walk.filter((p) -> p.toString().endsWith(".js")).sorted().toList();
        }
    }

    /**
     * Ключей вытесненных систем нет нигде: ни в каскаде, ни в разметке, ни в JS.
     *
     * <p>Комментарии вырезаются со всех трёх сторон намеренно: проза, которая
     * объясняет, ПОЧЕМУ {@code .is-submitting} больше нет, сама содержит это имя
     * и воскрешала бы правило для теста. На этой ловушке проект уже спотыкался.</p>
     */
    @Test
    void retiredPendingKeysAreGoneFromCascadeMarkupAndBehaviour() throws IOException {
        String css = structuralCss();
        for (String key : RETIRED) {
            assertThat(css).as("ключ вытесненной системы «%s» вернулся в каскад", key).doesNotContain(key);
        }
        for (Path module : jsModules()) {
            String body = JS_COMMENT.matcher(Files.readString(module, StandardCharsets.UTF_8)).replaceAll("\n");
            for (String key : RETIRED) {
                assertThat(body).as("«%s» осталось в %s", key, module.getFileName()).doesNotContain(key);
            }
        }
    }

    /**
     * {@code aria-busy} ставит РОВНО один модуль и не описывает ни один файл каскада.
     *
     * <p>Атрибут не объявляется скринридером и вида сам по себе не даёт — как
     * ключ вида он и породил системы A и B с двумя разными прозрачностями на
     * одно понятие. Голос ожидания живёт в {@code #pending-status}, вид — в
     * {@code data-pending}.</p>
     */
    @Test
    void ariaBusyHasExactlyOneOwnerAndNoAppearance() throws IOException {
        assertThat(structuralCss()).as("aria-busy снова красит").doesNotContain("aria-busy");

        List<String> setters = new ArrayList<>();
        for (Path module : jsModules()) {
            String body = JS_COMMENT.matcher(Files.readString(module, StandardCharsets.UTF_8)).replaceAll("\n");
            if (body.contains("aria-busy")) {
                setters.add(module.getFileName().toString());
            }
        }
        assertThat(setters).as("ARIA ожидания обязан держать один модуль").containsExactly("pending.js");
    }

    /**
     * Компонент подключён на ВСЕ страницы и СРАЗУ ПОСЛЕ controls.
     *
     * <p>Позиция здесь не вкусовая: {@code [data-pending="action"][aria-disabled="true"]}
     * и {@code .btn[aria-disabled="true"]} равны по специфичности (0,3,1), и спор
     * равных внутри {@code @layer components} решает порядок в ленте. Переставить
     * строку выше — показать занятой кнопке вид «сломана» вместо «идёт работа»,
     * хотя {@code aria-disabled} она ставит именно для скринридера.</p>
     */
    @Test
    void pendingComponentIsWiredGloballyRightAfterControls() throws IOException {
        List<String> entries = manifestEntries();
        assertThat(entries).contains("* /css/components-pending.css");

        int controls = entries.indexOf("* /css/components-controls.css");
        int pending = entries.indexOf("* /css/components-pending.css");
        assertThat(controls).as("controls обязан быть в манифесте").isNotNegative();
        assertThat(pending).as("ожидание обязано побеждать «недоступно»").isEqualTo(controls + 1);
    }

    /**
     * Роль у ключа обязательна и роли ровно две.
     *
     * <p>Неявное «атрибут просто есть» через год превращается в третью роль, о
     * которой никто не договаривался: голый {@code [data-pending]} совпал бы и
     * с кнопкой, и с областью, и скелетон приехал бы на кнопку.</p>
     */
    @Test
    void pendingKeyAlwaysCarriesOneOfTwoRoles() throws IOException {
        String css = CSS_COMMENT.matcher(read("static/css/components-pending.css")).replaceAll("\n");
        Matcher m = Pattern.compile("\\[data-pending(=\"([^\"]*)\")?]").matcher(css);
        int seen = 0;
        while (m.find()) {
            seen++;
            assertThat(m.group(2)).as("селектор ожидания без явной роли").isIn("action", "block");
        }
        assertThat(seen).as("селекторы ожидания в компоненте").isPositive();

        String owner = JS_COMMENT.matcher(read(OWNER)).replaceAll("\n");
        assertThat(owner).contains("'data-pending', 'action'").contains("'data-pending', 'block'");
    }

    /**
     * Компонентный файл не знает про идентификаторы страниц.
     *
     * <p>Пять копий {@code :not(#interview-submit)} в components-controls.css —
     * это и был сигнал «две системы спорят»: компонент подстраивался под
     * страницу вместо того, чтобы её вытеснить (§2, «требует слишком много
     * исключений»).</p>
     */
    @Test
    void componentFilesCarryNoPageIdExceptions() throws IOException {
        for (String entry : manifestEntries()) {
            String[] parts = entry.split(" ");
            if (parts.length != 2 || !parts[1].startsWith("/css/components-")) {
                continue;
            }
            String css = CSS_COMMENT.matcher(read("static" + parts[1])).replaceAll("\n");
            assertThat(css).as("%s подстраивается под идентификатор страницы", parts[1])
                    .doesNotContainPattern(":not\\(#");
        }
    }

    /**
     * Голос ожидания — один живой регион, напечатанный оболочкой.
     *
     * <p>Регион создаётся сервером, а не скриптом: {@code aria-live} на узле,
     * вставленном в тот же кадр, что и текст, во многих связках не срабатывает
     * — регион обязан существовать ЗАРАНЕЕ. Оболочка есть на всех семи
     * страницах, поэтому ожидание может заговорить на любой из них.</p>
     */
    @Test
    void pendingSpeaksThroughOneShellPrintedLiveRegion() throws IOException {
        String shell = TEMPLATE_COMMENT.matcher(read("templates/fragments/shell-head.html")).replaceAll("\n");
        assertThat(shell).contains("id=\"pending-status\"")
                .contains("role=\"status\"")
                .contains("aria-live=\"polite\"")
                .contains("aria-atomic=\"true\"")
                .contains("visually-hidden");
    }

    /**
     * Ожидание молчит в спокойном режиме — и в системном, и в продуктовом.
     *
     * <p>{@code data-motion="off"} из «Оформления» — тот же отказ от движения,
     * просто сказанный внутри приложения; система, которая слушает только
     * {@code prefers-reduced-motion}, для этого человека продолжает крутиться.</p>
     */
    @Test
    void pendingMotionStopsInBothCalmModes() throws IOException {
        String css = CSS_COMMENT.matcher(read("static/css/components-pending.css")).replaceAll("\n");
        assertThat(css).contains("@media (prefers-reduced-motion: reduce)");
        assertThat(css).contains("html[data-design][data-motion=\"off\"] .pending-rune");
        assertThat(css).contains("html[data-design][data-motion=\"off\"] [data-pending=\"block\"]");
    }

    /**
     * Занятость не гасится нативным {@code disabled}.
     *
     * <p>Замерено: нативный атрибут в Chromium выбрасывает фокус в {@code <body>},
     * в WebKit оставляет — то есть человек, нажавший кнопку с клавиатуры, терял
     * место в документе ровно в момент ожидания, и терял его движко-зависимо.
     * {@code aria-disabled} сохраняет фокус в обоих, а повторное действие
     * закрывает перехват на фазе capture — он к тому же ловит клавиатуру,
     * которой {@code pointer-events} не касается.</p>
     */
    @Test
    void pendingNeverUsesNativeDisabled() throws IOException {
        String owner = JS_COMMENT.matcher(read(OWNER)).replaceAll("\n");
        assertThat(owner).as("нативный disabled уносит фокус в body").doesNotContain(".disabled =");
        assertThat(owner).contains("'aria-disabled', 'true'");
        assertThat(owner).as("перехват повторного действия обязан быть на capture")
                .contains("addEventListener('click', swallow, true)");

        String css = CSS_COMMENT.matcher(read("static/css/components-pending.css")).replaceAll("\n");
        assertThat(css).as("pointer-events: none забирает у контрола курсор progress")
                .doesNotContain("pointer-events: none");
        assertThat(css).contains("cursor: progress");
        // `.btn[aria-disabled="true"]` в controls снимает интерактивность, а
        // ожидание попадает под этот селектор — возврат обязан быть ЯВНЫМ.
        assertThat(css).as("занятая кнопка обязана вернуть себе указатель")
                .contains("pointer-events: auto");
    }

    /**
     * Замок ширины меряется в тех же единицах, в которых читается.
     *
     * <p>{@code style.minWidth} интерпретируется в ЛОКАЛЬНЫХ пикселях элемента,
     * а {@code getBoundingClientRect().width} под браузерным масштабом приезжает
     * уже в ЭКРАННЫХ — и умножается на zoom второй раз. Замерено на 1440×900,
     * зум 125%: rect=294.56 при offsetWidth=236, замок 295px разносило до 369px,
     * то есть кнопка в ожидании РОСЛА на 74px и толкала соседей. Замок,
     * поставленный против сдвига, сам сдвиг и создавал. Дефект движко-зависимый:
     * в WebKit rect под тем же зумом остаётся локальным (235.65), в Chromium нет
     * — поэтому проверка на одном движке его не видит.</p>
     */
    @Test
    void pendingWidthLockIsMeasuredInLocalPixels() throws IOException {
        String owner = JS_COMMENT.matcher(read(OWNER)).replaceAll("\n");
        assertThat(owner).as("замок ширины обязан мериться offsetWidth")
                .contains("el.offsetWidth");
        assertThat(owner).as("rect под зумом даёт экранные пиксели — замок раздувается")
                .doesNotContain("getBoundingClientRect().width");
    }

    /**
     * Отменённая отправка не считается отправкой.
     *
     * <p>Опасное действие («Завершить сессию», формы {@code .danger-zone})
     * гасит первый {@code submit} и ждёт ответа в модалке, а подтверждение
     * шлёт форму заново через {@code requestSubmit()}. Гвард однократной
     * отправки слушает то же событие на всплытии — и если он помечает форму
     * отправленной и включает ожидание ДО того, как выяснилось, что отправки
     * не было, повторную отправку глушат уже двое: сам гвард и capture-гвард
     * ожидания. Замерено в Chromium: подтверждение «Завершить сессию» не
     * порождало POST /finish вовсе — в сети пусто, страница молча оставалась
     * на месте, сессия не закрывалась и итоги не появлялись.</p>
     */
    @Test
    void submitOnceGuardIgnoresCancelledSubmissions() throws IOException {
        String guards = JS_COMMENT.matcher(read("static/js/ui/form-guards.js")).replaceAll("\n");
        assertThat(guards)
                .as("гвард однократной отправки обязан пропускать отменённый submit")
                .contains("e.defaultPrevented");
        assertThat(guards.indexOf("e.defaultPrevented"))
                .as("проверка отмены обязана стоять ДО пометки формы отправленной")
                .isLessThan(guards.indexOf("form.dataset.submitted = '1'"));
    }

    /** Свойства, которыми ожидание переопределяет вид контрола. */
    private static final List<String> PENDING_PROPERTIES =
            List.of("cursor", "opacity", "pointer-events");

    /**
     * Идентификатор страницы не смеет объявлять то же, что объявляет ожидание.
     *
     * <p>Ветка вида {@code html[data-design] #session-start-btn} внутри общего
     * правила весит (1,1,1) и побеждает ЛЮБОЕ состояние, ключуемое атрибутом,
     * включая «занято» (0,2,1) — независимо от порядка файлов, потому что спор
     * решает уже не порядок. Замерено в обоих движках: кнопка «Начать
     * тренировку» в ожидании показывала {@code cursor: pointer} вместо
     * обещанного {@code progress}. Компонент объявлял правило, которое до
     * экрана не доезжало, а прошлая версия гейта этого не видела: она читала
     * текст файла и находила в нём строку {@code cursor: progress}.</p>
     *
     * <p>Проверяется поэтому не «есть ли идентификатор» (их ещё хватает —
     * например ранг 48px у {@code #interview-submit}), а пересечение: правило с
     * идентификатором НЕ ВПРАВЕ объявлять свойство, за которое отвечает
     * ожидание. Ранг, ховер, фокус-кольцо и press-сдвиг под это не подпадают —
     * они говорят о другом.</p>
     */
    @Test
    void pageIdSelectorsNeverDeclarePendingProperties() throws IOException {
        Pattern rule = Pattern.compile("([^{}]+)\\{([^{}]*)}");
        for (String entry : manifestEntries()) {
            String[] parts = entry.split(" ");
            if (parts.length != 2 || !parts[1].startsWith("/css/components-")) {
                continue;
            }
            String css = CSS_COMMENT.matcher(read("static" + parts[1])).replaceAll("\n");
            Matcher m = rule.matcher(css);
            while (m.find()) {
                String selector = m.group(1);
                if (!Pattern.compile("#[A-Za-z][\\w-]*").matcher(selector).find()) {
                    continue;
                }
                String body = m.group(2);
                for (String property : PENDING_PROPERTIES) {
                    assertThat(body)
                            .as("%s: правило с идентификатором объявляет «%s» и перебьёт ожидание — %s",
                                    parts[1], property, selector.trim())
                            .doesNotContainPattern("(^|[;\\s])" + property + "\\s*:");
                }
            }
        }
    }
}
