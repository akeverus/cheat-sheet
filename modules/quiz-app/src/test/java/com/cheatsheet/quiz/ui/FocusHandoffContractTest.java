package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Передача фокуса через MPA-переход шага цикла (A11Y-11, REF-014).
 *
 * <p>Замеры REF-012 (живой Chromium): после AJAX-ответа фокус садится на вердикт,
 * серверный result сажает его autofocus'ом — а вот GET «Следующий вопрос» и
 * redirect'ы форм цикла (skip, finish → итоги, resume…) оставляли activeElement
 * на BODY. Клавиатурный пользователь после КАЖДОГО шага начинал Tab-путь с нуля
 * (skip-link → шапка → навигация → …), скринридер терял место в цикле (§54).</p>
 *
 * <p>Механика — рукопожатие из трёх частей, и контракт держит все три: перед
 * переходом шага ставится sessionStorage-флаг (клик по [data-cycle-next] или
 * submit формы цикла), после загрузки фокус садится на main#main-content
 * (tabindex=-1 уже в разметке всех страниц цикла) и флаг снимается. Обычные
 * заходы и reload флага не имеют — skip-link остаётся первым Tab'ом, фокус
 * никто не угоняет.</p>
 */
class FocusHandoffContractTest {

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    @Test
    void cycleFocusModuleCarriesTheFullHandshake() throws IOException {
        String js = read("static/js/ui/cycle-focus.js");
        assertThat(js)
                .as("флаг перехода не ставится — фокус после шага цикла снова на BODY")
                .contains("sessionStorage.setItem");
        assertThat(js)
                .as("флаг обязан сниматься после употребления — иначе обычный reload угонит фокус")
                .contains("sessionStorage.removeItem");
        assertThat(js)
                .as("фокус садится на main#main-content — точку отсчёта контента")
                .contains("getElementById('main-content')");
        assertThat(js)
                .as("формы цикла ставят флаг на submit (redirect ведёт на следующий экран цикла)")
                .contains("form[action$=\"/skip\"]")
                .contains("form[action$=\"/finish\"]")
                .contains("form[action$=\"/resume\"]");
    }

    @Test
    void nextQuestionLinksAreMarkedAsCycleSteps() throws IOException {
        assertThat(read("templates/fragments/training-actions.html"))
                .as("ссылка «Следующий вопрос» AJAX-разбора не помечена шагом цикла")
                .containsPattern("id=\"next-question\"[^>]*data-cycle-next");
        assertThat(read("templates/result.html"))
                .as("«Следующий вопрос» серверного result не помечен шагом цикла")
                .contains("data-cycle-next");
        assertThat(read("templates/focus-training.html"))
                .as("browse-ссылка «Следующий вопрос» не помечена шагом цикла")
                .containsPattern("flashcard-browse-next[^>]*data-cycle-next|data-cycle-next[^>]*flashcard-browse-next");
    }

    @Test
    void appEntryInitializesTheHandoff() throws IOException {
        assertThat(read("static/js/app-entry.js"))
                .as("модуль не инициализируется — рукопожатие мёртвое")
                .contains("initCycleFocusHandoff()");
    }

    /**
     * Рукопожатие уступает более специфичному приземлению страницы (REF-017).
     *
     * <p>Живая регрессия REF-014, пойманная только e2e (typed recall, оба движка):
     * POST /flashcard-reveal — форма цикла, флаг ставился, и после загрузки
     * cycle-focus сажал фокус на main УЖЕ ПОСЛЕ того, как сработал autofocus
     * эталонного ответа (.flashcard-answer, инвариант iter-167) — то есть угонял
     * фокус с точного приземления на общее. Правило: страница с [autofocus]
     * знает лучше — флаг снимается, фокус не трогается.</p>
     */
    @Test
    void handoffYieldsToThePagesOwnAutofocus() throws IOException {
        // Комментарии стрипаются: упоминание [autofocus] в прозе не является кодом
        // уступки (первая редакция контракта пропустила мутацию именно из-за этого).
        String js = read("static/js/ui/cycle-focus.js")
                .replaceAll("(?s)/\\*.*?\\*/", "")
                .replaceAll("(?m)^\\s*//.*$", "");
        assertThat(js)
                .as("модуль обязан запрашивать [autofocus] кодом, не комментарием")
                .contains("querySelector('[autofocus]')");
        int autofocusCheck = js.indexOf("querySelector('[autofocus]')");
        int mainFocus = js.indexOf("getElementById('main-content')");
        assertThat(autofocusCheck)
                .as("уступка autofocus должна стоять ДО общей посадки на main")
                .isLessThan(mainFocus)
                .isGreaterThan(-1);
        assertThat(js)
                .as("посадка на main обязана быть под условием отсутствия autofocus")
                .containsPattern("if\\s*\\(!specific\\)");
    }
}
