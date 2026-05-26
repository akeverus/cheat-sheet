package com.cheatsheet.quiz.infrastructure.health;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Подкладывает в {@code /actuator/info} секцию {@code seeds} со сводкой по
 * bundled JSON-сидерам: общее число тем и разбивка по top-level категории.
 * Читает classpath один раз при вызове info — без обращений к БД.
 *
 * <p>Пример ответа:
 * <pre>
 * {
 *   "seeds": {
 *     "totalTopics": 77,
 *     "byCategory": {"algorithms": 4, "api": 1, "behavioral": 2, ...}
 *   }
 * }
 * </pre>
 */
@Component
public class SeedInfoContributor implements InfoContributor {

    private static final String SEED_GLOB = "classpath*:seed/mcq/**/*.json";
    private static final String SEED_PREFIX = "seed/mcq/";

    @Override
    public void contribute(Info.Builder builder) {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        int total = 0;
        Map<String, Integer> byCategory = new TreeMap<>();
        try {
            Resource[] resources = resolver.getResources(SEED_GLOB);
            for (Resource r : resources) {
                String category = topCategory(r);
                if (category == null) continue;
                total++;
                byCategory.merge(category, 1, Integer::sum);
            }
        } catch (IOException e) {
            builder.withDetail("seeds", Map.of("error", e.getMessage()));
            return;
        }
        Map<String, Object> seedInfo = new LinkedHashMap<>();
        seedInfo.put("totalTopics", total);
        seedInfo.put("byCategory", byCategory);
        builder.withDetail("seeds", seedInfo);
    }

    private static String topCategory(Resource r) {
        try {
            String uri = r.getURI().toString();
            int idx = uri.indexOf(SEED_PREFIX);
            if (idx < 0) return null;
            String relative = uri.substring(idx + SEED_PREFIX.length());
            int slash = relative.indexOf('/');
            return slash > 0 ? relative.substring(0, slash) : null;
        } catch (IOException e) {
            return null;
        }
    }
}
