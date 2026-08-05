package com.cheatsheet.quiz.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты {@link InterviewFilter} — компактного record-фильтра с логикой в канониническом
 * конструкторе (нормализация topic/group, дефолт ordered) и деривациями для запросов к БД.
 * Фиксируют тонкости: blank→null, дефолт {@code ordered=true}, обнуление темы/группы при
 * shuffle и трёхзначную семантику {@code isOrdered} ({@code !Boolean.FALSE.equals}).
 */
class InterviewFilterTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    void blankTopicAndGroupNormalizeToNull(String blank) {
        InterviewFilter filter = InterviewFilter.builder().topic(blank).group(blank).build();
        assertThat(filter.topic()).isNull();
        assertThat(filter.group()).isNull();
    }

    @Test
    void topicAndGroupAreStripped() {
        InterviewFilter filter = InterviewFilter.builder().topic("  java/core  ").group("  Backend  ").build();
        assertThat(filter.topic()).isEqualTo("java/core");
        assertThat(filter.group()).isEqualTo("Backend");
    }

    @Test
    void orderedDefaultsToTrueWhenNull() {
        InterviewFilter filter = InterviewFilter.builder().topic("x").build();
        assertThat(filter.ordered()).isTrue();
        assertThat(filter.isOrdered()).isTrue();
    }

    @Test
    void isOrderedFalseOnlyWhenExplicitlyFalse() {
        assertThat(InterviewFilter.builder().ordered(false).build().isOrdered()).isFalse();
        assertThat(InterviewFilter.builder().ordered(true).build().isOrdered()).isTrue();
    }

    @Test
    void effectiveTopicAndGroupAreNullWhenShuffled() {
        InterviewFilter filter = InterviewFilter.builder()
                .topic("java").group("Backend").shuffle(true).build();
        assertThat(filter.effectiveTopic()).isNull();
        assertThat(filter.effectiveGroup()).isNull();
        assertThat(filter.isShuffled()).isTrue();
    }

    @Test
    void effectiveTopicAndGroupReturnValuesWhenNotShuffled() {
        InterviewFilter notShuffled = InterviewFilter.builder()
                .topic("java").group("Backend").shuffle(false).build();
        assertThat(notShuffled.effectiveTopic()).isEqualTo("java");
        assertThat(notShuffled.effectiveGroup()).isEqualTo("Backend");
        assertThat(notShuffled.isShuffled()).isFalse();

        InterviewFilter shuffleNull = InterviewFilter.builder().topic("java").group("Backend").build();
        assertThat(shuffleNull.effectiveTopic()).isEqualTo("java");
        assertThat(shuffleNull.effectiveGroup()).isEqualTo("Backend");
        assertThat(shuffleNull.isShuffled()).isFalse();
    }

    @Test
    void canonicalConstructorNormalizesAndKeepsExplicitValues() {
        InterviewFilter filter = new InterviewFilter("  java  ", "Backend", true, false, null, null);
        assertThat(filter.topic()).isEqualTo("java");
        assertThat(filter.group()).isEqualTo("Backend");
        assertThat(filter.importantOnly()).isTrue();
        assertThat(filter.onlyWrong()).isFalse();
        assertThat(filter.shuffle()).isNull();
        assertThat(filter.isShuffled()).isFalse();
        assertThat(filter.isOrdered()).isTrue();
    }

    /**
     * {@code isNarrowed} отвечает за состав выборки, поэтому порядок в него не входит:
     * подпись «фильтр» рядом с числом очереди обязана означать «показано не всё»,
     * а перемешивание показывает ровно тот же банк.
     */
    @Test
    void isNarrowedIgnoresOrderingFlags() {
        assertThat(InterviewFilter.builder().build().isNarrowed()).isFalse();
        assertThat(InterviewFilter.builder().shuffle(true).ordered(false).build().isNarrowed()).isFalse();
        assertThat(InterviewFilter.builder().importantOnly(false).onlyWrong(false).build().isNarrowed()).isFalse();
        // Пустая тема нормализуется в null и сузить выборку не может.
        assertThat(InterviewFilter.builder().topic("   ").build().isNarrowed()).isFalse();
    }

    @Test
    void isNarrowedWhenAnyCompositionFilterIsSet() {
        assertThat(InterviewFilter.builder().topic("java").build().isNarrowed()).isTrue();
        assertThat(InterviewFilter.builder().group("Backend").build().isNarrowed()).isTrue();
        assertThat(InterviewFilter.builder().importantOnly(true).build().isNarrowed()).isTrue();
        assertThat(InterviewFilter.builder().onlyWrong(true).build().isNarrowed()).isTrue();
    }
}
