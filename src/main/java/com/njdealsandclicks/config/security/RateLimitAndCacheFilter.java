package com.njdealsandclicks.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Order(2)
public class RateLimitAndCacheFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS_PER_IP = 100;
    private static final long TIME_WINDOW_MS = TimeUnit.MINUTES.toMillis(10);
    private static final long CACHE_TTL_SECONDS = 60;

    private static final Map<String, RequestCounter> ipRequestCounts = new ConcurrentHashMap<>();
    private static final Map<String, CachedResponse> cache = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        if (path.startsWith("/api/public/")) {
            // 1. Rate limiting su qualunque metodo
            RequestCounter counter = ipRequestCounts.computeIfAbsent(clientIp, k -> new RequestCounter());
            synchronized (counter) {
                long now = System.currentTimeMillis();
                if (now - counter.timestamp > TIME_WINDOW_MS) {
                    ipRequestCounts.remove(clientIp);
                } else {
                    counter.count++;
                    if (counter.count == MAX_REQUESTS_PER_IP + 1) {
                        log.warn("BLOCKED IP: {} - User-Agent: {}", clientIp, userAgent);
                    }
                    if (counter.count > MAX_REQUESTS_PER_IP) {
                        response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit exceeded");
                        return;
                    }
                }
            }

            // 2. Cache solo su /products + GET (anche con query string)
            if (HttpMethod.GET.matches(method) && path.startsWith("/api/public/products")) {
                String key = path + "?" + (request.getQueryString() != null ? request.getQueryString() : "");
                CachedResponse cached = cache.get(key);
                if (cached != null && Instant.now().isBefore(cached.expiry)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setHeader("Cache-Control", "public, max-age=" + CACHE_TTL_SECONDS);
                    response.getWriter().write(cached.body);
                    return;
                }

                CachingHttpServletResponseWrapper wrapper = new CachingHttpServletResponseWrapper(response);
                try {
                    filterChain.doFilter(request, wrapper);
                } catch (Exception e) {
                    e.printStackTrace(); // oppure loggalo con logger
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal error occurred");
                    return;
                }

                String body = wrapper.getContentAsString();
                response.setStatus(wrapper.getStatus());
                response.setContentType("application/json"); // oppure copia dinamicamente se serve
                response.setHeader("Cache-Control", "public, max-age=" + CACHE_TTL_SECONDS);
                response.getWriter().write(body);

                // solo se è 200, allora salvi in cache
                if (wrapper.getStatus() == HttpServletResponse.SC_OK) {
                    cache.put(key, new CachedResponse(body, Instant.now().plusSeconds(CACHE_TTL_SECONDS)));
                }

                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private static class RequestCounter {
        int count = 0;
        long timestamp = System.currentTimeMillis();
    }

    private static class CachedResponse {
        String body;
        Instant expiry;
        CachedResponse(String body, Instant expiry) {
            this.body = body;
            this.expiry = expiry;
        }
    }
}
