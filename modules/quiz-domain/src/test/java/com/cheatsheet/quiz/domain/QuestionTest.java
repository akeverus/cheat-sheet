package com.cheatsheet.quiz.domain;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Тесты инвариантов ядровой сущности {@link Question} (compact-конструктор,
 * фабрика {@link Question#forImport}, legacy-конструктор). Фиксируют контракт,
 * от которого зависит импорт и рендер вопросов:
 *
 * <ul>
 *   <li>обязательные поля {@code slug}/{@code questionText} → NPE с русским сообщением;</li>
 *   <li>{@code difficulty == null} → дефолт {@link Difficulty#MEDIUM};</li>
 *   <li>{@code tags}/{@code options == null} → пустые списки; переданные —
 *       <b>защитно копируются</b> и наружу отдаются иммутабельными;</li>
 *   <li>отрицательный {@code regenCount} → {@link IllegalArgumentException};</li>
 *   <li>{@link Question#type()} — синоним {@code questionType()};</li>
 *   <li>{@link Question#forImport} — id=0, {@code sourceSlug == slug}, дефолты nullable-полей.</li>
 * </ul>
 */
class QuestionTest {

    @Test
    void nullSlugThrowsNpeWithMessage() {
        assertThatThrownBy(() -> Question.builder().questionText("q").build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("slug не может быть null");
    }

    @Test
    void nullQuestionTextThrowsNpeWithMessage() {
        assertThatThrownBy(() -> Question.builder().slug("java.md#Q1").build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("questionText не может быть null");
    }

    @Test
    void nullDifficultyDefaultsToMedium() {
        Question q = Question.builder().slug("s").questionText("q").difficulty(null).build();
        assertThat(q.difficulty()).isEqualTo(Difficulty.MEDIUM);
    }

    @Test
    void nullCollectionsDefaultToEmpty() {
        Question q = Question.builder().slug("s").questionText("q").tags(null).options(null).build();
        assertThat(q.tags()).isEmpty();
        assertThat(q.options()).isEmpty();
    }

    @Test
    void negativeRegenCountThrows() {
        assertThatThrownBy(() -> Question.builder().slug("s").questionText("q").regenCount(-1).build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-1");
    }

    @Test
    void tagsAreDefensivelyCopiedFromSource() {
        List<String> source = new ArrayList<>(List.of("java", "jvm"));
        Question q = Question.builder().slug("s").questionText("q").tags(source).build();

        source.add("mutated-after-construction");

        assertThat(q.tags()).containsExactly("java", "jvm");
    }

    @Test
    void exposedCollectionsAreImmutable() {
        Question q = Question.builder()
                .slug("s")
                .questionText("q")
                .tags(new ArrayList<>(List.of("java")))
                .build();

        assertThatThrownBy(() -> q.tags().add("boom"))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> q.options().add(
                        QuestionOption.builder().id("x").text("t").build()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void typeIsSynonymOfQuestionType() {
        Question q = Question.builder()
                .slug("s").questionText("q").questionType(QuestionType.CODE).build();
        assertThat(q.type()).isEqualTo(q.questionType()).isEqualTo(QuestionType.CODE);
    }

    @Test
    void forImportSetsIdZeroAndSourceSlugEqualToSlug() {
        Question q = Question.forImport(
                "java.md#Q1", "java.md", "java", "Что такое JVM?",
                "Ответ...", true, "hash-abc", QuestionType.TEXT, null);

        assertThat(q.id()).isZero();
        assertThat(q.sourceSlug()).isEqualTo(q.slug()).isEqualTo("java.md#Q1");
        assertThat(q.important()).isTrue();
        assertThat(q.difficulty()).isEqualTo(Difficulty.MEDIUM);
        assertThat(q.regenCount()).isZero();
        assertThat(q.diagramMermaid()).isNull();
        assertThat(q.takeaway()).isNull();
        assertThat(q.tags()).isEmpty();
        assertThat(q.options()).isEmpty();
    }

    @Test
    void legacyConstructorAppliesV2Defaults() {
        Question q = new Question(
                7L, "s", "src", "f.md", "topic", "текст", "ответ",
                false, "hash", QuestionType.TEXT, null, null, 0, "вывод");

        assertThat(q.difficulty()).isEqualTo(Difficulty.MEDIUM);
        assertThat(q.tags()).isEmpty();
        assertThat(q.options()).isEmpty();
        assertThat(q.takeaway()).isEqualTo("вывод");
    }
}
