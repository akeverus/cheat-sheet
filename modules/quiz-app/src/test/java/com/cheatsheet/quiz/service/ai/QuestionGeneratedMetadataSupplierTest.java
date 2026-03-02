package com.cheatsheet.quiz.service.ai;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QuestionGeneratedMetadataSupplierTest {

    private final QuestionGeneratedMetadataSupplier supplier = new QuestionGeneratedMetadataSupplier();

    @Test
    void suppliesStableGeneratedMetadataValues() {
        assertThat(supplier.sourceSlug()).isEqualTo("generated");
        assertThat(supplier.filePath()).isEqualTo("generated");
        assertThat(supplier.sourceHash()).isEqualTo("generated");
    }
}
