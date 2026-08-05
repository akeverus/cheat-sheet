package com.cheatsheet.quiz.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Преобразование slug-а темы в человекочитаемое название для UI.
 * Slug в обычном UI не показывается — только {@link #displayName(String)}.
 */
@Component("topicUtils")
@RequiredArgsConstructor
public class TopicUtils {

    private final TopicDisplayNames displayNames;

    /**
     * Сырой slug темы (для data-атрибутов / API), без UI-форматирования.
     */
    public String slug(String topic) {
        if (topic == null || topic.isBlank()) {
            return "";
        }
        String name = topic.trim();
        int lastSlash = name.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < name.length() - 1) {
            name = name.substring(lastSlash + 1);
        }
        return name.replace("-interview", "").replace(".md", "").trim();
    }

    /**
     * Название темы для UI.
     *
     * <p>Берётся из {@link TopicDisplayNames} — реестра, который импорт наполняет
     * заголовками из самих файлов тем. Там правильные написания продуктов
     * («CockroachDB», «ArchUnit», «jOOQ», «Apache Airflow») и русские названия
     * общих тем («Транзакции и уровни изоляции»).</p>
     *
     * <p>Если тема в реестре не найдена (файл темы удалён, а вопросы ещё лежат в
     * БД), показываем slug с заглавной буквы — понятно, что тема осиротела, и
     * при этом UI не падает и не показывает пустоту. Восстанавливать название из
     * slug-а мы не пытаемся: slug — результат необратимого упрощения, и любая
     * такая реконструкция коверкает имена продуктов.</p>
     *
     * @param topic slug темы, например {@code "java/collections-interview.md"}
     * @return отображаемое имя для UI (не slug)
     */
    public String displayName(String topic) {
        String key = slug(topic);
        if (key.isEmpty()) {
            return "";
        }
        String registered = displayNames.lookup(key);
        if (registered != null) {
            return registered;
        }
        String spaced = key.replace('-', ' ').replace('_', ' ').trim();
        if (spaced.isEmpty()) {
            return "";
        }
        return Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }
}
