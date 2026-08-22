package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Экран вопроса в руке: контекст — строка состояния, работа — на первом экране.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер (Chromium и WebKit, DPR2, экзамен с примером кода, ОДИН и тот же вопрос) —
 * высота вспомогательного рельса и положение первого варианта ответа:
 *
 * <pre>
 *   вьюпорт      360×780   390×844   430×932
 *   рельс до        168       147       147
 *   1-й вариант     1286      1216      1163      ← сгиб 780 / 844 / 932
 * </pre>
 *
 * На телефоне ни одного варианта ответа не было видно без прокрутки. Причины две, и
 * обе — следствие правил, верных для БОЛЬШОГО экрана:
 * <ol>
 *   <li>дети рельса объявлены {@code flex: 1 1 14rem} — это верно для колонки 288px
 *       справа, где каждый показатель берёт свою строку, и неверно для полосы во всю
 *       ширину телефона: шесть показателей выстраивались лестницей над вопросом;</li>
 *   <li>справка «О теме» (305px) стоит в колонке задания — на большом экране рядом с
 *       работой, а в схлопнутой стопке поперёк пути от вопроса к ответу.</li>
 * </ol>
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>на телефоне рельс отвечает на один вопрос — «где я», а аналитика читается
 *       там, где собрана целиком;</li>
 *   <li>то, что ушло с телефонной полосы, доступно на странице аналитики — иначе это
 *       не перенос, а потеря;</li>
 *   <li>пока рабочее место одноколоночное, справочный материал идёт ПОСЛЕ работы, и
 *       порог этого правила смыкается с порогом самого рабочего места;</li>
 *   <li>зазоры принадлежат блокам, а не протекают из их нутра.</li>
 * </ol>
 */
class MobileContextContractTest {

    private static final Pattern CSS_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    /** Порог телефона: рельс перестаёт быть колонкой показателей. */
    private static final String PHONE = "@media (max-width: 767px)";
    /** Порог одноколоночного рабочего места. */
    private static final String ONE_COLUMN = "@media (max-width: 1279px)";
    /** Порог, на котором рабочее место становится двухколоночным. */
    private static final String TWO_COLUMN = "@media (min-width: 1280px)";

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String focusCss() throws IOException {
        return CSS_COMMENT.matcher(read("static/css/pages-focus.css")).replaceAll("\n");
    }

