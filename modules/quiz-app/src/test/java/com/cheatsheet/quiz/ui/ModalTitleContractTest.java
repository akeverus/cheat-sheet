package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Композиция заголовка модалки целит тот тег, который рендерит её скрипт.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * keyboard-help.js рендерит {@code <h2 id="kbd-help-title">}, а правило композиции
 * заголовка в components-modals.css целило {@code .kbd-help-modal h3} — мёртвый
 * селектор. Заголовок жил на голом reset: замер дал 36px display-кегля в диалоге
 * 28rem и НОЛЬ padding-right, расчищающего место под абсолютную кнопку «×»
 * (короткое «Горячие клавиши» не дотягивалось до кнопки, длинный заголовок зашёл
 * бы под неё). После выравнивания селектора — 28px десктоп / 19px на 390,
 * Literata, clearance 32px (Chromium и WebKit, обе темы).
 *
 * <p>Попутно отозван ложный минус пробы iter-161: «модалка не открылась по ?» —
 * проба проверяла видимость {@code .kbd-help-modal h3}, которого не существует.
 * Живой прогон: «?» открывает справку в обоих движках, Esc закрывает, фокус
 * уходит на «×».</p>
 */
class ModalTitleContractTest {

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    @Test
    void theHelpTitleRuleTargetsTheTagTheScriptRenders() throws IOException {
        String js = read("static/js/ui/keyboard-help.js");

        assertThat(js)
                .as("скрипт справки обязан рендерить h2 с id для aria-labelledby")
                .contains("<h2 id=\"kbd-help-title\">");
        assertThat(CssRules.stripComments(read("static/css/components-modals.css")))
                .as("композиция заголовка справки обязана целить h2 — тот тег, что в разметке")
                .containsPattern("\\.kbd-help-modal h2 \\{");
        // Комментарии выстрижены: литерал в пояснении — не селектор. Мёртвый h3
        // жил в ДВУХ файлах: композиция в modals и medium-вес в signatures.
        for (String file : new String[]{"components-modals.css", "components-signatures.css"}) {
            assertThat(CssRules.stripComments(read("static/css/" + file)))
                    .as("%s: селектор .kbd-help-modal h3 мёртв — такого тега в модалке нет, "
                            + "и правило молча не применяется", file)
                    .doesNotContain(".kbd-help-modal h3");
        }
    }

    @Test
    void theHelpTitleCarriesTheCloseButtonClearance() throws IOException {
        String css = CssRules.stripComments(read("static/css/components-modals.css"));
        java.util.regex.Matcher rule = java.util.regex.Pattern
                .compile("\\.kbd-help-modal h2 \\{([^}]*)\\}").matcher(css);
        assertThat(rule.find()).isTrue();
        assertThat(rule.group(1))
                .as("padding-right — расчистка под абсолютную кнопку «×»: без неё длинный "
                        + "заголовок заходит под кнопку")
                .contains("padding-right")
                .as("кегль заголовка модалки — на текучей шкале (FRD-213), не на статике")
                .contains("font-size: var(--font-size-heading-card)");
    }

    /** Prompt-модалка связана классом — разметка и правило держат один токен имени. */
    @Test
    void thePromptTitleStaysClassCoupled() throws IOException {
        assertThat(read("static/js/ui/modals.js"))
                .contains("class=\"prompt-modal-title\"");
        assertThat(read("static/css/components-modals.css"))
                .contains(".prompt-modal-title");
    }
}
