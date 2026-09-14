package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.assertj.core.api.Assertions.assertThat;

/** Декоративная печать удалена: пустоту не заполняем картинкой без задачи. */
class SummarySealContractTest {
    @Test
    void summaryUsesResultsInsteadOfAnUnrelatedSeal() throws IOException {
        String html = CssRules.stripComments(CssRules.read("templates/session-summary.html"));
        assertThat(html).doesNotContain("summary-seal", "summary-scene", "scene-chronicle-sealed");
        assertThat(html).contains("summary.correctCount", "summary.totalQuestions", "summary.plannedQuestions");
        String css = CssRules.stripComments(CssRules.read("static/css/pages-summary.css"));
        assertThat(css).doesNotContain(".summary-seal", ".summary-scene");
    }
}
