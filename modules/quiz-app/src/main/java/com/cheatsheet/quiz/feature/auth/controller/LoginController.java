package com.cheatsheet.quiz.feature.auth.controller;

import com.cheatsheet.quiz.config.app.AppProperties;
import com.cheatsheet.quiz.feature.auth.service.LoginPageService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Login page for Hybrid {@code app.security.mode=multi} (form login + optional OAuth2 + HTTP Basic for APIs).
 */
@Controller
@ConditionalOnProperty(prefix = "app.security", name = "mode", havingValue = "multi")
public class LoginController {

    private final AppProperties appProperties;
    private final ObjectProvider<ClientRegistrationRepository> clientRegistrations;
    private final LoginPageService loginPageService;

    public LoginController(
            AppProperties appProperties,
            ObjectProvider<ClientRegistrationRepository> clientRegistrations,
            LoginPageService loginPageService
    ) {
        this.appProperties = appProperties;
        this.clientRegistrations = clientRegistrations;
        this.loginPageService = loginPageService;
    }

    @GetMapping("/login")
    public String login(
            Model model,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout
    ) {
        model.addAttribute("loginError", error != null);
        model.addAttribute("loginLogout", logout != null);
        // Обзор банка вопросов — вторая колонка страницы. До входа это
        // единственное, что можно рассказать честно: прогресса ещё нет.
        model.addAttribute("bankOverview", loginPageService.overview());
        AppProperties.Oauth oauth = appProperties.getSecurity().getOauth();
        if (oauth.isEnabled()) {
            ClientRegistrationRepository repo = clientRegistrations.getIfAvailable();
            if (repo != null) {
                ClientRegistration registration = repo.findByRegistrationId(oauth.getRegistrationId());
                if (registration != null) {
                    model.addAttribute("oauthLoginUrl",
                            "/oauth2/authorization/" + registration.getRegistrationId());
                    model.addAttribute("oauthDisplayName", oauth.getDisplayName());
                }
            }
        }
        return "login";
    }
}
