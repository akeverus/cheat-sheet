package com.cheatsheet.quiz.common.util;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реестр отображаемых названий тем: slug → человеческое имя.
 *
 * <p>Заполняется импортом вопросов (на каждом старте, вместе с самим банком
 * вопросов) из frontmatter-заголовка markdown-файла темы. Название темы —
 * такая же часть исходника, как её вопросы, поэтому и живёт там же, а не в
 * захардкоженной таблице внутри Java: любая новая тема получает правильное имя
 * без правок кода, а переименование в .md сразу видно в UI.</p>
 *
 * <p>Хранилище in-memory и не персистится: банк вопросов всё равно
 * перевычитывается из файлов при каждом старте, так что отдельная таблица в БД
 * дублировала бы источник правды и умела бы расходиться с ним.</p>
 */
@Component
public class TopicDisplayNames {

    private final Map<String, String> names = new ConcurrentHashMap<>();
    /**
     * Описания тем — из того же frontmatter и по той же причине in-memory: описание
     * такая же часть исходника темы, как её название и её вопросы.
     */
    private final Map<String, String> descriptions = new ConcurrentHashMap<>();

    /**
     * Регистрирует название темы.
     *
     * @param slug        slug темы в форме {@link TopicUtils#slug(String)}
     * @param displayName человеческое название; пустое/{@code null} игнорируется
     */
    public void register(String slug, String displayName) {
        if (slug == null || slug.isBlank() || displayName == null || displayName.isBlank()) {
            return;
        }
        names.put(key(slug), displayName.trim());
    }

    /**
     * @param slug slug темы в форме {@link TopicUtils#slug(String)}
     * @return зарегистрированное название или {@code null}, если тема неизвестна
     */
    public String lookup(String slug) {
        if (slug == null || slug.isBlank()) {
            return null;
        }
        return names.get(key(slug));
    }

    /**
     * Регистрирует описание темы — одну строку о том, что в тему входит.
     *
     * @param slug        slug темы в форме {@link TopicUtils#slug(String)}
     * @param description описание из frontmatter; пустое/{@code null} игнорируется
     */
    public void registerDescription(String slug, String description) {
        if (slug == null || slug.isBlank() || description == null || description.isBlank()) {
            return;
        }
        descriptions.put(key(slug), description.trim());
    }

    /**
     * @param slug slug темы в форме {@link TopicUtils#slug(String)}
     * @return описание темы или {@code null}, если его нет во frontmatter
     */
    public String description(String slug) {
        if (slug == null || slug.isBlank()) {
            return null;
        }
        return descriptions.get(key(slug));
    }

    /** Количество известных тем — для лога импорта. */
    public int size() {
        return names.size();
    }

    /**
     * Ключ реестра: регистр и вид разделителя не должны влиять на поиск —
     * в БД тема хранится как путь файла, а в тестовых данных встречается
     * подчёркивание вместо дефиса.
     */
    private String key(String slug) {
        return slug.trim().toLowerCase(Locale.ROOT).replace('_', '-');
    }
}
