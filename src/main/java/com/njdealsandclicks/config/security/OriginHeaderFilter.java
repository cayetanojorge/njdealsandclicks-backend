package com.njdealsandclicks.config.security;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(1)
public class OriginHeaderFilter extends OncePerRequestFilter {

    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    private static final List<String> ALLOWED_ORIGINS = List.of(
        "https://njdealsandclicks.com",
        "https://www.njdealsandclicks.com",
        "http://localhost:5173"
    );

    private static final List<String> LOCAL_PROFILES = List.of("develop", "test");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/api/public/")) {
            String origin = request.getHeader("Origin");

            if (LOCAL_PROFILES.contains(activeProfile)) {
                filterChain.doFilter(request, response); // libero in dev e test
                return;
            }

            if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
                filterChain.doFilter(request, response); // solo da dominio valido
                return;
            }

            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied: Origin not allowed");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
