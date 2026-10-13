
package com.IIITV.EMS.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int LIMIT = 100;
    private static final int WINDOW_SECONDS = 60;

    private static class Counter {
        final AtomicInteger count = new AtomicInteger(0);
        volatile long windowStart = System.currentTimeMillis();
    }

    private final ConcurrentHashMap<String, Counter> clients =
            new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String client = request.getRemoteAddr();

        Counter counter = clients.computeIfAbsent(
                client, key -> new Counter());

        synchronized (counter) {
            long now = System.currentTimeMillis();

            if (now - counter.windowStart >= WINDOW_SECONDS * 1000L) {
                counter.count.set(0);
                counter.windowStart = now;
            }

            int current = counter.count.incrementAndGet();

            response.setHeader("X-RateLimit-Limit",
                    String.valueOf(LIMIT));
            response.setHeader("X-RateLimit-Remaining",
                    String.valueOf(Math.max(0, LIMIT - current)));

            if (current > LIMIT) {
                response.setStatus(429);
                response.setHeader("Retry-After",
                        String.valueOf(WINDOW_SECONDS));
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"status\":429,\"error\":\"Too Many Requests\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
