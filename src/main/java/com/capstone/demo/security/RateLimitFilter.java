package com.capstone.demo.security;
//Section: 103
//Group: 1
//Student Name: Osama Namroud, Hamad Ajja, Salih Samir
//Student ID: 1097833, 1087238,1087965
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 100;
    private static final long WINDOW_SECONDS = 60;

    private final Map<String, ClientRequestInfo> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.endsWith(".html")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/oauth2")
                || path.startsWith("/login")
                || path.startsWith("/logout")) {

            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        long currentTime = Instant.now().getEpochSecond();

        ClientRequestInfo info = requestCounts.get(clientIp);

        if (info == null || currentTime - info.windowStart >= WINDOW_SECONDS) {
            info = new ClientRequestInfo(1, currentTime);
            requestCounts.put(clientIp, info);
        } else {
            info.requestCount++;
        }

        if (info.requestCount > MAX_REQUESTS) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("""
                    {
                      "error": "Too Many Requests",
                      "message": "Rate limit exceeded. Please try again later."
                    }
                    """);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }

    private static class ClientRequestInfo {
        private int requestCount;
        private final long windowStart;

        private ClientRequestInfo(int requestCount, long windowStart) {
            this.requestCount = requestCount;
            this.windowStart = windowStart;
        }
    }
}