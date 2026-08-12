package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Гейт единственности системы пустого состояния (iter-83, §24).
 *
 * <p>До iter-83 «здесь пока ничего нет» приезжало из ТРЁХ независимых мест:
 * {@code .empty-card-inner} в pages-focus.css, {@code .bookmarks-empty} в
 * pages-bookmarks.css и класс-пустышка {@code .stats-empty}, которому вообще
 * ни один файл не давал правил. Замер WebKit 1440×900 в самих состояниях дал
 * на одно понятие четыре размера иллюстрации (120/72/64/28px), три кегля
 * заголовка и два вида коробки. Это не варианты подачи, а отсутствие
 * компонента.</p>
 *
 * <p>Здесь проверяется не внешний вид — его держит замер в браузере, — а то,
 * что система осталась ОДНА. Дефект такого рода возвращается тихо: второй
 * системе достаточно одного нового экрана с собственной разметкой пустоты,
 * и разъезд снова начнётся с «всего одного исключения».</p>
 */
class EmptyStateSystemContractTest {

    private static final Pattern CSS_COMMENT = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private static final Pattern TEMPLATE_COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    /** Шаблоны, где пустые состояния живут сегодня. */
    private static final List<String> HOST_TEMPLATES =
            List.of("templates/focus-training.html", "templates/bookmarks.html", "templates/stats.html",
                    "templates/topics.html");

    /**
     * Имена трёх вытесненных систем. Любое из них в разметке или в CSS означает,
     * что старая реализация не удалена, а оставлена рядом с новой.
     */
    private static final List<String> RETIRED = List.of(
            "empty-card-inner", "empty-pixel-art", "empty-title", "empty-description", "empty-actions",
            "bookmarks-empty", "stats-empty",
            // Четвёртое имя, заведённое уже ПОСЛЕ сборки компонента: каталог тем
            // рисовал глиф классом `empty-state-art`, которому ни один файл не
            // давал правил, и с размером в HTML-атрибутах. Замер в браузере — в
            // комментарии topics.html. Дефект возвращается именно так: новая
            // страница, «всего одно исключение».
            "empty-state-art");

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    /** Все CSS каскада — в порядке манифеста, без комментариев. */
    private String structuralCss() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String line : read("css-bundle-manifest.txt").split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            String[] parts = trimmed.split("\\s+");
            if (parts.length == 2 && parts[1].startsWith("/css/")) {
                sb.append(CSS_COMMENT.matcher(read("static" + parts[1])).replaceAll("\n")).append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Ни одного имени вытесненных систем — ни в шаблонах, ни в каскаде.
     *
     * <p>Комментарии вырезаются с обеих сторон: проза, объясняющая, ПОЧЕМУ
     * {@code .empty-card-inner} больше нет, сама содержит это имя и воскрешала
     * бы правило для теста. На этой ловушке проект уже спотыкался дважды.</p>
     */
    @Test
    void retiredEmptyStateSystemsAreGoneFromMarkupAndCascade() throws IOException {
        String css = structuralCss();
        for (String name : RETIRED) {
            assertThat(css).as("селектор вытесненной системы «%s» вернулся в каскад", name)
                    .doesNotContain("." + name);
        }
        for (String template : HOST_TEMPLATES) {
            String markup = TEMPLATE_COMMENT.matcher(read(template)).replaceAll("\n");
            for (String name : RETIRED) {
                assertThat(markup).as("«%s» осталось в разметке %s", name, template)
                        .doesNotContain(name);
            }
        }
    }

    /**
     * Единственный файл, описывающий композицию пустого состояния, — компонентный,
     * и он подключён на ВСЕ страницы.
     *
     * <p>Гейт {@code *} здесь не формальность: пустое состояние появляется не там,
     * где его планировали, а там, где у человека не оказалось данных. Страничный
     * гейт означал бы, что на следующем экране компонент приедет без стилей.</p>
     */
    @Test
    void emptyStateComponentIsWiredGloballyAfterTheCardItSitsIn() throws IOException {
        List<String> entries = new ArrayList<>();
        for (String line : read("css-bundle-manifest.txt").split("\\R")) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                entries.add(trimmed.replaceAll("\\s+", " "));
            }
        }
        assertThat(entries).contains("* /css/components-empty-state.css");

