package com.cheatsheet.quiz.config.web;

import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessDeniedHandler;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAuthenticationEntryPoint;
import com.cheatsheet.quiz.config.app.AppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

/**
 * Security baseline для production-grade конфигурации.
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CorsConfigurationSource corsConfigurationSource,
            SensitiveEndpointAccessService sensitiveEndpointAccessService,
            SensitiveEndpointAccessDeniedHandler sensitiveEndpointAccessDeniedHandler,
            SensitiveEndpointAuthenticationEntryPoint sensitiveEndpointAuthenticationEntryPoint,
            OwnerContextFilter ownerContextFilter,
            AppProperties appProperties,
            ObjectProvider<ClientRegistrationRepository> clientRegistrations,
            ObjectProvider<OAuth2UserService<OidcUserRequest, OidcUser>> oidcUserService,
            ObjectProvider<OAuth2UserService<OAuth2UserRequest, OAuth2User>> oauth2UserService
    ) throws Exception {
        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        boolean multi = "multi".equalsIgnoreCase(appProperties.getSecurity().getMode());
        boolean oauthEnabled = multi
                && appProperties.getSecurity().getOauth().isEnabled()
                && clientRegistrations.getIfAvailable() != null;
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        // SPA-friendly: cookie XSRF-TOKEN + header X-XSRF-TOKEN (JS apiFetch).
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                        // Export stays ignored: admin-token gate, often called without browser form.
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/export"),
                                new AntPathRequestMatcher("/actuator/**")
                        ))
                // Spring Security 6 defers cookie write until token is read — force materialize.
                .addFilterAfter(csrfCookieMaterializerFilter(), CsrfFilter.class)
                // CSP nonce per request (replaces static unsafe-inline script policy).
                .addFilterAfter(new CspNonceFilter(), CsrfFilter.class)
                // Bind owner_key after SecurityContext is populated (not as early servlet filter).
                .addFilterAfter(ownerContextFilter, SecurityContextHolderFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/admin/**", "/api/regenerate", "/export",
                                    "/settings/reset-options")
                            .access((authentication, context) -> hasSensitiveAccess(
                                    authentication.get(), context, sensitiveEndpointAccessService));
                    auth.requestMatchers(HttpMethod.GET, "/css/**", "/js/**", "/vendor/**", "/fonts/**",
                                    "/images/**", "/img/**", "/webjars/**", "/favicon.ico", "/favicon.svg").permitAll();
                    // Health only — metrics/prometheus/info follow mode (auth in multi).
                    auth.requestMatchers("/error", "/actuator/health", "/actuator/health/**").permitAll();
                    if (multi) {
                        auth.requestMatchers("/login", "/logout").permitAll();
                        auth.requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll();
                        // Progress SSR/API must be authenticated: anonymous maps to owner_key=local
                        // and would otherwise leak backfilled single-user progress.
                        auth.anyRequest().authenticated();
                    } else {
                        auth.requestMatchers(HttpMethod.GET, "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**")
                                .permitAll();
                        // Local single-user trust boundary (documented; prod guarded by SecurityModeStartupGuard).
                        auth.anyRequest().permitAll();
                    }
                })
                .httpBasic(multi ? Customizer.withDefaults() : AbstractHttpConfigurer::disable)
                .formLogin(form -> {
                    if (multi) {
                        form.loginPage("/login")
                                // Prefer SavedRequest (deep link) when present; else home.
                                .defaultSuccessUrl("/", false)
                                .permitAll();
                    } else {
                        form.disable();
                    }
                })
                .oauth2Login(oauth -> {
                    if (oauthEnabled) {
                        oauth.loginPage("/login")
                                .defaultSuccessUrl("/", false);
                        oauth.userInfoEndpoint(userInfo -> {
                            OAuth2UserService<OidcUserRequest, OidcUser> oidc = oidcUserService.getIfAvailable();
                            if (oidc != null) {
                                userInfo.oidcUserService(oidc);
                            }
                            OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2 = oauth2UserService.getIfAvailable();
                            if (oauth2 != null) {
                                userInfo.userService(oauth2);
                            }
                        });
                    } else {
                        oauth.disable();
                    }
                })
                .logout(logout -> {
                    if (multi) {
                        logout.logoutUrl("/logout")
                                .logoutSuccessUrl("/login?logout")
                                .permitAll();
                    } else {
                        logout.disable();
                    }
                })
                .headers(headers -> headers
                        // CSP: CspNonceFilter (nonce per request; no unsafe-inline for script).
                        .referrerPolicy(ref -> ref.policy(
                                org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                        .frameOptions(frame -> frame.sameOrigin())
                        .permissionsPolicy(policy -> policy.policy("geolocation=(), camera=(), microphone=()")))
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(sensitiveEndpointAccessDeniedHandler)
                        .authenticationEntryPoint(sensitiveEndpointAuthenticationEntryPoint))
                .rememberMe(AbstractHttpConfigurer::disable)
                .anonymous(Customizer.withDefaults());
        return http.build();
    }

    /**
     * OwnerContextFilter is wired into the security chain; disable servlet auto-registration
     * so it does not run before SecurityContextHolderFilter.
     */
    @Bean
    FilterRegistrationBean<OwnerContextFilter> ownerContextFilterRegistration(OwnerContextFilter filter) {
        FilterRegistrationBean<OwnerContextFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    /**
     * Forces {@code XSRF-TOKEN} cookie on GET responses so JS {@code apiFetch} can send
     * {@code X-XSRF-TOKEN} without a prior POST handshake (Spring Security 6 deferred CSRF).
     */
    private static OncePerRequestFilter csrfCookieMaterializerFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if (csrfToken != null) {
                    csrfToken.getToken();
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    /**
     * Spring Security SPA CSRF pattern: BREACH-protected request attribute + raw cookie for JS.
     */
    private static final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
        private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
        private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                           Supplier<CsrfToken> csrfToken) {
            xor.handle(request, response, csrfToken);
            csrfToken.get();
        }

        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
            String headerValue = request.getHeader(csrfToken.getHeaderName());
            return (headerValue != null && !headerValue.isBlank())
                    ? plain.resolveCsrfTokenValue(request, csrfToken)
                    : xor.resolveCsrfTokenValue(request, csrfToken);
        }
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(AppProperties appProperties) {
        AppProperties.Cors corsProps = appProperties.getSecurity().getCors();
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(safeList(corsProps.getAllowedOrigins()));
        configuration.setAllowedMethods(safeList(corsProps.getAllowedMethods()));
        configuration.setAllowedHeaders(safeList(corsProps.getAllowedHeaders()));
        configuration.setExposedHeaders(safeList(corsProps.getExposedHeaders()));
        configuration.setAllowCredentials(corsProps.isAllowCredentials());
        configuration.setMaxAge(corsProps.getMaxAgeSeconds());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // OPS-003: CORS только на API, не на страницы/ассеты.
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    private static List<String> safeList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
    }

    private static AuthorizationDecision hasSensitiveAccess(
            Authentication authentication,
            RequestAuthorizationContext context,
            SensitiveEndpointAccessService sensitiveEndpointAccessService
    ) {
        String token = context.getRequest().getHeader(SensitiveEndpointAccessService.ADMIN_TOKEN_HEADER);
        if (token == null || token.isBlank()) {
            token = context.getRequest().getParameter("adminToken");
        }
        if (sensitiveEndpointAccessService.isAuthorized(token)) {
            return new AuthorizationDecision(true);
        }
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> SensitiveEndpointAccessService.ROLE_ADMIN.equals(a.getAuthority()))) {
            return new AuthorizationDecision(true);
        }
        return new AuthorizationDecision(false);
    }
}
