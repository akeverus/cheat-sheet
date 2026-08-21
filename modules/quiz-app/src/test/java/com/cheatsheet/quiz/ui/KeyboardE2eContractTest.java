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

    @Test
    void theWebkitProjectExistsAndIsScopedToTheKeyboardSpec() throws IOException {
        String config = read("e2e/playwright.config.js");
        assertThat(config)
                .as("WebKit-проект пропал: клавиатура снова проверяется только в Chromium")
                .contains("name: 'webkit'");
        assertThat(config)
                .as("WebKit без testMatch погонит и визуальную матрицу — baselines сняты "
                        + "в Chromium, диффы будут ложными")
                .containsPattern("name: 'webkit'.*testMatch: /keyboard-quiz\\\\.spec\\\\.js/");
        // `.*` без DOTALL не перейдёт строку: проект-объявление обязано быть одной
        // строкой вместе со своим testMatch. Первая редакция с `[^}]*` падала на
        // верном конфиге — обрывалась на `}` внутри use: { ...devices }.
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
}
