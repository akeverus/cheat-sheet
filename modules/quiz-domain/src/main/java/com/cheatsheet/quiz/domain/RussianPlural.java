package com.cheatsheet.quiz.domain;

/**
 * Русская плюрализация числительных (одна каноничная реализация на весь проект).
 *
 * <p>Правило склонения по последним цифрам числа:</p>
 * <ul>
 *   <li>11–14 (по mod 100) → форма <b>many</b> («11 дней»);</li>
 *   <li>оканчивается на 1 → <b>one</b> («21 день»);</li>
 *   <li>оканчивается на 2–4 → <b>few</b> («22 дня»);</li>
 *   <li>иначе → <b>many</b> («25 дней»).</li>
 * </ul>
 *
 * <p>Раньше плюрализация дублировалась в JS двумя расходящимися реализациями
 * (наивная {@code daysText} путала 21/22/24, корректная {@code pluralRu}) — теперь
 * лейбл считается на сервере и кладётся в DTO, а клиент просто рендерит строку.</p>
 */
public final class RussianPlural {

    private RussianPlural() {
    }

    /**
     * Выбирает форму слова для числа {@code n}.
     *
     * @param n    число (знак игнорируется)
     * @param one  форма для «1 предмет» («день»)
     * @param few  форма для «2 предмета» («дня»)
     * @param many форма для «5 предметов» («дней»)
     * @return подходящая форма слова
     */
    public static String pluralize(long n, String one, String few, String many) {
        long abs = Math.abs(n);
        long mod100 = abs % 100;
        long mod10 = abs % 10;
        if (mod100 >= 11 && mod100 <= 14) {
            return many;
        }
        if (mod10 == 1) {
            return one;
        }
        if (mod10 >= 2 && mod10 <= 4) {
            return few;
        }
        return many;
    }

    /**
     * Готовый лейбл «N день/дня/дней» (число + пробел + просклонённое слово).
     *
     * @param n количество дней
     * @return например «1 день», «3 дня», «11 дней», «21 день»
     */
    public static String days(long n) {
        return n + " " + pluralize(n, "день", "дня", "дней");
    }
}
