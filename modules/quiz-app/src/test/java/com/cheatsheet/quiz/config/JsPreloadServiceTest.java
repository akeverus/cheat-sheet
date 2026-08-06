package com.cheatsheet.quiz.config;

import com.cheatsheet.quiz.config.web.JsPreloadService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Список модулей для {@code <link rel="modulepreload">} собирается разбором
 * исходников, а разбор ошибается молча: путь, которого нет, виден только на
 * рендере страницы — 500 вместо экрана вопроса.
 *
 * <p>Так и вышло на первом запуске: свободный regex принял за импорт строку
 * {@code export function initContentTabs(root = document.querySelector('.question-materials'))}
 * и потребовал модуль {@code /js/content/.question-materials}. Отсюда главная
 * проверка ниже — каждый путь обязан существовать.</p>
 */
class JsPreloadServiceTest {

    private final JsPreloadService service = new JsPreloadService();

    @ParameterizedTest
    @ValueSource(strings = {"focus", "result", "settings", "stats", "bookmarks", "summary", "error", "login", ""})
    void everyPreloadedModuleExistsAndIsListedOnce(String page) {
        List<String> modules = service.pathsFor(page);

        assertThat(modules).as("страница без модулей не бывает: app-entry.js нужен всем").isNotEmpty();
        assertThat(modules).doesNotHaveDuplicates();
        for (String path : modules) {
            assertThat(path).startsWith("/js/").endsWith(".js");
            assertThat(new ClassPathResource("static" + path).exists())
                    .as("preload ссылается на несуществующий модуль %s (страница %s)", path, page)
                    .isTrue();
        }
    }

    /**
     * Обход обязан доставать зависимости, а не только точки входа: {@code ui/tablist.js}
     * не упомянут в манифесте нигде и попадает в список лишь потому, что его
     * импортирует {@code content/materials-tabs.js}. Ровно эта транзитивность и
     * избавляет от ведения графа руками.
     */
    @Test
    void transitiveDependenciesOfEntryPointsAreIncluded() {
        assertThat(service.pathsFor("focus"))
                .contains("/js/app-entry.js", "/js/content/materials-tabs.js", "/js/ui/tablist.js");
    }

    /**
     * Гейт постраничный не ради экономии байтов, а потому что preload
     * неиспользованного модуля Chrome пишет предупреждением в консоль.
     */
    @Test
    void pagesDoNotPreloadForeignClusters() {
        assertThat(service.pathsFor("summary"))
                .doesNotContain("/js/settings/session-form.js", "/js/stats/index.js", "/js/quiz/flashcard.js");
        assertThat(service.pathsFor("stats"))
                .contains("/js/stats/index.js")
                .doesNotContain("/js/quiz/interview.js");
    }
}
