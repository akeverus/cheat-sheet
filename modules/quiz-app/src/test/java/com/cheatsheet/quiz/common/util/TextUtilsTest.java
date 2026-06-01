package com.cheatsheet.quiz.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class TextUtilsTest {

    @ParameterizedTest
    @NullAndEmptySource
    void stripMarkdownReturnsInputForNullOrEmpty(String input) {
        assertThat(TextUtils.stripMarkdown(input)).isEqualTo(input);
    }

    @Test
    void stripMarkdownRemovesInlineCode() {
        String result = TextUtils.stripMarkdown("Используй `HashMap` для O(1) lookup");
        assertThat(result).isEqualTo("Используй HashMap для O(1) lookup");
    }

    @Test
    void stripMarkdownRemovesCodeFences() {
        String result = TextUtils.stripMarkdown("```java\npublic class A {}\n```");
        assertThat(result).contains("public class A {}");
        assertThat(result).doesNotContain("```");
    }

    @Test
    void stripMarkdownRemovesBoldAndItalic() {
        String result = TextUtils.stripMarkdown("**жирный** и *курсив* и __полу__ и _жир_");
        assertThat(result).isEqualTo("жирный и курсив и полу и жир");
    }

    @Test
    void stripMarkdownRemovesStrikethrough() {
        assertThat(TextUtils.stripMarkdown("это ~~устарело~~ сейчас"))
                .isEqualTo("это устарело сейчас");
    }

    @Test
    void stripMarkdownRemovesHeadings() {
        String input = "# H1\n## H2\n### H3\nТекст";
        String result = TextUtils.stripMarkdown(input);
        assertThat(result).isEqualTo("H1 H2 H3 Текст");
    }

    @Test
    void stripMarkdownNormalizesWhitespace() {
        assertThat(TextUtils.stripMarkdown("  множественные   пробелы  "))
                .isEqualTo("множественные пробелы");
    }

    @Test
    void truncateAtWordBoundaryReturnsEmptyForNull() {
        assertThat(TextUtils.truncateAtWordBoundary(null, 10)).isEmpty();
    }

    @Test
    void truncateAtWordBoundaryReturnsEmptyForEmpty() {
        assertThat(TextUtils.truncateAtWordBoundary("", 10)).isEmpty();
    }

    @Test
    void truncateAtWordBoundaryKeepsShortText() {
        assertThat(TextUtils.truncateAtWordBoundary("short", 100)).isEqualTo("short");
    }

    @Test
    void truncateAtWordBoundaryCutsAtSpace() {
        String result = TextUtils.truncateAtWordBoundary("один два три четыре пять", 10);
        // Граница пробела есть: должно обрезаться по последнему пробелу до 10
        assertThat(result).endsWith("…");
        assertThat(result).doesNotContain("четыре"); // обрезано до конца
    }

    @Test
    void truncateAtWordBoundaryFallsBackToHardCut() {
        // Длинное слово без пробелов — должно обрезаться жёстко
        String result = TextUtils.truncateAtWordBoundary("aaaaaaaaaaaaaaaaaa", 10);
        assertThat(result).hasSize(11); // 10 + ellipsis
        assertThat(result).endsWith("…");
    }

    // NUL задаём через (char) 0, чтобы не вносить сам 0x00-байт в исходник теста.
    private static final String NUL = String.valueOf((char) 0);

    @ParameterizedTest
    @NullAndEmptySource
    void stripNulCharsReturnsInputForNullOrEmpty(String input) {
        assertThat(TextUtils.stripNulChars(input)).isEqualTo(input);
    }

    @Test
    void stripNulCharsRemovesNulByte() {
        String input = "abc" + NUL + "def" + NUL;
        assertThat(TextUtils.stripNulChars(input)).isEqualTo("abcdef");
        assertThat(TextUtils.stripNulChars(input)).doesNotContain(NUL);
    }

    @Test
    void stripNulCharsKeepsCleanTextUnchanged() {
        String clean = "обычный текст без мусора";
        assertThat(TextUtils.stripNulChars(clean)).isSameAs(clean);
    }
}
