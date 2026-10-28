package com.IIITV.EMS.Security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.IIITV.EMS.Model.ActivityLog;
import com.IIITV.EMS.Model.User;
import com.IIITV.EMS.Repository.ActivityLogRepository;
import com.IIITV.EMS.Repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ActivityLogFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ActivityLogFilter.class);

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public ActivityLogFilter(
            ActivityLogRepository activityLogRepository,
            UserRepository userRepository) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getServletPath();
        return !path.startsWith("/api/")
                || path.startsWith("/api/auth/")
                || "GET".equalsIgnoreCase(method)
                || "HEAD".equalsIgnoreCase(method)
                || "OPTIONS".equalsIgnoreCase(method);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            saveActivity(request, response.getStatus());
        }
    }

    private void saveActivity(
            HttpServletRequest request,
            int statusCode) {
        try {
            ActivityLog log = new ActivityLog();
            log.setOccurredAt(LocalDateTime.now());
            log.setEndpoint(request.getServletPath());
            log.setCategory(categoryFor(request.getServletPath()));
            log.setAction(actionFor(request.getMethod(), request.getServletPath()));
            log.setStatusCode(statusCode);
            log.setOutcome(statusCode < 400 ? "Success" : "Failed");

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String actorId = authentication.getName();
                log.setActorId(actorId);
                Optional<User> actor = userRepository.findById(actorId);
                log.setActorName(actor.map(User::getName).orElse(actorId));
                log.setActorRole(authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(authority -> authority.startsWith("ROLE_"))
                        .map(authority -> authority.substring("ROLE_".length()))
                        .findFirst()
                        .orElse("USER"));
            } else {
                log.setActorId("unknown");
                log.setActorName("Unknown user");
                log.setActorRole("UNKNOWN");
            }

            activityLogRepository.save(log);
        } catch (RuntimeException exception) {
            logger.warn("Could not save activity log for {}", request.getServletPath(), exception);
        }
    }

    private String categoryFor(String path) {
        if (path.startsWith("/api/users"))
            return "Users";
        if (path.startsWith("/api/quizzes"))
            return "Examinations";
        if (path.startsWith("/api/questions")
                || path.startsWith("/api/question-bank"))
            return "Questions";
        if (path.startsWith("/api/admin"))
            return "Administration";
        return "System";
    }

    private String actionFor(String method, String path) {
        if ("POST".equalsIgnoreCase(method))
            return "Created resource";
        if ("PUT".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method))
            return "Updated resource";
        if ("DELETE".equalsIgnoreCase(method))
            return "Deleted resource";
        return method + " request";
    }
}
