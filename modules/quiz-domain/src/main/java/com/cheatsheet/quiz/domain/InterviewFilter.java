package com.cheatsheet.quiz.domain;
import lombok.Builder;

/**
 * Фильтр вопросов тестирования.
 *
 * @param topic тема (null — все темы)
 * @param group группа тем (null — все группы)
 * @param importantOnly только вопросы с маркером «важно»
 * @param onlyWrong только вопросы с ошибками
 * @param shuffle перемешать порядок (при true тема игнорируется для выбора)
 * @param ordered использовать учебный порядок тем (по умолчанию true)
 */
@Builder(toBuilder = true)
public record InterviewFilter(
        String topic,
        String group,
        Boolean importantOnly,
        Boolean onlyWrong,
        Boolean shuffle,
        Boolean ordered
) {

    public InterviewFilter {
        topic = normalize(topic);
        group = normalize(group);
        if (ordered == null) {
            ordered = true;
        }
    }

    /**
     * Эффективная тема для запросов к БД: при перемешивании возвращается null (все темы).
     *
     * @return тему или null
     */
    public String effectiveTopic() {
        return Boolean.TRUE.equals(shuffle) ? null : topic;
    }

    /**
     * Эффективная группа для запросов к БД: при перемешивании возвращается null (все темы).
     */
    public String effectiveGroup() {
        return Boolean.TRUE.equals(shuffle) ? null : group;
    }

    /**
     * Признак перемешанного порядка вопросов.
     *
     * @return true если shuffle задан как true
     */
    public boolean isShuffled() {
        return Boolean.TRUE.equals(shuffle);
    }

    /**
     * Признак использования учебного порядка тем.
     */
    public boolean isOrdered() {
        return !Boolean.FALSE.equals(ordered);
    }

    /**
     * Сужает ли фильтр банк вопросов. Порядок ({@code shuffle}, {@code ordered})
     * сюда не входит: он меняет последовательность, а не состав выборки, и
     * подпись «фильтр» рядом с числом обязана отвечать за состав.
     */
    public boolean isNarrowed() {
        return topic != null || group != null
                || Boolean.TRUE.equals(importantOnly) || Boolean.TRUE.equals(onlyWrong);
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.strip();
    }
}
