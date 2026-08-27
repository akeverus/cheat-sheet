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
 * Preload шрифтов существует, целится в живые файлы и не тянет лишнего.
 *
 * <h2>Найденный замером факт (iter-177, Slow 4G + CPU 4x)</h2>
 * woff2 стартовали только ПОСЛЕ парса CSS-бандла — хвост критической цепи
 * 1793ms (html 889 → бандл 625 → шрифты). Честный A/B при равном протоколе
 * повторной навигации: render delay 646 → 528ms (−18%), LCP 776 → 704;
 * шрифты ушли из хвоста цепи. Первая редакция прелоадила четыре субсета —
 * повторный трейс показал латиницу JBMono/Literata всё ещё в хвосте: страницам
 * русского UI нужны ОБА субсета (осн. кириллица И латиница) всех трёх семейств.
 *
 * <h2>Почему связка имён — гейт</h2>
 * Имена woff2 зашиты в head. Обновление шрифтов сменит имена в fonts-local.css,
 * и протухший preload молча станет ДВОЙНОЙ загрузкой (прелоад мёртвого файла +
 * обычная загрузка нового). Каждый preload-href обязан существовать как src
 * в fonts-local.css.
 */
class FontPreloadContractTest {

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private List<String> preloadHrefs() throws IOException {
        String head = read("templates/fragments/head.html");
        Matcher m = Pattern.compile("rel=\"preload\" as=\"font\"[^>]*th:href=\"@\\{(/fonts/[^}]+)\\}\"")
                .matcher(head);
        List<String> hrefs = new ArrayList<>();
        while (m.find()) hrefs.add(m.group(1));
        return hrefs;
    }

    @Test
    void everyPreloadTargetsALivingFontFile() throws IOException {
        String fonts = read("static/css/fonts-local.css");
        List<String> hrefs = preloadHrefs();
        assertThat(hrefs).as("preload-блок пропал: шрифты снова в хвосте критической цепи")
                .hasSize(6);
        for (String href : hrefs) {
            assertThat(fonts)
                    .as("протухший preload %s: файла нет в fonts-local.css — двойная загрузка молча", href)
                    .contains("url('" + href + "')");
        }
    }

    @Test
    void everyPreloadCarriesCrossorigin() throws IOException {
        String head = read("templates/fragments/head.html");
        Matcher m = Pattern.compile("<link rel=\"preload\" as=\"font\"[^>]*>").matcher(head);
        int count = 0;
        while (m.find()) {
            count++;
            assertThat(m.group())
                    .as("font-preload без crossorigin: браузер качает файл дважды")
                    .contains("crossorigin");
        }
        assertThat(count).isEqualTo(6);
    }
}
