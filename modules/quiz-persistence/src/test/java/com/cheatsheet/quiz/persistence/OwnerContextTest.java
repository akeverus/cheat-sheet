package com.cheatsheet.quiz.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OwnerContextTest {

    @AfterEach
    void clear() {
        OwnerContext.clear();
    }

    @Test
    void defaultsToLocal() {
        assertThat(OwnerContext.current()).isEqualTo("local");
    }

    @Test
    void rejectsUnsafeKeys() {
        assertThatThrownBy(() -> OwnerContext.set("alice'; DROP TABLE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("owner_key");
        assertThat(OwnerContext.current()).isEqualTo("local");
    }

    @Test
    void acceptsUsernameLikeKeys() {
        OwnerContext.set("trainer@local");
        assertThat(OwnerContext.current()).isEqualTo("trainer@local");
    }

    @Test
    void acceptsOauthOwnershipKeys() {
        OwnerContext.set("oauth:oidc:user-42");
        assertThat(OwnerContext.current()).isEqualTo("oauth:oidc:user-42");
    }
}
