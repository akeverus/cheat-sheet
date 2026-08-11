package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Шапка страницы существует в одном экземпляре, а вкладки аналитики не обещают несуществующего.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * До iter-61 одна и та же шапка была реализована дважды: {@code .page-head} на /stats и
 * собственный {@code .bookmarks-intro} на /bookmarks. Копии уже разошлись — мера
 * поясняющей строки была {@code 42rem} у одной и {@code 62ch} у другой, — но ни один гейт
 * этого не видел: обе страницы по отдельности выглядели нормально, и расхождение
 * проявлялось только при сравнении двух скриншотов рядом. Это ровно «две системы для одной
 * задачи», которые мандат запрещает оставлять после миграции.
 *
 * <p>Вторая половина дефекта — вертикальная. Шапка стояла стопкой из трёх строк и занимала
 * 177px на 1440×900, при том что самая широкая её строка была 660px в полосе шириной
 * 1105px: 445px горизонтали не использовал никто, а платил за это первый экран. Карточка
 * «Повторить сегодня» уезжала на y=728 и не помещалась в окно на 1280×720, 1280×800,
 * 1366×768 и 1440×900 — то есть ровно на ходовых MacBook.
 *
 * <h2>Почему гейт, а не «мы же помним»</h2>
 * Класс дефекта — <b>дубль на второй странице</b>. Он рождается не правкой существующего
 * кода, а добавлением следующей страницы: автор новой шапки скопирует ближайшую разметку и
 * заведёт под неё локальный класс, потому что так быстрее и потому что глобальный
 * {@code .page-head} лежит в другом файле. Тест ниже сначала требует общий компонент на
 * обеих страницах, а потом — прямо запрещает воскрешать снятые локальные классы.
 *
 * <h2>Перенос шапки управляется масштабом шрифта, а не брейкпойнтом</h2>
 * {@code flex-basis} пояснения задан в {@code rem}, поэтому полоса САМА расстраивается в
 * стопку на крупном шрифте (personalization-bootstrap ставит font-size на {@code <html>}).
 * Замена на {@code px} или на медиазапрос выглядит эквивалентной правкой и молча ломает
 * ровно тот сценарий, ради которого правило написано, — увеличенный текст на узком окне.
 * Поэтому единица измерения закреплена явно.
 *
 * <h2>Вкладка, за которой ничего нет</h2>
 * Вкладка «Обзор» содержала ровно одну карточку; после подъёма карточки на уровень
 * страницы панель осталась бы пустой. Пустая вкладка — не косметика: она попадает в
 * клавиатурный обход ←→/Home/End и обещает раздел, которого нет. Тест держит три
 * структурных условия разом: каждый {@code aria-controls} указывает на существующую панель,
 * ровно одна вкладка выбрана изначально, и {@code defaultHash} в JS совпадает с
 * {@code data-tab-hash} первой вкладки — иначе «вкладка по умолчанию» начнёт писаться в URL
 * и deep-link перестанет быть обратимым.
 */
class PageHeadUnificationContractTest {

    private static final Path TEMPLATES = Path.of("src/main/resources/templates");
    private static final Path STATIC = Path.of("src/main/resources/static");

    /**
     * Страницы, обязанные использовать общую шапку.
     *
     * <p>{@code session-summary.html} добавлена в iter-63. До неё экран итогов был
     * единственным из шести без видимого заголовка: {@code h1} стоял с
     * {@code visually-hidden}, а роль надзаголовка играл чип режима внутри карточки
     * счёта. Экран, на который пользователя приводят после сессии, не назывался.
     *
     * <p>{@code login.html} добавлена в iter-71. Страница входа была последней с
     * собственной тройкой {@code .login-eyebrow} / {@code .login-title} /
     * {@code .login-lead} — той самой копией шапки, которую iter-61 снял с четырёх
     * остальных. Копия уже разошлась с оригиналом: надзаголовок печатал
     * «Multi-режим» (имя значения {@code app.security.mode}), а пояснение не
     * ограничивалось мерой и тянулось во всю полосу.
     */
    private static final List<String> PAGES_WITH_HEAD =
            List.of("stats.html", "bookmarks.html", "settings.html", "session-summary.html",
                    "login.html");

