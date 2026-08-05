package com.cheatsheet.quiz.config.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Модули страницы — одной волной вместо шести.
 *
 * <p>ES-модули открываются браузеру по цепочке: о существовании
 * {@code core/api-client.js} он узнаёт, только распарсив {@code quiz/streak.js},
 * а о нём — распарсив {@code app-entry.js}. Замер на Slow 4G дал критическую
 * цепочку в 5,2 с при 181 КБ кода: документ → app-entry → streak → api-client →
 * materials-tabs → tablist. Шесть последовательных RTT примерно по 590 мс —
 * платили снова не байты, а глубину графа.</p>
 *
 * <p>{@code <link rel="modulepreload">} снимает именно это: браузер узнаёт обо
 * всех модулях страницы из первого же ответа и качает их параллельно, а к моменту
 * исполнения {@code app-entry.js} они уже в кэше модулей. Сборщик решил бы задачу
 * склейкой, но его в проекте нет по решению (см. {@code docs/frontend-architecture.md}).</p>
 *
 * <p>Список считается, а не ведётся руками: {@code js-preload-manifest.txt}
 * перечисляет только точки входа с постраничными гейтами, остальное берётся
 * транзитивным обходом статических {@code import} самих файлов. Поэтому новый
 * модуль в существующем кластере попадает в preload сам, без правки манифеста.</p>
 *
 * <p>Гейт постраничный, а не «всё сразу»: preload неиспользованного модуля Chrome
 * пишет предупреждением в консоль, и разница вещественна — итогам сессии нужны
 * 19 модулей из 39, настройкам 23.</p>
 */
// Без сервлетного слоя нет и страниц, которым нужен preload: в non-web контексте
// бин не создаётся (та же причина, что у StaticVersionAdvice).
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Component("jsPreload")
public class JsPreloadService {

    private static final Logger log = LoggerFactory.getLogger(JsPreloadService.class);

    /** Манифест лежит в classpath, а не в {@code static/} — отдавать его наружу незачем. */
    public static final String MANIFEST_PATH = "js-preload-manifest.txt";

    private static final String STATIC_ROOT = "static";

    /**
     * Статический импорт с относительным путём.
     *
     * <p>{@code \s+} после ключевого слова отсекает динамический
     * {@code import('./x.js')}: там следом идёт скобка. Динамические импорты
     * обрабатываются не здесь: каждый из них обязан быть строкой манифеста,
     * иначе модуль снова уедет отдельной волной.</p>
     *
     * <p>Дальше — две страховки от ложного совпадения, и обе поставлены не из
     * осторожности. Свободная версия без них считала импортом строку
     * {@code export function initContentTabs(root = document.querySelector('.question-materials'))}
     * и роняла рендер на несуществующем модуле {@code /js/content/.question-materials}.
     * Поэтому: либо между ключевым словом и строкой стоит {@code from}, либо
     * строка идёт сразу за ним (side-effect-импорт), и в обоих случаях на ней
     * инструкция кончается — {@code $} после необязательной точки с запятой.</p>
     */
    private static final Pattern STATIC_IMPORT = Pattern.compile(
            "(?m)^\\s*(?:import|export)\\s+(?:[^;'\"]*?\\sfrom\\s*)?['\"](\\.[^'\"]+)['\"]\\s*;?\\s*$");

    private final List<Entry> manifest;
    private final Map<String, Preload> cache = new ConcurrentHashMap<>();

    public JsPreloadService() {
        this.manifest = readManifest();
    }

    /**
     * Модули страницы в порядке обхода: сначала точки входа из манифеста, затем их
     * зависимости.
     *
     * <p>Пути канонические. Версию накладывает {@code @{...}} в шаблоне — тем же
     * механизмом, что и всей остальной статике, и потому байт в байт совпадает с
     * тем, что браузер получит из import map. Версионировать здесь было бы хуже:
     * {@code @{...}} прогнал бы уже готовый URL через цепочку ресурсов повторно.</p>
     */
    public List<String> pathsFor(String page) {
        String key = page == null ? "" : page;
        Preload cached = cache.get(key);
        // Пересчёт по метке уже известных модулей: правка внутри любого из них может
        // добавить import, и без инвалидации новый модуль не попал бы в preload до
        // рестарта. Сломанным это не выглядит — он всё равно доедет, просто отдельной
        // волной, то есть ровно тем, что здесь и лечится.
        if (cached != null && cached.stamp() == lastModified(cached.modules())) {
            return cached.modules();
        }
        List<String> modules = closure(entryPointsFor(key));
        cache.put(key, new Preload(modules, lastModified(modules)));
        log.info("Preload модулей {}: {} шт.", key.isEmpty() ? "(без страницы)" : key, modules.size());
        return modules;
    }

    private long lastModified(List<String> modules) {
        long max = 0;
        for (String path : modules) {
            try {
                max = Math.max(max, new ClassPathResource(STATIC_ROOT + path).lastModified());
            } catch (IOException e) {
                // Ресурс без файловой метки (jar-запись некоторых сборок) не должен
                // ломать страницу — в prod список всё равно неизменен.
                log.debug("Нет метки времени у {}", path);
            }
        }
        return max;
    }

    private List<String> entryPointsFor(String page) {
        return manifest.stream().filter(entry -> entry.appliesTo(page)).map(Entry::path).toList();
    }

    /** Обход в ширину от точек входа по статическим импортам. */
    private List<String> closure(List<String> entryPoints) {
        Set<String> seen = new LinkedHashSet<>();
        Deque<String> queue = new ArrayDeque<>(entryPoints);
        while (!queue.isEmpty()) {
            String path = queue.poll();
            if (!seen.add(path)) {
                continue;
            }
            queue.addAll(importsOf(path));
        }
        return List.copyOf(seen);
    }

    private List<String> importsOf(String path) {
        String source = read(path);
        String dir = path.substring(0, path.lastIndexOf('/') + 1);
        List<String> result = new ArrayList<>();
        Matcher matcher = STATIC_IMPORT.matcher(source);
        while (matcher.find()) {
            // URI.normalize сворачивает ../ — сам по себе относительный путь ключом карты быть не может.
            result.add(URI.create(dir + matcher.group(1)).normalize().getPath());
        }
        return result;
    }

    private String read(String path) {
        try {
            return new ClassPathResource(STATIC_ROOT + path).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Импорт ведёт в никуда: браузер получил бы ту же ошибку, но уже в рантайме
            // и в виде молча неработающей страницы.
            throw new UncheckedIOException("Импорт ссылается на отсутствующий модуль: " + path, e);
        }
    }

    private List<Entry> readManifest() {
        String text;
        try {
            text = new ClassPathResource(MANIFEST_PATH).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Не найден " + MANIFEST_PATH, e);
        }
        List<Entry> entries = new ArrayList<>();
        for (String raw : text.split("\n")) {
            String line = raw.strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split("\\s+", 2);
            Set<String> pages = "*".equals(parts[0]) ? Set.of() : Set.of(parts[0].split(","));
            entries.add(new Entry(pages, parts[1]));
        }
        return List.copyOf(entries);
    }

    /** Пустой {@code pages} означает «на всех страницах». */
    private record Entry(Set<String> pages, String path) {
        boolean appliesTo(String page) {
            return pages.isEmpty() || pages.contains(page);
        }
    }

    private record Preload(List<String> modules, long stamp) {
    }
}
