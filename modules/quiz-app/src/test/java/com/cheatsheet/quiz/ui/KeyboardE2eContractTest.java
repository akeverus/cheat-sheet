package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Клавиатурный e2e говорит с каждым движком на его языке и покрывает весь цикл.
 *
 * <h2>Найденный факт, а не гипотеза</h2>
 * Замер (iter-160, Playwright 1.61): WebKit, как настольный Safari, по умолчанию
 * НЕ включает кнопки и ссылки в обход по Tab — 80 нажатий Tab на /settings так и
 * не дошли до кнопки старта. Option+Tab ('Alt+Tab') дошёл за 19 нажатий — ровно
 * как Tab в Chromium. Полная цепочка (старт → выбор → отправка → фокус в разборе →
 * следующий вопрос) в WebKit проходит целиком — дефекта продукта нет, «падение»
 * было поведением платформы.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>e2e-конфиг держит WebKit-проект — иначе клавиатура опять проверяется
 *       только в Chromium, и Safari-регрессия невидима;</li>
 *   <li>WebKit-проект ограничен клавиатурным спеком: visual-baselines сняты в
 *       Chromium, полный прогон матрицы дал бы ложные пиксельные диффы;</li>
 *   <li>спек выбирает клавишу по движку — жёсткий 'Tab' в WebKit ложно красный;</li>
 *   <li>спек покрывает старт сессии и переход к следующему вопросу, а не только
 *       ответ на уже открытом вопросе.</li>
 * </ol>
 *
 * <p>Файлы читаются из рабочего дерева (../../e2e) — они не ресурсы модуля,
 * но контракт на них держит ровно этот модуль: разметка задаёт tab-порядок.</p>
 */
class KeyboardE2eContractTest {

    private String read(String repoRelative) throws IOException {
        Path path = Path.of("../..", repoRelative);
        assertThat(Files.exists(path))
                .as("файл %s пропал из рабочего дерева", repoRelative)
                .isTrue();
        return Files.readString(path);
    }

    /**
     * С iter-164 WebKit гоняет ВСЁ, включая визуальную матрицу: базлайны
     * разведены по {@code {projectName}}. Прежний контракт «webkit ограничен
     * клавиатурным спеком» снят вместе со своей причиной — общим путём голденов,
     * в котором второй движок перезаписывал бы снимки первого.
     */
    @Test
    void theWebkitProjectRunsEverythingOnItsOwnBaselines() throws IOException {
        String config = read("e2e/playwright.config.js");
        assertThat(config)
                .as("WebKit-проект пропал: «Chromium недостаточен» — обязательный гейт")
                .contains("name: 'webkit'");
        assertThat(config)
                .as("testMatch у webkit вернулся: матрица снова слепа в Safari")
                .doesNotContainPattern("name: 'webkit'.*testMatch");
        assertThat(config)
                .as("{projectName} в пути голденов обязателен: без него движки "
                        + "перезаписывают снимки друг друга")
                .contains("visual-baselines/{projectName}/{arg}{ext}");
    }

    @Test
    void theSpecSpeaksEachEnginesTabConvention() throws IOException {
        String spec = read("e2e/keyboard-quiz.spec.js");
        assertThat(spec)
                .as("движко-зависимый выбор клавиши пропал: WebKit по кнопкам ходит "
                        + "Option+Tab, жёсткий 'Tab' даёт ложно-красный прогон")
                .contains("browserName === 'webkit' ? 'Alt+Tab' : 'Tab'");
        assertThat(spec)
                .as("полноцикловый тест обязан брать клавишу из конвенции движка")
                .contains("const TAB = tabKey(browserName)");
    }

    @Test
    void theSpecCoversTheWholeCycleNotJustTheAnswer() throws IOException {
        String spec = read("e2e/keyboard-quiz.spec.js");
        assertThat(spec)
                .as("старт сессии с конструктора клавиатурой больше не покрыт")
                .contains("builder-start-cta");
        assertThat(spec)
                .as("переход к следующему вопросу клавиатурой больше не покрыт")
                .contains("Следующий вопрос");
        assertThat(spec)
                .as("фокус после ответа обязан проверяться в приёмнике разбора")
                .contains("closest('#result-feedback, .result-feedback')");
    }

    /**
     * Конструктор сессии — собственная поверхность /train (b0a45a6f, UXD-001) и
     * ключевая страница цикла: обязана жить в визуальной матрице обоих движков.
     * Волатильные счётчики набора (.builder-outcome, чипы, превью) — в масках.
     */
    @Test
    void theTrainSurfaceIsGuardedByTheVisualMatrix() throws IOException {
        String spec = read("e2e/visual-matrix.spec.js");
        assertThat(spec)
                .as("/train выпал из матрицы: ключевая поверхность без визуальной страховки")
                .contains("{ id: 'train-dark-med-auto', path: '/train'")
                .contains("{ id: 'train-light-med-on', path: '/train'");
        assertThat(spec)
                .as("счётчики набора обязаны быть в масках — иначе голден краснеет от " 
                        + "каждого ответа")
                .contains("'.builder-outcome', '.filter-chip-count', '.session-preview',");
    }

