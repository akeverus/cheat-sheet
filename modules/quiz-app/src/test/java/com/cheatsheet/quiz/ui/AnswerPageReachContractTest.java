package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Главное действие достижимо: и на странице ответа без JS, и там, где висит
 * нижняя навигация.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Страница {@code /answer} — серверный путь ответа без скриптов, и этот цикл её ни
 * разу не трогал. Замер (Chromium, экзамен, без JS):
 *
 * <pre>
 *   вьюпорт    документ  «Следующий вопрос»  разбор неверных вариантов
 *   390×844      7009        6290 (7.5 экрана)   2660px, всегда развёрнут
 *   1512×982     2656        2188 (2.2 экрана)    682px, всегда развёрнут
 * </pre>
 *
 * Второй дефект найден замером на той же странице: док главного действия жил под
 * {@code max-width: 600px}, а фиксированная нижняя навигация — под
 * {@code max-width: 900px}. В полосе 601–900 навигация есть, а дока нет. На
 * 768×1024 (Chromium и WebKit, с JS и без) кнопка «Проверить ответ» стояла на 964,
 * навигация — на 960, и {@code elementFromPoint} в центре кнопки возвращал ссылку
 * «Тренировка»: нажатие на главное действие уводило со страницы вместо ответа.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>итог и действие идут ДО списка вариантов;</li>
 *   <li>разбор неверных вариантов — раскрытие, а не всегда развёрнутая простыня;</li>
 *   <li>правило интервальных повторений живёт в раскрытии SM-2, а не в строке итога;</li>
 *   <li>порог дока действия равен порогу навигации.</li>
 * </ol>
 */
class AnswerPageReachContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String resultMarkup() throws IOException {
        return COMMENT.matcher(read("templates/result.html")).replaceAll("");
    }

    @Test
    void theOutcomeAndTheActionComeBeforeTheOptions() throws IOException {
        String markup = resultMarkup();
        int meta = markup.indexOf("class=\"feedback-meta\"");
        int actions = markup.indexOf("class=\"result-actions\"");
        int options = markup.indexOf("class=\"options\"");

        assertThat(meta).as("строка итога исчезла со страницы ответа").isNotNegative();
        assertThat(actions).as("действий на странице ответа нет").isNotNegative();
        assertThat(options).as("списка вариантов нет").isNotNegative();
        assertThat(List.of(meta, actions, options))
                .as("варианты с пометками — это уже разбор; на телефоне они занимают 1832px "
                        + "и отодвигают «Следующий вопрос» за два экрана")
                .isSorted();
    }

    @Test
    void theDistractorAnalysisIsADisclosure() throws IOException {
        String markup = resultMarkup();
        assertThat(markup)
                .as("панель на 2660px из 7009 документа не может быть развёрнута всегда")
                .contains("<details class=\"explanation-panel distractor-panel\"");
        assertThat(markup)
                .as("у раскрытия обязана быть шапка, иначе его нечем открыть")
                .containsPattern("distractor-panel\"[^>]*>\\s*<summary");
    }

    @Test
    void theRepetitionPolicyLivesWithTheMechanism() throws IOException {
        String markup = resultMarkup();
        int sm2 = markup.indexOf("class=\"sm2-details\"");
        int policy = markup.indexOf("class=\"review-policy");
        assertThat(sm2).isNotNegative();
        assertThat(policy)
                .as("правило повторений объясняет механизм, а не этот ответ — его место "
                        + "в раскрытии SM-2, а не в строке итога над действием")
                .isGreaterThan(sm2);
    }

    /**
     * Док действия и нижняя навигация — одно и то же условие «экран узкий». Разные
     * пороги означают полосу ширин, где навигация есть, а дока нет.
     */
    @Test
    void theActionDockCoversTheWholeRangeOfTheFixedNav() throws IOException {
        String shell = read("static/css/shell.css");
        String focus = read("static/css/pages-focus.css");

        assertThat(shell)
                .as("навигация перестала быть фиксированной до 900px — порог дока надо пересчитать")
                .contains("@media (max-width: 900px)");
        // Границы медиазапроса берём по балансу скобок, а не окном в N символов:
        // блок длинный, и окно — это допущение о его длине, на котором первая
        // редакция гейта и споткнулась.
        assertThat(mediaBlock(focus, "@media (max-width: 900px)"))
                .as("док главного действия обязан жить в том же диапазоне, что навигация")
                .contains("training-actions\"]:not(.is-answered) .action-footer");
        // Запрещать в файле ЛЮБОЙ порог 600px нельзя: он остался у правил, которые
        // действительно про телефон (первая редакция гейта требовала именно этого и
        // падала на верном коде). Запрещено ровно одно — держать там док.
        if (focus.contains("@media (max-width: 600px)")) {
            assertThat(mediaBlock(focus, "@media (max-width: 600px)"))
                    .as("док вернулся под прежний порог — полоса 601–900 снова без него")
                    .doesNotContain(".action-footer {")
                    .doesNotContain("position: fixed");
        }
    }

    /** Тело медиазапроса по балансу скобок. */
    private String mediaBlock(String css, String query) {
        int at = css.indexOf(query);
        assertThat(at).as("медиазапроса нет: " + query).isNotNegative();
        int open = css.indexOf('{', at);
        int depth = 0;
        for (int i = open; i < css.length(); i++) {
            char c = css.charAt(i);
            if (c == '{') depth++;
            if (c == '}') {
                depth--;
                if (depth == 0) return css.substring(open, i);
            }
        }
        throw new AssertionError("медиазапрос не закрыт: " + query);
    }
}
