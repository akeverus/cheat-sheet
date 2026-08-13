package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Страница ошибки — статусный отчёт, а не читательская полоса: её ширину ведёт общая
 * шкала страницы, дозор виден при любой доставке модели, а две области разъезжаются
 * только там, где обеим хватает меры.
 *
 * <h2>Найденные дефекты, а не гипотезы</h2>
 * Замер WebKit 18.2 (DPR 2, матрица 1280…2560, {@code deviceScaleFactor: 2}), 2026-08-07:
 *
 * <ul>
 *   <li><b>Замороженный кап.</b> {@code .error-page .ed-page} нёс личный
 *       {@code max-width: var(--max-width-focus)} — 960px на любом окне. Неиспользованная
 *       ширина полосы: 100px (1280) → 332 (1512) → 740 (1920) → <b>1380</b> (2560), при
 *       карточке 800–858px. Это «одна маленькая карточка на огромном холсте» из §6.
 *       Допущение «страница ошибки читательская, значит мера focus» было ложным: длинного
 *       текста тут нет вовсе — есть заголовок, абзац и таблица фактов;</li>
 *   <li><b>Ранний разъезд.</b> Две области расходились с 901px при дозоре, жёстко
 *       заданном 23rem (368px). На 901–1199px заявлению оставалось 217–324px: главная
 *       колонка оказывалась <b>уже</b> вспомогательной. Замер на 1024: st=324 против
 *       ds=368. Тот же диапазон включается браузерным зумом 125–150% на 1512;</li>
 *   <li><b>Невидимый дозор.</b> Сведения (путь, статус, время, идентификатор запроса)
 *       рендерились только на контейнерном dispatch и лежали в свёрнутом {@code <details>}.
 *       На 404 и 500 — там, где они нужнее всего, — правая область была бы пустой рамкой
 *       ради симметрии.</li>
 * </ul>
 *
 * <p>Ни один пункт не виден на скриншоте одной ширины: мёртвая полоса растёт монотонно и
 * читается только сравнением вьюпортов, а перевёрнутая иерархия колонок живёт в узкой
 * вилке между брейкпоинтами.
 *
 * <h2>Что именно закреплено</h2>
 * Проверяем структурно — по составу правил, а не по вычисленному числу: считать CSS
 * {@code calc}/{@code min} в тесте значило бы проверять свою же арифметику.
 */
class ErrorPageCompositionContractTest {

    private static final Path ERROR_CSS = Path.of("src/main/resources/static/css/pages-error.css");
    private static final Path ERROR_HTML = Path.of("src/main/resources/templates/error.html");
    private static final Path HANDLER =
            Path.of("src/main/java/com/cheatsheet/quiz/common/exception/GlobalExceptionHandler.java");
    private static final Path ATTRIBUTES =
            Path.of("src/main/java/com/cheatsheet/quiz/common/exception/ErrorViewAttributes.java");

    /** Тело правила полосы страницы ошибки. */
    private static final Pattern PAGE_RULE = Pattern.compile(
            "\\.error-page \\.ed-page\\s*\\{([^}]*)}");

    /** Тело правила композиции. */
    private static final Pattern CARD_RULE = Pattern.compile(
            "\\.error-page \\.error-card\\s*\\{([^}]*)}");

    /** Открывающая строка медиазапроса, внутри которого композиция становится двухколоночной. */
    private static final Pattern SPLIT_MEDIA = Pattern.compile(
            "@media \\(min-width:\\s*(\\d+)px\\)\\s*\\{\\s*html\\[data-design] \\.error-page \\.error-card\\s*\\{"
                    + "[^}]*grid-template-areas:\\s*\"statement dossier\"");

    @Test
    void pageBandHasNoPersonalCap() {
        Matcher matcher = PAGE_RULE.matcher(stripComments(read(ERROR_CSS)));
        assertThat(matcher.find())
                .as("в pages-error.css нет правила .error-page .ed-page — полосу переименовали, "
                        + "контракт надо переписать под факт")
                .isTrue();

        do {
            assertThat(matcher.group(1))
                    .as("полосе страницы ошибки вернули личный max-width: она снова застынет "
                            + "константой, а неиспользованная ширина вырастет до 1380px на 2560 "
                            + "при карточке 800px — ровно дефект, который эта итерация закрыла. "
                            + "Ширину ведёт общая шкала --max-width-page, меру композиции — "
                            + ".error-card")
                    .doesNotContain("max-width");
        } while (matcher.find());
    }

