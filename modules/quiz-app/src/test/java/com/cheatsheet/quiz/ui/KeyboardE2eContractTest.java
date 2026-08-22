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
                .as("оценка обязана жаться с повтором — одиночное нажатие в WebKit гонится "
                        + "с подвесом обработчика")
                .containsPattern("poll\\(async \\(\\) => \\{\\s*await page\\.keyboard\\.press\\('2'\\);");
    }
}
