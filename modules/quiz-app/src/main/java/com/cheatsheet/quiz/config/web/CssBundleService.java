package com.cheatsheet.quiz.config.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Один CSS-запрос на страницу вместо тридцати трёх.
 *
 * <p>Сборщика в проекте нет по решению, поэтому CSS ехал так, как записан: шапка
 * перечисляла 33 {@code <link rel="stylesheet">}, и каждый из них — отдельный
 * блокирующий запрос. На быстром канале это незаметно, на Slow 4G замер дал
 * FCP 4,4 с: файлы весят суммарно ~130 КБ (0,8–26 КБ поштучно), то есть плату
 * брали не байты, а латентность — HTTP/1.1 держит шесть соединений, и очередь
 * из 33 запросов сериализуется в шесть волн. Первый пиксель ждал последнюю.</p>
 *
 * <p>Здесь файлы склеиваются в один ответ на страницу. Склейка именно постраничная,
 * а не «ядро + страничная добавка»: многие файлы объявляют больше одного
 * {@code @layer} (pages-focus — pages+components, shell — shell+pages,
 * pages-stats — pages+utilities), а внутри слоя выигрывает последний. Разрезав
 * ленту на две, мы поменяли бы взаимный порядок этих вторичных блоков и получили
 * бы другую картинку. Постраничная лента байт в байт повторяет порядок из шапки.</p>
 *
 * <p>Порядок и гейты живут в {@code css-bundle-manifest.txt}: это единственный
 * источник правды, который читают и сборка, и {@code TemplateFragmentContractTest}.
 * Раньше порядок был записан только в head.html.</p>
 *
 * <p>Имя файла несёт хеш содержимого, поэтому ответ отдаётся с годовым
 * {@code immutable}: правка любого CSS меняет URL. Внутри ленты корневые
 * {@code url(/fonts/…)} и {@code url(/img/pixel/…)} прогоняются через
 * {@link ResourceUrlProvider} — сама лента идёт мимо цепочки ресурсов Spring,
 * и без этого в prod потерялся бы content-hash вложенных файлов.</p>
 */
// Без сервлетного слоя нет ни ResourceUrlProvider, ни страниц: в non-web контексте
// бин не создаётся (та же причина, что у StaticVersionAdvice).
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Component("cssBundle")
public class CssBundleService {

    private static final Logger log = LoggerFactory.getLogger(CssBundleService.class);

    /** Манифест лежит в classpath, а не в {@code static/} — отдавать его наружу незачем. */
    public static final String MANIFEST_PATH = "css-bundle-manifest.txt";

    public static final String URL_PREFIX = "/css/bundle/";

    private static final String STATIC_ROOT = "static";

    /** Корневые url() внутри CSS. Ведущий {@code /} отсекает {@code data:} и относительные. */
    private static final Pattern ROOT_URL = Pattern.compile("url\\(\\s*(['\"]?)(/[^)'\"\\s]+)\\1\\s*\\)");

    /** Пустые строки, оставшиеся после вырезания комментариев. */
    private static final Pattern BLANK_LINES = Pattern.compile("\\n[ \\t]*(?=\\n)");

    private final ResourceUrlProvider resourceUrlProvider;
    private final List<Entry> manifest;
    private final Map<String, Bundle> cache = new ConcurrentHashMap<>();

    public CssBundleService(ResourceUrlProvider resourceUrlProvider) {
        this.resourceUrlProvider = resourceUrlProvider;
        this.manifest = readManifest();
    }

    /** Путь для {@code <link>} без контекста приложения: {@code /css/bundle/focus.a1b2c3d4.css}. */
    public String pathFor(String page) {
        return URL_PREFIX + bundle(page).fileName();
    }

    /**
     * Содержимое ленты, если хеш в имени актуален.
     *
     * <p>Устаревший хеш — это запрос из кэша браузера по адресу, которого уже нет:
     * отвечаем 404, а не молча свежим содержимым, иначе годовой {@code immutable}
     * закрепил бы за старым URL новое тело.</p>
     */
    public Optional<String> css(String page, String hash) {
        Bundle bundle = bundle(page);
        return bundle.hash().equals(hash) ? Optional.of(bundle.css()) : Optional.empty();
    }

    /** Файлы страницы в порядке каскада — для тестов, читающих структурный CSS. */
    public List<String> pathsFor(String page) {
        return manifest.stream().filter(entry -> entry.appliesTo(page)).map(Entry::path).toList();
    }