    @Test
    void compositionCarriesItsOwnMeasure() {
        Matcher matcher = CARD_RULE.matcher(stripComments(read(ERROR_CSS)));
        assertThat(matcher.find()).as("в pages-error.css нет правила .error-page .error-card").isTrue();
        String body = matcher.group(1);

        assertThat(body)
                .as("композиция без своей меры растянется во всю полосу (1792px на 2560) — "
                        + "строка абзаца уйдёт далеко за меру чтения")
                .contains("width: min(100%, 76rem)");
        assertThat(body)
                .as("композиция без margin: 0 auto прижмётся к левому краю полосы, и весь "
                        + "остаток ширины свалится в правый пустой угол (§6)")
                .contains("margin: 0 auto");
        assertThat(body)
                .as("области композиции заданы мимо grid-template-areas — порядок DOM перестанет "
                        + "совпадать с порядком чтения и обхода табом")
                .contains("grid-template-areas");
    }

    @Test
    void columnsSplitOnlyWhereBothFit() {
        // Пробелы схлопываем: медиазапрос и вложенное правило разделены переводом строки,
        // а регулярка описывает их как одну строку.
        Matcher matcher = SPLIT_MEDIA.matcher(stripComments(read(ERROR_CSS)).replaceAll("\\s+", " "));

        assertThat(matcher.find())
                .as("в pages-error.css нет медиазапроса, включающего области \"statement dossier\" — "
                        + "либо композицию переименовали, либо две области больше не расходятся")
                .isTrue();

        int breakpoint = Integer.parseInt(matcher.group(1));
        assertThat(breakpoint)
                .as("две области расходятся с %dpx при дозоре, зафиксированном 23rem (368px): "
                        + "заявлению останется меньше 462px и главная колонка окажется УЖЕ "
                        + "вспомогательной (замер на 1024: 324 против 368). Порог обязан оставлять "
                        + "заявлению его меру — 1200px первая честная ширина", breakpoint)
                .isGreaterThanOrEqualTo(1200);
    }

    @Test
    void dossierIsNeverHidden() {
        String css = stripComments(read(ERROR_CSS)).replaceAll("\\s+", " ");

        assertThat(css)
                .as("дозор снова прячут через display: none. Он несёт путь, статус, время и "
                        + "идентификатор запроса — ради них страницу и открывают; без них она "
                        + "не соединяется с лог-строкой")
                .doesNotContain(".error-dossier { display: none")
                .doesNotContain(".error-dossier{display:none");
    }

    @Test
    void templateShowsDossierOnEveryDeliveryPath() {
        String html = stripHtmlComments(read(ERROR_HTML));

        assertThat(html)
                .as("дозор вернули в <details>: свёрнутый он и рендерился только на контейнерном "
                        + "dispatch, так что на 404 и 500 сведений на экране не было вовсе")
                .doesNotContain("<details");
        assertThat(html)
                .as("в шаблоне нет области дозора — вторая колонка станет пустой рамкой ради "
                        + "симметрии, что §6 запрещает прямо")
                .contains("class=\"error-dossier\"")
                .contains("class=\"error-meta\"");
        assertThat(html)
                .as("идентификатор запроса не выводится: он есть в логе и в ProblemDetail API, "
                        + "но пользовательская жалоба «упало» без него не соединяется с лог-строкой")
                .contains("${correlationId}");
        // Иллюстрация ошибки декоративна и обязана быть скрыта от скринридера. В iter-119
        // растровый <img> заменён глифом спрайта, у <svg> атрибута alt не бывает —
        // эквивалент здесь aria-hidden. Проверяется СВОЙСТВО (скрыта), а не тег.
        assertThat(html)
                .as("иллюстрация ошибки исчезла со страницы")
                .contains("class=\"error-pixel-art");
        assertThat(html)
                .as("декоративная иллюстрация не скрыта от скринридера — озвучится мусором "
                        + "между заголовком дозора и его содержимым")
                .containsPattern("(?s)class=\"error-pixel-art.{0,400}?(alt=\"\"|aria-hidden=\"true\")");
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
