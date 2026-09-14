package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/** Recovery precedes optional diagnostics; raw backend error payloads stay out of the UI. */
class ErrorPageCompositionContractTest {

    private static final Path ERROR_CSS = Path.of("src/main/resources/static/css/pages-error.css");
    private static final Path ERROR_HTML = Path.of("src/main/resources/templates/error.html");
    private static final Path HANDLER =
            Path.of("src/main/java/com/cheatsheet/quiz/common/exception/GlobalExceptionHandler.java");
    private static final Path ATTRIBUTES =
            Path.of("src/main/java/com/cheatsheet/quiz/common/exception/ErrorViewAttributes.java");

    @Test
    void pageUsesTheSharedCanvasAndReadingMeasure() {
        String html = stripHtmlComments(read(ERROR_HTML));
        String css = stripComments(read(ERROR_CSS));
        assertThat(html).contains("class=\"ed-page page-canvas\"");
        assertThat(css).contains(".error-card", "max-width: 52rem", "max-width: 60ch");
        assertThat(css).doesNotContain("position: fixed", "grid-template-areas");
    }

    @Test
    void recoveryPrecedesOptionalDiagnosticsInReadingOrder() {
        String html = stripHtmlComments(read(ERROR_HTML));
        int heading = html.indexOf("id=\"error-heading\"");
        int recovery = html.indexOf("class=\"error-actions\"");
        int diagnostics = html.indexOf("<details class=\"error-dossier\"");
        assertThat(heading).isNotNegative();
        assertThat(recovery).isGreaterThan(heading);
        assertThat(diagnostics).isGreaterThan(recovery);
        assertThat(html).contains("class=\"disclosure-summary\"", "${correlationId}", "${errorTime}");
    }

    @Test
    void errorVariantsOfferSpecificExplanationsAndWorkingExits() {
        String html = stripHtmlComments(read(ERROR_HTML));
        assertThat(html).contains("code == 404", "code == 403", "code == 401", "code == 400", "code &gt;= 500");
        assertThat(html).contains("@{/login}", "@{/}", "@{/topics}");
        assertThat(html).contains("aria-labelledby=\"error-heading\"", "aria-label=\"Что дальше\"");
    }

    @Test
    void diagnosticsDoNotExposeBackendMessagesOrInternalPaths() {
        String html = stripHtmlComments(read(ERROR_HTML));
        assertThat(html).doesNotContain("${message}", "${error}", "${path}", "Admin-токен", "admin-токен");
        assertThat(html).contains("Идентификатор запроса", "Код ответа");
    }

    @Test
    void bothDeliveryPathsFillTheSameModel() {
        String handler = read(HANDLER);
        String attributes = read(ATTRIBUTES);

        assertThat(handler)
                .as("в GlobalExceptionHandler нет общего errorView(...): три обработчика снова "
                        + "собирают модель вручную и расходятся. Так и было — handleGenericException "
                        + "не клал даже status, и страница 500 печатала запасное «×» вместо номера")
                .contains("private ModelAndView errorView(");
        assertThat(handler.split("new ModelAndView\\(\"error\"\\)", -1).length - 1)
                .as("страница ошибки собирается более чем в одном месте обработчика — ключи модели "
                        + "снова разъедутся, и одна и та же страница будет выглядеть по-разному "
                        + "на 403 и на 500")
                .isEqualTo(1);

        assertThat(attributes)
                .as("контейнерная доставка (BasicErrorController → error.html) не дополняет модель "
                        + "correlationId — на 403/401 дозор потеряет идентификатор запроса, "
                        + "а на пути через обработчик он будет")
                .contains("CorrelationIdFilter.REQUEST_ATTRIBUTE")
                .contains("\"correlationId\"");
        assertThat(attributes)
                .as("контейнерная доставка не дополняет модель errorTime — в дозор попадёт сырой "
                        + "Date.toString() вида «Fri Aug 07 21:29:41 MSK 2026»")
                .contains("\"errorTime\"");
        assertThat(handler)
                .as("обработчик форматирует время своим форматом — два пути дадут разный вид "
                        + "одного поля; формат обязан быть один на оба")
                .contains("ErrorViewAttributes.TIME_FORMAT");
    }

    /** Комментарии документируют свои же значения прозой — снимать до любого замера. */
    private static String stripComments(String css) {
        return css.replaceAll("(?s)/\\*.*?\\*/", "");
    }

    /**
     * То же для шаблона: комментарий объясняет, почему дозор больше НЕ {@code <details>},
     * и без этой чистки контракт запретил бы собственное объяснение.
     */
    private static String stripHtmlComments(String html) {
        return html.replaceAll("(?s)<!--.*?-->", "");
    }

    private static String read(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
