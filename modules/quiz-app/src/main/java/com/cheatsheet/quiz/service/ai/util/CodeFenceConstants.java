package com.cheatsheet.quiz.service.ai.util;

import lombok.experimental.UtilityClass;

/**
 * Константы для markdown code-fence (тройные обратные кавычки).
 *
 * <p>Используются при удалении обёртки {@code ```json ... ```} из ответов модели.</p>
 */
@UtilityClass
public class CodeFenceConstants {

    /** Начало code-fence (три обратные кавычки). */
    public static final String CODE_FENCE = "```";

    /** Regex: начало code-fence с опциональным языком и пробелами после. */
    public static final String REGEX_CODE_FENCE_LEADING = "^```[a-zA-Z]*\\s*";

    /** Regex: конец code-fence с пробелами перед. */
    public static final String REGEX_CODE_FENCE_TRAILING = "\\s*```$";
}
