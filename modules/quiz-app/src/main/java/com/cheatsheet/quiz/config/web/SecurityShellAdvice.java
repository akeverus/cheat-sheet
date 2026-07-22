package com.cheatsheet.quiz.config.web;

import com.cheatsheet.quiz.api.security.CurrentUserAccessor;
import com.cheatsheet.quiz.api.security.SensitiveEndpointAccessService;
import com.cheatsheet.quiz.config.app.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Shell flags for Hybrid multi mode (logout affordance, display name)
 * and admin-gated danger-zone UI.
 */
@ControllerAdvice
@RequiredArgsConstructor
public class SecurityShellAdvice {

    private final AppProperties appProperties;
    private final CurrentUserAccessor currentUserAccessor;
    private final SensitiveEndpointAccessService sensitiveEndpointAccessService;

    @ModelAttribute("multiSecurityMode")
    public boolean multiSecurityMode() {
        return "multi".equalsIgnoreCase(appProperties.getSecurity().getMode());
    }

    @ModelAttribute("currentUsername")
    public String currentUsername() {
        return currentUserAccessor.currentUsername().orElse(null);
    }

    /** Show MCQ bank reset only when an admin gate exists (token and/or ROLE_ADMIN). */
    @ModelAttribute("optionsBankResetAllowed")
    public boolean optionsBankResetAllowed() {
        return sensitiveEndpointAccessService.isTokenConfigured()
                || sensitiveEndpointAccessService.hasAdminRole();
    }

    /** Form must collect admin token unless the principal already has ROLE_ADMIN. */
    @ModelAttribute("adminTokenRequired")
    public boolean adminTokenRequired() {
        return !sensitiveEndpointAccessService.hasAdminRole();
    }
}
