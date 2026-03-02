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

        GeneratedQuestionMetadata metadata = supplier.metadata();

        assertThat(metadata.getSourceSlug()).isEqualTo("ai-generated");
        assertThat(metadata.getFilePath()).isEqualTo("ai-generated");
        assertThat(metadata.getSourceHash()).isEqualTo("ai-generated");
    }
}
