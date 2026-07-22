package com.cheatsheet.quiz.config.web;

import com.cheatsheet.quiz.config.app.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityModeStartupGuardTest {

    @Test
    void prodLocalWithoutAckFailsFast() {
        AppProperties props = new AppProperties();
        props.getSecurity().setMode("local");
        props.getSecurity().setAllowInsecurePublic(false);
        Environment env = new MockEnvironment().withProperty("spring.profiles.active", "prod");
        ((MockEnvironment) env).setActiveProfiles("prod");

        SecurityModeStartupGuard guard = new SecurityModeStartupGuard(props, env);

        assertThatThrownBy(() -> guard.run(new DefaultApplicationArguments()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("allow-insecure-public");
    }

    @Test
    void prodMultiWithDefaultCredentialsFailsFast() {
        AppProperties props = new AppProperties();
        props.getSecurity().setMode("multi");
        props.getSecurity().getMulti().setUsername("trainer");
        props.getSecurity().getMulti().setPassword("trainer");
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles("prod");

        SecurityModeStartupGuard guard = new SecurityModeStartupGuard(props, env);

        assertThatThrownBy(() -> guard.run(new DefaultApplicationArguments()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("default credentials");
    }

    @Test
    void prodMultiWithCustomCredentialsPasses() {
        AppProperties props = new AppProperties();
        props.getSecurity().setMode("multi");
        props.getSecurity().getMulti().setUsername("ops");
        props.getSecurity().getMulti().setPassword("s3cret-strong");
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles("prod");

        SecurityModeStartupGuard guard = new SecurityModeStartupGuard(props, env);

        assertThatCode(() -> guard.run(new DefaultApplicationArguments())).doesNotThrowAnyException();
    }

    @Test
    void testProfileSkipsProdGuards() {
        AppProperties props = new AppProperties();
        props.getSecurity().setMode("local");
        MockEnvironment env = new MockEnvironment();
        env.setActiveProfiles("prod", "test");

        SecurityModeStartupGuard guard = new SecurityModeStartupGuard(props, env);

        assertThatCode(() -> guard.run(new DefaultApplicationArguments())).doesNotThrowAnyException();
    }
}
