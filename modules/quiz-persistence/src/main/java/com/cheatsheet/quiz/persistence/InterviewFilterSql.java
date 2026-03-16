package com.cheatsheet.quiz.persistence;

import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Утилита для построения SQL WHERE-условий тестирования.
 *
 * <p>Все фильтры добавляются как параметризованные условия
 * для предотвращения SQL-инъекций.</p>
 *
 * <p>Требования к вызывающему коду:</p>
 * <ul>
 *   <li>SQL уже должен содержать WHERE-клаузу (например, {@code WHERE 1=1});</li>
 *   <li>таблица {@code questions} имеет алиас {@code q};</li>
 *   <li>таблица {@code review_state} имеет алиас {@code rs}.</li>
 * </ul>
 */
@UtilityClass
public class InterviewFilterSql {

    /**
     * Добавляет условия фильтрации к SQL-запросу.
     *
     * @param sql       StringBuilder с текущим запросом
     * @param params    список параметров (будут добавлены новые)
     * @param topic     фильтр по теме (null — не фильтровать)
     * @param important только важные вопросы (null/false — не фильтровать)
     * @param onlyWrong только вопросы с ошибками (null/false — не фильтровать)
     */
    public static void appendFilters(StringBuilder sql, List<Object> params,
                                     String topic, Boolean important, Boolean onlyWrong) {
        if (topic != null && !topic.isBlank()) {
            sql.append(" AND q.topic = ? ");
            params.add(topic);
        }
        if (important != null && important) {
            sql.append(" AND q.is_important = 1 ");
        }
        if (onlyWrong != null && onlyWrong) {
            sql.append(" AND rs.wrong_count > 0 ");
        }
    }

    /**
     * Добавляет фильтр по списку тем через IN (...).
     * Если список пустой — добавляет условие, гарантирующее пустой результат.
     */
    public static void appendTopicsFilter(StringBuilder sql, List<Object> params, List<String> topics) {
        if (topics == null || topics.isEmpty()) {
            sql.append(" AND 1 = 0 ");
            return;
        }
        sql.append(" AND q.topic IN (");
        for (int i = 0; i < topics.size(); i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
            params.add(topics.get(i));
        }
        sql.append(") ");
    }
}
