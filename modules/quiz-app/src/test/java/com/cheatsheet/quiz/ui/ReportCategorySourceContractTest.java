package com.cheatsheet.quiz.ui;

import com.cheatsheet.quiz.domain.QuestionIssueCategory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Откуда модалка «Сообщить о проблеме» берёт список категорий.
 *
 * <p>До этого гейта список жил вторым экземпляром — константой {@code REPORT_CATEGORIES}
 * в {@code ui/modals.js}: пять пар «имя константы → русская подпись», которые
 * компилятор не сверял ни с {@link QuestionIssueCategory}, ни с его
 * {@link QuestionIssueCategory#label()}.
 *
 * <p>Обычная копия перечисления отказывает громко: неизвестное имя не разбирается,
 * запрос падает, кто-нибудь видит 400. Здесь фон другой и потому опаснее —
 * {@link QuestionIssueCategory#fromString} намеренно снисходителен: {@code null},
 * пусто и любое незнакомое имя молча становятся {@link QuestionIssueCategory#OTHER},
 * чтобы жалоба пользователя не потерялась из-за расхождения фронта и бэкенда.
 * Переименуй константу — и ничего не сломается: пользователь выберет «Опечатка»,
 * на проверку уедет «Другое», в логе не останется ни строчки. Это не тихая поломка,
 * а <b>тихая переклассификация</b>, и заметить её можно только по статистике жалоб.
 *
 * <p>Поэтому список приходит с сервера готовой разметкой — инертным
 * {@code <template id="report-category-options">}, который JS клонирует. Инертным:
 * без JS модалки нет, и {@code <template>} ничего не рисует. Ни сериализации в JSON,
 * ни экранирования, ни nonce для CSP.
 *
 * @see com.cheatsheet.quiz.domain.QuestionIssueCategory
 */
class ReportCategorySourceContractTest {

    private static final Path TEMPLATES = Path.of("src/main/resources/templates");
    private static final Path JS = Path.of("src/main/resources/static/js");

    private static final Pattern JS_COMMENT =
            Pattern.compile("/\\*.*?\\*/|//[^\\n]*", Pattern.DOTALL);

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Комментарии не проверяем: они как раз обязаны называть категории по имени. */
    private static String code(Path path) {
        return JS_COMMENT.matcher(read(path)).replaceAll("");
    }

    @Test
    void modalsJsHoldsNoSecondCopyOfTheCategorySet() {
        String modals = code(JS.resolve("ui/modals.js"));
        for (QuestionIssueCategory category : QuestionIssueCategory.values()) {
            assertThat(modals)
                    .describedAs("modals.js держит имя категории " + category
                            + " строковым литералом — копия перечисления, невидимая компилятору")
                    .doesNotContain("'" + category.name() + "'")
                    .doesNotContain("\"" + category.name() + "\"");
            assertThat(modals)
                    .describedAs("modals.js держит подпись категории " + category
                            + " своими словами — вторая редакция того же текста")
                    .doesNotContain(category.label());
        }
        assertThat(modals)
                .describedAs("категории должны клонироваться из серверного <template>")
                .contains("report-category-options");
    }

    @Test
    void templateRendersEveryCategoryFromTheServer() {
        assertThat(read(TEMPLATES.resolve("focus-training.html")))
                .describedAs("нет <template> — нет и группы категорий в модалке")
                .contains("id=\"report-category-options\"")
                .contains("cat : ${reportCategories}")
                .describedAs("значение радиокнопки — имя константы, подпись — её label()")
                .contains("th:value=\"${cat.name()}\"")
                .contains("th:text=\"${cat.label()}\"");
    }

    /**
     * Ни одна категория не выбрана заранее.
     *
     * <p>Предвыбор первой отправлял каждую жалобу, отправленную не глядя, как
     * «Неверный ответ» — самую тревожную из пяти и единственную, которая означает
     * фактическую ошибку в вопросе. Разобрать такую статистику потом нельзя: осознанный
     * выбор и значение по умолчанию в базе неразличимы. Пустая радиогруппа этой
     * двусмысленности не создаёт — сервер подставит {@code OTHER} сам.
     */
    @Test
    void noCategoryIsPreSelected() {
        assertThat(read(TEMPLATES.resolve("focus-training.html")))
                .describedAs("th:checked/checked в шаблоне категорий вернул бы предвыбор")
                .doesNotContain("th:checked")
                .doesNotContain("name=\"report-category\" checked");
        assertThat(code(JS.resolve("ui/modals.js")))
                .describedAs("JS не должен доставлять предвыбор в обход шаблона")
                .doesNotContain(".checked = true");
    }
}
