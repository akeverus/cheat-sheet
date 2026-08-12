package com.cheatsheet.quiz.config.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Версия для статики, которую не видит шаблонизатор.
 *
 * <p>Content-hash умеет переписывать только URL, прошедшие через Thymeleaf
 * {@code @{...}}. Мимо него проходят относительные {@code import '../core/dom.js'}
 * внутри ES-модулей: из 39 модулей шаблон видит три точки входа, остальные 36
 * браузер разрешает сам.</p>
 *
 * <p>Вторым таким классом были пути к спрайтам, зашитые строками в декоративном
 * слое {@code pixel-fx.js}; слой снят целиком, и вместе с ним ушла плоская карта
 * спрайтов — версионировать через неё стало нечего.</p>
 *
 * <p>Сборщика в проекте нет по решению (см. {@code docs/frontend-architecture.md}),
 * поэтому переписать эти строки на сборке некому. Пока они безверсионные, годовой
 * {@code max-age} означал бы, что правка любого из них не доедет до вернувшегося
 * браузера никогда — из-за этого кэш статики и был зажат часом.</p>
 *
 * <p>Здесь разрыв закрывается картой «канонический путь → версионированный»:
 * import map для модулей — браузер сверяет с ней уже РАЗРЕШЁННЫЙ URL, поэтому
 * относительные импорты работают без правки самих модулей. Карта живёт в HTML, а
 * HTML отдаётся с {@code no-store}: смена любого файла доезжает со следующей
 * навигацией.</p>
 *
 * <p>Обратный путь — переписывать текст .js на лету через {@code ResourceTransformer} —
 * тупиковый: Spring считает content-версию ДО трансформации, поэтому правка ребёнка
 * не меняла бы хеш родителя, и родитель остался бы в кэше со старым импортом.</p>
 *
 * <p>Когда версионирования нет (dev без content-strategy), карта пуста, атрибут
 * равен {@code null} и тег в разметку не попадает вовсе.</p>
 */
// Без сервлетного слоя нет ни ResourceUrlProvider, ни страниц, которым нужна карта:
// в non-web контексте (интеграционные тесты persistence) бин просто не создаётся.
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ControllerAdvice
public class StaticVersionAdvice {

    private static final Logger log = LoggerFactory.getLogger(StaticVersionAdvice.class);

    private static final String STATIC_ROOT = "classpath:/static";
    private static final String JS_ROOT = "/js";

    private final ResourceUrlProvider resourceUrlProvider;
    private final ObjectMapper objectMapper;

    /** Ключ — {@code contextPath + "\0" + urlRoot}: пути в картах абсолютные. */
    private final Map<String, String> renderedJson = new ConcurrentHashMap<>();

    /** Ключ — urlRoot; значение — «канонический путь → версионированный». */
    private volatile Map<String, Map<String, String>> versionedPaths;

    public StaticVersionAdvice(ResourceUrlProvider resourceUrlProvider, ObjectMapper objectMapper) {
        this.resourceUrlProvider = resourceUrlProvider;
        this.objectMapper = objectMapper;
    }

    /** JSON для {@code <script type="importmap">}: {@code {"imports":{...}}}. */
    @ModelAttribute("jsImportMap")
    public String jsImportMap(HttpServletRequest request) {
        return render(request.getContextPath(), JS_ROOT, true);
    }

    private String render(String contextPath, String urlRoot, boolean wrapAsImports) {
        Map<String, String> paths = resolveAll().getOrDefault(urlRoot, Map.of());
        if (paths.isEmpty()) {
            return null;
        }
        return renderedJson.computeIfAbsent(contextPath + "\0" + urlRoot,
                key -> toJson(contextPath, paths, wrapAsImports));
    }

    /**
     * Считает соответствия один раз.
     *
     * <p>Ленивая инициализация, а не {@code @PostConstruct}: {@link ResourceUrlProvider}
     * опирается на handler mappings, и на первом запросе они уже собраны, тогда как
     * на старте — не гарантированно.</p>
     */
    private Map<String, Map<String, String>> resolveAll() {
        Map<String, Map<String, String>> cached = versionedPaths;
        if (cached != null) {
            return cached;
        }
        synchronized (this) {
            if (versionedPaths == null) {
                versionedPaths = Map.of(JS_ROOT, scan(JS_ROOT, "**/*.js"));
            }
            return versionedPaths;
        }
    }

    private Map<String, String> scan(String urlRoot, String pattern) {
        // TreeMap — детерминированный порядок: одинаковый HTML при одинаковой статике.
        Map<String, String> result = new TreeMap<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            String rootUri = resolver.getResource(STATIC_ROOT + urlRoot + "/").getURI().toString();
            for (Resource resource : resolver.getResources(STATIC_ROOT + urlRoot + "/" + pattern)) {
                String uri = resource.getURI().toString();
                if (!uri.startsWith(rootUri)) {
                    continue;
                }
                String lookupPath = urlRoot + "/" + uri.substring(rootUri.length());
                String versioned = resourceUrlProvider.getForLookupPath(lookupPath);
                // Совпадение означает «версионирования нет» — такой записи в карте не место.
                if (versioned != null && !versioned.equals(lookupPath)) {
                    result.put(lookupPath, versioned);
                }
            }
        } catch (IOException e) {
            // Карта — оптимизация кэширования, а не условие работы страницы: без неё
            // модули и спрайты грузятся по каноническим URL и всё работает.
            log.warn("Не удалось построить карту версий для {}; отдаём без неё", urlRoot, e);
            return Map.of();
        }
        log.info("Карта версий {}: {} файлов", urlRoot, result.size());
        return result;
    }

    private String toJson(String contextPath, Map<String, String> paths, boolean wrapAsImports) {
        Map<String, String> absolute = new LinkedHashMap<>();
        paths.forEach((lookupPath, versioned) ->
                absolute.put(contextPath + lookupPath, contextPath + versioned));
        try {
            return objectMapper.writeValueAsString(wrapAsImports ? Map.of("imports", absolute) : absolute);
        } catch (JsonProcessingException e) {
            // Ключи и значения — ASCII-пути; если сериализация всё же упала, страница важнее карты.
            log.warn("Не удалось сериализовать карту версий", e);
            return null;
        }
    }
}
