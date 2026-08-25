package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тап-зона главного действия закладки (§131, REF-042).
 *
 * <p>Живой замер 390×844: ссылка вопроса — единственный переход к тренировке
 * из строки списка — имела высоту 20px (голая текстовая строка). WCAG 2.5.8
 * проходила только за счёт разреженности соседей, но на таче главную цель
 * страницы приходилось выцеливать. Паддинг растит цель до 36px, компенсирующий
 * отрицательный маргин сохраняет ритм колонки (гэп — те же 8px), поэтому
 * раскладка и перекрытия не меняются.</p>
 */
class BookmarkTouchTargetContractTest {

    @Test
    void bookmarkQuestionLinkGrowsTouchTargetWithoutLayoutShift() throws IOException {
        String css = new String(new ClassPathResource("static/css/pages-bookmarks.css")
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replaceAll("(?s)/\\*.*?\\*/", "");
        String questionRule = extractRule(css, ".bookmark-question ");
        assertThat(questionRule)
                .as("ссылка вопроса обязана расширять тап-зону паддингом — " +
                        "без него главная цель строки на таче остаётся 20px")
                .containsPattern("padding-block:\\s*var\\(--space-2\\)");
        assertThat(questionRule)
                .as("паддинг без компенсирующего отрицательного маргина сдвинул бы " +
                        "ритм колонки — тап-зона обязана расти без сдвига раскладки")
                .containsPattern("margin-block:\\s*calc\\(-1\\s*\\*\\s*var\\(--space-2\\)\\)");
    }

    /** Тело первого правила, чей селектор оканчивается данным суффиксом. */
    private static String extractRule(String css, String selectorSuffix) {
        int idx = css.indexOf(selectorSuffix + "{");
        if (idx < 0) {
            idx = css.indexOf(selectorSuffix.trim() + " {");
        }
        assertThat(idx).as("правило %s должно существовать", selectorSuffix.trim()).isNotNegative();
        int open = css.indexOf('{', idx);
        int close = css.indexOf('}', open);
        return css.substring(open + 1, close);
    }
}
