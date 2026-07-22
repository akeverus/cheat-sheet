package com.cheatsheet.quiz.config.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CspNonceFilterTest {

    @Test
    void buildPolicyUsesNonceWithoutStyleUnsafeInline() {
        String policy = CspNonceFilter.buildPolicy("abc123");

        assertThat(policy).contains("script-src 'self' 'nonce-abc123'");
        assertThat(policy).contains("style-src 'self' 'nonce-abc123'");
        assertThat(policy).contains("style-src-elem 'self' 'nonce-abc123'");
        assertThat(policy).contains("style-src-attr 'none'");
        assertThat(policy).doesNotContain("unsafe-inline");
    }
}
