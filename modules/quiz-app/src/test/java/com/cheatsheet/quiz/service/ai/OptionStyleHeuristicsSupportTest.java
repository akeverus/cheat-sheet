package com.cheatsheet.quiz.service.ai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OptionStyleHeuristicsSupportTest {

    @Test
    void detectsPathLikeFragmentsByMarker() {
        assertThat(OptionStyleHeuristicsSupport.isPathLikeOptionText("см. modules/quiz-app/src/main/java"))
                .isTrue();
    }

    @Test
    void treatsDomainRatioAsNonPathLike() {
        assertThat(OptionStyleHeuristicsSupport.isPathLikeOptionText("сравнивают отношение ценность/вес"))
                .isFalse();
    }

    @Test
    void detectsVaguePlaceholderWithoutConcreteSignal() {
        assertThat(OptionStyleHeuristicsSupport.isVaguePlaceholderWithoutConcreteSignal(
                "используют другой механизм в зависимости от ситуации"
        )).isTrue();
    }

    @Test
    void ignoresVaguePhraseWhenConcreteSignalExists() {
        assertThat(OptionStyleHeuristicsSupport.isVaguePlaceholderWithoutConcreteSignal(
                "используют другой механизм: ttl 60 сек и invalidate по ключу"
        )).isFalse();
    }
}
