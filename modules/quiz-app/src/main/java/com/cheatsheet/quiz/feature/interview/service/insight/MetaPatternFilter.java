package com.cheatsheet.quiz.feature.interview.service.insight;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Фильтр мета-комментариев в AI-генерированном тексте.
 *
 * <p>AI иногда генерирует фразы про «интервью» или «практическую ценность»
 * вместо технических выводов. Такие тексты считаются невалидными и отфильтровываются.</p>
 */
@UtilityClass
public class MetaPatternFilter {

    private static final List<Pattern> META_PATTERNS = List.of(
            Pattern.compile("[Нн]а интервью.{0,20}(ожидают|обычно)"),
            Pattern.compile("[Пп]рактическ(ая|ий) (ценность|акцент).*обычно")
    );

    /**
     * Проверяет, содержит ли текст мета-паттерн (фразы про интервью и т.п.).
     *
     * @param text текст для проверки
     * @return true если текст содержит мета-паттерн
     */
    public static boolean containsMetaPattern(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return META_PATTERNS.stream().anyMatch(p -> p.matcher(text).find());
    }
}
