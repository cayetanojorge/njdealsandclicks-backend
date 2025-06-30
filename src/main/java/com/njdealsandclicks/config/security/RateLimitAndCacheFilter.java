package com.njdealsandclicks.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        // Rate limit e cache solo su API pubbliche GET/HEAD
        if ((HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method)) && path.startsWith("/api/public/")) {
            // 1. Rate limiting
            RequestCounter counter = ipRequestCounts.computeIfAbsent(clientIp, k -> new RequestCounter());
            synchronized (counter) {
                long now = System.currentTimeMillis();
                if (now - counter.timestamp > TIME_WINDOW_MS) {
                    ipRequestCounts.remove(clientIp);
                } else {
                    counter.count++;
                    if (counter.count == MAX_REQUESTS_PER_IP + 1) {
                        System.out.println("BLOCKED IP: " + clientIp + " - User-Agent: " + userAgent);
                    }
                    if (counter.count > MAX_REQUESTS_PER_IP) {
                        response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit exceeded");
                        return;
                    }
                }
            }

            // 2. Caching solo per /products (anche con query string)
            if (path.startsWith("/api/public/products")) {
                String key = path + "?" + (request.getQueryString() != null ? request.getQueryString() : "");
                CachedResponse cached = cache.get(key);
                if (cached != null && Instant.now().isBefore(cached.expiry)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.setHeader("Cache-Control", "public, max-age=" + CACHE_TTL_SECONDS);
                    response.getWriter().write(cached.body);
                    return;
                }

                // Avvolgi la response
                CachingHttpServletResponseWrapper wrapper = new CachingHttpServletResponseWrapper(response);
                filterChain.doFilter(request, wrapper);

                if (wrapper.getStatus() == HttpServletResponse.SC_OK) {
                    String body = wrapper.getContent();
                    response.setHeader("Cache-Control", "public, max-age=" + CACHE_TTL_SECONDS);
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
