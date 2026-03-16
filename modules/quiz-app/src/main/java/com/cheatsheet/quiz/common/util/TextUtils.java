package com.cheatsheet.quiz.common.util;

import org.apache.commons.lang3.StringUtils;
import lombok.experimental.UtilityClass;

/**
 * Утилиты для обработки текста: очистка markdown-разметки и т.п.
 */
@UtilityClass
public class TextUtils {

    /** Regex: начало code block с опциональным языком и пробелами. */
    private static final String REGEX_CODE_BLOCK_LEADING = "```[a-zA-Z]*\\s*";
    /** Строка: тройные бэктики (code fence). */
    private static final String CODE_FENCE = "```";
    /** Символ бэктика (inline code в markdown). */
    private static final String BACKTICK = "`";
    /** Markdown: жирный (двойная звёздочка). */
    private static final String MARKDOWN_BOLD_DOUBLE = "**";
    /** Markdown: курсив (одиночная звёздочка). */
    private static final String MARKDOWN_ITALIC_STAR = "*";
    /** Markdown: заголовок (двойная решётка). */
    private static final String MARKDOWN_HEADING_DOUBLE = "##";
    /** Markdown: заголовок (решётка). */
    private static final String MARKDOWN_HEADING_HASH = "#";
    /** Regex: inline-код в бэктиках, группа 1 — содержимое. */
    private static final String REGEX_INLINE_CODE = "`([^`]*)`";
    /** Regex: жирный **text**. */
    private static final String REGEX_BOLD = "\\*\\*(.+?)\\*\\*";
    /** Regex: курсив *text*. */
    private static final String REGEX_ITALIC = "\\*(.+?)\\*";
    /** Regex: жирный __text__. */
    private static final String REGEX_BOLD_UNDERSCORE = "__(.+?)__";
    /** Regex: курсив _text_. */
    private static final String REGEX_ITALIC_UNDERSCORE = "_(.+?)_";
    /** Regex: зачёркнутый ~~text~~. */
    private static final String REGEX_STRIKETHROUGH = "~~(.+?)~~";
    /** Regex: заголовки markdown в начале строки (^# до ^######). */
    private static final String REGEX_HEADING_PREFIX = "(?m)^#{1,6}\\s+";
    /** Regex: один и более пробельных символов. */
    private static final String REGEX_WHITESPACE = "\\s+";

    /**
     * Удаляет из текста основные markdown-символы:
     * бэктики ({@code `}), звёздочки ({@code *}), решётки ({@code #}),
     * подчёркивания ({@code _}), тильды ({@code ~}).
     *
     * <p>Убирает блоки кода ({@code ```...```}), inline-код ({@code `...`}),
     * жирный/курсив, заголовки. Результат — plain text.</p>
     *
     * @param text исходный текст с возможной markdown-разметкой
     * @return очищенный plain text
     */
    public static String stripMarkdown(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String result = text;

        // Удаляем блоки кода ```...```
        result = result.replaceAll(REGEX_CODE_BLOCK_LEADING, StringUtils.EMPTY);
        result = result.replace(CODE_FENCE, StringUtils.EMPTY);

        // Удаляем inline-код `...` → содержимое
        result = result.replaceAll(REGEX_INLINE_CODE, "$1");

        // Удаляем оставшиеся бэктики
        result = result.replace(BACKTICK, StringUtils.EMPTY);

        // Удаляем жирный и курсив: **text** → text, *text* → text, __text__ → text, _text_ → text
        result = result.replaceAll(REGEX_BOLD, "$1");
        result = result.replaceAll(REGEX_ITALIC, "$1");
        result = result.replaceAll(REGEX_BOLD_UNDERSCORE, "$1");
        result = result.replaceAll(REGEX_ITALIC_UNDERSCORE, "$1");

        // Удаляем зачёркнутый: ~~text~~ → text
        result = result.replaceAll(REGEX_STRIKETHROUGH, "$1");

        // Удаляем заголовки markdown: ## Заголовок → Заголовок
        result = result.replaceAll(REGEX_HEADING_PREFIX, StringUtils.EMPTY);

        // Удаляем оставшиеся звёздочки и решётки
        result = result.replace(MARKDOWN_BOLD_DOUBLE, StringUtils.EMPTY);
        result = result.replace(MARKDOWN_ITALIC_STAR, StringUtils.EMPTY);
        result = result.replace(MARKDOWN_HEADING_DOUBLE, StringUtils.EMPTY);
        result = result.replace(MARKDOWN_HEADING_HASH, StringUtils.EMPTY);

        // Нормализуем пробелы
        result = result.replaceAll(REGEX_WHITESPACE, StringUtils.SPACE).trim();

        return result;
    }

    /**
     * Обрезает текст по границе слова до заданной длины.
     * Если длина текста не превышает {@code maxLength}, возвращает текст без изменений.
     * Иначе обрезает по последнему пробелу перед {@code maxLength} (или по {@code maxLength},
     * если пробела нет во второй половине диапазона), убирает хвостовые пробелы и добавляет «…».
     *
     * @param text      исходный текст (может быть null — вернётся пустая строка)
     * @param maxLength максимальная длина результата (символов)
     * @return обрезанный текст с «…» в конце при обрезке
     */
    public static String truncateAtWordBoundary(String text, int maxLength) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        int cut = text.lastIndexOf(' ', maxLength);
        if (cut < maxLength / 2) {
            cut = maxLength;
        }
        return text.substring(0, cut).trim() + "…";
    }
}
