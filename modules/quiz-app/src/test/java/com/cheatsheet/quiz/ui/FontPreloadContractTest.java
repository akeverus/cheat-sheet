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
 * Preload шрифтов существует, целится в живые файлы и не двоит загрузку.
 *
 * <h2>Найденный замером факт (iter-177, Slow 4G + CPU 4x)</h2>
 * woff2 стартовали только ПОСЛЕ парса CSS-бандла — хвост критической цепи
 * 1793ms (html 889 → бандл 625 → шрифты). Честный A/B при равном протоколе
 * повторной навигации: render delay 646 → 528ms (−18%), LCP 776 → 704;
 * шрифты ушли из хвоста цепи. Страницам русского UI нужны ОБА субсета
 * (основная кириллица И латиница) всех трёх семейств — шесть файлов.
 *
 * <h2>Почему линки создаёт скрипт (iter-178)</h2>
 * Для same-origin шрифтов движки зеркально расходятся в матчинге preload и
 * @font-face-загрузки (сетевые запросы на 6 файлов): Chromium с crossorigin —
 * 6, без — 12; WebKit с crossorigin — 12, без — 6. Любой статический вариант
 * атрибута = двойная загрузка каждого woff2 в одном из движков, поэтому
 * crossOrigin ветвится по UA в синхронном head-скрипте. После фикса — 6 и 6.
 *
 * <h2>Почему связка имён — гейт</h2>
 * Имена woff2 зашиты в head. Обновление шрифтов сменит имена в fonts-local.css,
 * и протухший preload молча станет ДВОЙНОЙ загрузкой (прелоад мёртвого файла +
 * обычная загрузка нового). Каждый preload-путь обязан существовать как src
 * в fonts-local.css.
 */
class FontPreloadContractTest {

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private List<String> preloadPaths() throws IOException {
        String head = read("templates/fragments/head.html");
        Matcher m = Pattern.compile("\\[\\[@\\{(/fonts/[^}]+)\\}]]").matcher(head);
        List<String> paths = new ArrayList<>();
        while (m.find()) paths.add(m.group(1));
        return paths;
    }

    @Test
    void everyPreloadTargetsALivingFontFile() throws IOException {
        String fonts = read("static/css/fonts-local.css");
        List<String> paths = preloadPaths();
        assertThat(paths).as("preload-блок пропал: шрифты снова в хвосте критической цепи")
                .hasSize(6);
        for (String path : paths) {
            assertThat(fonts)
                    .as("протухший preload %s: файла нет в fonts-local.css — двойная загрузка молча", path)
                    .contains("url('" + path + "')");
        }
    }

    @Test
    void thePreloaderBranchesCrossoriginByEngine() throws IOException {
        String head = read("templates/fragments/head.html");
        assertThat(head)
                .as("ушло UA-ветвление: один из движков снова качает каждый woff2 дважды")
                .contains("if (!safari) link.crossOrigin = 'anonymous';");
        assertThat(head)
                .as("скрипт перестал создавать preload-линки")
                .contains("link.rel = 'preload';")
                .contains("link.as = 'font';");
    }
}
