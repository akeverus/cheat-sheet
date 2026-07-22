package com.cheatsheet.quiz.config.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Генерирует CSP nonce на запрос и выставляет Content-Security-Policy.
 * Script/style elements require nonce (no unsafe-inline). Early head bootstrap stamps
 * nonce onto Mermaid/hljs-injected {@code <style>} via createElement + MutationObserver.
 * Style attributes are blocked ({@code style-src-attr 'none'}); use classes/CSSOM.
 */
public final class CspNonceFilter extends OncePerRequestFilter {

    public static final String REQUEST_ATTR = "cspNonce";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String nonce = newNonce();
        request.setAttribute(REQUEST_ATTR, nonce);
        response.setHeader("Content-Security-Policy", buildPolicy(nonce));
        filterChain.doFilter(request, response);
    }

    static String buildPolicy(String nonce) {
        // Script + style elements: nonce only (early head script stamps nonce on Mermaid/hljs <style>).
        // style-src-attr none: ban HTML style=; CSSOM (element.style) remains allowed by browsers.
        return "default-src 'self'; "
                + "script-src 'self' 'nonce-" + nonce + "'; "
                + "script-src-elem 'self' 'nonce-" + nonce + "'; "
                + "style-src 'self' 'nonce-" + nonce + "'; "
                + "style-src-elem 'self' 'nonce-" + nonce + "'; "
                + "style-src-attr 'none'; "
                + "font-src 'self' data:; "
                + "img-src 'self' data:; "
                + "connect-src 'self'";
    }

    private static String newNonce() {
        byte[] bytes = new byte[16];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
