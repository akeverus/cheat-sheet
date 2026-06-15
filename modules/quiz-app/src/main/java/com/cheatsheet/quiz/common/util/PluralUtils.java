package com.cheatsheet.quiz.common.util;

import org.springframework.stereotype.Component;

/**
 * Русская плюрализация числительных для UI.
 *
 * <p>Раньше шаблоны выбирали форму по абсолютному значению
 * ({@code n == 1 ? "вопрос" : n in 2..4 ? "вопроса" : "вопросов"}), что ломалось
 * для всех чисел ≥21: при {@code due == 21} выводилось «21 вопросов» вместо
 * «вопрос», при 22…24 — «вопросов» вместо «вопроса» (критика round-01 C26).
 */
@Component("plural")
public class PluralUtils {

    /**
     * Выбор формы по правилам русского языка.
     *
     * <ul>
     *   <li>{@code n % 100} в диапазоне 11..14 → {@code many} («11 вопросов»);</li>
     *   <li>{@code n % 10 == 1} → {@code one} («21 вопрос»);</li>
     *   <li>{@code n % 10} в 2..4 → {@code few} («22 вопроса»);</li>
     *   <li>иначе → {@code many} («25 вопросов»).</li>
     * </ul>
     *
     * @param n    число (знак игнорируется)
     * @param one  форма для 1 («вопрос»)
     * @param few  форма для 2–4 («вопроса»)
     * @param many форма для 0, 5–20 и т.д. («вопросов»)
     * @return подходящая форма слова
     */
    public String pick(long n, String one, String few, String many) {
        long mod100 = Math.abs(n) % 100;
        long mod10 = Math.abs(n) % 10;
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
}
