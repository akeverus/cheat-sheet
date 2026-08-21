package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Персонализация: один вопрос про экран + отдельные оси доступности.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Во вкладке «Оформление» стояло ШЕСТЬ переключателей: тема, размер шрифта, ширина
 * чтения, плотность, движение, пиксель-эффекты. Три из них — ширина, плотность и
 * пиксель — не про доступность, а про то, на чём человек работает, и по отдельности
 * давали 2×2×3 = 12 сочетаний, которые никто не проверял. Про две из них сам
 * {@code tokens.css} писал: «legacy-ось Instrument, удаляется на Этапе 10; пока
 * оставлена рабочей» — то есть код знал, что ось лишняя, и жил с ней.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>вкусовые оси свёрнуты в один пресет «под экран»;</li>
 *   <li>оси доступности НЕ свёрнуты — тема, размер текста и движение остались;</li>
 *   <li>таблица пресетов лежит в одном месте и до первой отрисовки;</li>
 *   <li>умолчание даёт прежний вид — смена умолчания перерисовала бы приложение
 *       всем, кто настройки не открывал;</li>
 *   <li>прежние ключи хранения переносятся, а не игнорируются.</li>
 * </ol>
 */
class ScreenPresetContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String settingsMarkup() throws IOException {
        return COMMENT.matcher(read("templates/settings.html")).replaceAll("");
    }

    @Test
    void theTasteAxesAreOneQuestionAboutTheScreen() throws IOException {
        String markup = settingsMarkup();
        assertThat(markup).contains("id=\"screen-preset-control\"");
        for (String preset : List.of("phone", "laptop", "wide")) {
            assertThat(markup).contains("data-screen-preset=\"" + preset + "\"");
        }
        for (String gone : List.of("reading-width-pref-control", "density-pref-control", "pixel-pref-control")) {
            assertThat(markup)
                    .as("ось свёрнута в пресет — второй системы выбора быть не должно: " + gone)
                    .doesNotContain(gone);
        }
    }

    @Test
    void theAccessibilityAxesStayOnTheirOwn() throws IOException {
        String markup = settingsMarkup();
        assertThat(markup)
                .as("тема — ось доступности, в пресет не сворачивается")
                .contains("id=\"theme-pref-control\"");
        assertThat(markup)
                .as("размер текста — ось доступности")
                .contains("id=\"font-scale-value\"");
        assertThat(markup)
                .as("движение — ось доступности (инвариант 4)")
                .contains("id=\"motion-pref-control\"");
    }

    /**
     * Плотность меняет шкалу отступов, то есть раскладку. Применять её после первого
     * кадра — значит показывать перекладку, поэтому таблица живёт в inline-бутстрапе,
     * а не в модуле, который грузится после отрисовки.
     */
    @Test
    void thePresetTableLivesInOnePlaceAndBeforeTheFirstPaint() throws IOException {
        String head = read("templates/fragments/head.html");
        assertThat(head).contains("SCREEN_PRESETS");
        assertThat(head).contains("readingWidth: 'wide'");
        assertThat(head).contains("density: 'compact'");
        assertThat(head).contains("window.__screenPreset");

        String ui = read("static/js/settings/personalization.js");
        assertThat(ui)
                .as("вторая копия таблицы в UI-модуле разошлась бы с первой молча")
                .doesNotContain("SCREEN_PRESETS")
                .doesNotContain("readingWidth")
                .doesNotContain("pixelIntensity");

        String bootstrap = read("static/js/settings/personalization-bootstrap.js");
        assertThat(bootstrap)
                .as("переехавшие оси не должны остаться второй реализацией после первого кадра")
                .doesNotContain("window.__readingWidth")
                .doesNotContain("window.__density");
    }

    @Test
    void theDefaultPresetKeepsTheCurrentLook() throws IOException {
        String head = read("templates/fragments/head.html");
        assertThat(head)
                .as("умолчание — «ноутбук»: просторные отступы, мера 72ch, средний пиксель")
                .contains("laptop: { density: 'spacious', readingWidth: 'standard', pixel: 'med' }");
        assertThat(head)
                .as("умолчание не пишет атрибутов — иначе оно перестанет быть умолчанием")
                .contains("if (cfg.density === 'spacious') root.removeAttribute('data-density');")
                .contains("if (cfg.readingWidth === 'standard') root.removeAttribute('data-reading-width');");
        assertThat(head).contains("return migrateLegacyPreset() || 'laptop';");
    }

    @Test
    void theOldKeysAreMigratedNotIgnored() throws IOException {
        String head = read("templates/fragments/head.html");
        assertThat(head).contains("migrateLegacyPreset");
        for (String key : List.of("'density'", "'readingWidth'", "'pixelIntensity'")) {
            assertThat(head)
                    .as("прежний ключ обязан быть прочитан и удалён: " + key)
                    .contains("localStorage.removeItem(" + key + ")");
        }
    }

    /**
     * Пиксельность участвует в выборе пресета, а не только читается.
     *
     * <p>Дефект найден visual-матрицей (iter-163): {@code pixelIntensity=low} давал
     * {@code data-pixel="med"} — {@code migrateLegacyPreset} читал legacyPixel и не
     * использовал его в выборе, то есть человек, настроивший ТОЛЬКО пиксельность,
     * молча получал laptop/med. Оси раскладки сильнее (явные compact/wide решают
     * первыми), пиксельность — следом: low → phone, high → wide. Живой прогон —
     * два migration-теста в e2e/visual-matrix.spec.js.</p>
     */
    @Test
    void theLonePixelChoiceSurvivesTheMigration() throws IOException {
        String head = read("templates/fragments/head.html");
        assertThat(head)
                .as("legacyPixel выпал из выбора пресета: одноосевой выбор пиксельности "
                        + "снова молча теряется при переносе")
                .contains("legacyPixel === 'low' ? 'phone'")
                .contains("legacyPixel === 'high' ? 'wide'");
    }
}
