package com.cheatsheet.quiz.feature.interview.service.copy;

import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.domain.AnswerResult;

import java.util.List;

/**
 * Текст разбора ответа: вердикт, срок следующего повторения, буквы вариантов.
 *
 * <p>Разбор рисуют два независимых рендерера — SSR-шаблон {@code result.html}
 * (путь без JS) и {@code quiz/interview.js} (AJAX). Пока каждый собирал
 * формулировки сам, у одного и того же события было две редакции: «через 1 дн.»
 * против «завтра», «Неверно — правильный ответ C» против «Ты выбрал A.
 * Правильный ответ C». Формулировка — это продукт, а не деталь разметки, и
 * автор у неё должен быть один. Здесь он и живёт: MVC кладёт готовые строки в
 * модель, API отдаёт их в JSON, оба клиента только печатают.</p>
 */
public final class AnswerCopy {

    private AnswerCopy() {
    }

    /**
     * Строка вердикта. При ошибке сначала напоминаем выбранную букву, потом
     * называем правильную: вердикт — единственное место, где эти две буквы стоят
     * рядом, и именно по ним читается разбор вариантов ниже.
     */
    public static String verdict(boolean correct, String selectedLetter, String correctLetter) {
        if (correct) {
            return "Верно";
        }
        String prefix = isBlank(selectedLetter) ? "" : "Ты выбрал " + selectedLetter + ". ";
        return isBlank(correctLetter) ? prefix + "Неверно" : prefix + "Правильный ответ " + correctLetter;
    }

    /** {@link #verdict(boolean, String, String)} для доменного результата. */
    public static String verdict(AnswerResult result) {
        return verdict(result.correctAnswer(),
                optionLetter(result, result.selected()),
                optionLetter(result, result.correct()));
    }

    /**
     * Когда карточка вернётся. «Завтра» вместо «через 1 день»: у одного и того же
     * интервала не может быть двух формулировок.
     */
    public static String nextReview(int intervalDays) {
        if (intervalDays <= 0) {
            return "—";
        }
        if (intervalDays == 1) {
            return "завтра";
        }
        return "через " + intervalDays + " " + pluralDays(intervalDays);
    }

    /**
     * Буква варианта (A/B/C/…) = 1-based позиция в списке вариантов, т.е. тот же
     * порядок, в котором фронт нумерует опции CSS-счётчиком. Пустая строка —
     * если вариант не найден в списке (не должно случаться).
     */
    public static String optionLetter(AnswerResult result, AnswerOption option) {
        if (option == null) {
            return "";
        }
        return optionLetter(result, option.id());
    }

    /** {@link #optionLetter(AnswerResult, AnswerOption)} по идентификатору варианта. */
    public static String optionLetter(AnswerResult result, long optionId) {
        List<AnswerOption> options = result.options();
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).id() == optionId) {
                return String.valueOf((char) ('A' + i));
            }
        }
        return "";
    }

    /** Русское склонение слова «день» по числу (1 день / 2 дня / 5 дней). */
    private static String pluralDays(int days) {
        int tail = days % 100;
        if (tail >= 11 && tail <= 14) {
            return "дней";
        }
        return switch (days % 10) {
            case 1 -> "день";
            case 2, 3, 4 -> "дня";
            default -> "дней";
        };
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