    /**
     * Локальные шапки, снятые в iter-61 (первые четыре) и iter-62 (остальные).
     * Проверяется по употреблению в разметке/CSS-селекторе, а не по вхождению в текст:
     * комментарий, объясняющий снятие, обязан оставаться читаемым.
     */
    private static final List<String> RETIRED_HEAD_CLASSES =
            List.of("bookmarks-intro", "bookmarks-intro-lead", "stats-intro", "stats-intro-lead",
                    "stats-title", "bookmarks-title", "builder-title", "builder-intro", "builder-lead",
                    "today-hero", "today-hero-head", "today-hero-body", "today-hero-figure",
                    "today-hero-cta", "today-hero-due", "today-hero-due-label",
                    // iter-63: чип режима внутри карточки счёта. Режим — метаданные страницы,
                    // а не элемент результата; его место — надзаголовок .eyebrow в шапке.
                    "summary-mode-line",
                    // iter-71: собственная шапка страницы входа — последняя копия .page-head.
                    "login-eyebrow", "login-title", "login-lead",
                    // iter-71 же: собственная система алертов страницы входа с зашитым #c45.
                    // Об ошибке входа говорит тот же `.notice`, что и на остальных семи
                    // страницах; вторая система сообщений — это вторая палитра статусов.
                    // (В iter-71 общий компонент звался `.inline-alert`; в iter-82 три
                    // конкурирующие системы сообщений сведены в один `.notice`.)
                    "login-alert", "login-alert-error", "login-alert-success",
                    // iter-71: страница носила грид оболочки без рельса — вся форма уезжала
                    // в колонку --sidebar-width. Класс при этом не имел ни одного правила.
                    "app-login");

    /** Файлы стилей, где жили снятые шапки: страничные и общий shell. */
    private static final List<String> HEAD_CSS_FILES =
            List.of("css/pages-bookmarks.css", "css/pages-stats.css", "css/pages-settings.css",
                    "css/pages-summary.css", "css/pages-login.css",
                    "css/shell.css", "css/components-widgets.css", "css/utilities-overrides.css");

    /**
     * Комментарии вырезаются перед сканом селекторов. Причина не гигиеническая: рядом со
     * снятым правилом обязан остаться текст, объясняющий, почему его больше нет и чем оно
     * заменено. Если тест ловит упоминание класса в прозе, он запрещает ровно ту запись,
     * которая мешает следующему автору завести дубль заново.
     */
    private String withoutComments(String source) {
        return source
                .replaceAll("(?s)<!--.*?-->", " ")
                .replaceAll("(?s)/\\*.*?\\*/", " ");
    }

