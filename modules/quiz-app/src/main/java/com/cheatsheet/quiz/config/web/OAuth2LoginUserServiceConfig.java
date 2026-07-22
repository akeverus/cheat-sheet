package com.cheatsheet.quiz.config.web;

import com.cheatsheet.quiz.api.security.OrgIdpGroupAuthoritiesMapper;
import com.cheatsheet.quiz.config.app.AppProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Wraps default OAuth2/OIDC user services to attach org group authorities.
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "app.security.oauth", name = "enabled", havingValue = "true")
public class OAuth2LoginUserServiceConfig {

    @Bean
    OrgIdpGroupAuthoritiesMapper orgIdpGroupAuthoritiesMapper(AppProperties appProperties) {
        return new OrgIdpGroupAuthoritiesMapper(appProperties.getSecurity().getOauth());
    }

    @Bean
    OAuth2UserService<OidcUserRequest, OidcUser> quizOidcUserService(OrgIdpGroupAuthoritiesMapper mapper) {
        OidcUserService delegate = new OidcUserService();
        return userRequest -> {
            OidcUser user = delegate.loadUser(userRequest);
            return new DefaultOidcUser(mapper.map(user), user.getIdToken(), user.getUserInfo());
        };
    }

    @Bean
    OAuth2UserService<OAuth2UserRequest, OAuth2User> quizOAuth2UserService(OrgIdpGroupAuthoritiesMapper mapper) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return userRequest -> {
            OAuth2User user = delegate.loadUser(userRequest);
            String nameAttributeKey = userRequest.getClientRegistration()
                    .getProviderDetails()
                    .getUserInfoEndpoint()
                    .getUserNameAttributeName();
            return new DefaultOAuth2User(mapper.map(user), user.getAttributes(), nameAttributeKey);
        };
    }
}
