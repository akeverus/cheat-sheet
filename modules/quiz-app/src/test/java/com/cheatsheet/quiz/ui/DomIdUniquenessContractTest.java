package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.TestInterviewPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Каждый `id` в отданном документе встречается один раз.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер живых страниц (Chromium и WebKit, 1512×982): на `/nope-404` документ нёс
 * <b>10 повторяющихся id</b> — `px-offline`, `px-portcullis`, `px-shelf`,
 * `px-chronicle`, `px-diagram-error` и ещё пять. Причина структурная: спрайт
 * продуктовых состояний печатал и `fragments/shell-head` (общий пролог всех семи
 * страниц), и сама `error.html` — include, который был верен до того, как шапка
 * взяла спрайт на себя.
 *
 * <pre>
 *   страница     &lt;symbol&gt;   спрайт-блоков   повторов id
 *   / и прочие      48            2               0
 *   /nope-404       58            3              10   ← до правки
 *   /nope-404       48            2               0   ← после
 * </pre>
 *
 * Повторяющийся id — это не косметика: `<use href="#state-…">` разрешается в ПЕРВОЕ
 * определение, поэтому второй набор всегда мёртв, а `getElementById` и связи
 * `aria-labelledby` / `aria-controls` перестают быть однозначными.
 *
 * <h2>Что закреплено</h2>
 * Гейт держит весь КЛАСС дефекта, а не один его случай: он рендерит страницы и
 * сверяет уникальность id в отданном HTML, плюс проверяет на уровне шаблонов, что
 * страница не включает спрайт, который уже печатает общая шапка.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DomIdUniquenessContractTest {

    private static final Pattern ID = Pattern.compile("\\sid=\"([^\"]+)\"");
    private static final Pattern SPRITE_INCLUDE =
            Pattern.compile("th:replace=\"~\\{fragments/(state-illustrations|icons)\\s*::");

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired
    MockMvc mockMvc;

    private String render(String path) throws Exception {
        return mockMvc.perform(get(path)).andReturn().getResponse().getContentAsString();
    }

    private List<String> duplicates(String html) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        Matcher m = ID.matcher(html);
        while (m.find()) counts.merge(m.group(1), 1, Integer::sum);
        List<String> repeated = new ArrayList<>();
        counts.forEach((id, n) -> {
            if (n > 1) repeated.add(id + "×" + n);
        });
        return repeated;
    }

    @Test
    void everyRenderedPageHasUniqueIds() throws Exception {
        for (String path : List.of("/", "/practice", "/topics", "/stats", "/bookmarks", "/settings", "/review")) {
            String html = render(path);
            assertThat(html)
                    .as("страница %s не отрендерилась — проверять нечего", path)
                    .contains("<body");
            assertThat(duplicates(html))
                    .as("на %s повторяются id: `<use href=\"#…\">` разрешается в первое "
                            + "определение, а `aria-labelledby`/`aria-controls` перестают быть "
                            + "однозначными", path)
                    .isEmpty();
        }
    }

    /**
     * Шаблон страницы не печатает спрайт, который уже печатает общая шапка. Именно так
     * возник дубль на странице ошибки: include был верен до того, как шапка взяла
     * спрайт на себя, и пережил переезд.
     */
    @Test
    void noPageTemplateRepeatsASpriteTheShellHeadAlreadyPrints() throws IOException {
        String shellHead = read("templates/fragments/shell-head.html");
        List<String> вШапке = matches(SPRITE_INCLUDE, shellHead);
        assertThat(вШапке)
                .as("общая шапка перестала печатать спрайт состояний — тогда страницы "
                        + "обязаны включать его сами, и это правило надо пересмотреть")
                .contains("state-illustrations");

        for (Path template : pageTemplates()) {
            String html = Files.readString(template, StandardCharsets.UTF_8);
            if (!html.contains("fragments/shell-head")) continue;
            for (String sprite : matches(SPRITE_INCLUDE, html)) {
                assertThat(вШапке)
                        .as("%s включает спрайт `%s`, который уже печатает общая шапка: "
                                + "документ получит по два определения на каждый глиф "
                                + "(замер на странице ошибки — 10 повторяющихся id)",
                                template.getFileName(), sprite)
                        .doesNotContain(sprite);
            }
        }
    }

    private List<Path> pageTemplates() throws IOException {
        Path root = Path.of("src/main/resources/templates");
        if (!Files.isDirectory(root)) root = Path.of("modules/quiz-app/src/main/resources/templates");
        assertThat(Files.isDirectory(root)).as("каталога шаблонов нет: %s", root).isTrue();
        try (var paths = Files.list(root)) {
            return paths.filter(p -> p.toString().endsWith(".html")).sorted().toList();
        }
    }

    private List<String> matches(Pattern pattern, String text) {
        List<String> out = new ArrayList<>();
        Matcher m = pattern.matcher(text);
        while (m.find()) out.add(m.group(1));
        return out;
    }

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }
}
