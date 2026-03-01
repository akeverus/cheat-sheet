package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import com.cheatsheet.quiz.domain.QuestionType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionUniquenessServiceTest {

    @Test
    void remembersFingerprintsPerTopicAndType() {
        QuestionUniquenessService service = new QuestionUniquenessService(testProperties(120, 20));

        service.rememberFingerprint("java", QuestionType.CODE, "fp-1");
        service.rememberFingerprint("java", QuestionType.CODE, "fp-2");
        service.rememberFingerprint("java", QuestionType.CONCEPT, "fp-3");

        assertThat(service.loadRecentFingerprints("java", QuestionType.CODE))
                .containsExactlyInAnyOrder("fp-1", "fp-2");
        assertThat(service.loadRecentFingerprints("java", QuestionType.CONCEPT))
                .containsExactly("fp-3");
    }

    @Test
    void evictsOldestWhenPerKeyLimitExceeded() {
        QuestionUniquenessService service = new QuestionUniquenessService(testProperties(120, 2));

        service.rememberFingerprint("spring", QuestionType.ARCHITECTURE, "fp-1");
        service.rememberFingerprint("spring", QuestionType.ARCHITECTURE, "fp-2");
        service.rememberFingerprint("spring", QuestionType.ARCHITECTURE, "fp-3");

        assertThat(service.loadRecentFingerprints("spring", QuestionType.ARCHITECTURE))
                .containsExactlyInAnyOrder("fp-2", "fp-3")
                .doesNotContain("fp-1");
    }

    private AppProperties testProperties(int windowMinutes, int maxPerKey) {
        AppProperties appProperties = new AppProperties();
        appProperties.getInterview().setQuestionUniquenessWindowMinutes(windowMinutes);
        appProperties.getInterview().setQuestionUniquenessMaxFingerprintsPerKey(maxPerKey);
        return appProperties;
    }
}
