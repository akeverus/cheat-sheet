package com.cheatsheet.quiz.config.web;

import com.cheatsheet.quiz.api.security.CurrentUserAccessor;
import com.cheatsheet.quiz.persistence.OwnerContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Binds {@link OwnerContext} from the authenticated principal.
 * Registered inside {@link SecurityConfig} after {@code SecurityContextHolderFilter}
 * (servlet {@code @Order} would run too early and always bind {@code local}).
 */
@Component
@RequiredArgsConstructor
public class OwnerContextFilter extends OncePerRequestFilter {

    private final CurrentUserAccessor currentUserAccessor;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        OwnerContext.set(currentUserAccessor.ownershipKey());
        try {
            filterChain.doFilter(request, response);
        } finally {
            OwnerContext.clear();
        }
    }
}