    /**
     * Шрифтовая сеть — постоянный страж обоих движков (iter-178): для
     * same-origin шрифтов Chromium и WebKit зеркально расходятся в матчинге
     * preload и @font-face-загрузки (6 против 12 сетевых запросов), поэтому
     * crossOrigin ветвится по UA в head-скрипте, а спек держит инвариант
     * «каждый woff2 — ровно один запрос» и «преднагруженное реально пришло».
     */
    @Test
    void theFontNetworkSpecGuardsBothDoubleLoadEnds() throws IOException {
        String spec = read("e2e/font-network.spec.js");
        assertThat(spec)
                .as("страж двойной загрузки пропал: рассинхрон crossorigin снова невидим")
                .contains("every woff2 is fetched exactly once");
        assertThat(spec)
                .as("проверка «преднагруженное запрошено» пропала: мёртвый preload невидим")
                .contains("так и не запрошен сетью");
    }

    /**
     * Chart.js оплачивается просмотром (iter-179): библиотека приезжает по
     * видимости канваса, как mermaid. Спек держит оба конца в обоих движках:
     * наверху /stats запроса chart.umd нет; после скролла к канвасу — есть,
     * и график реально отрисован.
     */
    @Test
    void theChartLazySpecGuardsBothEnds() throws IOException {
        String spec = read("e2e/chart-lazy.spec.js");
        assertThat(spec)
                .as("страж ленивой загрузки пропал: жадный chart.umd снова невидим")
                .contains("chart.umd arrives only when a canvas becomes visible");
        assertThat(spec)
                .as("проверка отрисовки пропала: библиотека может приезжать и молча не рисовать")
                .contains("но график не отрисован");
    }

    /**
     * WCAG 1.4.10 Reflow — постоянный страж: 320px без горизонтальной прокрутки
     * на всех семи поверхностях (замер iter-175 — чисто в обоих движках; спек
     * держит достигнутое, а перечень поверхностей закреплён здесь).
     */
    @Test
    void theReflowSpecGuardsAllSevenSurfaces() throws IOException {
        String spec = read("e2e/reflow.spec.js");
        assertThat(spec)
                .as("перечень reflow-поверхностей похудел")
                .contains("const SURFACES = ['/', '/train', '/stats', '/topics', "
                        + "'/bookmarks', '/settings', '/nope-404'];");
        assertThat(spec).contains("toBeLessThanOrEqual(1)");
        // Зум-матрица macOS (iter-183): ширины реальных зумов 90/110/125% ложатся
        // МЕЖДУ брейкпоинтами респонсив-матрицы — их переломы невидимы стандартным
        // прогонам. 1152 = 1440@125%, 1382 = 1728@125%, 1571 = 1728@110%.
        assertThat(spec)
                .as("зум-матрица пропала: переломы между брейкпоинтами снова невидимы")
                .contains("const ZOOM_WIDTHS = [1152, 1382, 1571];");
    }

    /**
     * Карточный цикл покрыт отдельно: MCQ-тесты скипаются на флешкартах, и до
     * iter-166 Space-раскрытие с оценкой 1–4 не проверялись нигде. Оценка
     * жмётся с повтором — в WebKit видимость оценок опережает подвес их
     * клавиатурного обработчика, и одиночная цифра теряется (замер iter-166).
     */
    @Test
    void theFlashcardCycleIsCoveredWithARetriedGrade() throws IOException {
        String spec = read("e2e/keyboard-quiz.spec.js");
        assertThat(spec)
                .as("флешкарточный тест пропал: Space и оценка снова без покрытия")
                .contains("flashcard: Space reveals, digit grades, next card arrives");
        assertThat(spec)
                .as("тест приземления фокуса после раскрытия пропал (FRD-219)")
                .contains("typed recall: reveal lands focus in the reference answer");
        assertThat(spec)
                .as("двухфазный STUDY-цикл (LEARN → Проверить себя → варианты) снова без покрытия")
                .contains("study: learn phase confirms into the quiz sub-phase");
        assertThat(spec)
                .as("MARATHON-смоук пропал — последний режим снова только на пробах")
                .contains("marathon: starts a positioned session");
        assertThat(spec)
                .as("оценка обязана жаться с повтором — одиночное нажатие в WebKit гонится "
                        + "с подвесом обработчика")
                .containsPattern("poll\\(async \\(\\) => \\{\\s*await page\\.keyboard\\.press\\('2'\\);");
    }
}
