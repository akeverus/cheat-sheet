package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
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
 * Обращение к Spring-бину в шаблоне обязано целиком стоять внутри {@code ${...}}.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * В каталоге тем было написано
 * {@code th:text="${#lists.size(section.topics)} + ' ' + @plural.pick(...)"} — часть внутри
 * {@code ${}}, а вызов бина снаружи. Thymeleaf такое выражение не разбирает, но падает он не
 * при рендере значения, а при ПАРСИНГЕ шаблона, то есть после того, как заголовки ответа уже
 * отправлены. Наружу это выходит так: код 200, тело обрывается посреди chunked-потока,
 * {@code curl} отдаёт exit 18, Chromium пишет {@code ERR_INCOMPLETE_CHUNK}, WebKit молча
 * рисует половину страницы, а реальный браузер на медленном соединении ждёт до таймаута.
 *
 * <p>Класс дефекта — «страница отвечает 200 и при этом сломана». Его не ловит ни проверка
 * статуса, ни размер ответа (усечённое тело весит правдоподобно), ни скриншот одного
 * состояния: на {@code ?q=zzzz} тот же шаблон отдавался целым, потому что до сломанного
 * выражения дело не доходило — разделов в выборке не было.
 *
 * <h2>Что закреплено</h2>
 * Для каждого {@code th:*}-атрибута всех шаблонов: каждое вхождение {@code @бин.метод(} лежит
 * внутри одного из {@code ${...}}-участков значения. Проверка структурная — по позиции в
 * строке, а не по списку известных бинов, поэтому новый бин закрывается ею сам.
 */
class TemplateExpressionContractTest {

    private static final Path TEMPLATES = Path.of("src/main/resources/templates");
    private static final Pattern TH_ATTRIBUTE = Pattern.compile("th:[a-zA-Z-]+=\"([^\"]*)\"");
    private static final Pattern BEAN_CALL = Pattern.compile("@\\w+\\.\\w+\\s*\\(");

    @Test
    void beanCallsLiveInsideExpressionBlocks() {
        List<String> offenders = new ArrayList<>();

        for (Path template : templates()) {
            String source = read(template);
            Matcher attribute = TH_ATTRIBUTE.matcher(source);
            while (attribute.find()) {
                String value = attribute.group(1);
                List<int[]> blocks = expressionBlocks(value);
                Matcher call = BEAN_CALL.matcher(value);
                while (call.find()) {
                    int at = call.start();
                    boolean inside = blocks.stream().anyMatch(b -> b[0] <= at && at <= b[1]);
                    if (!inside) {
                        int line = (int) source.substring(0, attribute.start()).chars()
                                .filter(c -> c == '\n').count() + 1;
                        offenders.add(TEMPLATES.relativize(template) + ":" + line + " → " + value);
                    }
                }
            }
        }

        assertThat(offenders)
                .as("вызов бина стоит ВНЕ ${...}: Thymeleaf не разберёт такое выражение и упадёт "
                        + "на парсинге шаблона — то есть после отправки заголовков. Страница ответит "
                        + "200 с оборванным телом, и ни статус, ни размер ответа этого не покажут. "
                        + "Оберни всё выражение целиком: th:text=\"${a + ' ' + @bean.m(a)}\"")
                .isEmpty();
    }

    /** Границы всех {@code ${...}} внутри значения атрибута, с учётом вложенных скобок. */
    private static List<int[]> expressionBlocks(String value) {
        List<int[]> blocks = new ArrayList<>();
        int i = value.indexOf("${");
        while (i >= 0) {
            int depth = 0;
            int j = i + 1;
            while (j < value.length()) {
                char c = value.charAt(j);
                if (c == '{') {
                    depth++;
                } else if (c == '}') {
                    depth--;
                    if (depth == 0) {
                        break;
                    }
                }
                j++;
            }
            blocks.add(new int[]{i, Math.min(j, value.length() - 1)});
            i = value.indexOf("${", j + 1);
        }
        return blocks;
    }

    private static List<Path> templates() {
        try (Stream<Path> paths = Files.walk(TEMPLATES)) {
            return paths.filter(p -> p.toString().endsWith(".html")).sorted().toList();
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
}
