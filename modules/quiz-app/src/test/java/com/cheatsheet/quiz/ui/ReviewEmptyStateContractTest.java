package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Пустой разбор ошибок называет событие и показывает, куда идти.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * `/review` — это экран тренировки с фильтром по ошибкам (замер подтвердил: композиция,
 * положение действия и высота документа совпадают с `/` до пикселя на 390/1280/1512/2560
 * в обоих движках; отличается только чип режима). Собственное у него одно — пустое
 * состояние, и оно говорило неправду:
 *
 * <ul>
 *   <li>глиф — опускная решётка «путь закрыт», хотя отсутствие ошибок это ХОРОШИЙ исход;
 *       глиф под этот исход в продукте уже был («очищенная доска», вкладка «Ошибки» в
 *       аналитике) — одно событие рисовалось двумя разными картинками;</li>
 *   <li>заголовок «Сейчас нет вопросов» — общий на все причины;</li>
 *   <li>главным действием стояло «Обновить повтор ошибок» — перезагрузка того же
 *       адреса, на котором вопросов заведомо нет. Тот же довод уже снял такую кнопку
 *       из ветки пустого отбора, а в разборе она осталась;</li>
 *   <li>под карточкой пустовало 412px на 1512 и 641px на 2560, и экран не отвечал на
 *       «а где ошибки тогда есть».</li>
 * </ul>
 *
 * <h2>Замер после</h2>
 * Карточка 247 → 522px (список тем занял пустоту), пустота под ней 412 → 268 (1512) и
 * 641 → 527 (2560); главное действие остаётся в первом экране на 360–2560 в обоих
 * движках.
 */
class ReviewEmptyStateContractTest {

    private static final Pattern COMMENT = Pattern.compile("<!--.*?-->", Pattern.DOTALL);

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    private String focusMarkup() throws IOException {
        return COMMENT.matcher(read("templates/focus-training.html")).replaceAll("");
    }

    /** Одно событие — одна картинка: «ошибок нет» рисуется очищенной доской. */
    @Test
    void theEmptyReviewUsesTheCleanSlateGlyph() throws IOException {
        String markup = focusMarkup();
        assertThat(markup)
                .as("в разборе снова глиф «путь закрыт» вместо «разобрано»")
                .containsPattern("th:if=\"\\$\\{reviewMode}\"><use href=\"#state-clean-slate\"");
        assertThat(markup)
                .as("вне разбора «путь закрыт» (state-locked) остаётся: там пустота "
                        + "действительно про «некуда идти»")
                .containsPattern("th:unless=\"\\$\\{reviewMode}\"><use href=\"#state-locked\"");
    }

    @Test
    void theTitleNamesTheEventAndNotTheScreenState() throws IOException {
        String markup = focusMarkup();
        assertThat(markup)
                .as("заголовок разбора снова общий: «Сейчас нет вопросов» верно для пустого "
                        + "отбора и неверно для разобранных ошибок")
                .contains("'В этой выборке ошибок нет' : 'Ошибок для повтора нет'");
        assertThat(markup)
                .as("вне разбора заголовок остаётся прежним")
                .containsPattern("<h1 class=\"empty-state-title\" th:unless=\"\\$\\{reviewMode}\">Сейчас нет вопросов</h1>");
    }

    /**
     * Кнопка, которая перезагружает адрес с заведомо пустым результатом, — это петля.
     */
    @Test
    void theReviewBranchHasNoReloadLoop() throws IOException {
        String markup = focusMarkup();
        assertThat(markup)
                .as("«Обновить повтор ошибок» вернулось в разбор: это перезагрузка того же "
                        + "адреса, на котором вопросов нет")
                .containsPattern("class=\"btn empty-action-retry\" th:unless=\"\\$\\{reviewMode}\"");
        assertThat(markup)
                .as("в разборе обязаны быть свои действия: «все ошибки» и «продолжить»")
                .contains("empty-action-review-all")
                .contains("empty-action-train");
    }

    /**
     * Маршруты — ссылки, то есть точки обхода с клавиатуры. Их место после действий:
     * первая сборка ставила список выше, и на 390×844 карточка выросла с 395 до 891px,
     * уведя главное действие за сгиб.
     */
    @Test
    void theRoutesComeAfterTheActions() throws IOException {
        String markup = focusMarkup();
        int actions = markup.indexOf("class=\"empty-state-actions\"");
        int routes = markup.indexOf("class=\"empty-state-routes\"");
        assertThat(actions).as("блока действий нет").isNotNegative();
        assertThat(routes).as("списка маршрутов нет").isNotNegative();
        assertThat(routes)
                .as("список маршрутов снова выше действий: пять ссылок оттесняют кнопку, "
                        + "ради которой экран и открывают, и обход с клавиатуры идёт мимо неё")
                .isGreaterThan(actions);
        assertThat(markup)
                .as("маршруты обязаны вести в разбор по теме, а не в общую тренировку")
                .containsPattern("empty-state-routes[\\s\\S]{0,600}?th:href=\"@\\{/review\\(topic=");
    }

}
