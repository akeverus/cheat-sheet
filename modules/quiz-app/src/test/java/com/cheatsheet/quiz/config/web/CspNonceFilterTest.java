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

    @Test
    void buildPolicyHardensDocumentDirectives() {
        String policy = CspNonceFilter.buildPolicy("abc123");

        // base-uri/form-action/object-src не наследуются от default-src → должны быть заданы явно.
        assertThat(policy).contains("base-uri 'self'");
        assertThat(policy).contains("form-action 'self'");
        assertThat(policy).contains("object-src 'none'");
    }
}