    /**
     * Тела ВСЕХ медиазапросов с этим условием — по балансу скобок. Две тонкости, и
     * обе пойманы при написании гейта: одно и то же условие в файле встречается не
     * раз (порог телефона и порог одноколоночного места набраны по нескольку блоков),
     * поэтому брать первое вхождение нельзя; а внутри блока лежат вложенные правила,
     * поэтому «до первой закрывающей скобки» отрезало бы его на первом же селекторе.
     */
    private static String mediaBlocks(String css, String query) {
        StringBuilder collected = new StringBuilder();
        int from = 0;
        while (true) {
            int start = css.indexOf(query, from);
            if (start < 0) {
                break;
            }
            int open = css.indexOf('{', start);
            int depth = 0;
            for (int i = open; i < css.length(); i++) {
                char c = css.charAt(i);
                if (c == '{') {
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0) {
                        collected.append(css, open + 1, i).append('\n');
                        from = i;
                        break;
                    }
                }
            }
            if (depth != 0) {
                throw new AssertionError("медиазапрос " + query + " не закрыт");
            }
        }
        assertThat(collected.length()).as("медиазапрос %s исчез из pages-focus.css", query).isPositive();
        return collected.toString();
    }

    /** На телефоне полоса контекста говорит «где я», а не показывает шесть чисел лестницей. */
    @Test
    void thePhoneRailIsAStatusLineAndNotAColumnOfMetrics() throws IOException {
        String phone = mediaBlocks(focusCss(), PHONE);

        assertThat(phone).as("дети рельса снова берут меру колонки (14rem) — на телефоне "
                        + "это лестница из шести показателей над вопросом, замер до правки: "
                        + "рельс 147–168px, первый вариант ответа за сгибом")
                .contains("flex: 0 1 auto");
        assertThat(phone).as("аналитика вернулась в телефонную полосу контекста")
                .contains(".focus-bank-facts")
                .contains(".today-chip")
                .contains("display: none");
        assertThat(phone).as("действия рельса перестали быть тач-целью во всю ширину")
                .contains("flex: 1 1 100%");
    }

    /**
     * Низкое мобильное окно (ландшафт телефона): хром не съедает половину высоты
     * (RSP-02, REF-008).
     *
     * <p>Замер 667×375 (Chromium, эмуляция landscape): sticky-шапка 44 + fixed-док
     * 64 = 108px постоянного налога (29% высоты), окно контента 267px; заголовок
     * вопроса стартовал на y=451 — целиком за сгибом. Orientation/max-height-
     * запросов в CSS не было вовсе: вертикальный бюджет никем не управлялся.</p>
     *
     * <p>Закреплено (@media низкого мобильного окна, порог по ВЫСОТЕ — довод
     * вертикальный бюджет, а не поворот): шапка уходит в поток (после прокрутки
     * не ест высоту; бренд и тема доступны прокруткой вверх — перенос, не потеря);
     * док компактен — ярус один, иконка В СТРОКУ с подписью, а высоту диктует
     * токен --mobile-nav-height, который читает и body-отступ (переопределение
     * токена держит их синхронными; литерал высоты в двух местах разъехался бы).</p>
     */
    @Test
    void lowMobileWindowGetsCompactChrome() throws IOException {
        String shell = CSS_COMMENT.matcher(read("static/css/shell.css")).replaceAll("");
        String low = "@media (max-width: 900px) and (max-height: 480px)";
        int at = shell.indexOf(low);
        assertThat(at)
                .as("компакт-композиции низкого мобильного окна нет: на 667×375 хром "
                        + "снова 108px из 375, вопрос за сгибом целиком")
                .isNotNegative();
        String block = shell.substring(at, shell.indexOf("\n}", at));
        assertThat(block)
                .as("шапка осталась липкой: 44px вычитаются из каждого прокрученного экрана")
                .contains("position: static");
        assertThat(block)
                .as("высота дока задана не токеном --mobile-nav-height: body-отступ "
                        + "разъедется с реальной высотой полосы")
                .contains("--mobile-nav-height: 44px");
        assertThat(block)
                .as("пункт дока не перестроен в строку: два яруса (иконка над подписью) "
                        + "не помещаются в 44px честно")
                .containsPattern("\\.mobile-nav a \\{[^}]*flex-direction: row");
    }

    /**
     * Компакт баннера незавершённой сессии (RSP-01) действует и в НИЗКОМ окне.
     *
     * <p>Замер 667×375: баннер разжался обратно до 148px — компакт жил только в
     * `@media (max-width: 600px)`, а ландшафт телефона шире (667) и ниже (375).
     * Довод компакта — дефицит МЕСТА, а не конкретная ось: условие блока обязано
     * покрывать оба дефицита (узко ИЛИ низко-мобильно).</p>
     */
    @Test
    void resumeBannerCompactCoversTheLowWindowToo() throws IOException {
        String focus = read("static/css/pages-focus.css");
        assertThat(focus)
                .as("компакт-блок баннера не расширен на низкое окно: в ландшафте "
                        + "телефона баннер снова трёхъярусный (148px)")
                .containsPattern("@media \\(max-width: 600px\\),\\s*"
                        + "\\(max-width: 900px\\) and \\(max-height: 480px\\)");

        String controls = read("static/css/components-controls.css");
        assertThat(controls)
                .as("ранг .btn-compact не действует в низком окне — кнопка «Продолжить» "
                        + "снова полноразмерная в самом тесном хроме")
                .containsPattern("@media \\(max-width: 600px\\),\\s*"
                        + "\\(max-width: 900px\\) and \\(max-height: 480px\\)");
    }

    /**
     * Перенос, а не потеря: всё, что ушло с телефонной полосы, читается на странице
     * аналитики. Без этой проверки правило выше — просто «спрятали, чтобы влезло».
     */
    @Test
    void whatThePhoneDropsIsReadableInAnalytics() throws IOException {
        String stats = read("templates/stats.html");

        assertThat(stats).as("освоенные карточки не читаются в аналитике").contains("Освоено");
        assertThat(stats).as("точность первых попыток не читается в аналитике")
                .contains("Точность");
        assertThat(stats).as("серия занятий не читается в аналитике")
                .containsPattern("подряд");
        assertThat(stats).as("очередь к повторению не читается в аналитике")
                .contains("к повтору");
    }

    /**
     * Справочный материал не стоит между вопросом и ответом, пока место одноколоночное.
     * Замер на 390×844 (экзамен с кодом): первый вариант 1216 → 809, вопрос 282 → 211.
     *
     * <p>iter-123 — УСЛОВИЕ БЫЛО ЗАПИСАНО ШИРИНОЙ, А ОЗНАЧАЛО ДРУГОЕ. Правило стояло в
     * `@media (max-width: 1279px)` и работало, пока одноколоночность совпадала с узким
     * окном. Как только рабочее место стало включаться по СОДЕРЖИМОМУ поверхности,
     * совпадение кончилось: у флешкарты до раскрытия колонка одна на любой ширине, и
     * справка снова встала между вопросом и действием (замер 1440×900: «Показать ответ»
     * на 917 при сгибе 900). Правило теперь безусловное, а рабочее место перебивает его
     * собственным `display: grid`.</p>
     */
    @Test
    void referenceMaterialFollowsTheWorkWhileTheDeskIsOneColumn() throws IOException {
        String css = focusCss();

        assertThat(css).as("рабочая колонка перестала быть flex-контейнером — "
                        + "без него порядок блоков не переставить")
                .containsPattern("\\.focus-main \\{\\s*display: flex;\\s*flex-direction: column;");
        // Правило переставляет теперь ОДИН узел — обёртку спутников, внутри которой
        // живёт справка (и вместе с ней ход сессии и заметка). Проверять имя
        // `.topic-brief` в селекторе больше нельзя: это было бы требование к
        // написанию правила, а не к его смыслу. Смысл — справка идёт ЗА работой,
        // и он держится двумя фактами: обёртка переставляется, справка внутри неё.
        assertThat(css).as("справка о теме снова стоит между вопросом и вариантами: "
                        + "305px поперёк пути к ответу")
                .containsPattern("\\.focus-main \\.focus-companions \\{ order: 1");
        assertThat(read("templates/focus-training.html"))
                .as("справка вне обёртки спутников — правило перестановки её не достанет")
                .containsPattern("(?s)class=\"focus-companions\".{0,4000}class=\"topic-brief\"");
        assertThat(css).as("хвост страницы («сообщить о проблеме») обогнал справку")
                .containsPattern("\\.report-zone \\{ order: 2");
        assertThat(css).as("правило перестановки снова заперто в медиазапрос по ширине — "
                        + "оно перестанет действовать там, где колонка одна на широком экране")
                .doesNotContainPattern("@media \\(max-width: 1279px\\) \\{\\s*html\\[data-design\\] \\.focus-page \\.focus-main \\{");
    }

    /**
     * Рабочее место включается по СОДЕРЖИМОМУ поверхности, а не по списку режимов.
     *
     * <p>Перечень TYPED_RECALL / INTERVIEW / CODE_CHALLENGE был не полон: FLASHCARD и
     * STUDY в него не входили, и их рабочая фаза жила стопкой под вопросом, материалами
     * и справкой. Замер главного действия от верха документа: STUDY «Проверить себя»
     * 768→1623, 1280→2353, 1440→2300, 1920→2319, 2560→2142 — ни на одной ширине от 768
     * действие не попадало на первый экран.</p>
     *
     * <p>Обратная сторона так же важна: голая кнопка второй колонки не заслуживает.
     * Первая сборка включала место по `.flashcard-phase`, и снимок 1440×900 показал
     * правую колонку с бейджем сверху, кнопкой снизу и ~900px пустоты между ними.</p>
     */
    @Test
    void theDeskIsGatedByItsSurfaceAndNotByAListOfModes() throws IOException {
        String css = focusCss();
        int desk = css.indexOf("grid-template-columns: minmax(0, var(--workspace-brief))");
        assertThat(desk).as("правило рабочего места исчезло из pages-focus.css").isNotNegative();
        String gate = css.substring(Math.max(0, desk - 1200), desk);

        // ПРАВИЛО ТО ЖЕ, НАПИСАНИЕ ДРУГОЕ (iter-138). Здесь стоял перечень из четырёх
        // признаков поверхности, и гейт требовал ровно его. Перечень оказался неполон
        // второй раз подряд (сначала были режимы, потом признаки): фаза вспоминания
        // флешкарты не подходит ни под один из четырёх, и композиции у неё не было.
        // Теперь каждая поверхность носит общий класс, и гейт проверяет ЕГО.
        assertThat(gate).as("рабочее место включается не по признаку поверхности — "
                        + "значит, снова по перечню, который уже дважды оказывался неполон")
                .contains(":has(.workspace-surface:not(.hidden))");
        assertThat(gate).as("`hidden` в гейте обязателен: приёмник разбора стоит в разметке "
                        + "всегда, и до заполнения скриптом композиции быть не должно")
                .doesNotContain(":has(.workspace-surface)}");
        assertThat(gate).as("место снова включается по СПИСКУ РЕЖИМОВ: перечень не полон "
                        + "по построению — забытый режим молча остаётся стопкой")
                .doesNotContain("data-session-mode=");
        assertThat(gate).as("РАБОЧЕЕ МЕСТО включается по самой фазе, а не по её содержимому: "
                        + "у флешкарты до раскрытия в поверхности одна кнопка, и правая "
                        + "колонка вырождается в ~900px пустоты. (У фазы вспоминания с "
                        + "iter-138 есть СВОЯ композиция — карточка и рельс спутников, — "
                        + "но она объявлена отдельно и второй колонкой берёт спутников, "
                        + "а не голое действие.)")
                .doesNotContain(":has(.flashcard-phase)");
    }

    /**
     * Меры принадлежат рабочему месту, а не одному его виду. Замер 1440×900, фаза
     * изучения: без объявленных мер колонки встали «24.6px 892.2px» вместо «602px 512px».
     */
    @Test
    void everyKindOfDeskDeclaresItsMeasures() throws IOException {
        String css = focusCss();
        int measures = css.indexOf("--workspace-brief: 48rem;");
        assertThat(measures).as("меры рабочего места исчезли").isNotNegative();
        String owners = css.substring(Math.max(0, measures - 800), measures);

        assertThat(owners).as("меры объявлены не для того же состава, который включает "
                        + "композицию: `minmax(var(--workspace-desk-min), 1fr)` разрешится "
                        + "в пустоту и колонка задания схлопнется до 25px")
                .contains(":has(.workspace-surface:not(.hidden))");
    }

    /**
     * Главное действие фазы остаётся в поле зрения: разбор занимает 599–1311px, и кнопка
     * под ним уезжает ровно настолько, насколько длинен текст.
     */
    @Test
    void thePhaseActionStaysWithinReach() throws IOException {
        String css = focusCss();

        assertThat(css).as("действие фазы перестало быть закреплённым — при длинном разборе "
                        + "оно снова уедет за нижнюю кромку (замер: 768→1623, 1280→2353)")
                .containsPattern("\\.flashcard-phase > form,[\\s\\S]{0,120}position: sticky");
        assertThat(css).as("на телефоне закреплённое действие снова ляжет под таббар")
                .contains("bottom: calc(var(--mobile-nav-height)");
    }

    /**
     * Зазор принадлежит блоку. Замер цены flex-контекста: шапка вопроса 26→34px,
     * материалы 389→437px, документ +48px — потому что внутренние поля перестали
     * схлопываться сквозь границу родителя.
     */
    @Test
    void gapsBelongToBlocksAndDoNotLeakFromInside() throws IOException {
        String css = focusCss();

        assertThat(css).as("отступ снова задаёт строка ВНУТРИ шапки вопроса — "
                        + "конструкция ломается от смены контекста форматирования")
                .containsPattern("\\.question-zone-head \\{ margin-bottom: 0");
        assertThat(css).as("поля материалов вопроса снова работают снаружи блока")
                .containsPattern("\\.question-materials > \\* \\{ margin-block: 0");
        assertThat(css).as("между несколькими материалами (код и схема) пропал ритм")
                .containsPattern("\\.question-materials > \\* \\+ \\*");
    }
}
