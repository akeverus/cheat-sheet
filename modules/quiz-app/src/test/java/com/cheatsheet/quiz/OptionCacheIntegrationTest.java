package com.cheatsheet.quiz;

import static org.assertj.core.api.Assertions.assertThat;

import com.cheatsheet.quiz.domain.AnswerOption;
import com.cheatsheet.quiz.service.cache.OptionCache;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@ActiveProfiles("test")
class OptionCacheIntegrationTest {

    @DynamicPropertySource
    static void setInterviewPath(DynamicPropertyRegistry registry) {
        TestInterviewPath.register(registry);
    }

    @Autowired
    private OptionCache optionCache;

    @Test
    void optionCacheBeanWorksInContext() {
        List<AnswerOption> options = List.of(
                new AnswerOption(1L, 10L, "A", true, 0, "test", null),
                new AnswerOption(2L, 10L, "B", false, 1, "test", null)
        );
        optionCache.put(10L, options);

        assertThat(optionCache.get(10L)).isNotNull().hasSize(2);

        optionCache.invalidate(10L);
        assertThat(optionCache.get(10L)).isNull();
    }
}