    private String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Не прочитан " + path.toAbsolutePath(), e);
        }
    }

    @Test
    void обеСтраницыИспользуютОбщуюШапкуСтраницы() {
        for (String page : PAGES_WITH_HEAD) {
            String markup = read(TEMPLATES.resolve(page));
            assertThat(markup)
                    .as("%s: шапка должна быть общим компонентом .page-head", page)
                    .contains("class=\"page-head\"")
                    .contains("class=\"page-head-title\"")
                    .contains("page-head-lead");
        }
    }

    @Test
    void снятыеЛокальныеШапкиНеВоскресаютНиВРазметкеНиВСтилях() {
        List<String> found = new ArrayList<>();
        for (String page : PAGES_WITH_HEAD) {
            String markup = withoutComments(read(TEMPLATES.resolve(page)));
            for (String retired : RETIRED_HEAD_CLASSES) {
                if (markup.contains("class=\"" + retired) || markup.contains(" " + retired + "\"")) {
                    found.add(page + " → " + retired);
                }
            }
        }
        for (String name : HEAD_CSS_FILES) {
            Path css = STATIC.resolve(name);
            if (!Files.exists(css)) continue;
            String text = withoutComments(read(css));
            for (String retired : RETIRED_HEAD_CLASSES) {
                if (text.contains("." + retired + " ") || text.contains("." + retired + "{")
                        || text.contains("." + retired + ",")) {
                    found.add(css.getFileName() + " → ." + retired);
                }
            }
        }
        assertThat(found)
                .as("локальные копии шапки сняты в iter-61: страница обязана брать .page-head из shell.css")
                .isEmpty();
    }

    /**
     * Заголовок шапки опознаётся по ПОЗИЦИИ, а не по страничному классу.
     *
     * <p>До iter-62 три страницы несли три класса с одним смыслом —
     * {@code .stats-title}, {@code .bookmarks-title}, {@code .builder-title}. Кегль у них
     * совпадал, а нижний отступ уже разъехался (8px против 12px), мобильный clamp знал
     * только про два из трёх, и ни один не действовал на /settings — там заголовка на
     * экране не было вообще. Класс на {@code h1} внутри {@code .page-head-title} снова
     * заводить незачем: внутри этого контейнера может стоять только заголовок страницы.
     */
    @Test
    void заголовокШапкиОпознаётсяПоПозицииАНеПоСтраничномуКлассу() {
        for (String page : PAGES_WITH_HEAD) {
            String markup = read(TEMPLATES.resolve(page));
            Matcher head = Pattern.compile(
                            "<div class=\"page-head-title\">(.*?)</div>", Pattern.DOTALL)
                    .matcher(markup);
            assertThat(head.find()).as("%s: .page-head-title обязан быть в разметке", page).isTrue();
            Matcher h1 = Pattern.compile("<h1([^>]*)>").matcher(head.group(1));
            assertThat(h1.find()).as("%s: h1 страницы обязан лежать внутри .page-head-title", page).isTrue();
            assertThat(h1.group(1))
                    .as("%s: h1 шапки не носит собственного класса — вид задаёт "
                            + "`.page-head-title h1` (shell.css), иначе страницы снова разъедутся", page)
                    .doesNotContain("class=");
        }

        String shell = read(STATIC.resolve("css/shell.css"));
        assertThat(withoutComments(shell))
                .as("shell.css обязан объявлять единственный носитель вида заголовка шапки")
                .contains(".page-head-title h1");
    }

    @Test
    void переносШапкиУправляетсяМасштабомШрифтаАНеБрейкпойнтом() {
        String shell = read(STATIC.resolve("css/shell.css"));
        Matcher lead = Pattern.compile(
                        "\\.page-head-lead\\s*\\{([^}]*)}", Pattern.DOTALL)
                .matcher(shell);
        assertThat(lead.find()).as(".page-head-lead должен быть объявлен в shell.css").isTrue();
        String body = lead.group(1);

        Matcher basis = Pattern.compile("flex:\\s*\\d+\\s+\\d+\\s+([\\d.]+)(\\w+)").matcher(body);
        assertThat(basis.find()).as(".page-head-lead обязан задавать flex-basis: %s", body).isTrue();
        assertThat(basis.group(2))
                .as("flex-basis в rem: полоса должна расстраиваться в стопку от масштаба шрифта, "
                        + "а не от ширины окна — px или медиазапрос ломают увеличенный текст")
                .isEqualTo("rem");
    }

    /** Токены кегля из tokens.css: имя → размер в rem. */
    private java.util.Map<String, Double> fontSizeTokens() {
        java.util.Map<String, Double> tokens = new java.util.LinkedHashMap<>();
        Matcher m = Pattern.compile("--font-size-([a-z0-9]+):\\s*([\\d.]+)rem\\s*;")
                .matcher(read(STATIC.resolve("css/tokens.css")));
        while (m.find()) tokens.put(m.group(1), Double.parseDouble(m.group(2)));
        assertThat(tokens).as("tokens.css обязан объявлять шкалу кегля").isNotEmpty();
        return tokens;
    }

    /** Одно значение кегля → rem. Понимает токен, rem и px. */
    private double toRem(String raw, java.util.Map<String, Double> tokens) {
        String value = raw.trim();
        Matcher token = Pattern.compile("var\\(\\s*--font-size-([a-z0-9]+)\\s*\\)").matcher(value);
        if (token.find()) {
            Double size = tokens.get(token.group(1));
            assertThat(size).as("неизвестный токен кегля: %s", value).isNotNull();
            return size;
        }
        Matcher rem = Pattern.compile("^([\\d.]+)rem$").matcher(value);
        if (rem.find()) return Double.parseDouble(rem.group(1));
        Matcher px = Pattern.compile("^([\\d.]+)px$").matcher(value);
        if (px.find()) return Double.parseDouble(px.group(1)) / 16.0;
        throw new AssertionError("кегль '" + value + "' не разобран: тест умеет токен, rem и px");
    }

    /** Объявление кегля → пара «пол, потолок» в rem. Для не-clamp пол равен потолку. */
    private double[] floorAndCeiling(String declaration, java.util.Map<String, Double> tokens) {
        Matcher clamp = Pattern.compile("clamp\\(([^,]+),([^,]+),([^,]+?)\\)\\s*$")
                .matcher(declaration.trim());
        if (clamp.find()) {
            return new double[]{toRem(clamp.group(1), tokens), toRem(clamp.group(3), tokens)};
        }
        double single = toRem(declaration, tokens);
        return new double[]{single, single};
    }

    /** Единственное объявление кегля для селектора во всём shell.css. */
    private String soleFontSizeOf(String selector, String shell) {
        Matcher rule = Pattern.compile(Pattern.quote(selector) + "\\s*\\{([^}]*)}", Pattern.DOTALL)
                .matcher(withoutComments(shell));
        List<String> sizes = new ArrayList<>();
        while (rule.find()) {
            Matcher size = Pattern.compile("font-size:\\s*([^;]+);").matcher(rule.group(1));
            while (size.find()) sizes.add(size.group(1));
        }
        assertThat(sizes)
                .as("%s: кегль обязан задаваться ровно один раз на весь shell.css. Второе "
                        + "объявление — это медиазапрос или перебивка; для ЭТОЙ пары медиазапрос "
                        + "уже однажды оказался мёртвым кодом из-за порядка правил, и «правило "
                        + "есть» читалось как «правило работает»", selector)
                .hasSize(1);
        return sizes.get(0);
    }

    /**
     * Заголовок шапки не может стать мельче пояснения, которое он озаглавливает.
     *
     * <h2>Дефект, а не гипотеза</h2>
     * До iter-75 заголовок клампился (iter-62) с полом {@code 1.1rem} = 17.6px, а пояснение
     * не клампилось вовсе и держало {@code --font-size-md} = 18px на любой ширине. На
     * 360…430px это давало перевёрнутую иерархию: замер WebKit при DPR 2 показал h1 17.6px
     * против пояснения 18.0px — название страницы было МЕЛЬЧЕ абзаца под ним. Ни один гейт
     * этого не видел: каждое из двух правил по отдельности выглядело осмысленным, дефект
     * жил в их разности.
     *
     * <h2>Почему пол сравнивается с потолком, а не пол с полом</h2>
     * Промежуточные значения обоих {@code clamp} зависят от {@code vw}, то есть от ширины
     * окна, которой тест по исходнику не знает. Сравнение «пол заголовка против потолка
     * пояснения» от ширины не зависит вообще: если минимум одного строго больше максимума
     * другого, порядок держится на всех ширинах разом. Условие сильнее фактически
     * необходимого и потому не даёт правилу тихо выродиться до «на нашем макбуке сходится».
     */
    @Test
    void кегльЗаголовкаШапкиНикогдаНеОпускаетсяДоКегляПояснения() {
        String shell = read(STATIC.resolve("css/shell.css"));
        java.util.Map<String, Double> tokens = fontSizeTokens();

        double[] title = floorAndCeiling(
                soleFontSizeOf("html[data-design] .page-head-title h1", shell), tokens);
        double[] lead = floorAndCeiling(
                soleFontSizeOf("html[data-design] .page-head-lead", shell), tokens);

        assertThat(title[0])
                .as("пол заголовка (%srem) обязан быть СТРОГО выше потолка пояснения (%srem): "
                        + "иначе на узком окне название страницы становится мельче абзаца, "
                        + "который оно озаглавливает", title[0], lead[1])
                .isGreaterThan(lead[1]);
        assertThat(lead[0])
                .as("пояснение обязано ужиматься на узком окне: без узкой ветки оно занимало "
                        + "63…72%% высоты шапки при полезном сгибе телефона в 780px")
                .isLessThan(lead[1]);
    }

    /**
     * Строки шапки разделяет ровно одна величина.
     *
     * <p>До iter-75 разделение нёс нижний {@code margin} заголовка при нулевом
     * {@code row-gap}. Величина была одна, но не та: {@code row-gap} действует только на
     * переносе — то есть ровно тогда, когда разделять есть что, — а margin виден всегда, в
     * том числе в рядной раскладке, где под заголовком в его колонке пусто. Замер на
     * десктопе (1024…2560, три страницы): шапка 93 → 81px, все 12px — тот самый отступ,
     * который никого ни от чего не отделял.
     *
     * <h2>Что именно стережёт гейт</h2>
     * Не «правильную» величину, а их количество. Класс дефекта — не ошибка в числе, а
     * возврат второй системы: следующий автор, увидев склейку на переносе, вернёт margin
     * заголовку, не тронув row-gap, и на переносе они сложатся. Обратный перекос тоже
     * ловится: обнулив обе, получим заголовок, склеенный с пояснением.
     */
    @Test
    void строкиШапкиРазделяетРовноОднаВеличина() {
        String shell = withoutComments(read(STATIC.resolve("css/shell.css")));

        Matcher head = Pattern.compile("html\\[data-design] \\.page-head\\s*\\{([^}]*)}", Pattern.DOTALL)
                .matcher(shell);
        assertThat(head.find()).as(".page-head обязан быть объявлен в shell.css").isTrue();
        Matcher rowGap = Pattern.compile("row-gap:\\s*([^;]+);").matcher(head.group(1));
        assertThat(rowGap.find()).as(".page-head обязан задавать row-gap явно").isTrue();
        boolean gapCarries = !rowGap.group(1).trim().matches("0(px|rem)?|var\\(--space-0\\)");

        Matcher title = Pattern.compile(
                        "html\\[data-design] \\.page-head-title h1\\s*\\{([^}]*)}", Pattern.DOTALL)
                .matcher(shell);
        assertThat(title.find()).as(".page-head-title h1 обязан быть объявлен в shell.css").isTrue();
        Matcher margin = Pattern.compile("margin:\\s*([^;]+);").matcher(title.group(1));
        assertThat(margin.find()).as("заголовок шапки обязан задавать margin явно").isTrue();
        String[] sides = margin.group(1).trim().split("\\s+");
        String bottom = sides.length >= 3 ? sides[2] : sides[sides.length - 1];
        boolean marginCarries = !bottom.matches("0(px|rem)?|var\\(--space-0\\)");

        assertThat(gapCarries && marginCarries)
                .as("разделение строк шапки несут ОБА: row-gap полосы и нижний margin заголовка. "
                        + "На переносе они складываются, а вне переноса margin добавляет высоту, "
                        + "ничего не разделяя")
                .isFalse();
        assertThat(gapCarries || marginCarries)
                .as("строки шапки не разделяет ничто: на переносе заголовок склеится с пояснением")
                .isTrue();
    }

    @Test
    void вкладкиАналитикиСсылаютсяТолькоНаСуществующиеПанели() {
        String stats = read(TEMPLATES.resolve("stats.html"));

        List<String> controls = new ArrayList<>();
        Matcher tab = Pattern.compile("role=\"tab\"[^>]*?aria-controls=\"([^\"]+)\"", Pattern.DOTALL)
                .matcher(stats);
        while (tab.find()) controls.add(tab.group(1));

        Set<String> panels = new LinkedHashSet<>();
        Matcher panel = Pattern.compile("role=\"tabpanel\"\\s+id=\"([^\"]+)\"", Pattern.DOTALL).matcher(stats);
        while (panel.find()) panels.add(panel.group(1));

        assertThat(controls).as("вкладки аналитики должны быть в разметке").isNotEmpty();
        assertThat(panels)
                .as("каждая вкладка обязана вести в существующую панель: пустая вкладка "
                        + "попадает в клавиатурный обход и обещает раздел, которого нет")
                .containsAll(controls);
        assertThat(panels)
                .as("панель без вкладки недостижима при включённом JS")
                .containsExactlyElementsOf(controls);
    }

    @Test
    void ровноОднаВкладкаВыбранаИСовпадаетСДефолтнымHash() {
        String stats = read(TEMPLATES.resolve("stats.html"));

        List<String> selected = new ArrayList<>();
        List<String> hashes = new ArrayList<>();
        Matcher tab = Pattern.compile(
                        "role=\"tab\"[^>]*?aria-selected=\"(true|false)\"[^>]*?data-tab-hash=\"([^\"]+)\"",
                        Pattern.DOTALL)
                .matcher(stats);
        while (tab.find()) {
            hashes.add(tab.group(2));
            if ("true".equals(tab.group(1))) selected.add(tab.group(2));
        }

        assertThat(hashes).as("у каждой вкладки должен быть data-tab-hash для deep-link").hasSize(3);
        assertThat(selected)
                .as("серверная разметка обязана выбирать ровно одну вкладку — иначе до JS видно две панели")
                .hasSize(1);
        assertThat(selected.get(0))
                .as("выбрана должна быть первая вкладка: JS без hash и без localStorage активирует индекс 0")
                .isEqualTo(hashes.get(0));

        String js = read(STATIC.resolve("js/stats/index.js"));
        Matcher defaultHash = Pattern.compile("defaultHash:\\s*'([^']+)'").matcher(js);
        assertThat(defaultHash.find()).as("stats/index.js обязан задавать defaultHash").isTrue();
        assertThat(defaultHash.group(1))
                .as("defaultHash — это hash первой вкладки; разойдись он с разметкой, "
                        + "вкладка по умолчанию начнёт писаться в URL и deep-link перестанет быть обратимым")
                .isEqualTo(hashes.get(0));
    }
}
