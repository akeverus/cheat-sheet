package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * После серверной навигации фокус приземляется в результат действия, а не на BODY.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Раскрытие эталона — POST /flashcard-reveal, то есть НОВЫЙ документ; ответ без
 * скриптов — POST /answer, тоже новый документ. Замер (iter-167, оба движка): во
 * всех четырёх reveal-режимах (TYPED_RECALL / FLASHCARD / INTERVIEW /
 * CODE_CHALLENGE) фокус после раскрытия стоял на BODY — клавиатурному и
 * SR-пользователю приходилось заново табать весь каркас до эталона. JS-путь MCQ
 * при этом фокусирует приёмник разбора явно (AnsweredOrder) — серверные пути
 * отставали от него.
 *
 * <h2>Что закреплено</h2>
 * autofocus + tabindex="-1" на раскрытом эталоне и на вердикте /answer: это HTML,
 * работает без скриптов. Замер после: все четыре режима и no-JS /answer
 * приземляются в контент в обоих движках. Study-LEARN autofocus НЕ несёт
 * намеренно: карточка изучения — обычная загрузка страницы, а не результат
 * действия «раскрыть».
 */
class RevealFocusContractTest {

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    @Test
    void theRevealedAnswerIsTheFocusLanding() throws IOException {
        String template = read("templates/focus-training.html");
        assertThat(template)
                .as("раскрытый эталон обязан быть приземлением фокуса: без autofocus "
                        + "POST /flashcard-reveal бросает клавиатуру на BODY")
                .containsPattern("th:if=\"\\$\\{sessionRecallMode and flashcardRevealed\\}\"[^>]*"
                        + "tabindex=\"-1\" autofocus");
        assertThat(template)
                .as("у приземления обязано быть SR-имя — иначе скринридер читает залпом")
                .containsPattern("flashcardRevealed[^>]*aria-label=\"Ответ раскрыт\"");
    }

    @Test
    void theNoJsVerdictIsTheFocusLanding() throws IOException {
        assertThat(read("templates/result.html"))
                .as("вердикт /answer обязан быть приземлением фокуса no-JS пути")
                .containsPattern("feedback-verdict result-banner[^>]*\"\\s*\\n\\s*tabindex=\"-1\" autofocus");
    }

    /** Учебная карточка LEARN — обычная загрузка, autofocus там был бы захватом. */
    @Test
    void theStudyLearnCardDoesNotStealFocus() throws IOException {
        String template = read("templates/focus-training.html");
        int learn = template.indexOf("studyLearnPhase and !flashcardMode");
        assertThat(learn).isNotNegative();
        assertThat(template.substring(learn, Math.min(template.length(), learn + 400)))
                .as("autofocus на карточке изучения захватывал бы фокус при обычной загрузке")
                .doesNotContain("autofocus");
    }
}
