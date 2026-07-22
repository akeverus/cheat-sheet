package com.cheatsheet.quiz.config.web;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.persistence.OwnerContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Skeleton multi-user identity: in-memory HTTP Basic / form accounts when {@code app.security.mode=multi}.
 * Optional OAuth2/OIDC: {@code app.security.oauth.enabled=true} + client registration properties.
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "app.security", name = "mode", havingValue = "multi")
public class MultiUserSecurityConfig {

    @Bean
    UserDetailsService multiUserDetailsService(AppProperties appProperties, PasswordEncoder passwordEncoder) {
        AppProperties.MultiUser multi = appProperties.getSecurity().getMulti();
        List<UserDetails> users = new ArrayList<>();
        users.add(account(multi.getUsername(), multi.getPassword(), passwordEncoder));
        if (multi.getExtraUsers() != null) {
            for (AppProperties.Account extra : multi.getExtraUsers()) {
                users.add(account(extra.getUsername(), extra.getPassword(), passwordEncoder));
            }
        }
        return new InMemoryUserDetailsManager(users);
    }

    private static UserDetails account(String username, String password, PasswordEncoder encoder) {
        if (!OwnerContext.isSafe(username)) {
            throw new IllegalStateException(
                    "app.security.multi username '" + username
                            + "' is not a valid owner_key (allowed: [a-zA-Z0-9._@+:-]{1,256})");
        }
        return User.builder()
                .username(username)
                .password(encoder.encode(password))
                .roles("USER")
                .build();
    }
}