    private Bundle bundle(String page) {
        List<String> paths = pathsFor(page);
        long stamp = lastModified(paths);
        Bundle cached = cache.get(page);
        // Пересборка по метке времени, а не по флагу профиля: в prod метка не меняется
        // вовсе, а в dev правка .css доезжает без рестарта — как и было с 33 <link>.
        if (cached != null && cached.stamp() == stamp) {
            return cached;
        }
        Bundle built = build(page, paths, stamp);
        cache.put(page, built);
        return built;
    }

    private Bundle build(String page, List<String> paths, long stamp) {
        StringBuilder sb = new StringBuilder(200_000);
        for (String path : paths) {
            sb.append(stripComments(rewriteUrls(read(path)))).append('\n');
        }
        String css = BLANK_LINES.matcher(sb).replaceAll("\n");
        String hash = shortHash(css);
        log.info("CSS-лента {}: {} файлов, {} КБ, хеш {}", page, paths.size(), css.length() / 1024, hash);
        return new Bundle(page, hash, css, stamp);
    }

    /**
     * Убирает комментарии из ленты.
     *
     * <p>Не косметика: CSS проекта на 52% состоит из прозы, объясняющей правила
     * (порядок слоёв, история удалённых селекторов, замеры). В исходнике она и
     * должна быть, но браузеру достаётся зря — после gzip лента страницы падает
     * с 94 до 26 КБ, то есть комментарии составляли три четверти трафика.</p>
     *
     * <p>Разбор посимвольный, а не регуляркой: {@code /*} внутри строкового
     * литерала ({@code content: "…"}) комментарий не открывает, и наивная
     * {@code /\*.*?\*}{@code /} однажды съест кусок правила.</p>
     */
    static String stripComments(String css) {
        StringBuilder out = new StringBuilder(css.length());
        char quote = 0;
        for (int i = 0; i < css.length(); i++) {
            char c = css.charAt(i);
            if (quote != 0) {
                out.append(c);
                if (c == '\\' && i + 1 < css.length()) {
                    out.append(css.charAt(++i));
                } else if (c == quote) {
                    quote = 0;
                }
                continue;
            }
            if (c == '"' || c == '\'') {
                quote = c;
                out.append(c);
                continue;
            }
            if (c == '/' && i + 1 < css.length() && css.charAt(i + 1) == '*') {
                int end = css.indexOf("*/", i + 2);
                // Незакрытый комментарий: браузер съел бы остаток файла — делаем то же.
                if (end < 0) {
                    break;
                }
                i = end + 1;
                continue;
            }
            out.append(c);
        }
        return out.toString();
    }

    private String rewriteUrls(String css) {
        Matcher matcher = ROOT_URL.matcher(css);
        StringBuilder out = new StringBuilder(css.length());
        while (matcher.find()) {
            String lookupPath = matcher.group(2);
            String versioned = resourceUrlProvider.getForLookupPath(lookupPath);
            String replacement = "url(\"" + (versioned != null ? versioned : lookupPath) + "\")";
            matcher.appendReplacement(out, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(out);
        return out.toString();
    }

    private String read(String path) {
        Resource resource = new ClassPathResource(STATIC_ROOT + path);
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            // Файл перечислен в манифесте, но его нет: страница без него сломана визуально,
            // тихо отдавать половину каскада хуже, чем упасть на старте первого рендера.
            throw new UncheckedIOException("Манифест ссылается на отсутствующий CSS: " + path, e);
        }
    }

    private long lastModified(List<String> paths) {
        long max = 0;
        for (String path : paths) {
            try {
                max = Math.max(max, new ClassPathResource(STATIC_ROOT + path).lastModified());
            } catch (IOException e) {
                // Ресурс без файловой метки (jar-запись некоторых сборок) не должен
                // ломать сборку ленты — в prod она всё равно неизменна.
                log.debug("Нет метки времени у {}", path);
            }
        }
        return max;
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

    private static String shortHash(String css) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(css.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest, 0, 4);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 обязателен в любой JVM", e);
        }
    }

    /** Пустой {@code pages} означает «на всех страницах». */
    private record Entry(Set<String> pages, String path) {
        boolean appliesTo(String page) {
            return pages.isEmpty() || (page != null && pages.contains(page));
        }
    }

    private record Bundle(String page, String hash, String css, long stamp) {
        String fileName() {
            return page + "." + hash + ".css";
        }
    }
}
