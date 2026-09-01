package com.cheatsheet.quiz.ui;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Схема вставляется в документ, не нарушая политику безопасности.
 *
 * <h2>Найденный дефект, а не гипотеза</h2>
 * Замер консоли на вопросе со схемой: 114 нарушений CSP в Chromium и 24 в WebKit
 * («Applying inline style violates…»). Это был не только шум — объявления
 * ОТБРАСЫВАЛИСЬ. A/B-прогон одной и той же схемы:
 *
 * <pre>
 *   с политикой      550×604   маркеры стрелок без stroke-width
 *   с bypassCSP      538×590   маркеры со своими 1px и 2px
 * </pre>
 *
 * Политика приложения строгая — {@code style-src-attr 'none'} — и разбор атрибута
 * {@code style} в разметке запрещён. Mermaid отдаёт SVG строкой, где такие атрибуты
 * есть, и вставка этой строки была разбором.
 *
 * <h2>Что закреплено</h2>
 * <ol>
 *   <li>атрибут переименовывается ДО разбора — парсеру нечего блокировать;</li>
 *   <li>объявления возвращаются через CSSOM, который под директиву не подпадает;</li>
 *   <li>наши размеры применяются ПОСЛЕ переноса, иначе он их затрёт;</li>
 *   <li>{@code setAttribute('style')} в рендерере не появляется снова.</li>
 * </ol>
 *
 * <h2>Что осталось и почему это не чинится здесь</h2>
 * После правки в Chromium остаётся 69 нарушений, в WebKit — 24, и все они приходят
 * из двух точек внутри {@code mermaid.min.js}: библиотека строит и измеряет схему в
 * собственном временном DOM ещё до того, как отдаёт строку. Переименовать там нечего
 * — код чужой и минифицированный. Ослаблять политику ради тишины в консоли мы не
 * стали: {@code style-src-attr 'none'} — реальное свойство приложения, а видимый
 * дефект (потерянные объявления) закрыт.
 *
 * <h2>Отозванная гипотеза</h2>
 * Пробовали {@code flowchart: { htmlLabels: false }} — предполагалось, что нарушения
 * идут от HTML-подписей в {@code foreignObject}. Замер: 69 → 76, то есть стало хуже.
 * Настройка откачена, гипотеза отозвана.
 */
class DiagramCspContractTest {

    private String read(String classpathLocation) throws IOException {
        return new String(new ClassPathResource(classpathLocation).getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);
    }

    @Test
    void theSvgStringIsRenamedBeforeItIsParsed() throws IOException {
        String js = read("static/js/content/mermaid-renderer.js");
        // Смотрим ВНУТРИ функции вставки, а не по всему файлу: рядом живёт второй
        // разбор (`stripScriptsFromSvg`, image/svg+xml), он ничего в документ не
        // вставляет, и первая редакция гейта поймала именно его — сравнивала
        // позицию переименования с чужим DOMParser и падала на верном коде.
        int fn = js.indexOf("function applySvgToElement");
        assertThat(fn).as("функция вставки обязана существовать").isNotNegative();
        String insertion = js.substring(fn);

        int rename = insertion.indexOf("replace(/\\sstyle=\"/g");
        int parse = insertion.indexOf("new DOMParser().parseFromString");
        assertThat(rename).as("переименование обязано быть в функции вставки").isNotNegative();
        assertThat(parse).isNotNegative();
        assertThat(rename)
                .as("переименовать ПОСЛЕ разбора бессмысленно: нарушение уже произошло")
                .isLessThan(parse);
    }

    @Test
    void theDeclarationsComeBackThroughCssom() throws IOException {
        // Эволюция пина (2026-09): раньше гейт требовал `node.style.cssText = …`.
        // Замер изоляционной пробой показал, что в Chromium 139+ присваивание
        // cssText САМО репортится как применение inline-стиля (style-src-attr),
        // поэтому декларации возвращаются пер-свойственно через setProperty —
        // единственный маршрут CSSOM, который политика не считает атрибутом.
        String js = read("static/js/content/mermaid-renderer.js");
        assertThat(js)
                .as("setProperty — единственный CSSOM-маршрут, который не репортится")
                .contains("node.style.setProperty(name, value, priority)");
        assertThat(js)
                .as("cssText в Chromium 139+ репортится style-src-attr — не возвращать")
                .doesNotContain(".style.cssText =");
        assertThat(js)
                .as("setAttribute('style') вернул бы ровно то нарушение, ради которого всё сделано")
                .doesNotContain("setAttribute('style'");
    }

    @Test
    void ourOwnSizingWinsOverTheCarriedStyles() throws IOException {
        String js = read("static/js/content/mermaid-renderer.js");
        int restore = js.indexOf("restoreCarriedStyles(imported)");
        int width = js.indexOf("imported.style.width = '100%'");
        assertThat(restore).isNotNegative();
        assertThat(width).isNotNegative();
        assertThat(restore)
                .as("перенос стилей mermaid обязан идти ДО наших размеров, иначе он их затрёт")
                .isLessThan(width);
    }

    @Test
    void thePolicyStaysStrict() throws IOException {
        String securityConfig = read("static/js/content/mermaid-renderer.js");
        assertThat(securityConfig)
                .as("securityLevel mermaid не ослабляем заодно с CSP")
                .contains("securityLevel: 'strict'");
        assertThat(securityConfig)
                .as("htmlLabels:false замером отвергнут (69 → 76) и не должен вернуться молча")
                .doesNotContain("htmlLabels");
    }

    /**
     * Инлайновые стили ВРЕМЕННОЙ фазы рендера идут через CSSOM, и патчи снимаются.
     *
     * <p>Замер (iter-159): в итоговом DOM отброшенных объявлений нет, но mermaid давал
     * 125 CSP-нарушений на вопрос из временного контейнера — консоль тонула в шуме.
     * Маршрутизация `setAttribute`/`setAttributeNS`/`innerHTML`/`DOMParser` через
     * CSSOM в окне рендера сняла 45 из них (125 → 80; остаток — парсинг в инертном
     * документе, недостижимый из главного realm, задокументирован в реестре).
     * Патчи обязаны СНИМАТЬСЯ в finally: глобальный обход ослабил бы политику
     * `style-src-attr 'none'` для всего приложения.</p>
     */
    @Test
    void inlineStylesOfTheRenderWindowAreRoutedThroughCssomAndRestored() throws IOException {
        String js = read("static/js/content/mermaid-renderer.js");
        assertThat(js)
                .as("окно маршрутизации пропало: mermaid снова даёт 125 CSP-нарушений на "
                        + "вопрос со схемой")
                .contains("async function withInlineStylesRouted(");
        assertThat(js)
                .as("рендер больше не завёрнут в окно маршрутизации")
                .contains("withInlineStylesRouted(() => mermaid.render(");
        boolean restored = false;
        java.util.regex.Matcher block = java.util.regex.Pattern.compile(
                "finally \\{([\\s\\S]{0,400}?)\\}").matcher(js);
        while (block.find()) {
            String body = block.group(1);
            if (body.contains("proto.setAttribute = setAttr")
                    && body.contains("proto.setAttributeNS = setAttrNS")
                    && body.contains("Object.defineProperty(proto, 'innerHTML', innerDesc)")
                    && body.contains("DOMParser.prototype.parseFromString = parseFrom")) {
                restored = true;
            }
        }
        assertThat(restored)
                .as("патчи окна не снимаются в finally: обход политики становится "
                        + "ГЛОБАЛЬНЫМ, и style-src-attr перестаёт защищать остальное приложение")
                .isTrue();
    }
}
