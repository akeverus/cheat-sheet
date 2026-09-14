package com.cheatsheet.quiz.ui;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/** Итог, знаменатели и действие предшествуют необязательному опыту во всех раскладках. */
class SummaryTopContractTest {
    private Document document() throws IOException {
        return Jsoup.parse(CssRules.read("templates/session-summary.html"));
    }

    @Test
    void resultsAndFactsShareTheTopButActionsStayOutsideThatGrid() throws IOException {
        Document doc = document();
        assertThat(doc.select(".summary-top > .summary-hero")).hasSize(1);
        assertThat(doc.select(".summary-top > .summary-progress")).hasSize(1);
        assertThat(doc.select(".summary-top > .summary-actions")).isEmpty();
        assertThat(doc.select("main > .summary-actions")).hasSize(1);
        assertThat(doc.select(".summary-hero [th\\:replace]")).isEmpty();
    }

    @Test
    void answeredCountNamesThePlanAndTimeNamesItsScope() throws IOException {
        Document doc = document();
        String facts = doc.selectFirst(".summary-facts").outerHtml();
        assertThat(facts).contains("summary.totalQuestions", "summary.plannedQuestions",
                "summary.wrongCount", "summary.unknownCount", "summary.compactDuration");
        assertThat(doc.selectFirst(".summary-time-note").text()).contains("чтение", "паузы");
        assertThat(doc.select(".summary-progress a, .summary-progress button, .summary-progress input")).isEmpty();
    }

    @Test
    void optionalExperienceFollowsActionsAndUsesNativeDisclosure() throws IOException {
        Document doc = document();
        assertThat(doc.selectFirst(".summary-extras").tagName()).isEqualTo("details");
        assertThat(doc.select(".summary-extras > summary > span")).hasSize(1);
        assertThat(doc.selectFirst(".summary-extras").previousElementSibling().hasClass("summary-actions")).isTrue();
        assertThat(doc.select(".summary-extras[open]")).isEmpty();
        assertThat(doc.select(".summary-kpis, .summary-metrics")).isEmpty();
    }

    @Test
    void responsiveLayoutKeepsDomAndKeyboardOrderAligned() throws IOException {
        String css = CssRules.stripComments(CssRules.read("static/css/pages-summary.css"));
        assertThat(css).doesNotContain("{ order:", "display: contents");
        assertThat(css).contains("grid-template-columns: minmax(0, 1fr) minmax(0, 1fr)");
        assertThat(css).contains("--score-ring-size: 96px");
    }

    @Test
    void mistakeLinksLeadToTheNamedQuestion() throws IOException {
        String href = document().selectFirst(".summary-mistake-item").attr("th:href");
        assertThat(href).contains("questionId=${m.questionId()}", "topic=${m.topic()}");
    }
}
