package com.cheatsheet.quiz.config.web;

import com.cheatsheet.quiz.config.app.AppProperties;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Hybrid security fail-fast:
 * <ul>
 *   <li>prod + mode=local without allow-insecure-public</li>
 *   <li>prod + mode=multi with default trainer/trainer credentials</li>
 * </ul>
 */
@Component
public class SecurityModeStartupGuard implements ApplicationRunner {

    private static final String DEFAULT_MULTI_USER = "trainer";
    private static final String DEFAULT_MULTI_PASSWORD = "trainer";

    private final AppProperties appProperties;
    private final Environment environment;

    public SecurityModeStartupGuard(AppProperties appProperties, Environment environment) {
        this.appProperties = appProperties;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        AppProperties.Security security = appProperties.getSecurity();
        boolean prod = Arrays.stream(environment.getActiveProfiles())
                .anyMatch(p -> "prod".equalsIgnoreCase(p));
        boolean test = Arrays.stream(environment.getActiveProfiles())
                .anyMatch(p -> "test".equalsIgnoreCase(p));
        if (!prod || test) {
            return;
        }
        if ("local".equalsIgnoreCase(security.getMode()) && !security.isAllowInsecurePublic()) {
            throw new IllegalStateException(
                    "app.security.mode=local is not allowed under prod profile without "
                            + "app.security.allow-insecure-public=true. "
                            + "Use mode=multi for public deploy, or acknowledge local trust boundary.");
        }
        if ("multi".equalsIgnoreCase(security.getMode())) {
            AppProperties.MultiUser multi = security.getMulti();
            if (DEFAULT_MULTI_USER.equals(multi.getUsername())
                    && DEFAULT_MULTI_PASSWORD.equals(multi.getPassword())) {
                throw new IllegalStateException(
                        "app.security.mode=multi under prod forbids default credentials trainer/trainer. "
                                + "Set app.security.multi.username / app.security.multi.password "
                                + "(env APP_SECURITY_MULTI_USERNAME / APP_SECURITY_MULTI_PASSWORD).");
            }
        }
    }
}
