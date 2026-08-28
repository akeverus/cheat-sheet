package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Образец оформления на /settings существует, честен и виден без JS.
 *
 * <h2>Найденный замером дефект (iter-185)</h2>
 * Панель «Оформление» на 1728 занимала одну полосу контролов, ~75% экрана
 * оставалось мёртвым фоном. Образец (вопрос + код + варианты на живых токенах)
 * даёт площади функцию: смена экрана, темы, шрифта и плотности видна
 * немедленно (замер: A+ ×2 меняет заголовок образца 36 → 43.2px).
 *
 * <h2>Пойманный при внедрении второй дефект — древний дисбаланс</h2>
 * У {@code .personalization-card} НЕ БЫЛО закрывающего div: браузер
 * автозакрывал карточку у конца секции, и всё, что вставало после грида,
 * «проваливалось» внутрь скрытой без JS карточки — первый вариант образца
 * в no-JS WebKit имел высоту 0. Тест держит баланс: закрытие карточки
 * обязано стоять РАНЬШЕ образца.
 */
class SettingsPreviewContractTest {

    private static final Pattern HTML_COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String markup() throws IOException {
        String raw = new String(new ClassPathResource("templates/settings.html").getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
        return HTML_COMMENT.matcher(raw).replaceAll("");
    }

    @Test
    void thePreviewLivesOutsideTheJsOnlyCard() throws IOException {
        String m = markup();
        int cardOpen = m.indexOf("personalization-card");
        cardOpen = m.lastIndexOf("<div", cardOpen);
        assertThat(cardOpen).as("карточка персонализации пропала").isNotNegative();

        int depth = 0;
        int cardClose = -1;
        Matcher div = Pattern.compile("<div\\b|</div>").matcher(m);
        div.region(cardOpen, m.length());
        while (div.find()) {
            depth += div.group().equals("</div>") ? -1 : 1;
            if (depth == 0) {
                cardClose = div.end();
                break;
            }
        }
        int preview = m.indexOf("class=\"settings-preview\"");
        assertThat(preview).as("образец оформления пропал: пустая нижняя половина /settings вернулась")
                .isNotNegative();
        assertThat(cardClose)
                .as("personalization-card снова не закрыта — всё после грида проваливается "
                        + "в скрытую без JS карточку (высота образца 0 в no-JS)")
                .isNotNegative()
                .isLessThan(preview);
    }

    @Test
    void theStageIsAnHonestIllustration() throws IOException {
        String m = markup();
        int stage = m.indexOf("settings-preview-stage");
        int end = m.indexOf("</section>", stage);
        String inside = m.substring(stage, end);
        assertThat(m.substring(m.lastIndexOf('<', stage), m.indexOf('>', stage)))
                .as("сцена обязана быть aria-hidden: это иллюстрация настроек, не контент")
                .contains("aria-hidden=\"true\"");
        assertThat(inside)
                .as("внутри aria-hidden сцены не должно быть интерактивных элементов")
                .doesNotContain("<button")
                .doesNotContain("<a ")
                .doesNotContain("<input");
        assertThat(m)
                .as("подпись обязана говорить, что образец не интерактивен")
                .contains("Образец не интерактивен");
    }
}
