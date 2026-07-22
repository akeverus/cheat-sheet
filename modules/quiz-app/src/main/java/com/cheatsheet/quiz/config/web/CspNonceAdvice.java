package com.cheatsheet.quiz.config.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CspNonceAdvice {

    @ModelAttribute("cspNonce")
    public String cspNonce(HttpServletRequest request) {
        Object nonce = request.getAttribute(CspNonceFilter.REQUEST_ATTR);
        return nonce == null ? "" : nonce.toString();
    }
}
