package com.cheatsheet.quiz.config;

import com.cheatsheet.quiz.TestInterviewPath;
import com.cheatsheet.quiz.config.web.CssBundleService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Контракт версионирования статики, до которой не дотягивается шаблонизатор.
 *
 * <p>Годовой {@code max-age} в prod держится ровно на одном допущении: у каждого
 * адреса, который реально запрашивает браузер, есть content-hash. Для относительных
 * {@code import '../core/dom.js'}
 * это обеспечивает не Thymeleaf, а карты из {@code StaticVersionAdvice}. Допущение
 * тихо ломается от обычной работы: новый модуль в {@code /js} или новый спрайт
 * появляются без единой правки шаблона — и, не будь этой проверки, уехали бы в прод
 * с годовым кэшем на безверсионном URL, то есть навсегда.</p>
 *
 * <p>Поэтому тест сверяет карту не с самой собой, а с файловой системой: сколько
 * модулей лежит в {@code static/js}, столько записей и обязано быть в import map.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StaticVersionAdviceTest {

    private static final Path STATIC_ROOT =
            Path.of("src/main/resources/static").toAbsolutePath().normalize();

    private static final ObjectMapper JSON = new ObjectMapper();

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    private static Set<String> filesUnder(String urlRoot, String suffix) throws IOException {
        Path dir = STATIC_ROOT.resolve(urlRoot.substring(1));
        try (Stream<Path> walk = Files.walk(dir)) {
            return walk.filter(Files::isRegularFile)
                    .map(p -> urlRoot + "/" + dir.relativize(p).toString().replace('\\', '/'))
                    .filter(p -> p.endsWith(suffix))
                    .collect(Collectors.toSet());
        }
    }

    private static Map<String, String> readMap(JsonNode node) {
        Map<String, String> result = new LinkedHashMap<>();
        node.fieldNames().forEachRemaining(name -> result.put(name, node.get(name).asText()));
        return result;
    }

    private static String extractScript(String html, String openingTagPrefix) {
        Matcher m = Pattern.compile(Pattern.quote(openingTagPrefix) + "[^>]*>(.*?)</script>",
                Pattern.DOTALL).matcher(html);
        return m.find() ? m.group(1) : null;
    }

    @Nested
    @TestPropertySource(properties = "spring.web.resources.chain.strategy.content.enabled=true")
    class WithContentVersioning {

        @Autowired
        MockMvc mockMvc;

        @Test
        void importMapCoversEveryJsModule() throws Exception {
            String html = mockMvc.perform(get("/")).andReturn().getResponse().getContentAsString();

            String map = extractScript(html, "<script type=\"importmap\"");
            assertThat(map).as("import map должен быть в разметке").isNotNull();

            Map<String, String> imports = readMap(JSON.readTree(map).get("imports"));

            // Ни один модуль не имеет права остаться без версии: годовой кэш на
            // безверсионном URL — это «правка не доедет никогда».
            assertThat(imports.keySet()).containsExactlyInAnyOrderElementsOf(filesUnder("/js", ".js"));
            imports.forEach((canonical, versioned) -> assertThat(versioned)
                    .as("значение карты обязано быть content-hash URL: " + canonical)
                    .matches(".*-[0-9a-f]{32}\\.js"));
        }

        @Test
        void noStaticUrlEscapesVersioning() throws Exception {
            String html = mockMvc.perform(get("/")).andReturn().getResponse().getContentAsString();

            // Канонические пути живут внутри самой карты — это ключи, а не запросы.
            // Второй карты (плоской, для спрайтов) больше нет: её единственным
            // потребителем был декоративный слой pixel-fx.js, снятый вместе с ним.
            String withoutMaps = html
                    .replaceAll("(?s)<script type=\"importmap\".*?</script>", "");

            Matcher m = Pattern.compile("[\"'](/(?:css|js|img|vendor|fonts)/[^\"']+)[\"']")
                    .matcher(withoutMaps);
            while (m.find()) {
                assertThat(m.group(1))
                        .as("статика в разметке обязана быть версионированной")
                        // Второй вариант — CSS-лента: она склеивается в памяти и идёт мимо
                        // цепочки ресурсов Spring, поэтому 32-символьного хеша от неё не
                        // будет никогда. Версия у неё собственная: /css/bundle/{page}.{8hex}.css,
                        // и требование то же — годовой кэш висит на уникальном адресе.
                        .matches(".*-[0-9a-f]{32}\\.[a-z0-9]+|" + CssBundleService.URL_PREFIX + "[a-z]+\\.[0-9a-f]{8}\\.css");
            }
        }
    }

    @Nested
    class WithoutContentVersioning {

        @Autowired
        MockMvc mockMvc;

        @Test
        void mapsAreOmittedEntirely() throws Exception {
            String html = mockMvc.perform(get("/")).andReturn().getResponse().getContentAsString();

            // Без версионирования карта была бы тождественной — а пустой или, хуже,
            // «null» внутри <script type="importmap"> сломал бы загрузку модулей.
            assertThat(html).doesNotContain("type=\"importmap\"");
        }
    }
}