        int card = entries.indexOf("* /css/components-card.css");
        int empty = entries.indexOf("* /css/components-empty-state.css");
        assertThat(card).as("карточка обязана быть в манифесте").isNotNegative();
        // Компонент добавляет вертикальный запас ПОВЕРХ полей карточки. Внутри
        // одного @layer при равной специфичности спор решает порядок в ленте.
        assertThat(empty).as("компонент должен идти после карточки-хозяина").isGreaterThan(card);
    }

    /**
     * Каждое пустое состояние в разметке несёт {@code .empty-state} и все три
     * его роли; коробку даёт хозяин, а не компонент.
     *
     * <p>Разметка без класса-компонента — это и есть четвёртая система в
     * зачаточном виде: она выглядит похоже ровно до первой правки ритма.</p>
     */
    @Test
    void everyEmptyStateCarriesTheComponentAndItsRoles() throws IOException {
        Pattern title = Pattern.compile("class=\"empty-state-title\"");
        int total = 0;
        for (String template : HOST_TEMPLATES) {
            String markup = TEMPLATE_COMMENT.matcher(read(template)).replaceAll("\n");
            Matcher m = title.matcher(markup);
            int titles = 0;
            while (m.find()) {
                titles++;
            }
            assertThat(titles).as("%s: пустых состояний не найдено", template).isPositive();
            total += titles;

            // Заголовок без объяснения — это вывеска: человек узнаёт, что пусто,
            // но не узнаёт почему и что делать.
            assertThat(markup).as("%s: состояние без объяснения", template)
                    .contains("class=\"empty-state-text\"");
            assertThat(markup).as("%s: состояние без выхода", template)
                    .contains("class=\"empty-state-actions\"");
            assertThat(markup).as("%s: глиф не из спрайта состояний", template)
                    .contains("empty-state-illus");
        }
        // Число сверяется с фактом, а не растёт «на всякий случай»: восемь состояний
        // трёх страниц, два состояния каталога тем (банк не импортирован / отбор ничего
        // не нашёл) и одно у журнала занятий в аналитике — «журнал пуст» на чистой базе,
        // где сессий ещё не было.
        assertThat(total).as("все одиннадцать состояний четырёх страниц").isEqualTo(11);
    }

    /**
     * Компонент описывает композицию и НЕ описывает коробку (доктрина FRD-053,
     * та же, что у {@code .notice}).
     *
     * <p>Если бы фон и кромка жили в компоненте, пустая выборка на /bookmarks —
     * она стоит ВНУТРИ карточки отбора — получила бы карточку в карточке.</p>
     */
    @Test
    void componentOwnsCompositionNotTheBox() throws IOException {
        String css = CSS_COMMENT.matcher(read("static/css/components-empty-state.css")).replaceAll("\n");

        assertThat(css).contains("display: grid").contains("justify-items: center").contains("text-align: center");
        assertThat(css).as("коробку даёт хозяин").doesNotContain("background").doesNotContain("border-radius");
        assertThat(css).doesNotContain("box-shadow");

        // Кегль заголовка держит компонент, а не уровень тега: h1 на экране
        // вопроса и h2 в секциях — это семантика положения, а не подача. Пока
        // размер приезжал из reset.css, одно сообщение набиралось 48/36/28px.
        assertThat(css).contains("font-size: var(--font-size-xl)");
        // Мера у центрированной строки своя: общий --measure (72ch, а при
        // широком чтении 100ch) рассчитан на текст с твёрдой левой кромкой.
        assertThat(css).contains("max-width: min(100%, 52ch)");
    }

    /**
     * Глифы состояний живут в спрайте и наследуют тему через currentColor;
     * растровых маскотов в пустых состояниях нет.
     *
     * <p>PNG не наследует тему, не масштабируется целочисленно от одного
     * источника и заводит второй язык рядом со спрайтом, который для этих
     * состояний и создан (§14).</p>
     */
    @Test
    void emptyStateGlyphsComeFromTheStateSpriteOnly() throws IOException {
        String sprite = read("templates/fragments/state-illustrations.html");
        // Список требуемых символов не перечисляется здесь руками: он ВЫВОДИТСЯ
        // из разметки состояний. Фиксированный список ловил ровно четыре глифа
        // и при этом пропускал остальные пять, а после чистки словаря сам стал
        // требовать удалённый px-empty-chart — гейт держал не факт, а свой
        // прошлый снимок. Вывод из шаблонов покрывает ВСЕ состояния и не
        // устаревает при добавлении нового.
        List<String> referenced = new ArrayList<>();

        for (String template : HOST_TEMPLATES) {
            String markup = TEMPLATE_COMMENT.matcher(read(template)).replaceAll("\n");
            Matcher m = Pattern.compile("class=\"[^\"]*empty-state-illus[^\"]*\"[^>]*>\\s*<use href=\"#([^\"]+)\"")
                    .matcher(markup);
            int found = 0;
            while (m.find()) {
                found++;
                assertThat(m.group(1)).as("%s: глиф не из спрайта состояний", template).startsWith("px-");
                referenced.add(m.group(1));
            }
            assertThat(found).as("%s: глифы пустых состояний", template).isPositive();
            assertThat(markup).as("%s: растровый маскот в пустом состоянии", template)
                    .doesNotContain("/img/pixel/empty");
        }

        assertThat(referenced).as("ссылки на глифы состояний").isNotEmpty();
        for (String symbol : referenced) {
            assertThat(sprite).as("символ %s отсутствует в спрайте", symbol)
                    .contains("id=\"" + symbol + "\"");
        }
    }
}
