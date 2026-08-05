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
 *   <li>{@code review_state} подключена через {@link #appendReviewStateJoin} с
 *       алиасом {@code rs} — и может не иметь строки.</li>
 * </ul>
 */
@UtilityClass
public class InterviewFilterSql {

    /**
     * Подключает состояние повторения к выборке вопросов — <b>LEFT</b> JOIN,
     * потому что состояние это прогресс, а прогресса у нового вопроса нет.
     *
     * <p>Раньше здесь стоял INNER JOIN, и строка в {@code review_state} была
     * обязательным условием того, что вопрос вообще попадёт в выборку. Строку
     * создавал импорт при вставке нового вопроса — то есть ровно один раз за
     * жизнь вопроса. Любая потеря таблицы прогресса (чистка, восстановление
     * дампа без неё, прогон QA-фикстур, которые её усекают) делала весь банк
     * невидимым: девять тысяч вопросов лежат в БД, а тренажёр молча показывает
     * те несколько, у которых состояние уцелело. Ошибки при этом нет ни одной —
     * запрос отрабатывает штатно и возвращает пустоту.
     *
     * <p>Скоуп владельца обязан жить в {@code ON}, а не в {@code WHERE}: условие
     * {@code rs.owner_key = ?} в {@code WHERE} отбрасывает строки с {@code NULL}
     * и молча превращает LEFT JOIN обратно в INNER — тот самый дефект, только
     * незаметнее.
     */
    public static void appendReviewStateJoin(StringBuilder sql, List<Object> params) {
        sql.append(" LEFT JOIN review_state rs ON rs.question_id = q.id AND rs.owner_key = ? ");
        params.add(OwnerContext.current());
    }

    /**
     * Предикат «вопрос готов к показу»: просроченное повторение либо вопрос, на
     * который ещё не отвечали. Требует одного параметра — «сейчас» в epoch-секундах.
     *
     * <p>Существует как константа, чтобы выборка и счётчик не разъезжались. Пока
     * запрос выборки понимал «готов» как «строка есть и срок прошёл», а карточка на
     * главной считала то же самое своим SQL, любое расхождение выражалось не
     * ошибкой, а неверным числом: «3 из 6 к повтору» при девяти тысячах вопросов
     * в банке.
     */
    public static final String READY_FOR_REVIEW = "(rs.next_review_at IS NULL OR rs.next_review_at <= ?)";

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
            // COALESCE, а не голое сравнение: у вопроса без прогресса ошибок
            // нет, и NULL > 0 дал бы UNKNOWN — строка выпала бы из выборки
            // «только с ошибками» и без COALESCE, но по случайной причине.
            sql.append(" AND COALESCE(rs.wrong_count, 0) > 0 ");
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
