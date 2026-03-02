package com.cheatsheet.quiz.service.ai;

import com.cheatsheet.quiz.config.AppProperties;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionGeneratedMetadataSupplierTest {

    @Test
    void suppliesConfiguredGeneratedMetadataValues() {
        AppProperties appProperties = new AppProperties();
        appProperties.getInterview().setQuestionGeneratedMetadataValue("ai-generated");
        QuestionGeneratedMetadataSupplier supplier = new QuestionGeneratedMetadataSupplier(appProperties);

        assertThat(supplier.sourceSlug()).isEqualTo("ai-generated");
        assertThat(supplier.filePath()).isEqualTo("ai-generated");
        assertThat(supplier.sourceHash()).isEqualTo("ai-generated");
    }
}
