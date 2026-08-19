package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.feature.interview.controller.MvcNavigationService;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Заметка к вопросу — на экране вопроса, а не только в списке закладок.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Хранилище заметок ({@code bookmark.note}) и эндпоинты существовали, но записать
 * мысль можно было ТОЛЬКО на {@code /bookmarks}: на экране, где вопрос перед глазами
 * и мысль возникает, поля не было вовсе. Значит её надо было запомнить до конца
 * сессии и найти вопрос в списке — то есть механизм существовал в месте, где им не
 * пользуются.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>поле есть на экране вопроса и отправляется обычной формой (без JS);</li>
 *   <li>уже написанное приходит с сервера — иначе человек стирает свой текст;</li>
 *   <li>возврат после сохранения — на ТОТ ЖЕ вопрос, а не «на следующий»;</li>
 *   <li>следствие (вопрос попадёт в закладки) названо, а не сделано молча;</li>
 *   <li>обработчик один на два экрана, а не вторая копия.</li>
 * </ol>
 */
class QuestionNoteContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String focusMarkup() throws IOException {
        return COMMENT.matcher(read("templates/focus-training.html")).replaceAll("");
    }

    @Test
    void theNoteFormLivesOnTheQuestionScreenAndWorksWithoutScripts() throws IOException {
        String markup = focusMarkup();
        assertThat(markup).contains("class=\"question-note\"");
        assertThat(markup)
                .as("обычная серверная форма: заметку пишут посреди сессии, терять текст нельзя")
                .contains("method=\"post\" th:action=\"@{/question/note}\"");
        assertThat(markup).contains("name=\"note\"");
        assertThat(markup)
                .as("CSRF-поле обязательно, иначе форма без JS получит 403")
                .containsPattern("question-note-form[\\s\\S]{0,400}_csrf\\.parameterName");
    }

    @Test
    void theExistingNoteComesFromTheServer() throws IOException {
        String markup = focusMarkup();
        assertThat(markup)
                .as("поле обязано приходить заполненным — пустое приглашает переписать заметку заново")
                .contains("<textarea name=\"note\" rows=\"3\" th:text=\"${questionNote}\"");
        assertThat(markup)
                .as("уже написанное показываем прозой: это ценность заметки, а не подпись к полю")
                .contains("class=\"question-note-text\"");

    }

    /**
     * Проверяем ПОВЕДЕНИЕ, а не текст исходника: адрес возврата — это контракт, и
     * читать его строкой из .java значило бы проверять написание, а не результат.
     */
    @Test
    void savingReturnsToTheSameQuestion() {
        MvcNavigationService navigation = new MvcNavigationService();
        assertThat(navigation.focusQuestionRedirect(7L))
                .as("возврат на «/» вне сессии выдал бы СЛЕДУЮЩИЙ вопрос — заметка осталась бы у потерянного")
                .isEqualTo("redirect:/?questionId=7");
        assertThat(navigation.focusRedirect())
                .as("обычный возврат на экран вопроса остаётся прежним")
                .isEqualTo("redirect:/");
    }

    @Test
    void theBookmarkConsequenceIsStatedNotSilent() throws IOException {
        assertThat(focusMarkup())
                .as("сохранение помечает вопрос избранным — молча менять состояние вопроса нельзя")
                .contains("class=\"question-note-hint\"")
                .contains("Закладки");
    }

    @Test
    void oneHandlerServesBothScreens() throws IOException {
        String js = read("static/js/quiz/bookmarks.js");
        assertThat(js)
                .as("гейт по классу страницы означал бы вторую копию обработчика для экрана вопроса")
                .doesNotContain("classList.contains('bookmarks-page')")
                .contains("if (!document.querySelector('.bookmark-note-form')) return;");
        assertThat(focusMarkup())
                .as("форма экрана вопроса носит тот же класс, которым обработчик её находит")
                .contains("class=\"bookmark-note-form question-note-form\"");
    }
}
